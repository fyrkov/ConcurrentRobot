package toyProject;


import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple Join() implementation
 * Created by anch0317 on 03.03.2017.
 */
//TODO cleanup
public class Robot implements IRobot {

    private volatile double distance;
    private AtomicInteger stepCounter;
    private volatile int legs;
    private volatile boolean isInterrupted;

    public Robot(int legsQuantity, double distance) {
        legs = legsQuantity;
        this.distance = distance;
        stepCounter = new AtomicInteger(0);
        GUI.robotIsRunning = true;
    }

    public void setLegs(int legs) {
        this.legs = legs;
    }

    public void interrupt() {
        isInterrupted = true;
    }

    public void run() {

        while (distance > 0 && !isInterrupted) {
            for (int i = 0; i < legs; i++) {
                Step s = new Step(i);
                s.setDaemon(true);
                s.start();
                try {
                    s.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (distance <= 0 || isInterrupted) break;
            }
        }
        GUI.robotIsRunning = false;
    }


    class Step extends Thread {

        private int legNumber;
        int stepNumber;

        Step(int legNumber) {
            this.legNumber = legNumber;
        }

        @Override
        public void run() {
            distance -= (Math.random() + 0.5);
            stepNumber = stepCounter.incrementAndGet();
            DecimalFormat f = new DecimalFormat("#0.00");
            String s = "Robot moved with leg " + (legNumber + 1) + ", step " + stepNumber + ", distance is: " + f.format(distance) + "\n";
            GUI.appendText(s);
            System.out.print(s);
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}




