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
    private final int STIR_DURATION = 100;
    private final int STIR_SWING = 45;
    private boolean milkMiloAdded = false;

    private Glass.Liquid baseLiquid = Glass.Liquid.WATER;
    private Glass.Liquid visualLiquidType = Glass.Liquid.WATER;
    private int waterCounter = 0;
    private final int RESET_WATER_THRESHOLD = 5;

    private boolean faucetOpen = false;
    private int dropSpawnCounter = 0;
    private final int DROP_SPAWN_INTERVAL = 10;

    private boolean overflowed = false;
    private boolean overflowWarned = false;
    private Glass.Liquid sinkLiquidType = Glass.Liquid.WATER; // sink only shows overflowed water

    private boolean glassVanished = false; // vanish on drinkGlass only

    private final int CUP_LEFT = 150, CUP_TOP = 180, CUP_WIDTH = 80, CUP_HEIGHT = 120;
    private final int SINK_TOP = 310, SINK_HEIGHT = 50;
    private final int NOZZLE_X = CUP_LEFT + CUP_WIDTH / 2 - 4, NOZZLE_Y = CUP_TOP - 50;

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

    // --- Drop & Faucet logic ---
    private void updateDrops() {
        int unitHeight = CUP_HEIGHT / glass.getCapacity();
        int liquidHeight = glass.getLevel() * unitHeight;
        int surfaceY = CUP_TOP + CUP_HEIGHT - liquidHeight - 1;

        if (faucetOpen) {
            dropSpawnCounter++;
            if (dropSpawnCounter >= DROP_SPAWN_INTERVAL) {
                drops.add(new Drop(NOZZLE_X, NOZZLE_Y, 2));
                dropSpawnCounter = 0;
            }
        } else {
            dropSpawnCounter = DROP_SPAWN_INTERVAL;
        }

        Iterator<Drop> it = drops.iterator();
        while (it.hasNext()) {
            Drop drop = it.next();
            drop.y += drop.speed;

            if (drop.y >= surfaceY) {
                boolean added = glass.addUnit(Glass.Liquid.WATER);

                // track water for resetting milk/milo
                if (milkMiloAdded) {
                    waterCounter++;
                    if (waterCounter >= RESET_WATER_THRESHOLD) {
                        milkMiloAdded = false;
                        baseLiquid = Glass.Liquid.WATER;
                        waterCounter = 0;
                        packet = null; // allow powder again
                    }
                } else {
                    baseLiquid = Glass.Liquid.WATER;
                }

                visualLiquidType = milkMiloAdded ? visualLiquidType : baseLiquid;

                // Overflow logic
                if (!added) {
                    overflowed = true;
                    sinkLiquidType = Glass.Liquid.WATER; // only water contributes to sink
                    if (!overflowWarned) {
                        overflowWarned = true;
                        JOptionPane.showMessageDialog(this,
                                "Warning: Glass is overflowing!",
                                "Overflow",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    overflowed = glass.isFull();
                    if (!overflowed) overflowWarned = false;
                }
                it.remove();
            }
        }
    }

    // --- Powder / Particle / Stir logic ---
    private void updateParticlesAndStir() {
        int unitHeight = CUP_HEIGHT / glass.getCapacity();
        int liquidSurface = CUP_TOP + CUP_HEIGHT - glass.getLevel() * unitHeight;

        if (packet != null && packet.generatedParticles < packet.totalParticles) {
            for (int i = 0; i < 2; i++) {
                int px = packet.x + 10 + (int)(Math.random() * 10 - 5);
                int py = packet.y + 20;
                particles.add(new Particle(px, py, packet.type, 1 + Math.random()));
                packet.generatedParticles++;
            }
        }

        Iterator<Particle> pit = particles.iterator();
        while (pit.hasNext()) {
            Particle p = pit.next();
            p.y += p.speed;
            if (p.y >= liquidSurface) pit.remove();
        }

        // Stirring logic for powder — does NOT increase water level
        if (packet != null && packet.generatedParticles >= packet.totalParticles && particles.isEmpty() && !stirring) {
            stirring = true;
            stirTicks = 0;

            baseLiquid = packet.type;
            visualLiquidType = packet.type;
            milkMiloAdded = true;
            waterCounter = 0;

            packet = null; // clear packet
        }

        if (stirring) {
            stirTicks++;
            stirAngle = (int)(Math.sin((double)stirTicks / STIR_DURATION * Math.PI * 2) * STIR_SWING);
            if (stirTicks >= STIR_DURATION) stirring = false;
        }

        if (!stirring) visualLiquidType = baseLiquid;
    }

    // --- Public actions ---
    public void openFaucet() {
        faucetOpen = true;
        dropSpawnCounter = DROP_SPAWN_INTERVAL;
    }

    public void closeFaucet() {
        faucetOpen = false;
    }

    public boolean toggleFaucet() {
        faucetOpen = !faucetOpen;
        if (faucetOpen) dropSpawnCounter = DROP_SPAWN_INTERVAL;
        return faucetOpen;
    }

    public boolean isFaucetOpen() {
        return faucetOpen;
    }

    public boolean addPowder(Glass.Liquid type) {
        if (faucetOpen) {
            JOptionPane.showMessageDialog(this, "Close the faucet before adding powder!", "Cannot Add Powder", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (glass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Glass is empty! Fill it before adding powder.", "Cannot Add Powder", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (milkMiloAdded) {
            JOptionPane.showMessageDialog(this, "Powder already added!", "Cannot Add Powder", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        packet = new Packet(NOZZLE_X - 10, NOZZLE_Y - 20, type, 50);
        return true;
    }

    public void removeUnit() {
        if (!glass.isEmpty()) {
            glass.removeUnit();
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
        overflowWarned = false;
        waterCounter = 0;
        sinkLiquidType = Glass.Liquid.WATER;

        // vanish outline temporarily
        glassVanished = true;
        Timer restoreTimer = new Timer(200, e -> glassVanished = false);
        restoreTimer.setRepeats(false);
        restoreTimer.start();
    }

    public boolean isStirring() {
        return stirring;
    }

    public boolean isOverflowing() {
        return overflowed || glass.isFull();
    }

    // --- Paint ---
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

        // Glass outline
        if (!glassVanished) {
            g2.setColor(Color.BLACK);
            g2.drawRect(CUP_LEFT, CUP_TOP, CUP_WIDTH, CUP_HEIGHT);
        }

        // Liquid inside
        if (!glass.isEmpty()) {
            Color liquidColor = visualLiquidType == Glass.Liquid.WATER ? Color.BLUE
                    : visualLiquidType == Glass.Liquid.MILK ? Color.WHITE
                    : visualLiquidType == Glass.Liquid.MILO ? new Color(139,69,19)
                    : Color.LIGHT_GRAY;
            g2.setColor(liquidColor);
            g2.fillRect(CUP_LEFT + 1, CUP_TOP + CUP_HEIGHT - liquidHeight, CUP_WIDTH - 1, liquidHeight);
        }

        // Sink: only filled when overflowing, with margin
        int sinkMargin = 5;
        if (overflowed) {
            Color sinkColor = (sinkLiquidType == Glass.Liquid.WATER ? Color.BLUE
                    : sinkLiquidType == Glass.Liquid.MILK ? Color.WHITE
                    : sinkLiquidType == Glass.Liquid.MILO ? new Color(139,69,19)
                    : Color.LIGHT_GRAY);
            g2.setColor(sinkColor);
            g2.fillRect(CUP_LEFT - 20, SINK_TOP + sinkMargin, CUP_WIDTH + 40, SINK_HEIGHT - 2 * sinkMargin);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(CUP_LEFT - 20, SINK_TOP, CUP_WIDTH + 40, SINK_HEIGHT);
        }
        g2.setColor(Color.BLACK); // always keep border
        g2.drawRect(CUP_LEFT - 20, SINK_TOP, CUP_WIDTH + 40, SINK_HEIGHT);

        // Drops
        for (Drop d : drops) {
            g2.setColor(Color.BLUE);
            g2.fillOval(d.x, d.y, 8, 8);
        }

        // Particles
        for (Particle p : particles) {
            g2.setColor(p.type == Glass.Liquid.MILK ? Color.WHITE : new Color(139,69,19));
            g2.fillOval(p.x, p.y, 3, 3);
        }

        // Stirring
        if (stirring) {
            AffineTransform old = g2.getTransform();
            g2.setColor(Color.ORANGE);
            g2.rotate(Math.toRadians(stirAngle), CUP_LEFT + CUP_WIDTH/2, CUP_TOP + CUP_HEIGHT/2);
            g2.fillRect(CUP_LEFT + CUP_WIDTH/2 - 2, CUP_TOP + CUP_HEIGHT/2 - 30, 4, 60);
            g2.setTransform(old);
        }
    }

    // --- Inner classes ---
    private static class Drop {
        int x, y;
        double speed;
        Drop(int x, int y, double s) { this.x = x; this.y = y; this.speed = s; }
    }

    private static class Particle {
        int x, y;
        double speed;
        Glass.Liquid type;
        Particle(int x, int y, Glass.Liquid t, double s) { this.x = x; this.y = y; this.type = t; this.speed = s; }
    }

    private static class Packet {
        int x, y;
        Glass.Liquid type;
        int totalParticles, generatedParticles = 0;
        Packet(int x, int y, Glass.Liquid t, int total) { this.x = x; this.y = y; this.type = t; this.totalParticles = total; }
    }
}
