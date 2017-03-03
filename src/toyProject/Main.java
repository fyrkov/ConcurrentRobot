package toyProject;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Robot r = new Robot(4, 11.);
        r.startMoving();


        System.out.println(r.getDistance());
    }
}
