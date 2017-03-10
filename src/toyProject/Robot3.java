package toyProject;


import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple Join() implementation
 * Created by anch0317 on 03.03.2017.
 */
//TODO cleanup
public class Robot3 implements IRobot {

    private volatile double distance;
    private AtomicInteger stepCounter;
    private volatile int legs;
    private volatile boolean isInterrupted;
    SenseBarrier b;

    public Robot3(int legsQuantity, double distance) {
        legs = legsQuantity;
        this.distance = distance;
        stepCounter = new AtomicInteger(0);
        GUI.robotIsRunning(true);
        b = new SenseBarrier(legs);
    }

    public void setLegs(int legs) {
        this.legs = legs;
    }

    public void interrupt() {
        isInterrupted = true;
    }

    public void run() {



        for (int i = 0; i < legs; i++) {
            Step s = new Step(i);
            s.setDaemon(true);
//            b.await();
            s.start();
            if (distance <= 0 || isInterrupted) break;
        }

        /*while (distance > 0 && !isInterrupted) {
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
        GUI.robotIsRunning(false);*/
    }


    class Step extends Thread {

        private final int legNumber;
        int stepNumber;

        Step(int legNumber) {
            this.legNumber = legNumber;
        }

        public void run() {

            while (true) {

                //barrier
                b.await();

                //work phase
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

    //sense-reversing barrier
    class SenseBarrier {
        private AtomicInteger count;
        private int size;
        private volatile boolean sense;
        ThreadLocal<Boolean> threadSense;

        public SenseBarrier(int n) {
            count = new AtomicInteger(n);
            size = n;
            sense = false;
            threadSense = new ThreadLocal<Boolean>() {
                protected Boolean initialValue() {
                    return !sense;
                }
            };
        }

        void await() {
            boolean mySense = threadSense.get();
            int position = count.getAndDecrement();
            if (position == 1) {
                count.set(size);
                sense = mySense;
            } else {
                while (sense != mySense) {
                }
            }
            threadSense.set(!mySense);
        }
    }

}




