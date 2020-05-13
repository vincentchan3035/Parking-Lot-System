package PCS.PCSCore.PCSCoreEmulator;

import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;
import PCS.PCSCore.PCSCore;
import PCS.PCSStarter;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.time.LocalDateTime;

//------------------------------------------------------------
// PCSCoreEmulator
public class PCSCoreEmulator extends PCSCore {
    protected Stage myStage;
    private final PCSStarter pcsStarter;
    private PCSCoreEmulatorController pcsCoreEmulatorController;
    private boolean autoPoll;
    private final String id;

    /**
     * Constructor of PCS Core emulator
     * @param id the name of the AppThread
     * @param pcsStarter a reference of pcsstarter
     */

    //------------------------------------------------------------
    // PCSCoreEmulator
    public PCSCoreEmulator(String id, PCSStarter pcsStarter) throws Exception {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id+"Emulator";
        autoPoll=true;
    } // PCSCoreEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "PCSCoreEmulator.fxml";
        loader.setLocation(PCSCoreEmulator.class.getResource(fxmlName));
        root = loader.load();
        pcsCoreEmulatorController = (PCSCoreEmulatorController) loader.getController();
        pcsCoreEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 500, 350));
        myStage.setX(0);
        myStage.setY(700);
        myStage.setTitle("PCSCore Emulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // start

    //------------------------------------------------------------
    // processMsg
    protected boolean processMsg(Msg msg) {
        boolean quit=false;
        switch (msg.getType()) {
            case TimesUp:
                handleTimesUp(msg);
                break;
            case TicketRequest:
                logInfo(id + ": Ticket Request received.");

                enteringTimeArray[ticketNum]=enteringTime(LocalDateTime.now());
                logInfo("Time is added to array[" + ticketNum + "]");
                dispatcherMBox.send(new Msg(id, mbox, Msg.Type.TicketRequestReply, String.format("Ticket No: %05d, Entering Time: %s", ticketNum,enteringTimeArray[ticketNum]))); // return ticket information
                ticketNum++;

                break;
            case TicketRemoved:
                logInfo(id + ": Ticket Removed.");
                entranceGateMBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, "Ticket Removed"));  // Call the entrance gate to open
                Timer.setTimer(id, mbox, openCloseGateTime, OpenGateTimerID);

            case GateOpenReply:
                logInfo(id + ": Gate is opened.");
                gateIsClosed = false;
                break;

            case GateCloseReply:
                logInfo(id + ": Gate is closed.");
                gateIsClosed = true;
                break;

            case CarDetected:
                logInfo(id + ": Car detected signal received.");
                handleCarDetected(msg);
                break;

            case PayRequest:
                logInfo(id + ": receive the pay request.");
                String fee = handlePayRequest(msg);
                sendMessage(msg,fee);
                break;
            case SuccessfullyPaid:
                logInfo(id + ": Paid signal received. Ticket ID is "+msg.getDetails());
                int TicketID=Integer.parseInt(msg.getDetails());
                paymentCheck[TicketID]=true;
                logInfo(id+": "+paymentCheck[TicketID]);
                break;

            case TicketInsertRequest:
                log.info(id + ": Ticket is inserted. I need to check is ticketID["+msg.getDetails()+"] valid");
                int ticketID = Integer.parseInt(msg.getDetails());
                if(paymentCheck[ticketID]) {
                    //send msg to gate to call gate open (if ticket valid)
                    log.info(id + ": Ticket is valid!");
                    exitGateMBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, " Send open request to exitGate"));
                    Timer.setTimer(id, mbox, openCloseGateTime, CloseGateTimerID);
                    collectorMBox.send(new Msg(id, mbox, Msg.Type.ValidInsertRequest, "Ticket is valid")); //if ticket valid
                    break;
                }else {
                    log.info(id + ": Ticket is not valid!");
                    collectorMBox.send(new Msg(id, mbox, Msg.Type.InvalidInsertRequest, "Ticket is not valid")); //not valid case
                    break;
                }
            case PollAck:
                logInfo("PollAck from " + msg.getDetails());
                break;

            case Terminate:
                quit = true;
                break;

            default:
                logWarning(id + ": unknown message type: [" + msg + "]");
        }

        return quit;

    }// processMsg

    //------------------------------------------------------------
    // handleTimesUp
    protected void handleTimesUp(Msg msg) {
        logInfo("------------------------------------------------------------");
        switch (Timer.getTimesUpMsgTimerId(msg)) {
            case PollTimerID:
                logInfo("Poll: " + msg.getDetails());

                vacancyDispMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                dispatcherMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                entranceGateMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                exitGateMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                payMachineMBox_lvl0.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                payMachineMBox_lvl1.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                payMachineMBox_lvl2.send(new Msg(id, mbox, Msg.Type.Poll, ""));

                sensor0up_MBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                sensor0dn_MBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                sensor1up_MBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                sensor1dn_MBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                sensor2up_MBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                sensor2dn_MBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));

                Timer.setTimer(id, mbox, pollTime, PollTimerID);
                break;

            case OpenGateTimerID:
                logInfo("Timer up, close the gate: "+msg.getDetails());
                entranceGateMBox.send(new Msg(id, mbox, Msg.Type.GateCloseRequest, "Please close the gate"));

            case CloseGateTimerID:
                log.info("Timer up, close the gate: "+msg.getDetails());
                exitGateMBox.send(new Msg(id, mbox, Msg.Type.GateCloseRequest, "Please close the gate"));
            default:
                logSevere(id + ": why am I receiving a timeout with timer id " + Timer.getTimesUpMsgTimerId(msg));
                break;
        }
    } // handleTimesUp

    //------------------------------------------------------------
    //send payResponse to payMachine

    /**
     * send the pay message to the corresponding emulator
     * @param msg the information regrading payment (Ticket id, entrance time, the emulator id)
     * @param fee the fee of payment
     */
    protected void sendMessage(Msg msg,String fee){
        String payMachineInfo[]=msg.getDetails().split("I");

        switch (payMachineInfo[2]){
            case "PayMachineHandler-lvl0Emulator":
                payMachineMBox_lvl0.send(new Msg(id, mbox, Msg.Type.PayReply, fee));
                break;
            case "PayMachineHandler-lvl1Emulator":
                payMachineMBox_lvl1.send(new Msg(id, mbox, Msg.Type.PayReply, fee));
                break;
            case "PayMachineHandler-lvl2Emulator":
                payMachineMBox_lvl2.send(new Msg(id, mbox, Msg.Type.PayReply, fee));
        }
    }

    //------------------------------------------------------------
    // sendVacancyUpdateRequest
    protected void sendVacancyUpdateRequest() {
        logInfo(": Send vacancy update signal to Vacancy Display");
        vacancyDispMBox.send(new Msg(id, mbox, Msg.Type.DisplayUpdateRequest, num_Level + "-" + level00 + "-" + level01 + "-" + level02+"-"+level03));
    }// sendVacancyUpdateRequest


    //------------------------------------------------------------
    // logFine
    private final void logFine(String logMsg) {
        pcsCoreEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine

    //------------------------------------------------------------
    // logWarning
    private final void logWarning(String logMsg) {
        pcsCoreEmulatorController.appendTextArea("[WARNING]: " + logMsg);
        log.warning(id + ": " + logMsg);
    } // logWarning

    //------------------------------------------------------------
    // logInfo
    private final void logInfo(String logMsg) {
        pcsCoreEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo

    //------------------------------------------------------------
    // logSevere
    private final void logSevere(String logMsg) {
        pcsCoreEmulatorController.appendTextArea("[SEVERE]: " + logMsg);
        log.severe(id + ": " + logMsg);
    } // logSevere



}
