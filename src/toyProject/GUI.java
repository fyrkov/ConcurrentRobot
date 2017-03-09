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
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.text.ParseException;

public class GUI extends Application {

    private static TextArea textArea;
    static volatile boolean robotIsRunning;
    private static Spinner<Integer> legsSpinner;
    private static Spinner<Double> ditanceSpinner;
    private static IRobot robot;
    private static Thread robotThread;
    private static int legsQuantity = 3;
    private static double distance = 14.1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ConcurrentRobot");

        //Start button
        final Button btnStart = new Button("Start");
        btnStart.setMinWidth(280);
        btnStart.setOnMouseClicked(event -> {
            btnStart.setDisable(true);
            if (robotThread == null || !robotThread.isAlive()) {
                appendText("");
                parseParams();
                robot = new Robot(legsQuantity, distance);
                robotThread = new Thread(robot);
                robotThread.setDaemon(true);
                robotThread.start();
//                btnStart.setText("Stop");
                textArea.positionCaret(textArea.getText().length() - 1);
            } else {
                robot.interrupt();
                appendText("Robot is interrupted \n");
//                btnStart.setText("Start");
            }
            btnStart.setDisable(false);
        });


        //Pane an TextArea
        StackPane root = new StackPane();
        root.setPadding(new Insets(10.0));
        textArea = new TextArea();
        textArea.setEditable(false);
        root.getChildren().add(textArea);
        StackPane.setMargin(textArea, new Insets(30.0, 0.0, 30.0, 0.0));
        root.getChildren().add(btnStart);
        StackPane.setAlignment(btnStart, Pos.BOTTOM_CENTER);

        //ditanceSpinner and label
        ditanceSpinner = new Spinner<>();
        ditanceSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 100, distance));
        root.getChildren().add(ditanceSpinner);
        ditanceSpinner.setMaxWidth(65);
        StackPane.setMargin(ditanceSpinner, new Insets(0.0, 110, 460.0, 0.0));
        ditanceSpinner.setEditable(true);
        Label textFieldLabel = new Label("Distance:");
        root.getChildren().add(textFieldLabel);
        StackPane.setAlignment(textFieldLabel, Pos.TOP_LEFT);

        //legsSpinner and label
        legsSpinner = new Spinner<>();
        legsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, legsQuantity));
        root.getChildren().add(legsSpinner);
        legsSpinner.setMaxWidth(55);
        StackPane.setMargin(legsSpinner, new Insets(0.0, 0, 460.0, 100.0));
        legsSpinner.setEditable(false);
        Label spinnerLabel = new Label("Legs:");
        root.getChildren().add(spinnerLabel);
        StackPane.setMargin(spinnerLabel, new Insets(0.0, 0, 460.0, 0.0));

        //submit button
        Button btnSubmit = new Button("Submit");
        btnSubmit.setOnMouseClicked(event -> {
            parseParams();
            appendText("Params set: distance=" + distance + ", legs=" + legsQuantity + "\n");
            if (robot !=null) {
                robot.setLegs(legsQuantity);
            }
        });
        root.getChildren().add(btnSubmit);
        StackPane.setMargin(btnSubmit, new Insets(0.0, 0, 460.0, 225));


        primaryStage.setScene(new Scene(root, 300, 500));
        primaryStage.show();
    }

    static synchronized void parseParams() {
        //TODO validation
        legsQuantity = Integer.valueOf(legsSpinner.getEditor().getText());
        NumberFormat nf = NumberFormat.getInstance();
        try {
            distance = nf.parse(ditanceSpinner.getEditor().getText()).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            ditanceSpinner.getEditor().setText(Double.valueOf(distance).toString());
        }
    }

    static synchronized void appendText(String s) {
        if (s.equals("")) textArea.setText("");
        else Platform.runLater( () -> textArea.appendText(s));
    }
}
