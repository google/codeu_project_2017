package codeU;

import javax.swing.JFrame;
import javax.swing.JComponent;

public class GUI implements Runnable{

    public void run() {
        // Invoked on the event dispatching thread.
        // Construct and show GUI.
	    JFrame frame = new JFrame("Chat Sign In");
	    JFrame.setDefaultLookAndFeelDecorated(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(500, 500);
	    frame.setVisible(true);
    }
		
}
