package gui;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Orb {
    private long mass;
    private float density = 22.59f;
    private float x, y;
    private float radius;
    private final double gravityConst = 6.6743e-11;  // Reale Gravitationskonstante
    private MainFrame frame;
    private Color color;
    private float velocityX = 0; // Geschwindigkeit in x-Richtung
    private float velocityY = 0; // Geschwindigkeit in y-Richtung
    public static int orbCount = 0;
    private static Orb highlighted = null;

    public Orb(float x, float y, Color color, MainFrame frame) {
        Random r = new Random();

        this.frame = frame;
        this.color = color;

        this.radius = r.nextInt(20, 200);
        this.mass = (long) (Math.pow(radius, 3) * Math.PI * 3/4 * density);

        this.x = x;
        this.y = y;

        orbCount++;
        System.out.printf("%d Orb created: %s\n", orbCount, this);
    }

    @Override
    public String toString() {
        String formattedMass = formatWithKiloStep(mass);
        return String.format("(%.2f|%.2f)\nMass: %s [kg]\nRadius:%.2f [px]\nVelocityX:%f\nVelocityY:%f", x, y,formattedMass, radius, velocityX, velocityY);
    }

    public static String formatWithKiloStep(long number) {
        String numberStr = Long.toString(number);
        StringBuilder formatted = new StringBuilder(numberStr);

        for (int i = numberStr.length() - 3; i > 0; i -= 3) {
            formatted.insert(i, " ");
        }

        return formatted.toString();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public long getMass() {
        return mass;
    }

    public float getDiameter() {
        return radius;
    }

    public void render(float dt) {
        float adjustedDt = dt * (float) Math.pow(10, frame.getSimSpeed());
        double gravity = gravityConst * Math.pow(10, frame.getGravMulitplier());
        List<Orb> orbs = frame.getOrbs();

        for (Orb orb : orbs) {
            if (orb == null || orb == this) continue;

            float xDistance = orb.getX() - this.x;
            float yDistance = orb.getY() - this.y;

            float centerDistanceSquared = xDistance * xDistance + yDistance * yDistance;
            float minDistSquared = (this.radius + orb.getDiameter()) * (this.radius + orb.getDiameter());

            centerDistanceSquared = Math.max(centerDistanceSquared, minDistSquared);

            float centerDistance = (float) Math.sqrt(centerDistanceSquared);

            double acceleration = gravity * orb.getMass() / centerDistanceSquared;

            // acceleration *= (double) orb.getMass() / getMass();

            // F =  G * m1 * m2 / r^2
            // F / m = a
            // F / m

            float ax = (float) ((xDistance / centerDistance) * acceleration);
            float ay = (float) ((yDistance / centerDistance) * acceleration);

            velocityX += ax * adjustedDt;
            velocityY += ay * adjustedDt;

            this.x += velocityX * adjustedDt;
            this.y += velocityY * adjustedDt;

            /*
            if (centerDistance < this.radius + orb.getDiameter()) {
                handleCollision(orb);
            }*/
        }
    }

    private void handleCollision(Orb orb) {
        float dx = orb.getX() - this.x;
        float dy = orb.getY() - this.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float nx = dx / distance;
        float ny = dy / distance;

        float relativeVelocityX = orb.velocityX - this.velocityX;
        float relativeVelocityY = orb.velocityY - this.velocityY;

        float m1 = this.mass;
        float m2 = orb.getMass();

        float e = (int) (0.2 * frame.getElasticity());
        float impulse = (1 + e) * (relativeVelocityX * nx + relativeVelocityY * ny) / (m1 + m2);

        this.velocityX += (impulse / this.mass) * nx;
        this.velocityY += (impulse / this.mass) * ny;

        orb.velocityX -= (impulse / orb.getMass()) * nx;
        orb.velocityY -= (impulse / orb.getMass()) * ny;

        float overlap = (this.radius + orb.getDiameter()) - distance;

        if (overlap > 0.1f) {
            this.x -= overlap * nx * 0.5f;
            this.y -= overlap * ny * 0.5f;
            orb.x += overlap * nx * 0.5f;
            orb.y += overlap * ny * 0.5f;
        }
    }

    public Color getColor() {
        return color;
    }

    public void setHighlighted() {
        highlighted = this;
    }

    public boolean isHighlighted() {
        return highlighted == this;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }
}
