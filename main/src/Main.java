import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Glass glass = new Glass(5); // shared glass instance
            FaucetPanel faucetPanel = new FaucetPanel(glass);

            JFrame frame = new JFrame("BCB Faucet And A Glass");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 400);
            frame.setLocationRelativeTo(null);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setLeftComponent(faucetPanel);

            // Controls panel
            JPanel controls = new JPanel();
            controls.setBorder(new EmptyBorder(20, 10, 10, 10));
            controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

            // Top label
            JLabel topLabel = new JLabel("What do you want to do?");
            topLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Buttons
            JButton waterBtn = new JButton("Add Water");
            JButton milkBtn = new JButton("Add Milk");
            JButton miloBtn = new JButton("Add Milo");
            JButton removeBtn = new JButton("Remove Some Water");
            JButton drinkBtn = new JButton("Drink Glass");
            JButton endBtn = new JButton("Exit");

            waterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            milkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            miloBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            removeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            drinkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            endBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Label rows
            JLabel leftLabel = new JLabel("Label 1 Left");
            JLabel rightLabel = new JLabel("Label 1 Right");
            JLabel SecleftLabel = new JLabel("Label 2 Left");
            JLabel SecrightLabel = new JLabel("Label 2 Right");

            JPanel firstRow = new JPanel();
            firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));
            firstRow.add(leftLabel);
            firstRow.add(Box.createHorizontalGlue());
            firstRow.add(rightLabel);

            JPanel secondRow = new JPanel();
            secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.X_AXIS));
            secondRow.add(SecleftLabel);
            secondRow.add(Box.createHorizontalGlue());
            secondRow.add(SecrightLabel);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(firstRow);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            mainPanel.add(secondRow);
            mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Add components to controls panel
            controls.add(topLabel);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            controls.add(waterBtn);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            controls.add(milkBtn);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            controls.add(miloBtn);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            controls.add(removeBtn);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            controls.add(drinkBtn);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            controls.add(endBtn);
            controls.add(Box.createRigidArea(new Dimension(0, 20)));
            controls.add(mainPanel); // add the labels panel

            // Button actions
            waterBtn.addActionListener(e -> faucetPanel.addLiquid(Glass.Liquid.WATER));
            milkBtn.addActionListener(e -> faucetPanel.addLiquid(Glass.Liquid.MILK));
            miloBtn.addActionListener(e -> faucetPanel.addLiquid(Glass.Liquid.MILO));
            removeBtn.addActionListener(e -> faucetPanel.removeUnit());
            drinkBtn.addActionListener(e -> faucetPanel.drinkGlass());
            endBtn.addActionListener(e -> System.exit(0));

            splitPane.setRightComponent(controls);
            splitPane.setDividerLocation(0.6);
            splitPane.setResizeWeight(0.6);

            frame.add(splitPane);
            frame.setVisible(true);
        });
    }
}
