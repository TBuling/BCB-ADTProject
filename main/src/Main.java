import javax.swing.*;
import java.awt.*;

public class Main {

    private static int waterLevel = 0; // in ml
    private static final int MAX_CAPACITY = 500;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // ===== FRAME =====
            JFrame frame = new JFrame("Water Bottle");
            frame.setSize(600, 350);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout(10, 10));

            // ===== IMAGE / STATUS PANEL (LEFT) =====
            JPanel imagePanel = new JPanel(new BorderLayout());
            imagePanel.setPreferredSize(new Dimension(220, 300));
            imagePanel.setBorder(BorderFactory.createTitledBorder("Bottle"));
            imagePanel.setBackground(Color.LIGHT_GRAY);

            JLabel imageLabel = new JLabel("Bottle Image", SwingConstants.CENTER);
            imageLabel.setForeground(Color.DARK_GRAY);

            JLabel waterStatus = new JLabel("Water: 0 ml", SwingConstants.CENTER);
            waterStatus.setFont(new Font("Arial", Font.BOLD, 14));

            imagePanel.add(imageLabel, BorderLayout.CENTER);
            imagePanel.add(waterStatus, BorderLayout.SOUTH);

            // ===== OPTIONS PANEL (RIGHT) =====
            JPanel optionPanel = new JPanel();
            optionPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
            optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));

            JButton addWaterBtn = new JButton("Add Water (+50 ml)");
            JButton drinkBtn = new JButton("Drink");

            addWaterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            drinkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            // ===== BUTTON ACTIONS =====
            addWaterBtn.addActionListener(e -> {
                if (waterLevel + 50 <= MAX_CAPACITY) {
                    waterLevel += 50;
                    waterStatus.setText("Water: " + waterLevel + " ml");
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Bottle is full!",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            });

            drinkBtn.addActionListener(e -> {
                waterLevel = 0;
                waterStatus.setText("Water: 0 ml");
                JOptionPane.showMessageDialog(frame,
                        "You drank all the water 💧",
                        "Drink",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            // ===== ADD COMPONENTS =====
            optionPanel.add(addWaterBtn);
            optionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            optionPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            optionPanel.add(drinkBtn);

            frame.add(imagePanel, BorderLayout.WEST);
            frame.add(optionPanel, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}
