package gui;

public class Main {
    public static void main(String[] args) {
        var frame = new MainFrame(800, 600,"Gravity Simulation");

        new Thread(() -> {
            long lastTime = System.currentTimeMillis();

            while (true) {
                long currentTime = System.currentTimeMillis();
                float timeDelta = ((float) (currentTime - lastTime)) / 1000f;
                lastTime = currentTime;

                frame.updateOrbList();

                for (Orb orb : frame.getOrbs()) {
                    orb.render(timeDelta);
                }

                frame.repaintScreen();

                try {
                    Thread.sleep(16); // Ca. 60 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}