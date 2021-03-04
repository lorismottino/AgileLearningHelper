package mottinol.alh;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import mottinol.alh.gui.Gui;


/**
 * Agile Learning Helper
 * 
 * @version 1.1
 * @since 1.9
 * @author Loris Mottino
 *
 */
public class Main {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(() -> new Gui());
	}
	
}
