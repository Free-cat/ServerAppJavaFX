package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Controller {
    public Button startbutton;
    public TextField port;
    public Button stop;
    private Thread thread;

    public void startserver(ActionEvent actionEvent) {
        Server server = new Server(port.getText());
        startbutton.setDisable(true);
        stop.setDisable(false);
        thread = new Thread(server);
        thread.start();
    }

    public void stopserver(ActionEvent actionEvent) {
        startbutton.setDisable(false);
        stop.setDisable(true);
        thread.interrupt();
    }
}
