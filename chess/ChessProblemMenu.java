package chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessProblemMenu extends JFrame {

    public ChessProblemMenu() {
        setTitle("Menú de Problemas - Ajedrez");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Problemas de Ajedrez", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));

        // Botones para cada problema
        JButton problem1 = createStyledButton("Problema 1");
        JButton problem2 = createStyledButton("Problema 2");
        JButton problem3 = createStyledButton("Problema 3");
        JButton problem4 = createStyledButton("Problema 4");
        JButton problem5 = createStyledButton("Problema 5");
        JButton backButton = createStyledButton("Volver al Menú Principal");

        problem1.addActionListener(e -> {
            new ChessProblemGUI(1);
            dispose();
        });
        problem2.addActionListener(e -> {
            new ChessProblemGUI(2);
            dispose();
        });
        problem3.addActionListener(e -> {
            new ChessProblemGUI(3);
            dispose();
        });
        problem4.addActionListener(e -> {
            new ChessProblemGUI(4);
            dispose();
        });
        problem5.addActionListener(e -> {
            new ChessProblemGUI(5);
            dispose();
        });
        backButton.addActionListener(e -> {
            new ChessMainMenu();
            dispose();
        });

        buttonPanel.add(problem1);
        buttonPanel.add(problem2);
        buttonPanel.add(problem3);
        buttonPanel.add(problem4);
        buttonPanel.add(problem5);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBackground(new Color(220, 220, 220));
        button.setForeground(Color.BLACK);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(180, 180, 180));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(220, 220, 220));
            }
        });

        return button;
    }
}