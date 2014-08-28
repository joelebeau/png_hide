package pnghide;
/* PNGHide - Joe LeBeau
 * CS 492 - Computer Security - April 2013
 * PHSteg.java handles the actual steganography operations for the program.
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PHSteg
{
	private final String BREAK = "b^r^k"; // Constant used for determining the end of the data.
	private File hideFile;
	private File outputFile;
	private FileInputStream iStream;
	private FileOutputStream oStream;
	private BufferedImage revealImage;
	private BufferedImage outImage;
	private File hiddenFile;
	private File revealFile;
	private long availableBits;
	private long requiredBits;
	private int requiredBytes;
	
	public PHSteg()
	{
		hideFile = null;
		hiddenFile = null;
		revealFile = null;		
		availableBits = 0;
		requiredBits = 0;
		requiredBytes = 0;
	}

	public boolean hide() throws IOException
	{
		outImage = ImageIO.read(hideFile);
		iStream = new FileInputStream(hiddenFile);
	
		// Calculate the number of bits available so we can see if
		// our PNG is large enough to store our hidden data.
		availableBits = outImage.getWidth()*outImage.getHeight()*3;
		requiredBits = calcRequiredBits(hiddenFile); // Get the required size to store 
		requiredBytes = (int)requiredBits/Byte.SIZE;
		System.out.println("Required Bits:  " + requiredBits);
		System.out.println("Available Bits: " + availableBits);
		
		if(availableBits < requiredBits)
		{
			System.out.println("Your PNG is not big enough to hold that amount of data.");
			return false;
		}
		else
		{
			System.out.println("It looks like your file will fit just fine.");
		}
		
		byte[] fileBytes = new byte[requiredBytes];
		int endOfData = iStream.read(fileBytes);
		for(int i = 0; i < BREAK.getBytes().length; i++)
		{
			fileBytes[endOfData+i] = BREAK.getBytes()[i];
		}
		boolean[] outputBits = createBitArray(fileBytes);
		
		int x = 0, y = 0; // To track which pixel is being modified in the current frame.
		int currColor = 0; // 0 = R, 1 = G, 2 = B

		for(int i = 0; i < outputBits.length; i++) // Until the full data is hidden...
		{
			if(currColor == 0)
			{
				if(outputBits[i])
				{
					outImage.setRGB(x, y, (outImage.getRGB(x,y) | 0b00000000000000010000000000000000));
				}
				else
				{
					outImage.setRGB(x, y, (outImage.getRGB(x,y) & 0b11111111111111101111111111111111));
				}
				currColor++;
			}
			else if(currColor == 1)
			{
				if(outputBits[i])
				{
					outImage.setRGB(x, y, (outImage.getRGB(x,y) | 0b00000000000000000000000100000000));
				}
				else
				{
					outImage.setRGB(x, y, (outImage.getRGB(x,y) & 0b11111111111111111111111011111111));
				}
				currColor++;
			}
			else
			{
				if(outputBits[i])
				{
					outImage.setRGB(x, y, (outImage.getRGB(x,y) | 0b00000000000000000000000000000001));

				}
				else
				{
					outImage.setRGB(x, y, (outImage.getRGB(x,y) & 0b11111111111111111111111111111110));
				}
				
				// Since this is the last value to modify on this pixel
				// Move to the next one in the frame unless it is the last
				// pixel in the frame.
				
				currColor = 0;
				x++;
				if(x == outImage.getWidth())
				{
					if(y == outImage.getHeight())
					{
						System.out.println("Reached the end of the image data. Aborting");
						return false;
					}
					else
					{
						y++;
						x = 0;
					}
				}
			}
		}
		
		String pathName = hideFile.getPath(); // We'll create a convenient output file name by modifying the original .png file's name and adding "output.png"	
		pathName = pathName.substring(0, pathName.length()-4); // Get everything in the file's path except the extension
		pathName += "_output.png"; // Add "_output.png" to distinguish between the original and the output file.
		outputFile = new File(pathName);
		
		ImageIO.write(outImage, "png", outputFile);
		System.out.println("Hidden file output to: " + outputFile.getPath());
		return true;
	}
	
	
	public boolean reveal() throws IOException
	{
		System.out.println("Reveal operation started.");
		String pathName = revealFile.getParent(); 
		pathName += "/output"; // Create a new file without an extension. It is up to the recipient to know what to change the extension to.
		File revealedFile = new File(pathName);
		
		oStream = new FileOutputStream(revealedFile);
		revealImage = ImageIO.read(revealFile);
				
		int currColor = 0; // 0 = R, 1 = B, 2 = G
		int x,y; // Pixel coordinates
		x = y = 0;
		int outByteIndex = 0;
		// The number of bytes going into the output file can't be more than the amount of space available in the image.
		byte[] outBytes = new byte[revealImage.getWidth()*revealImage.getHeight()*3];
		byte[] checkForBreak = new byte[BREAK.getBytes().length];
				
		while(outByteIndex < outBytes.length)
		{
			byte currByte = 0;
			for(int i = 0; i < 8; i++) // Control loop for loading bytes from pixel data.
			{
				if(i > 0)
				{
					currByte = (byte) (currByte<<1);
				}
				if(currColor == 0)
				{
					if((revealImage.getRGB(x, y) & 0b00000000000000010000000000000000) == 0b00000000000000010000000000000000)
					{
						currByte = (byte)(currByte | 0b00000001);
						currColor++;
					}
					else
						currColor++;
				}
				else if(currColor == 1)
				{
					if((revealImage.getRGB(x, y) & 0b00000000000000000000000100000000) == 0b00000000000000000000000100000000)
					{
						currByte = (byte) (currByte | 0b00000001);
						currColor++;
					}
					else
						currColor++;
				}
				else if(currColor == 2)
				{
					if((revealImage.getRGB(x, y) & 0b00000000000000000000000000000001) == 0b00000000000000000000000000000001)
					{
						currByte = (byte) (currByte | 0b00000001);
					}
					x++;
					currColor = 0;
					if(x == revealImage.getWidth())
					{
						y++;
						x = 0;
						if(y == revealImage.getHeight())
						{
							oStream.write(outBytes);
							oStream.close();
							System.out.println("You have reached the end of the image. No hidden data present");
							return false;
						}
					}
				}
			}
			// Create our array for checking for the end of the hidden file
			for(int i = 1; i < checkForBreak.length; i++)
			{
				checkForBreak[i-1] = checkForBreak[i];
			}
			checkForBreak[checkForBreak.length-1] = currByte;
			
			boolean hitEOF = true;
			for(int i = 0; i < BREAK.getBytes().length; i++)
			{
				if(checkForBreak[i] != BREAK.getBytes()[i])
					hitEOF = false;
			}
			if(hitEOF == true)
			{
				System.out.println("Found EOF BREAK constant successfully");
				oStream.write(outBytes, 0, outByteIndex-(BREAK.getBytes().length-1));
				oStream.close();
				return true;
			}
			
			if(outByteIndex >= outBytes.length)
			{
				System.out.println("A problem occurred. The output array grew larger than expected. Aborting");
				return false;
			}
			outBytes[outByteIndex] = currByte;
			outByteIndex++;
		}	
	oStream.write(outBytes);
	oStream.close();
	return false;
	}
	
	private long calcRequiredBits(File f)
	{
		long bits = 0;
		bits += f.length() * Byte.SIZE; 
		bits += BREAK.getBytes().length * Byte.SIZE; // Add the size of bits in our title break constant.
		return bits;
	}
	
	boolean[] createBitArray(byte[] byteArray)
	{
		boolean[] bits = new boolean[byteArray.length*8];
		for(int i = 0; i < byteArray.length; i++)
		{
			bits[i*8+7] = ( (byteArray[i] & 0b00000001) == 0b00000001 ? true:false);
			bits[i*8+6] = ( (byteArray[i] & 0b00000010) == 0b00000010 ? true:false);
			bits[i*8+5] = ( (byteArray[i] & 0b00000100) == 0b00000100 ? true:false);
			bits[i*8+4] = ( (byteArray[i] & 0b00001000) == 0b00001000 ? true:false);
			bits[i*8+3] = ( (byteArray[i] & 0b00010000) == 0b00010000 ? true:false);
			bits[i*8+2] = ( (byteArray[i] & 0b00100000) == 0b00100000 ? true:false);
			bits[i*8+1] = ( (byteArray[i] & 0b01000000) == 0b01000000 ? true:false);
			bits[i*8]   = ( (byteArray[i] & 0b10000000) == 0b10000000 ? true:false);
		}
		return bits;
	}
	/**********************************************************
	 * Setters and getters for class variables that need them *
	 **********************************************************/
	
	public File getHideFile() 
	{
		return hideFile;
	}

	public void setHideFile(File hideFile) 
	{
		this.hideFile = hideFile;
	}

	public File getHiddenFile() 
	{
		return hiddenFile;
	}

	public void setHiddenFile(File hiddenFile) 
	{
		this.hiddenFile = hiddenFile;
	}

	public File getRevealFile() 
	{
		return revealFile;
	}

	public void setRevealFile(File revealFile) 
	{
		this.revealFile = revealFile;
	}
}
