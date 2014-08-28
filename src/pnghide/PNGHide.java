package pnghide;
/* PNGHide - Joe LeBeau
 * CS 492 - Computer Security - April 2013
 * PNGHide hides a file in a PNG file or recovers data from a
 * file where the data has been hidden.
 */
public class PNGHide 
{
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		PHSteg steg = new PHSteg();
		PHGUI gui = new PHGUI(steg);
		gui.setVisible(true);
	}

}
