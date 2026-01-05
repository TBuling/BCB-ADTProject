import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;

public class FaucetPanel extends JPanel {

    private final Glass glass;

    private final ArrayList<Drop> drops = new ArrayList<>();
    private final ArrayList<Particle> particles = new ArrayList<>();
    private Packet packet = null;

    private boolean stirring = false;
    private int stirAngle = 0;
    private int stirTicks = 0;
    private final int STIR_DURATION = 100; // ~3s at 30ms per tick
    private final int STIR_SWING = 45;
    private boolean milkMiloAdded = false;

    private Glass.Liquid visualLiquidType = Glass.Liquid.WATER;

    private final int CUP_LEFT = 150;
    private final int CUP_TOP = 180;
    private final int CUP_WIDTH = 80;
    private final int CUP_HEIGHT = 120;
    private final int SINK_TOP = 310;
    private final int SINK_HEIGHT = 50;

    private final int NOZZLE_X = CUP_LEFT + CUP_WIDTH / 2 - 4;
    private final int NOZZLE_Y = CUP_TOP - 50;

    private final Timer timer;

    public FaucetPanel(Glass glass) {
        this.glass = glass;
        setPreferredSize(new Dimension(400, 350));
        setBackground(Color.LIGHT_GRAY);

        timer = new Timer(30, e -> {
            updateDrops();
            updateParticlesAndStir();
            repaint();
        });
        timer.start();
    }

    // ---------------- Updates ----------------
    private void updateDrops() {
        int unitHeight = CUP_HEIGHT / glass.getCapacity();
        int liquidHeight = glass.getLevel() * unitHeight;

        Iterator<Drop> it = drops.iterator();
        while (it.hasNext()) {
            Drop drop = it.next();
            drop.y += drop.speed;
            int surfaceY = CUP_TOP + CUP_HEIGHT - liquidHeight - 1;
            if (drop.y >= surfaceY) {
                glass.addUnit(Glass.Liquid.WATER);
                visualLiquidType = Glass.Liquid.WATER; // <-- update color immediately
                it.remove();
            }
        }
    }

    private void updateParticlesAndStir() {
        int unitHeight = CUP_HEIGHT / glass.getCapacity();
        int liquidHeight = glass.getLevel() * unitHeight;
        int liquidSurface = CUP_TOP + CUP_HEIGHT - liquidHeight;

        if (packet != null) {
            // Generate particles gradually
            if (packet.generatedParticles < packet.totalParticles) {
                for (int i = 0; i < 2; i++) { // 2 per tick
                    int px = packet.x + 10 + (int) (Math.random() * 10 - 5);
                    int py = packet.y + 20;
                    particles.add(new Particle(px, py, packet.type, 1 + Math.random()));
                    packet.generatedParticles++;
                }
            }
        }

        // Move particles
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.y += p.speed;

            // Stop at current liquid surface
            if (p.y >= liquidSurface) {
                p.y = liquidSurface;
                it.remove();
            }
        }

        // Start stirring if all particles have landed
        if (packet != null && packet.generatedParticles >= packet.totalParticles && particles.isEmpty() && !stirring) {
            stirring = true;
            stirTicks = 0;

            // Add the milk/milo to the glass now
            glass.addUnit(packet.type);

            packet = null; // remove packet after particles released
        }

        // Stirring animation
        if (stirring) {
            stirTicks++;
            stirAngle = (int) (Math.sin((double) stirTicks / STIR_DURATION * Math.PI * 2) * STIR_SWING);
            if (stirTicks >= STIR_DURATION) {
                stirring = false;
                stirTicks = 0;

                // Update visual color to reflect the liquid in the glass
                visualLiquidType = glass.getCurrentLiquid();
            }
        }

        // Keep visual in sync when not stirring
        if (!stirring) {
            visualLiquidType = glass.getCurrentLiquid();
        }
    }

    // ---------------- Actions ----------------
    public void addLiquid(Glass.Liquid type) {
        if (type == Glass.Liquid.WATER) {
            drops.add(new Drop(NOZZLE_X, NOZZLE_Y, 2));
            visualLiquidType = Glass.Liquid.WATER;
            if (glass.isFull()) {
                JOptionPane.showMessageDialog(this, "Warning: Glass is overflowing!", "Overflow", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            if (glass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Glass is empty! Add water first.", "Cannot Add", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (milkMiloAdded) {
                JOptionPane.showMessageDialog(this, "Milk/Milo already added. Drink the glass first.", "Cannot Add", JOptionPane.WARNING_MESSAGE);
                return;
            }
            packet = new Packet(NOZZLE_X - 10, NOZZLE_Y - 20, type, 0, 50);
            milkMiloAdded = true;
        }
    }

    public void removeUnit() { if (!glass.isEmpty()) glass.removeUnit(); }

    public void drinkGlass() {
        if (!glass.isEmpty()) {
            glass.drinkAll();
            drops.clear();
            particles.clear();
            packet = null;
            stirring = false;
            milkMiloAdded = false;
            visualLiquidType = Glass.Liquid.WATER;
        }
    }

    // ---------------- Paint ----------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int unitHeight = CUP_HEIGHT / glass.getCapacity();
        int liquidHeight = glass.getLevel() * unitHeight;

        Graphics2D g2 = (Graphics2D) g;

        // Faucet
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(NOZZLE_X - 10, NOZZLE_Y, 40, 20);
        g2.setColor(Color.GRAY);
        g2.fillRect(NOZZLE_X - 5, NOZZLE_Y + 20, 10, 20);

        // Glass outline
        g2.setColor(Color.BLACK);
        g2.drawRect(CUP_LEFT, CUP_TOP, CUP_WIDTH, CUP_HEIGHT);

        // Sink
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(CUP_LEFT - 20, SINK_TOP, CUP_WIDTH + 40, SINK_HEIGHT);
        g2.setColor(Color.BLACK);
        g2.drawRect(CUP_LEFT - 20, SINK_TOP, CUP_WIDTH + 40, SINK_HEIGHT);

        // Draw liquid (color only updates after stirring)
        g2.setColor(visualLiquidType == Glass.Liquid.WATER ? Color.CYAN :
                visualLiquidType == Glass.Liquid.MILK ? Color.WHITE :
                        new Color(139, 69, 19));
        if (!glass.isEmpty())
            g2.fillRect(CUP_LEFT + 1, CUP_TOP + CUP_HEIGHT - liquidHeight, CUP_WIDTH - 1, liquidHeight);

        // Water drops
        for (Drop drop : drops) {
            g2.setColor(Color.BLUE);
            g2.fillOval(drop.x, drop.y, 8, 8);
        }

        // Packet (fixed above faucet while particles fall)
        if (packet != null) {
            g2.setColor(packet.type == Glass.Liquid.MILK ? Color.WHITE : new Color(139, 69, 19));
            g2.fillRect(packet.x, packet.y, 20, 20);
        }

        // Powder particles
        for (Particle p : particles) {
            g2.setColor(p.type == Glass.Liquid.MILK ? Color.WHITE : new Color(139, 69, 19));
            g2.fillOval(p.x, p.y, 3, 3);
        }

        // Stirring spoon
        if (stirring) {
            AffineTransform old = g2.getTransform();
            g2.setColor(Color.ORANGE);
            g2.rotate(Math.toRadians(stirAngle), CUP_LEFT + CUP_WIDTH / 2, CUP_TOP + CUP_HEIGHT / 2);
            g2.fillRect(CUP_LEFT + CUP_WIDTH / 2 - 2, CUP_TOP + CUP_HEIGHT / 2 - 30, 4, 60);
            g2.setTransform(old);
        }

        // Overflow
        if (glass.isFull()) {
            g2.setColor(Color.BLUE);
            g2.fillRect(CUP_LEFT, SINK_TOP + 5, CUP_WIDTH, SINK_HEIGHT - 10);
        }
    }

    // ---------------- Inner Classes ----------------
    private static class Drop { int x, y; double speed; Drop(int x,int y,double s){this.x=x;this.y=y;this.speed=s;} }
    private static class Particle { int x,y; double speed; Glass.Liquid type; Particle(int x,int y,Glass.Liquid t,double s){this.x=x;this.y=y;this.type=t;this.speed=s;} }
    private static class Packet { int x,y; double speed; Glass.Liquid type; int totalParticles; int generatedParticles=0; Packet(int x,int y,Glass.Liquid t,double s,int total){this.x=x;this.y=y;this.type=t;this.speed=s;this.totalParticles=total;} }
}
