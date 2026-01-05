import javax.swing.*;
import java.awt.*;

public class FaucetPanel extends JPanel {

    private Glass glass;
    private int dropY = -1;
    private boolean isSplash = false;
    private boolean showPacket = false;
    private boolean showSpoon = false;
    private Glass.Liquid lastLiquid = Glass.Liquid.WATER;

    private final int CUP_LEFT = 150;
    private final int CUP_TOP = 180;
    private final int CUP_WIDTH = 80;
    private final int CUP_HEIGHT = 100;
    private final int NOZZLE_X = 190;
    private final int NOZZLE_Y = 50;

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

    private void drawLiquid(Graphics g) {
        if (glass.getLevel() > 0) {
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

        // draw falling drop
        if (dropY >= 0) {
            g.setColor(Color.BLUE);
            g.fillOval(NOZZLE_X, dropY, 8, 8);
        }
    }

    private void drawPacket(Graphics g) {
        if (!showPacket) return;
        g.setColor(lastLiquid == Glass.Liquid.MILK ? Color.WHITE : new Color(139, 69, 19));
        g.fillRect(NOZZLE_X - 5, dropY, 20, 20);
        g.setColor(Color.BLACK);
        g.drawString(lastLiquid == Glass.Liquid.MILK ? "Milk" : "Milo", NOZZLE_X, dropY + 15);
    }

    private void drawSpoon(Graphics g) {
        if (!showSpoon) return;
        g.setColor(Color.GRAY);
        g.fillRect(CUP_LEFT + CUP_WIDTH / 2 - 3, CUP_TOP + CUP_HEIGHT - 40, 6, 40); // handle
        g.fillOval(CUP_LEFT + CUP_WIDTH / 2 - 10, CUP_TOP + CUP_HEIGHT - 50, 20, 10); // spoon head
    }

    // ===== ANIMATIONS =====
    public void addLiquid(Glass.Liquid liquid) {
        lastLiquid = liquid;
        new Thread(() -> {
            try {
                dropY = NOZZLE_Y;
                isSplash = false;
                showPacket = liquid != Glass.Liquid.WATER;
                while (dropY < CUP_TOP + CUP_HEIGHT - 10) {
                    repaint();
                    dropY += 5;
                    Thread.sleep(50);
                }
                isSplash = true;
                repaint();
                Thread.sleep(200);

                // show spoon stirring
                if (showPacket) {
                    showSpoon = true;
                    for (int i = 0; i < 6; i++) {
                        repaint();
                        Thread.sleep(200);
                    }
                }

                glass.addUnit(liquid);

                // reset visuals
                dropY = -1;
                isSplash = false;
                showPacket = false;
                showSpoon = false;
                repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void drinkGlass() {
        glass.drinkAll();
        repaint();
    }

    public void removeUnit() {
        glass.removeUnit();
        repaint();
    }
}
