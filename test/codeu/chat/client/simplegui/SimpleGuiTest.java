//Backend Test of MenuBar method

package codeu.chat.client.simplegui;

import static org.junit.Assert.*;

import org.junit.Test;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.JFrame; 

public final class SimpleGuiTest {
  
  @Test
  public void testSimpleGui() {
	JMenuBar test = ChatSimpleGui.menuBar();
	JMenuBar another = ChatSimpleGui.menuBar(); 
	 
	assertNotNull(test); 
  }

}