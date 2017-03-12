package toyProject;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Barrier implementation
 * Created by anch0317 on 03.03.2017.
 */
public class Robot3 implements IRobot {

    private final Barrier barrier;
    private final List<Thread> steps;
    private volatile double distance;
    private AtomicInteger stepCounter;
    private volatile int legs;
    private volatile boolean isGlobalInterrupted;

    public Robot3(int legs, double distance) {
        this.legs = legs;
        steps = new ArrayList<>();
        this.distance = distance;
        stepCounter = new AtomicInteger(0);
        barrier = new Barrier(this.legs);
        GUI.robotIsRunning(true);
    }

    public void setParams(int legs, double distance) {

        barrier.lock.lock();
        final int delta = legs - this.legs;
        if (delta > 0) {
            barrier.setSize(legs);
            for (int i = this.legs; i < legs; i++) {
                steps.add(i, new Step(i));
                steps.get(i).setDaemon(true);
                steps.get(i).start();
            }
            this.legs = legs;
        } else if (delta < 0) {
            killLastThreads(-delta);
            this.legs = legs;
            barrier.setSize(legs);
        }
//        this.distance = distance;
        barrier.lock.unlock();
    }

    private void killLastThreads(int n) {
        for (int i = legs - 1; i >= legs - n; i--) {
            steps.get(i).interrupt();
        }
        while (true) {
            boolean b = true;
            for (int i = legs - 1; i >= legs - n; i--) {
                if (steps.get(i).isAlive()) b = false;
            }
            if (b) break;
        }
        System.out.println(n + " last threads killed");
    }

    public void interrupt() {
        killLastThreads(legs);
        isGlobalInterrupted = true;
    }

    public void run() {

        IntStream.range(0, legs).forEach(i -> {
            Step s = new Step(i);
            steps.add(s);
            s.setDaemon(true);
            s.start();
        });

        while (distance > 0 && !isGlobalInterrupted) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        GUI.robotIsRunning(false);
    }

    class Step extends Thread {

        private final int legNumber;

        Step(int legNumber) {
            this.legNumber = legNumber;
        }

        public void run() {

            while (!(isInterrupted() && isGlobalInterrupted)) {

                //barrier
                barrier.await(legNumber);

                //work phase
                if (distance <= 0 || isGlobalInterrupted || isInterrupted()) break;
                try {
                    barrier.lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    break;
                }
                barrier.advance();
                distance -= (Math.random() + 0.5);
                int stepNumber = stepCounter.incrementAndGet();
                DecimalFormat f = new DecimalFormat("#0.00");
                String s = "Robot moved with leg " + (legNumber + 1) + ", step " + stepNumber + ", distance is: " + f.format(distance) + "\n";
                GUI.appendText(s);
                System.out.print(s);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    interrupt();
                }
                barrier.lock.unlock();
            }
        }
    }

    //cycle barrier
    class Barrier {

        final ReentrantLock lock = new ReentrantLock();
        private AtomicInteger phaseCount;
        private int size;

        Barrier(int n) {
            phaseCount = new AtomicInteger(0);
            size = n - 1;
        }

        void setSize(int n) {
            this.size = n - 1;
            if (phaseCount.get() >= size) {
                System.out.println("PhaseCount reset");
                phaseCount.set(0);
            }
        }

        void await(int legNumber) {
            while ((phaseCount.get() != legNumber || lock.hasQueuedThreads()) && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        void advance() {
            int position = phaseCount.getAndIncrement();
            if (position == size) {
                phaseCount.set(0);
            }
        }
    }

}




