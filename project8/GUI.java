package codeU;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.*;

public class GUI {

    void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Chat Sign In");
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
	
}
