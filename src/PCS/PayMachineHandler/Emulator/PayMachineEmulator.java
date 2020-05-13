package PCS.PayMachineHandler.Emulator;

import AppKickstarter.misc.Msg;
import PCS.PCSStarter;
import PCS.PayMachineHandler.PayMachineHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

//------------------------------------------------------------
// PayMachineEmulator
    public class PayMachineEmulator extends PayMachineHandler{
        private Stage myStage;
        private PayMachineEmulatorController payMachineEmulatorController;
        private final PCSStarter pcsStarter;
        private boolean autoPoll;
        private final String id;
        protected final int level;

    //------------------------------------------------------------
    // PayMachineEmulator
    /**
     * Constructor of payMachine emulator
     * @param id the name of the AppThread
     * @param pcsStarter a reference of pcsstarter
     * @param level the floor of the Emulator
     */
    public PayMachineEmulator(String id, PCSStarter pcsStarter,int level) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
        this.level=level;
        autoPoll=true;
    } // PayMachineEmulator


    //------------------------------------------------------------
    // start

    /**
     * Start the emulator
     * @throws Exception any Exception
     */
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "PayMachineEmulator.fxml";
        loader.setLocation(PayMachineEmulator.class.getResource(fxmlName));
        root = loader.load();
        payMachineEmulatorController = (PayMachineEmulatorController) loader.getController();
        payMachineEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 520));
        myStage.setX(1300+level*100);
        myStage.setY(400+level*100);
        myStage.setTitle("PayMachine Emulator-lvl "+level);
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // start

    //------------------------------------------------------------
    // processMsg

    /**
     * Handle the Message Process
     * @param msg the message received from different mbox
     * @return the boolean variable, true or false
     */
    protected final boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
            case DisplayFee:
                handleDisplayFee(msg);
                break;

            case PayMachineEmulatorAutoPollToggle:
                handlePayMachineEmulatorAutoPollToggle();
                break;

            default:
                quit = super.processMsg(msg);
        }
        return quit;
    } // processMsg




    //------------------------------------------------------------
    // handlePayMachineEmulatorAutoPollToggle

    /**
     *  Handle the auto poll
     * @return the updated boolean
     */
    protected boolean handlePayMachineEmulatorAutoPollToggle(){
        autoPoll = !autoPoll;
        logFine("Auto poll change: " + (autoPoll ? "off --> on" : "on --> off"));
        return autoPoll;
    }// handlePayMachineEmulatorAutoPollToggle

    //------------------------------------------------------------
    // handleDisplayFee

    /**
     * handle the display fee message
     * @param msg fee of the payment
     */
    protected void handleDisplayFee(Msg msg){
        String [] feeInfo =msg.getDetails().split("I");

        payMachineEmulatorController.displayFee(feeInfo[0]);

        logFine("The fee is "+msg.getDetails()+". Please pay by Octopus.");
    }// handleDisplayFee

    //------------------------------------------------------------
    // handleSuccessfullyPaidReply

    /**
     * handle the successfully paid reply
     * @param msg the information of the payment (fee, exit time)
     */
    protected void handleSuccessfullyPaidReply(Msg msg) {
        logFine("Exit information received! Printing on the ticket.");
        printTicketInfo(msg.getDetails());
    }// handleSuccessfullyPaidReply

    /**
     * Show the exit information
     * @param exitInfo exit fee and time
     */
    protected void printTicketInfo(String exitInfo){
        String exitInfos[]=exitInfo.split("-");
        logFine("The parking amount is "+exitInfos[0]+", exit time is "+exitInfos[1]);
    }

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
    // logFine
    private final void logFine(String logMsg) {
        payMachineEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine


    //------------------------------------------------------------
    // logInfo
    private final void logInfo(String logMsg) {
        payMachineEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo


    //------------------------------------------------------------
    // logWarning
    private final void logWarning(String logMsg) {
        payMachineEmulatorController.appendTextArea("[WARNING]: " + logMsg);
        log.warning(id + ": " + logMsg);
    } // logWarning


    //------------------------------------------------------------
    // logSevere
    private final void logSevere(String logMsg) {
        payMachineEmulatorController.appendTextArea("[SEVERE]: " + logMsg);
        log.severe(id + ": " + logMsg);
    } // logSevere

}
