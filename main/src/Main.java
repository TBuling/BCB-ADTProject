import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            Glass glass = new Glass(5); // 5 units
            FaucetPanel faucetPanel = new FaucetPanel(glass);

            JFrame frame = new JFrame("Faucet and Glass");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 400);
            frame.setLocationRelativeTo(null);

            // 60% FaucetPanel, 40% Controls
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setLeftComponent(faucetPanel);

            JPanel controls = new JPanel();
            controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

            JButton waterBtn = new JButton("Add Water");
            JButton milkBtn = new JButton("Add Milk");
            JButton miloBtn = new JButton("Add Milo");
            JButton removeBtn = new JButton("Remove Unit");
            JButton drinkBtn = new JButton("Drink All");

            waterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            milkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            miloBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            removeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            drinkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            controls.add(waterBtn);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            controls.add(milkBtn);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            controls.add(miloBtn);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            controls.add(removeBtn);
            controls.add(Box.createRigidArea(new Dimension(0, 10)));
            controls.add(drinkBtn);

            waterBtn.addActionListener(e -> faucetPanel.addLiquid(Glass.Liquid.WATER));
            milkBtn.addActionListener(e -> faucetPanel.addLiquid(Glass.Liquid.MILK));
            miloBtn.addActionListener(e -> faucetPanel.addLiquid(Glass.Liquid.MILO));
            removeBtn.addActionListener(e -> faucetPanel.removeUnit());
            drinkBtn.addActionListener(e -> faucetPanel.drinkGlass());

            splitPane.setRightComponent(controls);
            splitPane.setDividerLocation(0.6); // 60/40 ratio
            splitPane.setResizeWeight(0.6);
            frame.add(splitPane);

            frame.setVisible(true);
        });
    }
}
