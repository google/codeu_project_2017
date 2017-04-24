package codeu.chat.client.simplegui;

import codeu.chat.client.ClientContext;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by jeaniewu on 2017-03-26.
 */
@SuppressWarnings("serial")
public class JStylePanel extends JPanel {

    public JStylePanel(LayoutManager layout){
        super(layout);
    }

    protected void stylePanel(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(new Color(0x59, 0xC3, 0xC3));
    }

    protected void styleButton(JButton button){
        button.setForeground(Color.white);
        button.setFont(new Font("Tahoma", Font.BOLD, 12));
        button.setOpaque(false);
        Border line = new LineBorder(Color.white);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        button.setBorder(compound);
    }

    protected void styleLabel(JLabel label){
        label.setForeground(Color.white);
        label.setFont(new Font("Tahoma", Font.PLAIN, 18));
    }

}
