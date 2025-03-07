package chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessMainMenu extends JFrame {

    public ChessMainMenu() {
        setTitle("Menú Principal - Ajedrez");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Ajedrez", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        // Ajusto a 5 filas en el GridLayout (porque agregamos uno nuevo)
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));

        JButton startButton = createStyledButton("Iniciar Partida (PvP)");
        JButton botButton = createStyledButton("Jugar contra Bot");
        JButton replayButton = createStyledButton("Reproducir"); // <--- NUEVO
        JButton problemsButton = createStyledButton("Problemas");
        JButton exitButton = createStyledButton("Salir");

        startButton.addActionListener(e -> {
            new ChessGUI(false); // PvP
            dispose();
        });

        botButton.addActionListener(e -> {
            new ChessGUI(true); // vs Bot
            dispose();
        });

        // NUEVO Botón "Reproducir"
        replayButton.addActionListener(e -> {
            new ChessReplayGUI();  // <--- Llamaremos a la clase ChessReplayGUI
            dispose();
        });

        problemsButton.addActionListener(e -> {
            new ChessProblemMenu();
            dispose();
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        buttonPanel.add(startButton);
        buttonPanel.add(botButton);
        buttonPanel.add(replayButton);  // <--- Lo agregamos en el layout
        buttonPanel.add(problemsButton);
        buttonPanel.add(exitButton);
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

    public static void main(String[] args) {
        new ChessMainMenu();
    }
}