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
        drawPacket(g);
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
                case MILO -> new Color(139, 69, 19); // brown
            };
            g.setColor(color);
            g.fillRect(CUP_LEFT + 1, CUP_TOP + CUP_HEIGHT - liquidHeight, CUP_WIDTH - 1, liquidHeight);
        }

        // Draw falling drop
        if (dropY >= 0) {
            g.setColor(Color.BLUE);
            g.fillOval(NOZZLE_X, dropY, 8, 8);
        }

        // If glass is full and more liquid added, show spill in sink
        if (glass.isFull() && dropY >= CUP_TOP + CUP_HEIGHT - 10) {
            g.setColor(Color.BLUE);
            g.fillRect(CUP_LEFT, SINK_TOP + 5, CUP_WIDTH, SINK_HEIGHT - 10);
        }
    }

    private void drawPacket(Graphics g) {
        if (!showPacket) return;
        int packetXOffset = lastLiquid == Glass.Liquid.MILK ? -15 : 15; // offset from faucet
        g.setColor(lastLiquid == Glass.Liquid.MILK ? Color.WHITE : new Color(139, 69, 19));
        g.fillRect(NOZZLE_X + packetXOffset, dropY, 20, 20);
        g.setColor(Color.BLACK);
        g.drawString(lastLiquid == Glass.Liquid.MILK ? "Milk" : "Milo", NOZZLE_X + packetXOffset, dropY + 15);
    }

    private void drawSpoon(Graphics g) {
        if (!showSpoon) return;
        g.setColor(Color.GRAY);
        g.fillRect(CUP_LEFT + CUP_WIDTH / 2 - 3, CUP_TOP + CUP_HEIGHT - 40, 6, 40); // handle
        g.fillOval(CUP_LEFT + CUP_WIDTH / 2 - 10, CUP_TOP + CUP_HEIGHT - 50, 20, 10); // spoon head
    }

    // ===== ANIMATIONS =====
    public void addLiquid(Glass.Liquid liquid) {
        new Thread(() -> {
            try {
                dropY = NOZZLE_Y;
                lastLiquid = liquid;
                showPacket = liquid != Glass.Liquid.WATER;
                showSpoon = false;
                isSplash = false;

                while (dropY < CUP_TOP + CUP_HEIGHT - 10) {
                    repaint();
                    dropY += 5;
                    Thread.sleep(50);
                }

                // Check if glass is full
                if (!glass.isFull()) {
                    glass.addUnit(liquid);
                    isSplash = true;
                } else {
                    // overflow
                    isSplash = true;
                }

                repaint();
                Thread.sleep(200);

                if (showPacket) {
                    showSpoon = true;
                    for (int i = 0; i < 6; i++) {
                        repaint();
                        Thread.sleep(200);
                    }
                }

                // Reset visuals
                dropY = -1;
                showPacket = false;
                showSpoon = false;
                isSplash = false;
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
