package PCS.VacancyDispHandler.Emulator;

import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;


import PCS.PCSStarter;
import PCS.VacancyDispHandler.VacancyDispHandler;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


//======================================================================
// VacancyDispEmulator
public class VacancyDispEmulator extends VacancyDispHandler{
    private Stage myStage;
    private VacancyDipsEmulatorController vacancyDipsEmulatorController;
    private final PCSStarter pcsStarter;
    private final String id;

    private boolean autoPoll;

    /**
     * Constructor of vacancy display emulator
     * @param id the name of the AppThread
     * @param pcsStarter a reference of pcsstarter
     */

    //------------------------------------------------------------
    // VacancyDispEmulator
    public VacancyDispEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";

        this.autoPoll = true;

    }

    /**
     * Start the thread
     * @throws Exception handle exception for java fx
     */
    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "VacancyDispEmulator.fxml";
        loader.setLocation(VacancyDispEmulator.class.getResource(fxmlName));
        root = loader.load();
        vacancyDipsEmulatorController = (VacancyDipsEmulatorController) loader.getController();
        vacancyDipsEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 450, 300));
        myStage.setX(1300);
        myStage.setTitle("Vacancy display Emulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // start

    /**
     * Process message from different mbox
     * @param msg the message received from different mbox
     * @return boolean variable
     */

    //------------------------------------------------------------
    // processMsg
    protected final boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {

            case VacancyDispEmulatorAutoPollToggle:
                autoPoll=handleVacancyDispEmulatorAutoPollToggle();
                break;

            default:
                quit = super.processMsg(msg);
        }
        return quit;
    } // processMsg

    /**
     * Handle Display update request
     * @param msg the message received from different mbox
     */

    protected void handleDisplayUpdateRequest(Msg msg){
        String details[]=msg.getDetails().split("-");
        int num_Level=Integer.parseInt(details[0]);
        for(int i = 0; i<num_Level; i++){
            logFine("       >>>>>>>> Level 0"+i+": "+details[i+1]);
        }
        mbox.send(new Msg(id,mbox,Msg.Type.DisplayUpdateReply,""));
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
    }


    //------------------------------------------------------------
    // handleVacancyDispEmulatorAutoPollToggle
    private final boolean handleVacancyDispEmulatorAutoPollToggle() {
        autoPoll=!autoPoll;
        logFine("Auto Poll Change: "+(autoPoll ? "off --> on" : "on --> off"));
        return autoPoll;
    }



    //------------------------------------------------------------
    // logFine
    private final void logFine(String logMsg) {
        vacancyDipsEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine

    //------------------------------------------------------------
    // logInfo
    private final void logInfo(String logMsg) {
        vacancyDipsEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo


    //------------------------------------------------------------
    // logWarning
    private final void logWarning(String logMsg) {
        vacancyDipsEmulatorController.appendTextArea("[WARNING]: " + logMsg);
        log.warning(id + ": " + logMsg);
    } // logWarning


    //------------------------------------------------------------
    // logSevere
    private final void logSevere(String logMsg) {
        vacancyDipsEmulatorController.appendTextArea("[SEVERE]: " + logMsg);
        log.severe(id + ": " + logMsg);
    } // logSevere



}


