package PCS.PayMachineHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;


//------------------------------------------------------------
// PayMachineEmulatorController
public class PayMachineEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private PayMachineEmulator payMachineEmulator;
    private Logger log;
    private MBox payMachineMbox;
    public TextArea payMachineTextArea;
    private int lineNo = 0;
    public Button autoPollButton;
    public Label feeLabel;
    public TextField TicketID;
    public TextField EnteringTime;


    //------------------------------------------------------------
    // initialize
    /**
     * The initialized setting of the payMachine emulator
     * @param id the name of the emulator
     * @param appKickstarter a reference to our AppKickStarter
     * @param log the logger for this emulator
     * @param payMachineEmulator the payMachine emulator
     */
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, PayMachineEmulator payMachineEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.payMachineEmulator = payMachineEmulator;
        try {
            this.payMachineMbox = appKickstarter.getThread("PayMachineHandler-lvl" + payMachineEmulator.level).getMBox();
        }catch (Exception e){
            System.out.println("PayMachineHandler-lvl" + payMachineEmulator.level);
        }
        this.feeLabel.setText("0");
        this.TicketID.setPromptText("Enter your Ticket ID.");
        this.EnteringTime.setPromptText("Enter your entering time.");



    } // initialize

    //------------------------------------------------------------
    // buttonPressed
    /**
     * The action after the button is pressed
     * @param actionEvent the action from device
     */
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {

            case "Input Ticket":
                appendTextArea("Ticket inputted.");

                if(checkTicketID(TicketID.getText())==false){
                    break;
                }; // check the TicketID is the correct format

                if(checkEnteringTime(EnteringTime.getText())==false){
                    break;
                };// check the enter time is in "yyyy-MM-dd HH:mm:ss" format

                if(TicketID.getText()=="" || EnteringTime.getText()==""){                       //check empty input
                    appendTextArea("Invalid information, please input again");
                }


            else
                payMachineMbox.send(new Msg(id, null, Msg.Type.PayRequest, TicketID.getText()+"I"+EnteringTime.getText()+"I"+this.id));
                appendTextArea("Send pay request to pay machine handler.");
                break;

            case "Pay the fee":
                appendTextArea("The driver has successfully paid.");
                payMachineMbox.send(new Msg(id, null, Msg.Type.SuccessfullyPaid, TicketID.getText()));
                displayFee("0");
                TicketID.setText("");
                EnteringTime.setText("");
                break;

            case "Poll Request":
                appendTextArea("Send poll request.");
                payMachineMbox.send(new Msg(id, null, Msg.Type.Poll, ""));
                break;

            case "Poll ACK":
                appendTextArea("Send poll ack.");
                payMachineMbox.send(new Msg(id, null, Msg.Type.PollAck, ""));
                break;

            case "Auto Poll: On":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: Off"));
                payMachineMbox.send(new Msg(id, null, Msg.Type.PayMachineEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            case "Auto Poll: Off":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: On"));
                payMachineMbox.send(new Msg(id, null, Msg.Type.PayMachineEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed

    //------------------------------------------------------------
    // appendTextArea
    /**
     * Show the status on the payMachine emulator text area
     * @param status the emulator status
     */
    protected void appendTextArea(String status) {
        Platform.runLater(() -> payMachineTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea

    //-----------------------------------------------------------
    //checkTicketID

    /**
     * Check the input ticket id is possible
     * @param ticketID ticket id
     * @return boolean value true or false
     */
    protected boolean checkTicketID(String ticketID){

        if(ticketID.length()!=5){ //check the length of the id to check it's format
            appendTextArea("The id is invalid");
            return false;
        }
        try{ //check the Id is in number format
            Integer.parseInt(ticketID);
        }catch (Exception e ){
            appendTextArea("The id is not the number. Not valid");
            return false;
        }
        return true;
    }//checkTicketID
    //-----------------------------------------------------------
    //checkEnteringTime

    /**
     * check the input entering time is in the yyyy-MM-dd HH:mm:ss format
     * @param enteringTime the input entering time
     * @return boolean value true or false
     */
    protected boolean checkEnteringTime(String enteringTime){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try{
            LocalDateTime inputtedDateTime = LocalDateTime.parse(enteringTime, format);

        }catch (Exception e){
            appendTextArea("The entrance time is invalid.");
            return false;
        }
        return true;

    }
    /**
     * Show the fee on the emulator
     * @param fee the require fee
     */
    protected void displayFee(String fee){
        Platform.runLater(() ->feeLabel.setText(fee));
    }


}
