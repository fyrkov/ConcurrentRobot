package toyProject;

public class Main {

    public static void main(String[] args) {

        Robot2 r = new Robot2(3, 34.);

        Thread input = new InputRunner(r);
        input.start();

        r.startMoving();

        System.out.println("finished in "+r.getStepCounter()+" steps");
        input.interrupt();
    }
}
