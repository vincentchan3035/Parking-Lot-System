package PCS.VacancyDispHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.awt.*;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;


//======================================================================
// VacancyDipsEmulatorController
public class VacancyDipsEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private VacancyDispEmulator vacancyDispEmulator;
    private MBox vacancyDispMBox;
    public TextArea vacancyDispTextArea;
    public Button autoPollButton;
   // public Label displayLabel;
    private int lineNo = 0;

    /**
     * The initialized setting of the vacancy display emulator
     * @param id the name of the emulator
     * @param appKickstarter a reference to our AppKickStarter
     * @param log the logger for this emulator
     * @param vacancyDispEmulator the vacancy display emulator
     */

    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, VacancyDispEmulator vacancyDispEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.vacancyDispEmulator = vacancyDispEmulator;
        this.vacancyDispMBox = appKickstarter.getThread("VacancyDispHandler").getMBox();
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


            case "Poll Request":
                appendTextArea("Send poll request.");
                vacancyDispMBox.send(new Msg(id, null, Msg.Type.Poll, ""));
                break;

            case "Poll ACK":
                appendTextArea("Send poll ack.");
                vacancyDispMBox.send(new Msg(id, null, Msg.Type.PollAck, ""));
                break;

            case "Auto Poll: On":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: Off"));
                vacancyDispMBox.send(new Msg(id, null, Msg.Type.VacancyDispEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            case "Auto Poll: Off":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: On"));
                vacancyDispMBox.send(new Msg(id, null, Msg.Type.VacancyDispEmulatorAutoPollToggle, "ToggleAutoPoll"));
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
        Platform.runLater(() -> vacancyDispTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea

//    private final void updateDisplayLabel(int vacancyDisplayNumber){
//        displayLabel.setText(Integer.toString(vacancyDisplayNumber));
//    }



}
