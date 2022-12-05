package org.digger.classic;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.*;

public class Launcher {

    public static void main(String[] args) {
        Digger game = new Digger();
        game.setFocusable(true);
        game.init();
        game.start();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Digger Remastered");
//		frame.setSize((int) (game.width * 4.03), (int) (game.height * 4.17));
        frame.setSize(
                (int) (game.width * 2 + Digger.PADDING * 4),
                (int) (game.height * 2 + Digger.PADDING * 5));
//		frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon(Resources.findResource("/icons/digger.png"));
        frame.setIconImage(icon.getImage());

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(game, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}