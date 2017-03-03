package toyProject;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Robot2 r = new Robot2(3, 123.);
        r.startMoving();

        Thread t = new Thread(new InputRunner(r));
        t.start();

        System.out.println("finished in "+r.getStepCounter()+" steps");


    }
}
