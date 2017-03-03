package toyProject;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by anch0317 on 03.03.2017.
 */
public class Robot2 {

    private volatile double distance;
    private int legs;
    private Step steps[];
    private Queue<Step> q = new LinkedList<>();

    synchronized double getDistance() {
        return distance;
    }

    synchronized void decrementDistance(double decrement) {
        this.distance -= decrement;
    }


    public Robot2(int legsQuantity, double distance) {
        legs = legsQuantity;
        this.distance = distance;
        steps = new Step[legsQuantity];
        for (int i = 0; i < legsQuantity; i++) {
            q.add(new Step(i+1));
        }

    }

    public void startMoving() {

        while (getDistance() > 0) {
            Step s = q.poll();
            if (!s.isAlive()) s.start();
            synchronized (this) {notify();}
            q.add(s);
        }

    }


    class Step extends Thread {

        private int legNumber;

        Step(int legNumber) {
            this.legNumber = legNumber;
        }

        @Override
        public void run() {
            decrementDistance(Math.random() + 0.5);
            System.out.println("Robot moved with leg " + legNumber + ", distance is " + getDistance());
            try {
                sleep((long) ((Math.random() * 500)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}



