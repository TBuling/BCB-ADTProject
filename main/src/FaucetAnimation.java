import java.util.Scanner;

public class FaucetAnimation {

    // Canvas dimensions
    static final int WIDTH = 30;
    static final int HEIGHT = 15;

    // Physics/State variables
    static int waterLevel = 0; // How full the cup is (0 to 5)
    static final int CUP_BOTTOM_Y = 13;
    static final int DROP_COL = 14;
    static final int NOZZLE_Y = 4; // Start position of drop

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        // Initial draw of the empty state
        renderFrame(-1, false);

        while (true) {
            System.out.println("Water Level: " + waterLevel + "/5");
            System.out.println("Command (add/delete/exit): ");
            System.out.print("> ");

            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "add":
                    if (waterLevel < 5) {
                        // animation sequence
                        animateDrop();
                    } else {
                        System.out.println(">>> The cup is already full!");
                        Thread.sleep(1000);
                    }
                    break;

                case "delete":
                case "remove":
                    if (waterLevel > 0) {
                        waterLevel--;
                    } else {
                        System.out.println(">>> The cup is already empty!");
                        Thread.sleep(1000);
                    }
                    break;

                case "exit":
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid input.");
                    Thread.sleep(800);
                    break;
            }

            // Clear screen and show the static state (updated water level)
            // passing -1 as dropY means "no drop currently falling"
            renderFrame(-1, false);
        }
    }

    /**
     * Handles the animation loop for a single drop falling
     */
    private static void animateDrop() throws InterruptedException {
        int currentDropY = NOZZLE_Y;
        boolean splashing = false;

        while (true) {
            int surfaceY = CUP_BOTTOM_Y - waterLevel;

            // Check if drop hits water surface or bottom of cup
            if (currentDropY >= surfaceY) {
                splashing = true;

                // splash frame
                renderFrame(currentDropY, true);
                Thread.sleep(150);

                // Increment water level after splash finishes
                waterLevel++;
                break;
            } else {
                // Render the falling drop frame
                renderFrame(currentDropY, false);
                currentDropY++;
                Thread.sleep(100); // Speed of fall
            }
        }
    }

    private static void renderFrame(int currentDropY, boolean isSplashing) {
        // Canvas
        char[][] canvas = new char[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                canvas[i][j] = ' ';
            }
        }

        // static faucet
        drawString(canvas, 0, 0, "          =()=");
        drawString(canvas, 1, 0, "      ,/'\\_||_");
        drawString(canvas, 2, 0, "      ( (___  `.");
        drawString(canvas, 3, 0, "      `\\./  `=='");

        // static cup
        int cupLeft = 10;
        int cupRight = 20;

        for (int y = 9; y < 14; y++) {
            canvas[y][cupLeft] = '|';
            canvas[y][cupRight] = '|';
        }
        drawString(canvas, 14, cupLeft, "|_________|");

        // existing water
        if (waterLevel > 0) {
            for (int i = 0; i < waterLevel; i++) {
                int waterY = CUP_BOTTOM_Y - i;
                drawString(canvas, waterY, cupLeft + 1, "~~~~~~~~~");
            }
        }

        // Dynamic Element (Drop or Splash)
        if (currentDropY != -1) {
            if (isSplashing) {
                int surfaceY = CUP_BOTTOM_Y - waterLevel;
                drawString(canvas, surfaceY, DROP_COL - 1, "~o~");
            } else {
                if (currentDropY < HEIGHT && currentDropY >= 0) {
                    canvas[currentDropY][DROP_COL] = 'o';
                }
            }
        }

        // output
        clearScreen();
        printCanvas(canvas);
    }

    // Helper to put a string into the 2D array
    private static void drawString(char[][] canvas, int r, int c, String s) {
        for (int i = 0; i < s.length(); i++) {
            if (c + i < WIDTH && r < HEIGHT && r >= 0) {
                canvas[r][c + i] = s.charAt(i);
            }
        }
    }

    // Helper to print the buffer to console
    private static void printCanvas(char[][] canvas) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                sb.append(canvas[i][j]);
            }
            sb.append("\n");
        }
        System.out.print(sb.toString());
    }

    // clear terminal
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}