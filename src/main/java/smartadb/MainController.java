package smartadb;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

public class MainController implements EventHandler<ActionEvent> {
    public Label helloWorld;
    public TextField sdkpath;
    public ListView devicelist;
    public ListView debugapplist;
    public Button btnstartserver;
    public Button btnkillserver;
    public Button btncleardata;
    public Button btninstall;
    public Button btnuninstall;
    public Button btnreconnect;

    private String SDK_PLATFROM_TOOLS;
    private String SDK_TOOLS;
    private String ADB;
    private String selectedPkgName = "";
    private String selectedDevice = "";

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        Preferences prefs = Preferences.userNodeForPackage(MainController.class);
        String prefSdkPath = prefs.get("sdkPath", null);
        if (prefSdkPath != null) {
            sdkpath.setText(prefSdkPath);
            SDK_TOOLS = prefSdkPath + "/tools";
            SDK_PLATFROM_TOOLS = prefSdkPath + "/platform-tools";
            ADB = SDK_PLATFROM_TOOLS + "/adb";
            //SDK_TOOLS=prefSdkPath+"/tools/";
            fillUI();
        }
    }

    private void fillUI() {
        extractDeviceList();
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.add("Hi");
        observableList.add("Hello");
        debugapplist.setItems(observableList);

        debugapplist.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue, newValue) -> System.out.println(newValue));

        btnstartserver.setOnAction(this);
        btnkillserver.setOnAction(this);
        btncleardata.setOnAction(this);
        btninstall.setOnAction(this);
        btnuninstall.setOnAction(this);
        btnreconnect.setOnAction(this);
    }

    private void extractDeviceList() {
        String output = runCommands("devices");
        ObservableList<String> observableList = FXCollections.observableArrayList();
        String devList[] = output.split("[\n]");
        for (int i = 0; i < devList.length; i++) {
            observableList.add(devList[i]);
        }
        devicelist.setItems(observableList);
        devicelist.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (devicelist.getSelectionModel().getSelectedIndex() == 0) {
                        return;
                    }
                    selectedDevice = newValue.toString().split("[\t]")[0];
                    System.out.println(newValue);
                });

    }

    private String runCommands(String cmd) {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {ADB, cmd};
        Process proc = null;
        try {
            proc = rt.exec(ADB+" "+cmd);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            StringBuffer stringBuffer = new StringBuffer();
// read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                stringBuffer.append(s);
                stringBuffer.append("\n");
                System.out.println(s);
            }

// read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void sayHelloWorld(ActionEvent actionEvent) {
        helloWorld.setText("Hello ADB");
    }

    public void openFileChoose(ActionEvent actionEvent) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Android SDK Path");
        File file = dirChooser.showDialog(Main.getStageInstance());
        if (file != null) {
            Preferences prefs = Preferences.userNodeForPackage(MainController.class);
            prefs.put("sdkPath", file.getAbsolutePath());
            sdkpath.setText(file.getAbsolutePath());
        }
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        Object whichSource = actionEvent.getSource();
        if (whichSource.equals(btnstartserver)) {
            runCommands("start-server");
        } else if (whichSource.equals(btnkillserver)) {
            runCommands("kill-server");
        } else if (whichSource.equals(btncleardata)) {
            if (selectedPkgName.length() != 0) {
                runCommands("pm clear " + selectedPkgName);
            } else {
                System.out.println("No Package found");
            }
        } else if (whichSource.equals(btninstall)) {
            if (selectedDevice.length() != 0 && selectedPkgName.length() != 0) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Android APK", "*.apk"));
                fileChooser.setTitle("Choose apk file to Install");
                File file = fileChooser.showOpenDialog(Main.getStageInstance());
                if (file != null) {
                    runCommands("-s " + selectedDevice + " install " + file.getAbsolutePath());
                }
            } else {
                System.out.println("No Package found");
            }
        } else if (whichSource.equals(btnuninstall)) {
            if (selectedDevice.length() != 0 && selectedPkgName.length() != 0) {
                runCommands("-s " + selectedDevice + " uninstall " + selectedPkgName);
            } else {
                System.out.println("No Package found");
            }
        } else if (whichSource.equals(btnreconnect)) {
            if (selectedDevice.length() != 0) {
                runCommands("-s " + selectedDevice + " reconnect");
            } else {
                System.out.println("No Package found");
            }
        }
    }
}
