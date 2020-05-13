package PCS.CollectorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.logging.Logger;

public class CollectorEmulatorController {
    public Button PollACKButton;
    public Button PollRequestButton;
    public Button InputIDButton;
    public Button TurnOffAlarmButton;
    private String id;
    private AppKickstarter appKickstarter;
    private CollectorEmulator collectorEmulator;
    private Logger log;
    private MBox collectorMbox;
    public TextArea collectorTextArea;
    private int lineNo = 0;
    public Button autoPollButton;
    public Label ticketIdLabel;
    public TextField TicketID;

    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CollectorEmulator collectorEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.collectorEmulator = collectorEmulator;
        this.collectorMbox = appKickstarter.getThread("CollectorHandler").getMBox();
        this.TicketID.setPromptText("Enter your Ticket ID.");
    } // initialize

    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {

            case "Enter ID":
                appendTextArea("Ticket inputted.");

                if(checkTicketID(TicketID.getText())==false){
                    break;
                }; // check the TicketID is the correct format

                if(TicketID.getText()==""){ // empty input handling
                    appendTextArea("Please enter the ticket ID!");
                }else{
                    collectorMbox.send(new Msg(id, null, Msg.Type.TicketInsertRequest, TicketID.getText())); //send to PCS with the id
                    appendTextArea("Ticket inserted. Send ticket verification request to collector handler.");
                    break;
                }

            case "Poll Request":
                appendTextArea("Send poll request.");
                collectorMbox.send(new Msg(id, null, Msg.Type.Poll, ""));
                break;

            case "Poll ACK":
                appendTextArea("Send poll ack.");
                collectorMbox.send(new Msg(id, null, Msg.Type.PollAck, ""));
                break;

            case "Auto Poll: On":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: Off"));
                collectorMbox.send(new Msg(id, null, Msg.Type.CollectorEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            case "Auto Poll: Off":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: On"));
               collectorMbox.send(new Msg(id, null, Msg.Type.CollectorEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            case "Turn off alarm":
                appendTextArea("Turning off the alarm");
                collectorMbox.send(new Msg(id, null, Msg.Type.PollAck, ""));
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed

    //------------------------------------------------------------
    // appendTextArea
    protected void appendTextArea(String status) {
        Platform.runLater(() -> collectorTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea

    //-----------------------------------------------------------
    //checkTicketID
    protected boolean checkTicketID(String ticketID){
        if(ticketID.length()!=5){ //check the length of the id to check it's format
            appendTextArea("The id is invalid");
            return false;
        }
        try{ //check the ticket id whether in integer format
            Integer.parseInt(ticketID);
        }catch (Exception e ){
            appendTextArea("The id is not the number. Not valid");
            return false;
        }
        return true;
    }
    //checkTicketID
}
