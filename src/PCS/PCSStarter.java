package PCS;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;

import PCS.DispatcherHandler.DispatcherHandler;
import PCS.PCSCore.PCSCore;
import PCS.GateHandler.GateHandler;

import PCS.PayMachineHandler.PayMachineHandler;
import PCS.SensorHandler.SensorHandler;
import PCS.VacancyDispHandler.VacancyDispHandler;
import PCS.CollectorHandler.CollectorHandler;
import javafx.application.Platform;


//======================================================================
// PCSStarter
public class PCSStarter extends AppKickstarter {
    protected Timer timer;
    protected PCSCore pcsCore;
    protected GateHandler entranceGateHandler;
    protected GateHandler exitGateHandler;
    protected VacancyDispHandler vacancyDispHandler;
    protected DispatcherHandler dispatcherHandler;
    protected CollectorHandler collectorHandler;
    protected PayMachineHandler payMachineHandler_lvl0;
    protected PayMachineHandler payMachineHandler_lvl1;
    protected PayMachineHandler payMachineHandler_lvl2;

    protected SensorHandler sensorHandler_lvl0_up;
    protected SensorHandler sensorHandler_lvl0_dn;
    protected SensorHandler sensorHandler_lvl1_up;
    protected SensorHandler sensorHandler_lvl1_dn;
    protected SensorHandler sensorHandler_lvl2_up;
    protected SensorHandler sensorHandler_lvl2_dn;


    //------------------------------------------------------------
    // main
    public static void main(String[] args) {
        new PCSStarter().startApp();
    } // main


    //------------------------------------------------------------
    // PCSStart
    public PCSStarter() {
        super("PCSStarter", "etc/PCS.cfg");
    } // PCSStart


    //------------------------------------------------------------
    // startApp
    protected void startApp() {
        // start our application
        log.info("");
        log.info("");
        log.info("============================================================");
        log.info(id + ": Application Starting...");

        startHandlers();
    } // startApp


    //------------------------------------------------------------
    // startHandlers
    protected void startHandlers() {
        // create handlers
        try {
            timer = new Timer("timer", this);
            pcsCore = new PCSCore("PCSCore", this);

            entranceGateHandler = new GateHandler("EntranceGateHandler", this);
            exitGateHandler = new GateHandler("ExitGateHandler", this);

            vacancyDispHandler = new VacancyDispHandler("VacancyDispHandler", this);

            collectorHandler = new CollectorHandler("CollectorHandler", this);

            dispatcherHandler = new DispatcherHandler("DispatcherHandler", this);

            payMachineHandler_lvl0= new PayMachineHandler("PayMachineHandler-lvl0",this);
            payMachineHandler_lvl1= new PayMachineHandler("PayMachineHandler-lvl1",this);
            payMachineHandler_lvl2= new PayMachineHandler("PayMachineHandler-lvl2",this);

            sensorHandler_lvl0_up =new SensorHandler("SensorHandler-lvl0 (up)", this,0,"up");
            sensorHandler_lvl0_dn =new SensorHandler("SensorHandler-lvl0 (dn)", this,0,"dn");
            sensorHandler_lvl1_up =new SensorHandler("SensorHandler-lvl1 (up)", this,1,"up");
            sensorHandler_lvl1_dn =new SensorHandler("SensorHandler-lvl1 (dn)", this,1,"dn");
            sensorHandler_lvl2_up =new SensorHandler("SensorHandler-lvl2 (up)", this,2,"up");
            sensorHandler_lvl2_dn =new SensorHandler("SensorHandler-lvl2 (dn)", this,2,"dn");



            //Create all the hardware handlers here!

        } catch (Exception e) {
            System.out.println("AppKickstarter: startApp failed");
            e.printStackTrace();
            Platform.exit();
        }

        // start threads
        new Thread(timer).start();
        new Thread(pcsCore).start();

        new Thread(entranceGateHandler).start();
        new Thread(exitGateHandler).start();

        new Thread(vacancyDispHandler).start();

        new Thread(collectorHandler).start();

        new Thread(dispatcherHandler).start();

        new Thread(payMachineHandler_lvl0).start();
        new Thread(payMachineHandler_lvl1).start();
        new Thread(payMachineHandler_lvl2).start();

        new Thread(sensorHandler_lvl0_up).start();
        new Thread(sensorHandler_lvl0_dn).start();
        new Thread(sensorHandler_lvl1_up).start();
        new Thread(sensorHandler_lvl1_dn).start();
        new Thread(sensorHandler_lvl2_up).start();
        new Thread(sensorHandler_lvl2_dn).start();


        //Start all threads here!
    } // startHandlers


    //------------------------------------------------------------
    // stopApp
    public void stopApp() {
        log.info("");
        log.info("");
        log.info("============================================================");
        log.info(id + ": Application Stopping...");
        pcsCore.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));

        entranceGateHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        exitGateHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));

        timer.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));

        vacancyDispHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));

        dispatcherHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));

        collectorHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));

        payMachineHandler_lvl0.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        payMachineHandler_lvl1.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        payMachineHandler_lvl2.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));

        sensorHandler_lvl0_up.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        sensorHandler_lvl0_dn.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        sensorHandler_lvl1_up.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        sensorHandler_lvl1_dn.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        sensorHandler_lvl2_up.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        sensorHandler_lvl2_dn.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
    } // stopApp
} // PCS.PCSStarter
