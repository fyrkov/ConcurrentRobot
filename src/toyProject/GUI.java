package toyProject;

/**
 * Created by user on 06.03.2017.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GUI extends Application {

    private static TextArea textArea;
    private static Spinner<Integer> legsSpinner;
    private static Spinner<Double> distanceSpinner;
    private static IRobot robot;
    private static Thread robotThread;
    private static int legsQuantity = 3;
    private static double distance = 54.1;
    private static Button btnStart;

    public static void main(String[] args) {
        launch(args);
    }

    static synchronized boolean parseParams() {
        boolean result = false;
        String s = distanceSpinner.getEditor().getText();
        if (s.matches("\\d+([.,]\\d+)?")) {
            try {
                if (s.contains(",")) s = s.replace(',', '.');
                distance = Double.valueOf(s);
                result = true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                appendText("Incorrect distance input \n");
            }
        } else {
            appendText("Incorrect distance input \n");
            distanceSpinner.getEditor().setText(Double.valueOf(distance).toString());
        }
        String s2 = legsSpinner.getEditor().getText();
        if (s2.matches("\\d+$")) {
            legsQuantity = Integer.valueOf(s2);
            result = true;
        } else {
            appendText("Incorrect legs input \n");
            legsSpinner.getEditor().setText(Integer.valueOf(legsQuantity).toString());
        }
        return result;
    }

    static synchronized void appendText(String s) {
        if (s.equals("")) textArea.setText("");
        else Platform.runLater(() -> textArea.appendText(s));
    }

    static void robotIsRunning(boolean b) {
        if (b) Platform.runLater(() -> btnStart.setText("Stop"));
        else Platform.runLater(() -> btnStart.setText("Start"));
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ConcurrentRobot");

        //Start button
        btnStart = new Button("Start");
        btnStart.setMinWidth(300);
        btnStart.setOnMouseClicked(event -> {
            btnStart.setDisable(true);
            if (robotThread == null || !robotThread.isAlive()) {
                appendText("");
                parseParams();
                robot = new Robot3(legsQuantity, distance);
                robotThread = new Thread(robot);
                robotThread.setDaemon(true);
                robotThread.start();
                textArea.positionCaret(textArea.getText().length() - 1);
            } else {
                robot.interrupt();
                appendText("Robot is interrupted \n");
            }
            btnStart.setDisable(false);
        });

        //Pane and TextArea
        StackPane root = new StackPane();
        root.setPadding(new Insets(10.0));
        textArea = new TextArea();
        textArea.setEditable(false);
        root.getChildren().add(textArea);
        StackPane.setMargin(textArea, new Insets(30.0, 0.0, 30.0, 0.0));
        root.getChildren().add(btnStart);
        StackPane.setAlignment(btnStart, Pos.BOTTOM_CENTER);

        //distanceSpinner and label
        distanceSpinner = new Spinner<>();
        distanceSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 100, distance));
        root.getChildren().add(distanceSpinner);
        distanceSpinner.setMaxWidth(80);
        StackPane.setMargin(distanceSpinner, new Insets(0.0, 110, 460.0, 0.0));
        distanceSpinner.setEditable(true);
        Label distanceLabel = new Label("Distance:");
        root.getChildren().add(distanceLabel);
        StackPane.setMargin(distanceLabel, new Insets(0.0, 250, 460.0, 0.0));

        //legsSpinner and label
        legsSpinner = new Spinner<>();
        legsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, legsQuantity));
        root.getChildren().add(legsSpinner);
        legsSpinner.setMaxWidth(60);
        StackPane.setMargin(legsSpinner, new Insets(0.0, 0, 460.0, 100.0));
        legsSpinner.setEditable(true);
        Label spinnerLabel = new Label("Legs:");
        root.getChildren().add(spinnerLabel);
        StackPane.setMargin(spinnerLabel, new Insets(0.0, 0, 460.0, 5));

        //submit button
        Button btnSubmit = new Button("Submit");
        btnSubmit.setOnMouseClicked(event -> {
            if (robot != null && parseParams()) {
                robot.setParams(legsQuantity, distance);
                String s = "Params set: distance=" + distance + ", legs=" + legsQuantity + "\n";
                GUI.appendText(s);
                System.out.println(s);
            } else if (robot == null && parseParams()) {
                String s = "Params set: distance=" + distance + ", legs=" + legsQuantity + "\n";
                GUI.appendText(s);
                System.out.println(s);
            }
        });
        root.getChildren().add(btnSubmit);
        StackPane.setMargin(btnSubmit, new Insets(0.0, 0, 460.0, 245));


        //main window
        primaryStage.setScene(new Scene(root, 310, 500));
        primaryStage.show();
        primaryStage.setResizable(false);
    }
}
