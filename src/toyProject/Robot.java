package toyProject;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by anch0317 on 03.03.2017.
 */
public class Robot {

    private volatile double distance;
    private AtomicInteger stepCounter;
    private int legs;

    int getStepCounter() {
        return stepCounter.get();
    }
    synchronized void decrementDistance(double decrement) {
        this.distance -= decrement;
    }

    public Robot(int legsQuantity, double distance) {
        legs = legsQuantity;
        this.distance = distance;
        stepCounter = new AtomicInteger(0);
    }

    public void startMoving() {

        while (distance > 0) {
            for (int i = 0; i < legs; i++) {
                Step s = new Step(i + 1);
                s.start();
                try {
                    s.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (distance <= 0) break;
            }
        }

    }


    class Step extends Thread {

        private int legNumber;
        Step(int legNumber) {
            this.legNumber = legNumber;
        }

        @Override
        public void run() {
//            distance -= (Math.random() + 0.5);
            decrementDistance(Math.random() + 0.5);
            stepCounter.incrementAndGet();
            System.out.println("Robot moved with leg " + legNumber + ", step " + stepCounter.get());
            try {
                sleep((long) ((Math.random() * 200)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
}



