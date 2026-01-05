import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
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
    private final int STIR_DURATION = 100;
    private final int STIR_SWING = 45;
    private boolean milkMiloAdded = false;

    private Glass.Liquid baseLiquid = Glass.Liquid.WATER;
    private Glass.Liquid visualLiquidType = Glass.Liquid.WATER;
    private int waterCounter = 0;
    private final int RESET_WATER_THRESHOLD = 5;

    private boolean faucetOpen = false;
    private int dropSpawnCounter = 0;
    private final int DROP_SPAWN_INTERVAL = 6;

    private boolean overflowed = false;
    private boolean overflowWarned = false;
    private Glass.Liquid sinkLiquidType = Glass.Liquid.WATER;

    private boolean glassVanished = false;

    // --- Dynamic Sizing Constants ---
    private final int CUP_WIDTH = 90;
    private final int CUP_HEIGHT = 140;
    private final int CUP_TOP = 150;
    private final int SINK_TOP = 300;
    private final int SINK_HEIGHT = 45;
    private final int NOZZLE_Y = 60;

    private final Timer timer;

    public FaucetPanel(Glass glass) {
        this.glass = glass;
        // Background color is a clean charcoal to make the liquid pop
        setBackground(new Color(30, 30, 35));

        timer = new Timer(25, e -> {
            updateDrops();
            updateParticlesAndStir();
            repaint();
        });
        timer.start();
    }

    private void updateDrops() {
        int centerX = getWidth() / 2;
        int unitHeight = CUP_HEIGHT / glass.getCapacity();
        int liquidHeight = glass.getLevel() * unitHeight;
        int surfaceY = CUP_TOP + CUP_HEIGHT - liquidHeight;

        if (faucetOpen) {
            dropSpawnCounter++;
            if (dropSpawnCounter >= DROP_SPAWN_INTERVAL) {
                // Drop comes from exact center
                drops.add(new Drop(centerX - 4, NOZZLE_Y + 30, 7));
                dropSpawnCounter = 0;
            }
        }

        Iterator<Drop> it = drops.iterator();
        while (it.hasNext()) {
            Drop drop = it.next();
            drop.y += drop.speed;

            if (drop.y >= surfaceY) {
                boolean added = glass.addUnit(Glass.Liquid.WATER);

                if (milkMiloAdded) {
                    waterCounter++;
                    if (waterCounter >= RESET_WATER_THRESHOLD) {
                        milkMiloAdded = false;
                        baseLiquid = Glass.Liquid.WATER;
                        waterCounter = 0;
                        packet = null;
                    }
                } else {
                    baseLiquid = Glass.Liquid.WATER;
                }
                visualLiquidType = milkMiloAdded ? visualLiquidType : baseLiquid;

                if (!added) {
                    overflowed = true;
                    sinkLiquidType = Glass.Liquid.WATER;
                } else {
                    overflowed = glass.isFull();
                }
                it.remove();
            }
        }
    }

    private void updateParticlesAndStir() {
        int centerX = getWidth() / 2;
        int unitHeight = CUP_HEIGHT / glass.getCapacity();
        int liquidSurface = CUP_TOP + CUP_HEIGHT - glass.getLevel() * unitHeight;

        if (packet != null && packet.generatedParticles < packet.totalParticles) {
            for (int i = 0; i < 3; i++) {
                int px = centerX + (int)(Math.random() * 24 - 12);
                int py = NOZZLE_Y + 35;
                particles.add(new Particle(px, py, packet.type, 2 + Math.random() * 3));
                packet.generatedParticles++;
            }
        }

        Iterator<Particle> pit = particles.iterator();
        while (pit.hasNext()) {
            Particle p = pit.next();
            p.y += p.speed;
            if (p.y >= liquidSurface) pit.remove();
        }

        if (packet != null && packet.generatedParticles >= packet.totalParticles && particles.isEmpty() && !stirring) {
            stirring = true;
            stirTicks = 0;
            baseLiquid = packet.type;
            visualLiquidType = packet.type;
            milkMiloAdded = true;
            waterCounter = 0;
            packet = null;
        }

        if (stirring) {
            stirTicks++;
            stirAngle = (int)(Math.sin((double)stirTicks / STIR_DURATION * Math.PI * 6) * STIR_SWING);
            if (stirTicks >= STIR_DURATION) stirring = false;
        }
    }

    // --- Action Methods matching Main.java ---
    public boolean toggleFaucet() {
        faucetOpen = !faucetOpen;
        if (faucetOpen) dropSpawnCounter = DROP_SPAWN_INTERVAL;
        return faucetOpen;
    }

    public boolean addPowder(Glass.Liquid type) {
        if (faucetOpen || glass.isEmpty() || milkMiloAdded) {
            return false;
        }
        packet = new Packet(getWidth() / 2, NOZZLE_Y, type, 60);
        return true;
    }

    public void removeUnit() {
        if (!glass.isEmpty()) {
            glass.removeUnit();
            overflowed = false; // clear overflow if we remove liquid
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
        baseLiquid = Glass.Liquid.WATER;
        overflowed = false;
        waterCounter = 0;

        glassVanished = true;
        Timer t = new Timer(250, e -> glassVanished = false);
        t.setRepeats(false);
        t.start();
    }

    public boolean isStirring() { return stirring; }
    public boolean isOverflowing() { return overflowed; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int cupLeft = centerX - (CUP_WIDTH / 2);

        // --- Sink (Bottom) ---
        g2.setColor(new Color(20, 20, 20));
        g2.fill(new RoundRectangle2D.Double(cupLeft - 50, SINK_TOP, CUP_WIDTH + 100, SINK_HEIGHT, 15, 15));
        if (overflowed) {
            g2.setColor(new Color(30, 144, 255, 120));
            g2.fill(new RoundRectangle2D.Double(cupLeft - 45, SINK_TOP + 5, CUP_WIDTH + 90, SINK_HEIGHT - 10, 10, 10));
        }

        // --- Liquid ---
        if (!glass.isEmpty() && !glassVanished) {
            int unitHeight = CUP_HEIGHT / glass.getCapacity();
            int lHeight = glass.getLevel() * unitHeight;
            Color c = (visualLiquidType == Glass.Liquid.WATER) ? new Color(0, 150, 255) :
                    (visualLiquidType == Glass.Liquid.MILK) ? Color.WHITE : new Color(101, 67, 33);

            g2.setPaint(new GradientPaint(cupLeft, CUP_TOP, c, cupLeft + CUP_WIDTH, CUP_TOP, c.darker()));
            g2.fill(new RoundRectangle2D.Double(cupLeft + 3, CUP_TOP + CUP_HEIGHT - lHeight, CUP_WIDTH - 6, lHeight - 3, 5, 5));
        }

        // --- Glass ---
        if (!glassVanished) {
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(new Color(255, 255, 255, 60)); // Transparent glass
            g2.draw(new RoundRectangle2D.Double(cupLeft, CUP_TOP, CUP_WIDTH, CUP_HEIGHT, 10, 10));
            // Shine highlight
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fill(new Rectangle2D.Double(cupLeft + 8, CUP_TOP + 10, 8, CUP_HEIGHT - 20));
        }

        // --- Faucet ---
        g2.setPaint(new GradientPaint(centerX - 20, 0, Color.LIGHT_GRAY, centerX + 20, 0, Color.DARK_GRAY));
        g2.fill(new RoundRectangle2D.Double(centerX - 30, 15, 60, 25, 10, 10));
        g2.fill(new Rectangle2D.Double(centerX - 10, 40, 20, 15));

        // --- Drops & Particles ---
        for (Drop d : drops) {
            g2.setColor(new Color(0, 191, 255));
            g2.fillOval(d.x, d.y, 8, 12);
        }
        for (Particle p : particles) {
            g2.setColor(p.type == Glass.Liquid.MILK ? Color.WHITE : new Color(139, 69, 19));
            g2.fillOval(p.x, p.y, 4, 4);
        }

        // --- Stirrer ---
        if (stirring) {
            AffineTransform old = g2.getTransform();
            g2.rotate(Math.toRadians(stirAngle), centerX, CUP_TOP + CUP_HEIGHT / 2);
            g2.setColor(new Color(222, 184, 135)); // BurlyWood color
            g2.fill(new RoundRectangle2D.Double(centerX - 3, CUP_TOP - 10, 6, 110, 5, 5));
            g2.setTransform(old);
        }
    }

    private static class Drop { int x, y; double speed; Drop(int x, int y, double s) { this.x = x; this.y = y; this.speed = s; } }
    private static class Particle { int x, y; double speed; Glass.Liquid type; Particle(int x, int y, Glass.Liquid t, double s) { this.x = x; this.y = y; this.type = t; this.speed = s; } }
    private static class Packet { int x, y; Glass.Liquid type; int totalParticles, generatedParticles = 0; Packet(int x, int y, Glass.Liquid t, int total) { this.x = x; this.y = y; this.type = t; this.totalParticles = total; } }
}