package ref;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class WaterTankGraphics {
    public static void main(String[] args) {
        String[] menu = new String[]{"Add Water", "Remove Water", "Clear tank", "Setup Tank", "End"};
        String choice = "";
        String color = "Dirty Black";
        String brand = "";
        float amount = 0.0F;
        float capacity = 0.0F;
        new JOptionPane();
        WaterTank tank = new WaterTank("Sadie Sink", 5000.0F);
        tank.setColor(color);
        String display = "";
        String hold = "";
        String validData = "";

        do {
            display = tank.drawTank("~");
            hold = tank.viewWTInfo() + "\nSelect:";
            choice = JOptionPane.showInputDialog((Component)null, new JTextArea(display + "\n\n" + hold), "Menu", 1, (Icon)null, menu, menu[0]).toString();
            switch (choice.hashCode()) {
                case -1290125075:
                    if (choice.equals("Setup Tank")) {
                        brand = JOptionPane.showInputDialog("Brand: ");
                        color = JOptionPane.showInputDialog("Color: ");
                        capacity = Float.parseFloat(JOptionPane.showInputDialog("Change Capacity:"));
                        tank.setBrand(brand);
                        tank.setColor(color);
                        tank.setCapacity(capacity);
                        JOptionPane.showMessageDialog((Component)null, "Tank has been updated.");
                    }
                    break;
                case 69819:
                    if (!choice.equals("End")) {
                    }
                    break;
                case 517457048:
                    if (!choice.equals("Add Water")) {
                        break;
                    }

                    for(validData = JOptionPane.showInputDialog("Amount(L): "); isValid(validData); validData = JOptionPane.showInputDialog("Invalid input! Try again.")) {
                    }

                    if (tank.isFull()) {
                        JOptionPane.showMessageDialog((Component)null, "Tank is full", "Error", 0);
                    } else {
                        for(amount = Float.parseFloat(validData); amount <= 0.0F || (double)amount > tank.getCapacity(); amount = Float.parseFloat(JOptionPane.showInputDialog("Invalid! Type amount in (L) again:"))) {
                        }

                        if ((double)(tank.currentWater() + amount) > tank.getCapacity()) {
                            String str = "Remaining capacity is \t: " + tank.remainingCapacity() + "L" + "\nWater to Add\t\t: " + amount + "L" + "\nPossible wastage\t: " + (amount - tank.remainingCapacity()) + "L" + "\nDo you wish to proceed?";
                            int i = JOptionPane.showConfirmDialog((Component)null, new JTextArea(str), "Proceed?", 0);
                            if (i == 0) {
                                amount -= tank.remainingCapacity();
                                tank.addWater(tank.remainingCapacity());
                                JOptionPane.showMessageDialog((Component)null, amount + "L is added to tank. Wastage\t: " + amount + "L");
                            } else {
                                JOptionPane.showMessageDialog((Component)null, amount + "L is NOT added to the tank! ");
                            }
                        } else {
                            tank.addWater(amount);
                            JOptionPane.showMessageDialog((Component)null, amount + "L is added to tank.");
                        }
                    }
                    break;
                case 1202467581:
                    if (choice.equals("Clear tank")) {
                        if (tank.isEmpty()) {
                            JOptionPane.showMessageDialog((Component)null, "Tank is empty. Nothing to remove!");
                        } else {
                            JOptionPane.showMessageDialog((Component)null, "Tank is now empty. Wastage\t: " + tank.clearWater());
                        }
                    }
                    break;
                case 2118500987:
                    if (choice.equals("Remove Water")) {
                        for(validData = JOptionPane.showInputDialog("Amount(L): "); isValid(validData); validData = JOptionPane.showInputDialog("Invalid input! Try again.")) {
                        }

                        if (!tank.isEmpty()) {
                            for(amount = Float.parseFloat(validData); amount <= 0.0F || (double)amount > tank.getCapacity(); amount = Float.parseFloat(JOptionPane.showInputDialog("Invalid! Type amount in (L) again:"))) {
                            }

                            if (amount > tank.currentWater()) {
                                String str = "Water Level is \t\t: " + tank.currentWater() + "L" + "\nWater to Remove\t: " + amount + "L" + "\nDo you wish to proceed? It will empty your tank!";
                                int i = JOptionPane.showConfirmDialog((Component)null, new JTextArea(str), "Proceed?", 0);
                                if (i == 0) {
                                    JOptionPane.showMessageDialog((Component)null, tank.currentWater() + "L is removed from the tank.");
                                    tank.removeWater(tank.currentWater());
                                } else {
                                    JOptionPane.showMessageDialog((Component)null, amount + "L NOT is removed successfully! ");
                                }
                            } else {
                                tank.removeWater(amount);
                                JOptionPane.showMessageDialog((Component)null, amount + "L is removed to tank.");
                            }
                        } else {
                            JOptionPane.showMessageDialog((Component)null, "Tank is empty!", "Error", 0);
                        }
                    }
            }
        } while(!choice.equals("End"));

    }

    public static boolean isValid(String data) {
        boolean valid = false;
        if (!data.isBlank() && !data.isEmpty() && !data.isBlank()) {
            for(int i = 0; i < data.length(); ++i) {
                if (!Character.isDigit(data.charAt(i))) {
                    valid = true;
                    break;
                }
            }
        } else {
            valid = true;
        }

        return valid;
    }
}
