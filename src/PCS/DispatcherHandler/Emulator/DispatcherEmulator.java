package PCS.DispatcherHandler.Emulator;

import AppKickstarter.misc.Msg;
import PCS.DispatcherHandler.DispatcherHandler;
import PCS.PCSStarter;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class DispatcherEmulator extends DispatcherHandler {
    private Stage myStage;
    private DispatcherEmulatorController dispatcherEmulatorController;
    private final PCSStarter pcsStarter;
    private final String id;
    private boolean autoPoll;
    private String entranceInfo;


    /**
     * Constructor of dispatcher emulator
     * @param id the name of the AppThread
     * @param pcsStarter a reference of pcsstarter
     */
    //----------------------------------------
    // DispatcherEmulator
    public DispatcherEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
        this.autoPoll = true;
        this.entranceInfo = super.getEntranceInfo();
    }

    /**
     * Start the emulator
     * @throws Exception any Exception
     */
    //----------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "DispatcherEmulator.fxml";
        loader.setLocation(DispatcherEmulator.class.getResource(fxmlName));
        root = loader.load();
        dispatcherEmulatorController = (DispatcherEmulatorController) loader.getController();
        dispatcherEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 500, 470));
        myStage.setX(600);
        myStage.setTitle("Ticket Dispatcher Emulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }

    /**
     * Handle the Message Process
     * @param msg the message received from different mbox
     * @return the boolean variable, true or false
     */
    //-----------------------------------------
    // processMsg
    public final boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
            case TicketRequestReply:
                handleTicketRequestReply(msg);
                break;
            case TimesUp:
                handleTimesUp(msg);
                break;
            case DispatcherEmulatorAutoPollToggle:
                handleDispatcherEmulatorAutoPollToggle();
                break;
            default:
                quit = super.processMsg(msg);
        }
        return quit;
    }

    /**
     * Handle the poll request
     */
    //------------------------------------------------------------
    // sendPollReq
    @Override
    protected void sendPollReq() {
        logFine("Poll request received.  [autoPoll is " + (autoPoll ? "on]" : "off]"));
        if (autoPoll) {
            logFine("Send poll ack.");
            mbox.send(new Msg(id, mbox, Msg.Type.PollAck, ""));
        }
    } // sendPollReq

    //------------------------------------------------------------
    // handleTimesUp
    public final void handleTimesUp(Msg msg) {
        logFine("Times up received.");
    } // handleTimesUp

    /**
     * Handle the ticket request reply
     * @param msg the entrance information return from PCSCore
     */
    //------------------------------------------------------------
    // handleTicketRequestReply
    protected void handleTicketRequestReply(Msg msg){
        printEntranceInfo(msg);
    } // handleTicketRequestReply

    /**
     * Show the entrance information on UI
     * @param msg the entrance information
     */
    //------------------------------------------------------------
    // printEntranceInfo
    protected final void printEntranceInfo(Msg msg){
        logFine(msg.getDetails());
        dispatcherEmulatorController.setDisplayTicketInfo(msg.getDetails());
    } // printEntranceInfo

    /**
     * Handle the auto poll
     * @return the updated boolean
     */
    //------------------------------------------------------------
    // handleDispatcherEmulatorAutoRequestToggle
    private final boolean handleDispatcherEmulatorAutoPollToggle() {
        autoPoll = !autoPoll;
        logFine("Auto poll change: " + (autoPoll ? "off --> on" : "on --> off"));
        return autoPoll;
    } // handleGateEmulatorAutoPollToggle


    //------------------------------------------------------------
    // logFine
    private final void logFine(String logMsg){
        dispatcherEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.info(id + ": " + logMsg);
    }// logFine

    //------------------------------------------------------------
    // logInfo
    private final void logInfo(String logMsg){
        dispatcherEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    }// logInfo

    //------------------------------------------------------------
    // logWarning
    private final void logWarning(String logMsg){
        dispatcherEmulatorController.appendTextArea("[WARNING]: " + logMsg);
        log.info(id + ": " + logMsg);
    }// logWarning

    //------------------------------------------------------------
    // logSevere
    private final void logSevere(String logMsg){
        dispatcherEmulatorController.appendTextArea("[SEVERE]: " + logMsg);
        log.info(id + ": " + logMsg);
    }// logSevere
}
