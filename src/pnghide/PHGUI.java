package pnghide;
/* PNGHide - Joe LeBeau
 * CS 492 - Computer Security - April 2013
 * GHGUI.java is used to build the GUI for PNGHide and receive
 * user input. Most of the GUI was built by WindowBuilder in Eclipse.
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.SpringLayout;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class PHGUI extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final JFileChooser fileChooser;
	private JPanel contentPane;
	private JTextField PNGPath;
	private JTextField dataPath;
	private JTextField revealPath;
	private JButton selectPNGButton;
	private JButton selectDataButton;
	private JButton selectRevealButton;
	private JButton hideButton;
	private JButton revealButton;
	private PHSteg steg;

	/**
	 * Create the frame.
	 */
	public PHGUI(PHSteg steg)
	{
		this.steg = steg;
		setResizable(false);

		setTitle("PNGHide");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 651, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		fileChooser = new JFileChooser();
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);
		
		JPanel hidePane = new JPanel();
		tabbedPane.addTab("Hide", null, hidePane, null);
		SpringLayout sl_hidePane = new SpringLayout();
		hidePane.setLayout(sl_hidePane);
		
		selectPNGButton = new JButton("Select...");
		selectPNGButton.addActionListener(this);
		sl_hidePane.putConstraint(SpringLayout.NORTH, selectPNGButton, 38, SpringLayout.NORTH, hidePane);
		sl_hidePane.putConstraint(SpringLayout.WEST, selectPNGButton, 10, SpringLayout.WEST, hidePane);
		hidePane.add(selectPNGButton);
		
		JLabel selectPNGLabel = new JLabel("Select the PNG to hide in");
		sl_hidePane.putConstraint(SpringLayout.NORTH, selectPNGLabel, 10, SpringLayout.NORTH, hidePane);
		sl_hidePane.putConstraint(SpringLayout.WEST, selectPNGLabel, 10, SpringLayout.WEST, hidePane);
		hidePane.add(selectPNGLabel);
		
		JLabel selectDataLabel = new JLabel("Select the file to hide");
		sl_hidePane.putConstraint(SpringLayout.NORTH, selectDataLabel, 61, SpringLayout.SOUTH, selectPNGButton);
		sl_hidePane.putConstraint(SpringLayout.WEST, selectDataLabel, 10, SpringLayout.WEST, hidePane);
		hidePane.add(selectDataLabel);
		
		selectDataButton = new JButton("Select...");
		selectDataButton.addActionListener(this);
		sl_hidePane.putConstraint(SpringLayout.NORTH, selectDataButton, 6, SpringLayout.SOUTH, selectDataLabel);
		sl_hidePane.putConstraint(SpringLayout.WEST, selectDataButton, 10, SpringLayout.WEST, hidePane);
		hidePane.add(selectDataButton);
		
		PNGPath = new JTextField();
		PNGPath.setEditable(false);
		sl_hidePane.putConstraint(SpringLayout.NORTH, PNGPath, 19, SpringLayout.SOUTH, selectPNGLabel);
		sl_hidePane.putConstraint(SpringLayout.WEST, PNGPath, 6, SpringLayout.EAST, selectPNGButton);
		hidePane.add(PNGPath);
		PNGPath.setColumns(10);
		
		dataPath = new JTextField();
		sl_hidePane.putConstraint(SpringLayout.EAST, PNGPath, 0, SpringLayout.EAST, dataPath);
		sl_hidePane.putConstraint(SpringLayout.EAST, dataPath, 474, SpringLayout.EAST, selectDataButton);
		dataPath.setEditable(false);
		sl_hidePane.putConstraint(SpringLayout.NORTH, dataPath, 12, SpringLayout.SOUTH, selectDataLabel);
		sl_hidePane.putConstraint(SpringLayout.WEST, dataPath, 6, SpringLayout.EAST, selectDataButton);
		hidePane.add(dataPath);
		dataPath.setColumns(10);
		
		hideButton = new JButton("Hide");
		sl_hidePane.putConstraint(SpringLayout.NORTH, hideButton, 49, SpringLayout.SOUTH, dataPath);
		hideButton.addActionListener(this);
		sl_hidePane.putConstraint(SpringLayout.WEST, hideButton, 107, SpringLayout.WEST, hidePane);
		sl_hidePane.putConstraint(SpringLayout.EAST, hideButton, 232, SpringLayout.WEST, hidePane);
		hidePane.add(hideButton);
		
		JPanel revealPane = new JPanel();
		tabbedPane.addTab("Reveal", null, revealPane, null);
		SpringLayout sl_revealPane = new SpringLayout();
		revealPane.setLayout(sl_revealPane);
		
		selectRevealButton = new JButton("Select...");
		selectRevealButton.addActionListener(this);
		sl_revealPane.putConstraint(SpringLayout.NORTH, selectRevealButton, 33, SpringLayout.NORTH, revealPane);
		sl_revealPane.putConstraint(SpringLayout.WEST, selectRevealButton, 10, SpringLayout.WEST, revealPane);
		revealPane.add(selectRevealButton);
		
		JLabel selectHiddenPath = new JLabel("Select the PNG to look in");
		sl_revealPane.putConstraint(SpringLayout.WEST, selectHiddenPath, 0, SpringLayout.WEST, selectRevealButton);
		sl_revealPane.putConstraint(SpringLayout.SOUTH, selectHiddenPath, -8, SpringLayout.NORTH, selectRevealButton);
		revealPane.add(selectHiddenPath);
		
		revealPath = new JTextField();
		revealPath.setEditable(false);
		sl_revealPane.putConstraint(SpringLayout.WEST, revealPath, 6, SpringLayout.EAST, selectRevealButton);
		sl_revealPane.putConstraint(SpringLayout.SOUTH, revealPath, 0, SpringLayout.SOUTH, selectRevealButton);
		sl_revealPane.putConstraint(SpringLayout.EAST, revealPath, -43, SpringLayout.EAST, revealPane);
		revealPath.setColumns(10);
		revealPane.add(revealPath);
		
		revealButton = new JButton("Reveal Data");
		revealButton.addActionListener(this);
		sl_revealPane.putConstraint(SpringLayout.WEST, revealButton, 109, SpringLayout.WEST, revealPane);
		sl_revealPane.putConstraint(SpringLayout.SOUTH, revealButton, -84, SpringLayout.SOUTH, revealPane);
		sl_revealPane.putConstraint(SpringLayout.EAST, revealButton, -110, SpringLayout.EAST, revealPane);
		revealPane.add(revealButton);
	}


	@Override
	public void actionPerformed(ActionEvent e) 
	{
		/* FileFilter code is mostly from
		 * http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
		 * 
		 * Prepares a FileFilter for selecting only PNG files in the
		 * cases where we need such a filter.
		 */
		
		FileFilter filter = new FileFilter()
		{
			public boolean accept(File f) 
			{
				if (f.isDirectory()) {
			        return true;
			    }

				String ext = null;
		        String s = f.getName();
		        int i = s.lastIndexOf('.');
		        if (i > 0 &&  i < s.length() - 1) 
		        {
		            ext = s.substring(i+1).toLowerCase();
		        }
			    if (ext != null) {
			        if (ext.equals("png"))
			        {
			        	return true;
			        } 
			        else 
			        {
			            return false;
			        }
			    }
			    else
			    	return false;
			}
			@Override
			public String getDescription()
			{
				return "PNG Files";
			}
		};
		
		
		if(e.getSource() == selectPNGButton)
		{
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				steg.setHideFile(fileChooser.getSelectedFile());
				PNGPath.setText(fileChooser.getSelectedFile().getPath());
			}
		}
		else if(e.getSource() == selectDataButton)
		{
			fileChooser.setFileFilter(null);
			int returnVal = fileChooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				steg.setHiddenFile(fileChooser.getSelectedFile());
				dataPath.setText(fileChooser.getSelectedFile().getPath());
			}
		}
		else if(e.getSource() == selectRevealButton)
		{
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				steg.setRevealFile(fileChooser.getSelectedFile());
				revealPath.setText(fileChooser.getSelectedFile().getPath());
			}
		}
		else if(e.getSource() == hideButton)
		{
			try
			{
				steg.hide();
			}
			catch(IOException exception)
			{
				exception.printStackTrace();
			}
		}
		else if(e.getSource() == revealButton)
		{
			try
			{
				steg.reveal();
			}
			catch(IOException exception)
			{
				exception.printStackTrace();
			}
		}
	}
}
