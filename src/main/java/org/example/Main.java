package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class Main {
    private static final int SPEED = 5;
    private static final int RADIUS = 50;
    private static ImageIcon idleIconRight;
    private static ImageIcon runningIconRight;
    private static ImageIcon idleIconLeft;
    private static ImageIcon runningIconLeft;
    private static ImageIcon layingIcon;
    private static ImageIcon sleepingIcon;
    private static ImageIcon stretchingIcon;
    private static ImageIcon meowIcon;
    private static ImageIcon meowVFXIcon;
    private static ImageIcon standUpIcon;
    private static JLabel catLabel;
    private static JLabel meowVFXLabel;
    private static JFrame frame;
    private static Timer stretchingTimer;
    private static Timer returnToIdleTimer;
    private static Timer layingTimer;
    private static Timer sleepingTimer;
    private static Timer meowTimer;
    private static Timer standUpTimer;
    private static boolean isWithinRadius = false;
    private static boolean isSpecialAction = false;

    public static void main(String[] args) {
        frame = new JFrame();
        loadIcons();

        Random random = new Random();

        catLabel = new JLabel(idleIconRight);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        int randomX = random.nextInt(1921);
        int randomY = random.nextInt(1081);
        frame.setLocation(randomX, randomY);
        frame.setType(Window.Type.UTILITY);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.getContentPane().add(catLabel);
        frame.pack();
        frame.setVisible(true);

        meowVFXLabel = new JLabel();
        frame.getContentPane().add(meowVFXLabel);
        meowVFXLabel.setVisible(false);

        initializeMeowTimer();

        catLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    handleMouseClick();
                }
            }
        });

        Timer movementTimer = new Timer(16, e -> updateCatMovement());
        movementTimer.start();

        stretchingTimer = new Timer(7000, e -> {
            isSpecialAction = true;
            catLabel.setIcon(stretchingIcon);
            stretchingTimer.stop();
            returnToIdleTimer.start();
        });

        standUpTimer = new Timer(2080, e -> {
            setCatIdleIcon();
            standUpTimer.stop();
        });

        returnToIdleTimer = new Timer(2600, e -> {
            setCatIdleIcon();
            returnToIdleTimer.stop();
            layingTimer.start();
        });

        layingTimer = new Timer(5000, e -> {
            catLabel.setIcon(layingIcon);
            layingTimer.stop();
            sleepingTimer.start();
        });

        sleepingTimer = new Timer(2080, e -> {
            catLabel.setIcon(sleepingIcon);
            sleepingTimer.stop();
        });
    }

    private static void handleMouseClick() {
        if (catLabel.getIcon() == sleepingIcon) {
            catLabel.setIcon(standUpIcon);
            standUpTimer.start();
        }

        if (!isSpecialAction) {
            isSpecialAction = true;
            catLabel.setIcon(meowIcon);
            meowTimer.start();

            Point catPosition = frame.getLocation();
            int x = catPosition.x + catLabel.getWidth() + 1000;
            int y = catPosition.y;
            meowVFXLabel.setBounds(x, y, meowVFXIcon.getIconWidth(), meowVFXIcon.getIconHeight());
            meowVFXLabel.setIcon(meowVFXIcon);
            meowVFXLabel.setVisible(true);
        }
    }

    private static void initializeMeowTimer() {
        meowTimer = new Timer(1000, e -> {
            setCatIdleIcon();
            isSpecialAction = false;
            meowTimer.stop();
            meowVFXLabel.setVisible(false);
        });
    }


    private static void setCatIdleIcon() {
        Point catPosition = frame.getLocation();
        int dx = MouseInfo.getPointerInfo().getLocation().x - catPosition.x;
        if (dx < 0) {
            catLabel.setIcon(idleIconLeft);
        } else {
            catLabel.setIcon(idleIconRight);
        }
    }

    private static void updateCatMovement() {
        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        Point catPosition = frame.getLocation();
        int dx = mousePosition.x - catPosition.x - catLabel.getWidth() / 2;
        int dy = mousePosition.y - catPosition.y - catLabel.getHeight() / 2;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > RADIUS) {
            int moveX = (int) (SPEED * Math.cos(Math.atan2(dy, dx)));
            int moveY = (int) (SPEED * Math.sin(Math.atan2(dy, dx)));

            catPosition.translate(moveX, moveY);
            frame.setLocation(catPosition);

            if (moveX < 0) {
                catLabel.setIcon(runningIconLeft);
            } else {
                catLabel.setIcon(runningIconRight);
            }

            isWithinRadius = false;
            isSpecialAction = false;
            stretchingTimer.stop();
            layingTimer.stop();
            sleepingTimer.stop();
        } else {
            if (!isSpecialAction) {
                if (dx < 0) {
                    catLabel.setIcon(idleIconLeft);
                } else {
                    catLabel.setIcon(idleIconRight);
                }
            }

            if (!isWithinRadius && !isSpecialAction) {
                isWithinRadius = true;
                stretchingTimer.restart();
            }
        }
    }

    private static void loadIcons() {
        URL idleRightURL = Main.class.getResource("/Cat 5/idle.gif");
        URL runRightURL = Main.class.getResource("/Cat 5/run.gif");
        URL idleLeftURL = Main.class.getResource("/Cat 5/idle_left.gif");
        URL runLeftURL = Main.class.getResource("/Cat 5/run_left.gif");
        URL layingURL = Main.class.getResource("/Cat 5/laying.gif");
        URL sleepingURL = Main.class.getResource("/Cat 5/sleeping.gif");
        URL stretchingURL = Main.class.getResource("/Cat 5/stretching.gif");
        URL meowURL = Main.class.getResource("/Cat 5/meow.gif");
        URL meowVFXURL = Main.class.getResource("/Cat 5/meowVFX.gif");
        URL standUpURL = Main.class.getResource("/Cat 5/standUp.gif");

        if (idleRightURL == null || runRightURL == null || idleLeftURL == null || runLeftURL == null) {
            System.err.println("Einige der erforderlichen GIFs wurden nicht gefunden.");
            System.exit(1);
        }

        idleIconRight = new ImageIcon(idleRightURL);
        runningIconRight = new ImageIcon(runRightURL);
        idleIconLeft = new ImageIcon(idleLeftURL);
        runningIconLeft = new ImageIcon(runLeftURL);
        assert layingURL != null;
        layingIcon = new ImageIcon(layingURL);
        assert sleepingURL != null;
        sleepingIcon = new ImageIcon(sleepingURL);
        assert stretchingURL != null;
        stretchingIcon = new ImageIcon(stretchingURL);
        assert meowURL != null;
        meowIcon = new ImageIcon(meowURL);
        assert meowVFXURL != null;
        meowVFXIcon = new ImageIcon(meowVFXURL);
        assert standUpURL != null;
        standUpIcon = new ImageIcon(standUpURL);
    }
}
