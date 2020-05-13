package PCS.SensorHandler.Emulator;


import AppKickstarter.misc.Msg;
import PCS.PCSStarter;
import PCS.SensorHandler.SensorHandler;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


//======================================================================
// SensorEmulator
public class SensorEmulator extends SensorHandler {
    private Stage myStage;
    private SensorEmulatorController sensorEmulatorController;
    private final PCSStarter pcsStarter;
    private final String id;
    private boolean autoPoll;
    protected int lvl;
    protected String dir;

    /**
     * Constructor of sensor emulator
     * @param id the name of the AppThread
     * @param pcsStarter a reference of pcsstarter
     * @param level the number of level
     * @param dir the direction sensor
     */
    //------------------------------------------------------------
    // SensorEmulator
    public SensorEmulator(String id, PCSStarter pcsStarter, int level, String dir) {
        super(id, pcsStarter,level,dir);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
        this.lvl=level;
        this.dir=dir;
        this.autoPoll=true;
    }// SensorEmulator

    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "SensorEmulator.fxml";
        loader.setLocation(SensorEmulator.class.getResource(fxmlName));
        root = loader.load();
        sensorEmulatorController = (SensorEmulatorController) loader.getController();
        sensorEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 450, 350));
        myStage.setY(lvl*100);
        myStage.setTitle(id);
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // start

    //------------------------------------------------------------
    // processMsg
    protected final boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
            case CarDetected:
                handleCarDetected(msg);
                break;

            case SensorEmulatorAutoPollToggle:
                autoPoll=handleSensorEmulatorAutoPollToggle();
                break;

            default:
                quit = super.processMsg(msg);
        }
        return quit;
    } // processMsg

    //------------------------------------------------------------
    // handleCarDetected:
    private void handleCarDetected(Msg msg) {
        logFine("A car is detected! Send CarDetected signal to PCS core");
        pcsCore.send(new Msg(id, mbox, Msg.Type.CarDetected, level+dir));
    }// handleCarDetected:


    //------------------------------------------------------------
    // handleSensorEmulatorAutoPollToggle:
    public final boolean handleSensorEmulatorAutoPollToggle() {
        autoPoll = !autoPoll;
        logFine("Auto poll change: " + (autoPoll ? "off --> on" : "on --> off"));
        return autoPoll;
    } // handleSensorEmulatorAutoPollToggle

    //------------------------------------------------------------
    // sendPollReq
    protected void sendPollReq() {
        logFine("Poll request received.  [autoPoll is " + (autoPoll ? "on]" : "off]"));
        if (autoPoll) {
            logFine("Send poll ack.");
            mbox.send(new Msg(id, mbox, Msg.Type.PollAck, ""));
        }
    }    // sendPollReq


    //------------------------------------------------------------
    // logFine
    private final void logFine(String logMsg) {
        sensorEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine


    //------------------------------------------------------------
    // logInfo
    private final void logInfo(String logMsg) {
        sensorEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo


    //------------------------------------------------------------
    // logWarning
    private final void logWarning(String logMsg) {
        sensorEmulatorController.appendTextArea("[WARNING]: " + logMsg);
        log.warning(id + ": " + logMsg);
    } // logWarning


    //------------------------------------------------------------
    // logSevere
    private final void logSevere(String logMsg) {
        sensorEmulatorController.appendTextArea("[SEVERE]: " + logMsg);
        log.severe(id + ": " + logMsg);
    } // logSevere

}
