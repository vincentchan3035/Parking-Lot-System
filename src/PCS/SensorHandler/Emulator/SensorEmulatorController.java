package PCS.SensorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

//======================================================================
// SensorEmulatorController
public class SensorEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private SensorEmulator sensorEmulator;
    public TextArea sensorTextArea;
    private MBox sensorMbox;
    public Button autoPollButton;
    private int lineNo = 0;

    /**
     * The initialized setting of the sensor emulator
     * @param id the name of the emulator
     * @param appKickstarter a reference to our AppKickStarter
     * @param log the logger for this emulator
     * @param sensorEmulator the sensor emulator
     */

    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, SensorEmulator sensorEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.sensorEmulator = sensorEmulator;
        this.sensorMbox = appKickstarter.getThread("SensorHandler-lvl"+sensorEmulator.lvl+" ("+sensorEmulator.dir+")").getMBox();
    } // initialize



    /**
     * The action after the button is pressed
     * @param actionEvent the action from device
     */
    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {

            case "Sensor Detected":
                sensorMbox.send(new Msg(id, null, Msg.Type.CarDetected, ""));
                break;

            case "Poll Request":
                appendTextArea("Send poll request.");
                sensorMbox.send(new Msg(id, null, Msg.Type.Poll, ""));
                break;

            case "Poll ACK":
                appendTextArea("Send poll ack.");
                sensorMbox.send(new Msg(id, null, Msg.Type.PollAck, ""));
                break;

            case "Auto Poll: On":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: Off"));
                sensorMbox.send(new Msg(id, null, Msg.Type.SensorEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            case "Auto Poll: Off":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: On"));
                sensorMbox.send(new Msg(id, null, Msg.Type.SensorEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed

    /**
     * Show the status on the dispatcher emulator's text area
     * @param status the emulator status
     */

    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        Platform.runLater(() -> sensorTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea
}
