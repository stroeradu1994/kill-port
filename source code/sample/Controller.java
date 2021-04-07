package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextField port;
    @FXML
    private TextArea output;

    public void kill(ActionEvent event) throws IOException {
        boolean foundPort = false;
        System.out.println("Searching for processes on port " + port.getText() + ".");
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "netstat -ano | findstr " + port.getText());
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }
            if (line.contains("LISTENING")) {
                foundPort = true;
                String[] parts = line.split("\\s+");
                String process = parts[parts.length - 1];
                System.out.println("Process found on port " + port.getText() + ". Id: " + process + ".");
                ProcessBuilder builder2 = new ProcessBuilder(
                        "cmd.exe", "/c", "taskkill /PID " + process + " /F");
                builder.redirectErrorStream(true);
                Process p2 = builder2.start();
                BufferedReader r2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                String line2;
                System.out.println("Result: ");
                while (true) {
                    line2 = r2.readLine();
                    if (line2 == null) {
                        break;
                    }
                    System.out.println(line2);
                }
            }
        }
        if (!foundPort) {
            System.out.println("No process was found on port " + port.getText() + ".");

        }
    }

    public void appendText(final String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                output.appendText(str);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                appendText(String.valueOf((char) b));
            }
        };
        System.setOut(new PrintStream(out, true));
    }
}
