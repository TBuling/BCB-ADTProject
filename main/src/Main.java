import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {

        String name = JOptionPane.showInputDialog(null, "Enter your name:");
        String ageInput = JOptionPane.showInputDialog(null, "Enter your age:");

        int age = Integer.parseInt(ageInput);

        int nextYearAge = age + 1;

        JOptionPane.showMessageDialog(
                null,
                "Hello " + name + "!\n" +
                        "You are " + age + " years old.\n" +
                        "Next year, you will be " + nextYearAge + ".",
                "User Information",
                JOptionPane.INFORMATION_MESSAGE
        );

        System.exit(0);
    }
}
