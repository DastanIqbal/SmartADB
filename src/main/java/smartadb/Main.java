package smartadb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class Main extends Application {

    public static Stage stage;

    public static Stage getStageInstance() {
        return stage;
    }

    @Override
    public void init() throws Exception {
        super.init();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        MainController mainController = new MainController();
        loader.setController(mainController);
        primaryStage.setTitle("SmartADB");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(loader.load(), 800, 400));
        primaryStage.show();
        mainController.setUp();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
