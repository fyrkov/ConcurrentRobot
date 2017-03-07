package toyProject;

public class Main2 {

    public static void main(String[] args) {

        Robot2 r = new Robot2(3, 914.1);

        Thread input = new InputRunner(r);
        input.setDaemon(true);
        input.start();

        r.startMoving();

        System.out.println("finished in "+r.getStepCounter()+" steps");
    }
}
