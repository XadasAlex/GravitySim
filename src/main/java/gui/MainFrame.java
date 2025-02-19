package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainFrame {
    private JFrame frame;
    private final Screen screen;
    private final List<Orb> orbs = new ArrayList<>();
    private final List<Orb> toAddOrbs = new ArrayList<>();
    private final Random r = new Random();

    private final int width;
    private final int height;

    private SettingState settingState = SettingState.UNSELECTED;

    private int simSpeed = 0;
    private int gravMulitplier = 0;
    private int elasticity = 0;
    private Graphics paintComponent;


    public MainFrame(int width, int height, String title) {
        this.width = width;
        this.height = height;
        initialize(title);
        screen = new Screen();
        screen.setBounds(0, 0, width, height);
        frame.add(screen);
        frame.addMouseListener(new MouseHandler());
        frame.addKeyListener(new KeyHandler());
    }

    public void initialize(String title) {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void repaintScreen() {
        SwingUtilities.invokeLater(screen::repaint);
    }

    public void addOrb(Orb orb) {
        toAddOrbs.add(orb);
    }

    public List<Orb> getOrbs() {
        return orbs;
    }

    public void updateOrbList() {
        orbs.addAll(toAddOrbs);
        toAddOrbs.clear();
    }

    public SettingState getSettingState() {
        return settingState;
    }

    public void setSettingState(SettingState settingState) {
        this.settingState = settingState;
    }

    private class Screen extends JLabel {
        @Override
        protected void paintComponent(Graphics g) {
            setPaintComponent(g);
            super.paintComponent(g);


            for (Orb orb : orbs) {
                if (!orb.isHighlighted()) {
                    g.setColor(orb.getColor());
                    g.fillOval((int) (orb.getX() - orb.getDiameter() / 2),
                            (int) (orb.getY() - orb.getDiameter() / 2),
                            (int) orb.getDiameter(),
                            (int) orb.getDiameter());
                }
            }

            Orb highlighedOrb = getHighlightedOrb();

            if (highlighedOrb != null) {
                g.setColor(highlighedOrb.getColor());
                g.fillOval((int) (highlighedOrb.getX() - highlighedOrb.getDiameter() / 2),
                        (int) (highlighedOrb.getY() - highlighedOrb.getDiameter() / 2),
                        (int) highlighedOrb.getDiameter(),
                        (int) highlighedOrb.getDiameter());

                // Draw the highlight border
                g.setColor(Color.BLACK);
                g.drawOval((int) (highlighedOrb.getX() - highlighedOrb.getDiameter() / 2),
                        (int) (highlighedOrb.getY() - highlighedOrb.getDiameter() / 2),
                        (int) highlighedOrb.getDiameter(),
                        (int) highlighedOrb.getDiameter());

                renderOrbDetails(g, highlighedOrb);
            }

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));

            String statusText = switch (settingState) {
                case SPEED -> "Speed: " + simSpeed;
                case GRAVITY -> "Gravity: " + gravMulitplier;
                case VIEW -> "Viewing Mode click Orb for Information";
                case CREATE -> "Left click to create an Orb";
                case ELASTICITY -> "Elasticity: " + (float) (elasticity * 0.2);
                default -> "No Setting Selected";
            };

            g.drawString("Current Setting: " + settingState, 10, 20);
            g.drawString(statusText, 10, 40);
        }
    }

    private void setPaintComponent(Graphics g) {
        this.paintComponent = g;
    }

    private Graphics getPaintComponent() {
        return paintComponent;
    }

    private Orb getHighlightedOrb() {
        for (Orb orb : getOrbs()) {
            if (orb.isHighlighted()) {
                return orb;
            }
        }

        return null;
    }

    private void renderOrbDetails(Graphics g, Orb orb) {
        float velx = orb.getVelocityX();
        float vely = orb.getVelocityY();

        // Berechnung der Geschwindigkeit und der Richtung
        double distance = Math.sqrt(velx * velx + vely * vely);
        if (distance == 0) return; // Verhindern, dass Division durch null auftritt

        // Normierung der Geschwindigkeit auf eine feste Länge (zum Beispiel 100 Pixel)
        double normalizedX = (velx / distance) * 100;  // Länge des Vektors in Pixel
        double normalizedY = (vely / distance) * 100;  // Länge des Vektors in Pixel

        // Zeichne den Vektor von der Position des Orbs
        int startX = (int) orb.getX();
        int startY = (int) orb.getY();
        int endX = (int) (startX + normalizedX);  // Zielpunkt auf der x-Achse
        int endY = (int) (startY + normalizedY);  // Zielpunkt auf der y-Achse

        g.setColor(Color.RED);
        g.drawLine(startX, startY, endX, endY);

        // Schriftart und -größe festlegen
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Text in Zeilen aufteilen
        String[] lines = orb.toString().split("\n");

        // Position des Texts relativ zum Orb
        int textX = (int) orb.getX();
        int textY = (int) orb.getY();

        // Umrandung zeichnen (weiß)
        g.setColor(Color.WHITE);
        for (int i = 0; i < lines.length; i++) {
            // Text an 8 Positionen um die eigentliche Position zeichnen (für den Outline-Effekt)
            g.drawString(lines[i], textX - 1, textY + 16 * i - 1); // oben links
            g.drawString(lines[i], textX, textY + 16 * i - 1);     // oben
            g.drawString(lines[i], textX + 1, textY + 16 * i - 1); // oben rechts
            g.drawString(lines[i], textX - 1, textY + 16 * i);     // links
            g.drawString(lines[i], textX + 1, textY + 16 * i);     // rechts
            g.drawString(lines[i], textX - 1, textY + 16 * i + 1); // unten links
            g.drawString(lines[i], textX, textY + 16 * i + 1);     // unten
            g.drawString(lines[i], textX + 1, textY + 16 * i + 1); // unten rechts
        }

        // Text in der Mitte zeichnen (schwarz)
        g.setColor(Color.BLACK);
        for (int i = 0; i < lines.length; i++) {
            g.drawString(lines[i], textX, textY + 16 * i);
        }

        g.setColor(Color.CYAN);
    }

    private class KeyHandler implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (settingState == SettingState.SPEED) {
                    if (simSpeed < 9) simSpeed++;
                }

                if (settingState == SettingState.GRAVITY) {
                    if (gravMulitplier < 5) gravMulitplier++;
                }

                if (settingState == SettingState.ELASTICITY) {
                    if (elasticity < 5) elasticity++;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                if (settingState == SettingState.SPEED) {
                    if (simSpeed > -4) simSpeed--;
                }

                if (settingState == SettingState.GRAVITY) {
                    if (gravMulitplier > -4) gravMulitplier--;
                }

                if (settingState == SettingState.ELASTICITY) {
                    if (elasticity > 0) elasticity--;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_X) {
                orbs.clear();
            }
            if (e.getKeyCode() == KeyEvent.VK_G) {
                settingState = SettingState.GRAVITY;
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                settingState = SettingState.SPEED;
            }
            if (e.getKeyCode() == KeyEvent.VK_V) {
                settingState = SettingState.VIEW;
            }
            if (e.getKeyCode() == KeyEvent.VK_C) {
                settingState = SettingState.CREATE;
            }
            if (e.getKeyCode() == KeyEvent.VK_E) {
                settingState = SettingState.ELASTICITY;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    private class MouseHandler implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (settingState == SettingState.CREATE) {
                Point convertedPoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), screen);

                addOrb(
                        new Orb(
                                convertedPoint.x,
                                convertedPoint.y,
                                new Color(
                                        r.nextInt(256),
                                        r.nextInt(256),
                                        r.nextInt(256)
                                ),
                                MainFrame.this
                        )
                );
            } else if (settingState == SettingState.VIEW) {
                viewOrb(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private void viewOrb(MouseEvent e) {
        int xClicked = e.getX();
        int yClicked = e.getY();

        for (Orb orb : getOrbs()) {
            int diameter = (int)orb.getDiameter();

            int xMin = (int) orb.getX() - (int) (0.7 * diameter / 2);
            int yMin = (int) orb.getY() - (int) (0.7 * diameter / 2);
            int xMax = xMin + diameter;
            int yMax = yMin + diameter;

            if (
                    xClicked > xMin &&
                    xClicked < xMax &&
                    yClicked < yMax &&
                    yClicked > yMin
            ) {
                orb.setHighlighted();
                return;
            }
        }
    }

    public int getSimSpeed() {
        return simSpeed;
    }

    public int getGravMulitplier() {
        return gravMulitplier;
    }

    public int getElasticity() {
        return elasticity;
    }
}