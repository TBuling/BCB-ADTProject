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

            // ---------------- Controls Panel ----------------
            JPanel controls = new JPanel();
            controls.setBorder(new EmptyBorder(20, 10, 10, 10));
            controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

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

            // ---------------- Status Labels ----------------
            JLabel levelLabel = new JLabel("0 / " + glass.getCapacity());
            JLabel typeLabel = new JLabel("Liquid: Water");
            JLabel fullLabel = new JLabel("Glass is not full");
            JLabel emptyLabel = new JLabel("Glass is not empty");
            JLabel stirLabel = new JLabel("Stirring: No");

            // First row: Level and Liquid type
            JPanel firstRow = new JPanel();
            firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));
            firstRow.add(new JLabel("Level:"));
            firstRow.add(Box.createRigidArea(new Dimension(5,0)));
            firstRow.add(levelLabel);
            firstRow.add(Box.createHorizontalGlue());
            firstRow.add(new JLabel("Liquid:"));
            firstRow.add(Box.createRigidArea(new Dimension(5,0)));
            firstRow.add(typeLabel);

            // Second row: Full / Empty
            JPanel secondRow = new JPanel();
            secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.X_AXIS));
            secondRow.add(fullLabel);
            secondRow.add(Box.createHorizontalGlue());
            secondRow.add(emptyLabel);

            // Third row: Stirring status
            JPanel thirdRow = new JPanel();
            thirdRow.setLayout(new BoxLayout(thirdRow, BoxLayout.X_AXIS));
            thirdRow.add(stirLabel);

            // Combine rows into mainPanel
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(firstRow);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            mainPanel.add(secondRow);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            mainPanel.add(thirdRow);
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

            // ---------------- Button Actions ----------------
            waterBtn.addActionListener(e -> faucetPanel.addLiquid(Glass.Liquid.WATER));
            milkBtn.addActionListener(e -> faucetPanel.addLiquid(Glass.Liquid.MILK));
            miloBtn.addActionListener(e -> faucetPanel.addLiquid(Glass.Liquid.MILO));
            removeBtn.addActionListener(e -> faucetPanel.removeUnit());
            drinkBtn.addActionListener(e -> faucetPanel.drinkGlass());
            endBtn.addActionListener(e -> System.exit(0));

            // ---------------- Label Updater ----------------
            Timer labelTimer = new Timer(100, e -> {
                levelLabel.setText(glass.getLevel() + " / " + glass.getCapacity());
                typeLabel.setText("" + (glass.getCurrentLiquid() == null ? "None" : glass.getCurrentLiquid().name()));
                fullLabel.setText(glass.isFull() ? "Glass is full" : "Glass is not full");
                emptyLabel.setText(glass.isEmpty() ? "Glass is empty" : "Glass is not empty");
                stirLabel.setText(faucetPanel.isStirring() ? "Yes" : "No");
            });
            labelTimer.start();

            // ---------------- Final SplitPane ----------------
            splitPane.setRightComponent(controls);
            splitPane.setDividerLocation(0.6);
            splitPane.setResizeWeight(0.6);

            frame.add(splitPane);
            frame.setVisible(true);
        });
    }
}
