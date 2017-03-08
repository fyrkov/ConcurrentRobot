package toyProject;

public class Main {

    public static void main(String[] args) {

        Robot r = new Robot(3, 14.1);

        Thread input = new InputRunner(r);
        input.setDaemon(true);
        input.start();

//        r.startMoving();

        System.out.println("finished in "+r.getStepCounter()+" steps");
    }
}
