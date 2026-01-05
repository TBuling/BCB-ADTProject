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

    // --- Sink & Drainage ---
    private double sinkLevel = 0; // Changed to double for smoother draining
    private final int SINK_MAX_LEVEL = 200;
    private final int SINK_FILL_RATE = 5;
    private final double DRAIN_RATE = 0.2; // Slowly drains every tick
    private Glass.Liquid sinkLiquidType = Glass.Liquid.WATER;

    private boolean glassVanished = false;

    // --- Dynamic Sizing Constants ---
    private final int CUP_WIDTH = 90;
    private final int CUP_HEIGHT = 140;
    private final int CUP_TOP = 150;
    private final int SINK_TOP = 300;
    private final int SINK_HEIGHT = 60;
    private final int DRAIN_WIDTH = 25; // Size of the drain pipe
    private final int NOZZLE_Y = 60;

    private final Timer timer;

    public FaucetPanel(Glass glass) {
        this.glass = glass;
        setBackground(new Color(25, 25, 30));

        timer = new Timer(25, e -> {
            updateDrops();
            updateParticlesAndStir();
            updateDrainage(); // New logic
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
                drops.add(new Drop(centerX - 4, NOZZLE_Y + 30, 8));
                dropSpawnCounter = 0;
            }
        }

        Iterator<Drop> it = drops.iterator();
        while (it.hasNext()) {
            Drop drop = it.next();
            drop.y += drop.speed;

            if (drop.y >= surfaceY || (glass.isEmpty() && drop.y >= CUP_TOP + CUP_HEIGHT)) {
                boolean added = glass.addUnit(Glass.Liquid.WATER);

                if (milkMiloAdded) {
                    waterCounter++;
                    if (waterCounter >= RESET_WATER_THRESHOLD) {
                        milkMiloAdded = false;
                        baseLiquid = Glass.Liquid.WATER;
                        waterCounter = 0;
                        packet = null;
                    }
                }
                visualLiquidType = milkMiloAdded ? visualLiquidType : Glass.Liquid.WATER;

                if (!added) {
                    overflowed = true;
                    sinkLevel = Math.min(sinkLevel + SINK_FILL_RATE, SINK_MAX_LEVEL);
                } else {
                    overflowed = glass.isFull();
                }
                it.remove();
            }
        }
    }

    private void updateDrainage() {
        if (sinkLevel > 0) {
            sinkLevel -= DRAIN_RATE;
            if (sinkLevel < 0) sinkLevel = 0;
        }
    }

    private void updateParticlesAndStir() {
        int centerX = getWidth() / 2;
        int liquidSurface = CUP_TOP + CUP_HEIGHT - (glass.getLevel() * (CUP_HEIGHT / glass.getCapacity()));
        if (glass.isEmpty()) liquidSurface = CUP_TOP + CUP_HEIGHT;

        if (packet != null && packet.generatedParticles < packet.totalParticles) {
            for (int i = 0; i < 3; i++) {
                particles.add(new Particle(centerX + (int)(Math.random() * 20 - 10), NOZZLE_Y + 35, packet.type, 3));
                packet.generatedParticles++;
            }
        }

        Iterator<Particle> pit = particles.iterator();
        while (pit.hasNext()) {
            Particle p = pit.next();
            p.y += p.speed;
            if (p.y >= liquidSurface) pit.remove();
        }

        if (!glass.isEmpty() && packet != null && packet.generatedParticles >= packet.totalParticles && particles.isEmpty() && !stirring) {
            stirring = true;
            stirTicks = 0;
            visualLiquidType = packet.type;
            milkMiloAdded = true;
            packet = null;
        }

        if (stirring) {
            stirTicks++;
            stirAngle = (int)(Math.sin((double)stirTicks / STIR_DURATION * Math.PI * 6) * STIR_SWING);
            if (stirTicks >= STIR_DURATION) stirring = false;
        }
    }

    public boolean toggleFaucet() {
        faucetOpen = !faucetOpen;
        if (faucetOpen) dropSpawnCounter = DROP_SPAWN_INTERVAL;
        return faucetOpen;
    }

    public boolean addPowder(Glass.Liquid type) {
        if (faucetOpen || milkMiloAdded) return false;
        packet = new Packet(getWidth() / 2, NOZZLE_Y, type, 60);
        return true;
    }

    public void removeUnit() {
        if (!glass.isEmpty()) {
            glass.removeUnit();
            overflowed = false;
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
        overflowed = false;
        sinkLevel = 0;

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

        // --- 1. Draw Drain Pipe (Behind Sink) ---
        g2.setPaint(new GradientPaint(centerX - DRAIN_WIDTH/2, 0, Color.DARK_GRAY, centerX + DRAIN_WIDTH/2, 0, Color.BLACK));
        g2.fillRect(centerX - DRAIN_WIDTH/2, SINK_TOP + SINK_HEIGHT - 10, DRAIN_WIDTH, 50);

        // --- 2. Draw Sink Basin ---
        int sinkWidth = CUP_WIDTH + 160;
        int sinkLeft = centerX - (sinkWidth / 2);
        g2.setColor(new Color(40, 40, 45));
        g2.fill(new RoundRectangle2D.Double(sinkLeft, SINK_TOP, sinkWidth, SINK_HEIGHT, 20, 20));

        // Sink inner shadow
        g2.setColor(new Color(15, 15, 15));
        g2.draw(new RoundRectangle2D.Double(sinkLeft, SINK_TOP, sinkWidth, SINK_HEIGHT, 20, 20));

        // --- 3. Draw Sink Water ---
        if (sinkLevel > 0) {
            double pct = sinkLevel / SINK_MAX_LEVEL;
            int waterHeight = (int) ((SINK_HEIGHT - 10) * pct);
            g2.setColor(new Color(30, 144, 255, 150));
            g2.fill(new RoundRectangle2D.Double(sinkLeft + 5, SINK_TOP + SINK_HEIGHT - 5 - waterHeight, sinkWidth - 10, waterHeight, 15, 15));

            // Flow into drain visual
            g2.fillRect(centerX - DRAIN_WIDTH/4, SINK_TOP + SINK_HEIGHT - 5, DRAIN_WIDTH/2, 10);
        }

        // --- 4. Draw Glass Liquid ---
        if (!glass.isEmpty() && !glassVanished) {
            int unitHeight = CUP_HEIGHT / glass.getCapacity();
            int lHeight = glass.getLevel() * unitHeight;
            Color c = (visualLiquidType == Glass.Liquid.WATER) ? new Color(0, 150, 255) :
                    (visualLiquidType == Glass.Liquid.MILK) ? Color.WHITE : new Color(101, 67, 33);
            g2.setPaint(new GradientPaint(cupLeft, CUP_TOP, c, cupLeft + CUP_WIDTH, CUP_TOP, c.darker()));
            g2.fill(new RoundRectangle2D.Double(cupLeft + 3, CUP_TOP + CUP_HEIGHT - lHeight, CUP_WIDTH - 6, lHeight - 3, 5, 5));
        }

        // --- 5. Draw Glass Outline ---
        if (!glassVanished) {
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(new Color(255, 255, 255, 70));
            g2.draw(new RoundRectangle2D.Double(cupLeft, CUP_TOP, CUP_WIDTH, CUP_HEIGHT, 10, 10));
        }

        // --- 6. Draw Faucet ---
        g2.setPaint(new GradientPaint(centerX - 30, 0, Color.LIGHT_GRAY, centerX + 30, 0, Color.DARK_GRAY));
        g2.fill(new RoundRectangle2D.Double(centerX - 30, 15, 60, 25, 10, 10)); // Body
        g2.fill(new Rectangle2D.Double(centerX - 10, 40, 20, 15)); // Spout

        // --- 7. Particles & Drops ---
        for (Drop d : drops) {
            g2.setColor(new Color(135, 206, 250));
            g2.fillOval(d.x, d.y, 8, 12);
        }
        for (Particle p : particles) {
            g2.setColor(p.type == Glass.Liquid.MILK ? Color.WHITE : new Color(139, 69, 19));
            g2.fillOval(p.x, p.y, 4, 4);
        }

        // --- 8. Stirrer ---
        if (stirring) {
            AffineTransform old = g2.getTransform();
            g2.rotate(Math.toRadians(stirAngle), centerX, CUP_TOP + CUP_HEIGHT / 2);
            g2.setColor(new Color(210, 180, 140));
            g2.fill(new RoundRectangle2D.Double(centerX - 3, CUP_TOP - 10, 6, 110, 5, 5));
            g2.setTransform(old);
        }
    }

    private static class Drop { int x, y; double speed; Drop(int x, int y, double s) { this.x = x; this.y = y; this.speed = s; } }
    private static class Particle { int x, y; double speed; Glass.Liquid type; Particle(int x, int y, Glass.Liquid t, double s) { this.x = x; this.y = y; this.type = t; this.speed = s; } }
    private static class Packet { int x, y; Glass.Liquid type; int totalParticles, generatedParticles = 0; Packet(int x, int y, Glass.Liquid t, int total) { this.x = x; this.y = y; this.type = t; this.totalParticles = total; } }
}