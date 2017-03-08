package toyProject;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/** Wait()-Notify() implementation
 * Created by anch0317 on 03.03.2017.
 */

public class Robot2 implements IRobot {

    private List<AtomicBoolean> stepFlag;
    private volatile double distance;
    private volatile int stepCounter;
    private volatile int legs;
    private volatile List<Thread> step;
    private AtomicBoolean legsChangeFlag = new AtomicBoolean();
    private volatile boolean isInterrupted;

    public Robot2(int legsQuantity, double distance) {
        GUI.robotIsRunning = true;
        legs = legsQuantity;
        this.distance = distance;
        stepFlag = new ArrayList<>();
        step = new ArrayList<>();
        for (int i = 0; i < legs; i++) {
            step.add(i, new Step(i));
            stepFlag.add(i, new AtomicBoolean());
        }
    }

    int getStepCounter() {
        return stepCounter;
    }

    public void setLegs(int legs) {
        legsChangeFlag.set(true);
        synchronized (this) {
            System.out.println("LegSetter works");
            int delta = legs - this.legs;
            if (delta > 0) {
                for (int i = this.legs; i < legs; i++) {
                    step.add(i, new Step(i));
                    if (stepFlag.size() > i) stepFlag.set(i, new AtomicBoolean());
                    else stepFlag.add(i, new AtomicBoolean());
                    step.get(i).setDaemon(true);
                    step.get(i).start();
                }
            } else if (delta < 0) {
                for (int i = this.legs - 1; i >= legs; i--) {
                    step.get(i).interrupt();
                    step.remove(i);
                }
            }
            this.legs = legs;
            legsChangeFlag.set(false);
            try {
                //TODO
                //timeout for proper step threads interruption, may be changed to check loop before leaving sync block
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notify();
            System.out.println("LegSetter released the lock");
        }
    }

    public void interrupt() {
        isInterrupted = true;
    }

    public void run() {

        for (int i = 0; i < legs; i++) {
            step.get(i).setDaemon(true);
            step.get(i).start();
        }

        synchronized (this) {
            while (distance > 0 && !isInterrupted) {
                for (int i = 0; i < legs; i++) {
                    synchronized (step.get(i)) {
                        stepFlag.get(i).set(true);
                        step.get(i).notify();
                        while (stepFlag.get(i).get() && !isInterrupted) {
                            try {
                                step.get(i).wait();
                            } catch (InterruptedException e) {
                                System.out.println(e);
                            }
                        }
                        if (distance <= 0 || isInterrupted) break;
                    }
                    if (legsChangeFlag.get()) try {
                        wait();
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
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
            while (!isInterrupted) {
                while (stepFlag.get(legNumber).get()) {
                    synchronized (this) {
                        distance -= (Math.random() + 0.5);
                        stepCounter++;
                        stepFlag.get(legNumber).set(false);
                        DecimalFormat f = new DecimalFormat("#0.00");
                        String s = "Robot moved with leg " + (legNumber + 1) + ", step " + stepCounter + ", distance is: " + f.format(distance) + "\n";
                        GUI.appendText(s);
                        System.out.print(s);
                        notify();
                        while (!stepFlag.get(legNumber).get()) {
                            try {
                                sleep(600);
                                wait();
                            } catch (InterruptedException e) {
                                System.out.println("Leg task " + (legNumber + 1) + " cancelled");
//                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}




