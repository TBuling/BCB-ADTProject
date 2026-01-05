import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Glass glass = new Glass(5);
            FaucetPanel faucetPanel = new FaucetPanel(glass);

            JFrame frame = new JFrame("BCB Faucet And A Glass");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 500); // bigger to fit all details
            frame.setLocationRelativeTo(null);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setLeftComponent(faucetPanel);

            // Controls panel
            JPanel controls = new JPanel();
            controls.setBorder(new EmptyBorder(20, 10, 10, 10));
            controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

            JLabel topLabel = new JLabel("Choose an action:");
            topLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton faucetBtn = new JButton("Open Faucet");
            JButton milkBtn = new JButton("Add Milk");
            JButton miloBtn = new JButton("Add Milo");
            JButton removeBtn = new JButton("Remove 1 Unit");
            JButton drinkBtn = new JButton("Drink Glass");
            JButton exitBtn = new JButton("Exit");

            JButton[] buttons = {faucetBtn, milkBtn, miloBtn, removeBtn, drinkBtn, exitBtn};
            for (JButton btn : buttons) btn.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Status labels
            JLabel levelLabel = new JLabel(glass.getLevel() + " / " + glass.getCapacity());
            JLabel typeLabel = new JLabel(glass.getCurrentLiquid() == null ? "Empty" : glass.getCurrentLiquid().name());
            JLabel stirLabel = new JLabel(faucetPanel.isStirring() ? "Yes" : "No");
            JLabel lastActionLabel = new JLabel("None");

            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setBorder(BorderFactory.createTitledBorder("Faucet and Glass Details"));

            JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            r1.add(new JLabel("Level:")); r1.add(levelLabel);
            JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            r2.add(new JLabel("Liquid:")); r2.add(typeLabel);
            JPanel r3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            r3.add(new JLabel("Stirring:")); r3.add(stirLabel);
            JPanel r4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            r4.add(new JLabel("Last Action:")); r4.add(lastActionLabel);

            detailsPanel.add(r1); detailsPanel.add(r2); detailsPanel.add(r3); detailsPanel.add(r4);

            controls.add(topLabel);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            for (JButton btn : buttons) {
                controls.add(btn);
                controls.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            controls.add(Box.createRigidArea(new Dimension(0, 15)));
            controls.add(detailsPanel);

            // Button actions
            faucetBtn.addActionListener(e -> {
                boolean nowOpen = faucetPanel.toggleFaucet();
                faucetBtn.setText(nowOpen ? "Close Faucet" : "Open Faucet");
                lastActionLabel.setText(nowOpen ? "Faucet opened" : "Faucet closed");
            });

            milkBtn.addActionListener(e -> {
                boolean ok = faucetPanel.addPowder(Glass.Liquid.MILK);
                lastActionLabel.setText(ok ? "Added Milk (will drop particles then stir)" :
                        "Cannot add Milk — check warnings");
            });

            miloBtn.addActionListener(e -> {
                boolean ok = faucetPanel.addPowder(Glass.Liquid.MILO);
                lastActionLabel.setText(ok ? "Added Milo (will drop particles then stir)" :
                        "Cannot add Milo — check warnings");
            });

            removeBtn.addActionListener(e -> {
                faucetPanel.removeUnit();
                lastActionLabel.setText("Removed 1 unit");
            });

            drinkBtn.addActionListener(e -> {
                faucetPanel.drinkGlass();
                lastActionLabel.setText("Glass emptied");
                faucetBtn.setText("Open Faucet");
            });

            exitBtn.addActionListener(e -> System.exit(0));

            // Label updater
            Timer labelTimer = new Timer(100, e -> {
                levelLabel.setText(glass.getLevel() + " / " + glass.getCapacity());
                typeLabel.setText(glass.getCurrentLiquid() == null ? "Empty" : glass.getCurrentLiquid().name());
                stirLabel.setText(faucetPanel.isStirring() ? "Yes" : "No");
                if (faucetPanel.isOverflowing()) {
                    lastActionLabel.setText("Overflowing!");
                }
            });
            labelTimer.start();

            splitPane.setRightComponent(controls);
            splitPane.setDividerLocation(0.65); // bigger faucet panel
            splitPane.setResizeWeight(0.65);
            frame.add(splitPane);
            frame.setVisible(true);
        });
    }
}
