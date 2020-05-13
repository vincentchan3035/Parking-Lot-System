package PCS.DispatcherHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.logging.Logger;

public class DispatcherEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private DispatcherEmulator dispatcherEmulator;
    private MBox dispatcherMBox;
    public TextArea dispatcherTextArea;
    public Button autoPollButton;
    private int lineNo = 0;
    @FXML
    protected Label displayTicketInfo;

    /**
     * The initialized setting of the dispatcher emulator
     * @param id the name of the emulator
     * @param appKickstarter a reference to our AppKickStarter
     * @param log the logger for this emulator
     * @param dispatcherEmulator the dispatcher emulator
     */
    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, DispatcherEmulator dispatcherEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.dispatcherEmulator = dispatcherEmulator;
        this.dispatcherMBox = appKickstarter.getThread("DispatcherHandler").getMBox();
        this.displayTicketInfo.setText("ID and Time");
    } // initialize

    /**
     * The action after the button is pressed
     * @param actionEvent the action from device
     */
    //-----------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();

        switch (button.getText()) {
            case "Ticket Request":
                appendTextArea("Ticket Request.");
                dispatcherMBox.send(new Msg(id, null, Msg.Type.TicketRequest, "Ticket Request"));
                break;

            case "Ticket Removed":
                appendTextArea("Ticket Removed.");
                dispatcherMBox.send(new Msg(id, null, Msg.Type.TicketRemoved, "Ticket Removed"));
                //displayTicketInfo.setText("ID and Time");
                break;

            case "Poll Request":
                appendTextArea("Send poll request.");
                dispatcherMBox.send(new Msg(id, null, Msg.Type.Poll, ""));
                break;

            case "Poll ACK":
                appendTextArea("Send poll ack.");
                dispatcherMBox.send(new Msg(id, null, Msg.Type.PollAck, ""));
                break;

            case "Auto Poll: On":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: Off"));
                dispatcherMBox.send(new Msg(id, null, Msg.Type.DispatcherEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            case "Auto Poll: Off":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: On"));
                dispatcherMBox.send(new Msg(id, null, Msg.Type.DispatcherEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            default:
                log.warning(id + ": unknown button: [ " + button.getText() + " ]");
                break;
        }
    }

    /**
     * Show the status on the dispatcher emulator's text area
     * @param status the emulator status
     */
    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        Platform.runLater(() -> dispatcherTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea

    /**
     * Show the entrance information on the emulator
     * @param ticketInfo the entrance information
     */
    public void setDisplayTicketInfo(String ticketInfo) {
       Platform.runLater(()->this.displayTicketInfo.setText(ticketInfo));
    }
}
