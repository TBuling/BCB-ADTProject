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
    private static final int STIR_DURATION = 100;
    private static final int STIR_SWING = 45;

    private Glass.Liquid visualLiquidType = Glass.Liquid.WATER; // current visible liquid
    private boolean milkMiloAdded = false; // lock color when milk/milo added

    private static final int CUP_LEFT = 150;
    private static final int CUP_TOP = 180;
    private static final int CUP_WIDTH = 80;
    private static final int CUP_HEIGHT = 120;

    private static final int SINK_TOP = 310;
    private static final int SINK_HEIGHT = 50;

    private static final int NOZZLE_X = CUP_LEFT + CUP_WIDTH / 2 - 4;
    private static final int NOZZLE_Y = CUP_TOP - 50;

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

    // ================= UPDATE =================

    private void updateDrops() {
        int unitHeight = CUP_HEIGHT / glass.getCapacity();
        int liquidHeight = glass.getLevel() * unitHeight;

        Iterator<Drop> it = drops.iterator();
        while (it.hasNext()) {
            Drop d = it.next();
            d.y += d.speed;

            int surfaceY = CUP_TOP + CUP_HEIGHT - liquidHeight - 1;
            if (d.y >= surfaceY) {
                if (!glass.isFull()) {
                    glass.addUnit(Glass.Liquid.WATER);
                    // Only update color if milk/milo hasn't been added yet
                    if (!milkMiloAdded) {
                        visualLiquidType = Glass.Liquid.WATER;
                    }
                }
                it.remove();
            }
        }
    }

    private void updateParticlesAndStir() {
        int unitHeight = CUP_HEIGHT / glass.getCapacity();
        int liquidHeight = glass.getLevel() * unitHeight;
        int surfaceY = CUP_TOP + CUP_HEIGHT - liquidHeight;

        if (packet != null && packet.generatedParticles < packet.totalParticles) {
            for (int i = 0; i < 2; i++) {
                particles.add(new Particle(
                        packet.x + 10 + (int)(Math.random() * 10 - 5),
                        packet.y + 20,
                        packet.type,
                        1 + Math.random()
                ));
                packet.generatedParticles++;
            }
        }

        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.y += p.speed;
            if (p.y >= surfaceY) it.remove();
        }

        if (packet != null && packet.generatedParticles >= packet.totalParticles && particles.isEmpty() && !stirring) {
            stirring = true;
            stirTicks = 0;

            if (!glass.isFull()) {
                glass.addUnit(packet.type);
            }

            // Lock visual color to milk/milo
            visualLiquidType = packet.type;
            milkMiloAdded = true;

            packet = null;
        }

        if (stirring) {
            stirTicks++;
            stirAngle = (int)(Math.sin((double) stirTicks / STIR_DURATION * Math.PI * 2) * STIR_SWING);
            if (stirTicks >= STIR_DURATION) {
                stirring = false;
            }
        }
    }

    // ================= ACTIONS =================

    public void addLiquid(Glass.Liquid type) {
        if (glass.isFull()) {
            JOptionPane.showMessageDialog(this,
                    "The glass is already full!",
                    "Cannot Add",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (type == Glass.Liquid.WATER) {
            drops.add(new Drop(NOZZLE_X, NOZZLE_Y, 2));
            if (!milkMiloAdded) {
                visualLiquidType = Glass.Liquid.WATER;
            }
            return;
        }

        if (glass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Add water first before Milk or Milo.",
                    "Cannot Add",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (milkMiloAdded) {
            JOptionPane.showMessageDialog(this,
                    "Milk or Milo already added. Drink the glass first.",
                    "Cannot Add",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        packet = new Packet(NOZZLE_X - 10, NOZZLE_Y - 20, type, 50);
    }

    public void removeUnit() {
        if (!glass.isEmpty()) {
            glass.removeUnit();

            if (glass.isEmpty()) {
                drops.clear();
                particles.clear();
                packet = null;
                stirring = false;
                milkMiloAdded = false;
                visualLiquidType = Glass.Liquid.WATER;
            }
        }
    }

    public void drinkGlass() {
        glass.drinkAll();
        drops.clear();
        particles.clear();
        packet = null;
        stirring = false;
        milkMiloAdded = false;
        visualLiquidType = Glass.Liquid.WATER;
    }

    public boolean isStirring() {
        return stirring;
    }

    // ================= PAINT =================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int unitHeight = CUP_HEIGHT / glass.getCapacity();
        int liquidHeight = glass.getLevel() * unitHeight;

        // Faucet
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(NOZZLE_X - 10, NOZZLE_Y, 40, 20);
        g2.setColor(Color.GRAY);
        g2.fillRect(NOZZLE_X - 5, NOZZLE_Y + 20, 10, 20);

        // Glass
        g2.setColor(Color.BLACK);
        g2.drawRect(CUP_LEFT, CUP_TOP, CUP_WIDTH, CUP_HEIGHT);

        // Sink
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(CUP_LEFT - 20, SINK_TOP, CUP_WIDTH + 40, SINK_HEIGHT);
        g2.setColor(Color.BLACK);
        g2.drawRect(CUP_LEFT - 20, SINK_TOP, CUP_WIDTH + 40, SINK_HEIGHT);

        // Liquid color (locked after milk/milo)
        if (!glass.isEmpty()) {
            g2.setColor(
                    visualLiquidType == Glass.Liquid.WATER ? Color.BLUE :
                            visualLiquidType == Glass.Liquid.MILK  ? Color.WHITE :
                                    new Color(139, 69, 19)
            );
            g2.fillRect(CUP_LEFT + 1,
                    CUP_TOP + CUP_HEIGHT - liquidHeight,
                    CUP_WIDTH - 1,
                    liquidHeight);
        }

        // Water drops (always blue)
        g2.setColor(Color.BLUE);
        for (Drop d : drops) {
            g2.fillOval(d.x, d.y, 8, 8);
        }

        // Powder particles
        for (Particle p : particles) {
            g2.setColor(p.type == Glass.Liquid.MILK ? Color.WHITE : new Color(139,69,19));
            g2.fillOval(p.x, p.y, 3, 3);
        }

        // Stirring spoon
        if (stirring) {
            AffineTransform old = g2.getTransform();
            g2.setColor(Color.ORANGE);
            g2.rotate(Math.toRadians(stirAngle),
                    CUP_LEFT + CUP_WIDTH / 2,
                    CUP_TOP + CUP_HEIGHT / 2);
            g2.fillRect(
                    CUP_LEFT + CUP_WIDTH / 2 - 2,
                    CUP_TOP + CUP_HEIGHT / 2 - 30,
                    4, 60
            );
            g2.setTransform(old);
        }

        // Overflow
        if (glass.isFull()) {
            g2.setColor(Color.BLUE);
            g2.fillRect(CUP_LEFT, SINK_TOP + 5, CUP_WIDTH, SINK_HEIGHT - 10);
        }
    }

    // ================= INNER CLASSES =================

    private static class Drop {
        int x, y; double speed;
        Drop(int x, int y, double s) { this.x = x; this.y = y; this.speed = s; }
    }

    private static class Particle {
        int x, y; double speed;
        Glass.Liquid type;
        Particle(int x, int y, Glass.Liquid t, double s) {
            this.x = x; this.y = y; this.type = t; this.speed = s;
        }
    }

    private static class Packet {
        int x, y;
        Glass.Liquid type;
        int totalParticles, generatedParticles = 0;
        Packet(int x, int y, Glass.Liquid t, int total) {
            this.x = x; this.y = y; this.type = t; this.totalParticles = total;
        }
    }
}
