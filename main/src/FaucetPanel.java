import javax.swing.*;
import java.awt.*;

public class FaucetPanel extends JPanel {

    private Glass glass;
    private int dropY = -1;
    private boolean isSplash = false;
    private boolean showPacket = false;
    private boolean showSpoon = false;
    private Glass.Liquid lastLiquid = Glass.Liquid.WATER;

    // Graphics constants
    private final int CUP_LEFT = 150;
    private final int CUP_TOP = 180;
    private final int CUP_WIDTH = 80;
    private final int CUP_HEIGHT = 100;
    private final int NOZZLE_X = 190;
    private final int NOZZLE_Y = 50;

    // Sink constants
    private final int SINK_TOP = CUP_TOP + CUP_HEIGHT + 10;
    private final int SINK_HEIGHT = 40;

    public FaucetPanel(Glass glass) {
        this.glass = glass;
        setPreferredSize(new Dimension(400, 350));
        setBackground(Color.LIGHT_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawFaucet(g);
        drawGlass(g);
        drawSink(g);
        drawLiquid(g);
        drawLevelLabels(g);
        drawSpoon(g);
    }

    private void drawFaucet(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(NOZZLE_X - 10, NOZZLE_Y, 40, 20);
        g.setColor(Color.GRAY);
        g.fillRect(NOZZLE_X - 5, NOZZLE_Y + 20, 10, 20);
    }

    private void drawGlass(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(CUP_LEFT, CUP_TOP, CUP_WIDTH, CUP_HEIGHT);
    }

    private void drawSink(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(CUP_LEFT - 20, SINK_TOP, CUP_WIDTH + 40, SINK_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawRect(CUP_LEFT - 20, SINK_TOP, CUP_WIDTH + 40, SINK_HEIGHT);
    }

    private void drawLiquid(Graphics g) {
        if (!glass.isEmpty()) {
            int unitHeight = CUP_HEIGHT / glass.getCapacity();
            int liquidHeight = glass.getLevel() * unitHeight;

            Color color = switch (glass.getCurrentLiquid()) {
                case WATER -> Color.CYAN;
                case MILK -> Color.WHITE;
                case MILO -> new Color(94, 43, 5);
            };

            g.setColor(color);
            g.fillRect(
                    CUP_LEFT + 1,
                    CUP_TOP + CUP_HEIGHT - liquidHeight,
                    CUP_WIDTH - 1,
                    liquidHeight
            );
        }

        // Falling drop (color depends on liquid)
        if (dropY >= 0) {
            Color dropColor = switch (lastLiquid) {
                case WATER -> Color.BLUE;
                case MILK -> Color.WHITE;
                case MILO -> new Color(94, 43, 5);
            };
            g.setColor(dropColor);
            g.fillOval(NOZZLE_X, dropY, 8, 8);
        }

        // Overflow spill
        if (glass.isFull() && dropY >= CUP_TOP + CUP_HEIGHT - 10) {
            g.setColor(Color.BLUE);
            g.fillRect(CUP_LEFT, SINK_TOP + 5, CUP_WIDTH, SINK_HEIGHT - 10);
        }
    }

    private void drawLevelLabels(Graphics g) {
        g.setColor(Color.BLACK);
        int unitHeight = CUP_HEIGHT / glass.getCapacity();

        for (int i = 1; i <= glass.getCapacity(); i++) {
            int y = CUP_TOP + CUP_HEIGHT - (i * unitHeight);
            g.drawLine(CUP_LEFT + CUP_WIDTH + 5, y, CUP_LEFT + CUP_WIDTH + 15, y);
            g.drawString((i * 100) + " mL", CUP_LEFT + CUP_WIDTH + 20, y + 5);
        }
    }

    private void drawSpoon(Graphics g) {
        if (!showSpoon) return;

        g.setColor(Color.GRAY);
        g.fillRect(
                CUP_LEFT + CUP_WIDTH / 2 - 3,
                CUP_TOP + CUP_HEIGHT - 40,
                6,
                40
        );
        g.fillOval(
                CUP_LEFT + CUP_WIDTH / 2 - 10,
                CUP_TOP + CUP_HEIGHT - 50,
                20,
                10
        );
    }

    // ===== ANIMATIONS =====
    public void addLiquid(Glass.Liquid liquid) {
        if (glass.isFull()){
            JOptionPane.showMessageDialog(
                    this,
                    "The glass is already full.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }   // 🚫 stop if full

        new Thread(() -> {
            try {
                lastLiquid = liquid;
                showSpoon = false;
                isSplash = false;

                dropY = NOZZLE_Y;
                int impactY = CUP_TOP + CUP_HEIGHT - 10;

                while (dropY < impactY) {
                    repaint();
                    dropY += 5;
                    Thread.sleep(50);
                }

                dropY = impactY;
                repaint();

                if (!glass.isFull()) {
                    glass.addUnit(liquid);
                }

                Thread.sleep(200);

                dropY = -1;
                showSpoon = false;
                repaint();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void drinkGlass() {
        if (!glass.isEmpty()) {
            glass.drinkAll();
            repaint();
        }
    }

    public void removeUnit() {
        if (!glass.isEmpty()) {
            glass.removeUnit();
            repaint();
        }
    }
}
