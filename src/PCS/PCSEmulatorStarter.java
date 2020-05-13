package PCS;

import AppKickstarter.timer.Timer;
import PCS.CollectorHandler.CollectorHandler;
import PCS.CollectorHandler.Emulator.CollectorEmulator;
import PCS.DispatcherHandler.DispatcherHandler;
import PCS.DispatcherHandler.Emulator.DispatcherEmulator;
import PCS.GateHandler.Emulator.GateEmulator;
import PCS.GateHandler.GateHandler;
import PCS.PCSCore.PCSCore;
import PCS.PCSCore.PCSCoreEmulator.PCSCoreEmulator;
import PCS.PayMachineHandler.Emulator.PayMachineEmulator;
import PCS.PayMachineHandler.PayMachineHandler;
import PCS.SensorHandler.Emulator.SensorEmulator;
import PCS.SensorHandler.SensorHandler;
import PCS.VacancyDispHandler.Emulator.VacancyDispEmulator;
import PCS.VacancyDispHandler.VacancyDispHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

//======================================================================
// PCSEmulatorStarter
public class PCSEmulatorStarter extends PCSStarter {
    //------------------------------------------------------------
    // main
    public static void main(String[] args) {
        new PCSEmulatorStarter().startApp();
    } // main


    //------------------------------------------------------------
    // startHandlers
    @Override
    protected void startHandlers() {
        Emulators.pcsEmulatorStarter = this;
        new Emulators().start();
    } // startHandlers


    //------------------------------------------------------------
    // Emulators
    public static class Emulators extends Application {
        private static PCSEmulatorStarter pcsEmulatorStarter;

        //----------------------------------------
        // start
        public void start() {
            launch();
        } // start

        //----------------------------------------
        // start
        public void start(Stage primaryStage) {
            Timer timer = null;
            PCSCoreEmulator pcsCoreEmulator=null;

            VacancyDispEmulator vacancyDispEmulator = null;
            DispatcherEmulator dispatcherEmulator = null;
            GateEmulator entranceGateEmulator = null;
            GateEmulator exitGateEmulator = null;
            PayMachineEmulator payMachineEmulator_lvl0=null;
            PayMachineEmulator payMachineEmulator_lvl1=null;
            PayMachineEmulator payMachineEmulator_lvl2=null;
            CollectorEmulator collectorEmulator = null;

            SensorEmulator sensorEmulator_lvl0_up = null;
            SensorEmulator sensorEmulator_lvl0_dn = null;
            SensorEmulator sensorEmulator_lvl1_up = null;
            SensorEmulator sensorEmulator_lvl1_dn = null;
            SensorEmulator sensorEmulator_lvl2_up = null;
            SensorEmulator sensorEmulator_lvl2_dn = null;

            // create emulators
            try {
                timer = new Timer("timer", pcsEmulatorStarter);
                pcsCoreEmulator = new PCSCoreEmulator("PCSCore", pcsEmulatorStarter);

                vacancyDispEmulator = new VacancyDispEmulator("VacancyDispHandler", pcsEmulatorStarter);
                dispatcherEmulator = new DispatcherEmulator("DispatcherHandler", pcsEmulatorStarter);
                entranceGateEmulator = new GateEmulator("EntranceGateHandler", pcsEmulatorStarter,"Entrance");
                exitGateEmulator = new GateEmulator("ExitGateHandler", pcsEmulatorStarter,"Exit");

                payMachineEmulator_lvl0=new PayMachineEmulator("PayMachineHandler-lvl0",pcsEmulatorStarter,0);
                payMachineEmulator_lvl1=new PayMachineEmulator("PayMachineHandler-lvl1",pcsEmulatorStarter,1);
                payMachineEmulator_lvl2=new PayMachineEmulator("PayMachineHandler-lvl2",pcsEmulatorStarter,2);

                collectorEmulator = new CollectorEmulator("CollectorHandler", pcsEmulatorStarter);

                sensorEmulator_lvl0_up=new SensorEmulator("SensorHandler-lvl0 (up)",pcsEmulatorStarter,0,"up");
                sensorEmulator_lvl0_dn=new SensorEmulator("SensorHandler-lvl0 (dn)",pcsEmulatorStarter,0,"dn");
                sensorEmulator_lvl1_up=new SensorEmulator("SensorHandler-lvl1 (up)",pcsEmulatorStarter,1,"up");
                sensorEmulator_lvl1_dn=new SensorEmulator("SensorHandler-lvl1 (dn)",pcsEmulatorStarter,1,"dn");
                sensorEmulator_lvl2_up=new SensorEmulator("SensorHandler-lvl2 (up)",pcsEmulatorStarter,2,"up");
                sensorEmulator_lvl2_dn=new SensorEmulator("SensorHandler-lvl2 (dn)",pcsEmulatorStarter,2,"dn");

                // start emulator GUIs
                pcsCoreEmulator.start();
                entranceGateEmulator.start();
                exitGateEmulator.start();
                vacancyDispEmulator.start();
                dispatcherEmulator.start();
                collectorEmulator.start();

                payMachineEmulator_lvl0.start();
                payMachineEmulator_lvl1.start();
                payMachineEmulator_lvl2.start();


                sensorEmulator_lvl0_up.start();
                sensorEmulator_lvl0_dn.start();
                sensorEmulator_lvl1_up.start();
                sensorEmulator_lvl1_dn.start();
                sensorEmulator_lvl2_up.start();
                sensorEmulator_lvl2_dn.start();

            } catch (Exception e) {
                System.out.println("Emulators: start failed");
                e.printStackTrace();
                Platform.exit();
            }

            pcsEmulatorStarter.setTimer(timer);
            pcsEmulatorStarter.setPCSCore(pcsCoreEmulator);

            pcsEmulatorStarter.setEntranceGateHandler(entranceGateEmulator);
            pcsEmulatorStarter.setExitGateHandler(exitGateEmulator);

            pcsEmulatorStarter.setVacancyDispHandler(vacancyDispEmulator);
            pcsEmulatorStarter.setDispatcherHandler(dispatcherEmulator);

            pcsEmulatorStarter.setCollectorHandler(collectorEmulator);

            pcsEmulatorStarter.setPayMachineHandler(payMachineEmulator_lvl0,0);
            pcsEmulatorStarter.setPayMachineHandler(payMachineEmulator_lvl1,1);
            pcsEmulatorStarter.setPayMachineHandler(payMachineEmulator_lvl2,2);

            pcsEmulatorStarter.setSensorHandler(sensorEmulator_lvl0_up, "0up");
            pcsEmulatorStarter.setSensorHandler(sensorEmulator_lvl0_dn, "0dn");
            pcsEmulatorStarter.setSensorHandler(sensorEmulator_lvl1_up, "1up");
            pcsEmulatorStarter.setSensorHandler(sensorEmulator_lvl1_dn, "1dn");
            pcsEmulatorStarter.setSensorHandler(sensorEmulator_lvl2_up, "2up");
            pcsEmulatorStarter.setSensorHandler(sensorEmulator_lvl2_dn, "2dn");


            // start threads
            new Thread(timer).start();
            new Thread(pcsCoreEmulator).start();

            new Thread(entranceGateEmulator).start();
            new Thread(exitGateEmulator).start();

            new Thread(vacancyDispEmulator).start();

            new Thread(dispatcherEmulator).start();

            new Thread(collectorEmulator).start();

            new Thread(payMachineEmulator_lvl0).start();
            new Thread(payMachineEmulator_lvl1).start();
            new Thread(payMachineEmulator_lvl2).start();

            new Thread(sensorEmulator_lvl0_up).start();
            new Thread(sensorEmulator_lvl0_dn).start();
            new Thread(sensorEmulator_lvl1_up).start();
            new Thread(sensorEmulator_lvl1_dn).start();
            new Thread(sensorEmulator_lvl2_up).start();
            new Thread(sensorEmulator_lvl2_dn).start();

        } // start

    } // Emulators


    //------------------------------------------------------------
    //  setters
    private void setTimer(Timer timer) {
        this.timer = timer;
    }

    private void setPCSCore(PCSCore pcsCore) {
        this.pcsCore = pcsCore;
    }

    private void setEntranceGateHandler(GateHandler entranceGateHandler){
        this.entranceGateHandler = entranceGateHandler;
    }

    private void setExitGateHandler(GateHandler exitGateHandler){
        this.exitGateHandler = exitGateHandler;
    }

    private void setVacancyDispHandler(VacancyDispHandler vacancyDispHandler) {
        this.vacancyDispHandler = vacancyDispHandler;
    }

    private void setDispatcherHandler(DispatcherHandler dispatcherHandler) {
        this.dispatcherHandler = dispatcherHandler;
    }

    private void setCollectorHandler(CollectorHandler collectorHandler) {
        this.collectorHandler = collectorHandler;
    }

    private void setPayMachineHandler(PayMachineHandler payMachineHandler, int level){
        switch (level) {
            case 0:
                this.payMachineHandler_lvl0 = payMachineHandler;
                break;
            case 1:
                this.payMachineHandler_lvl1 = payMachineHandler;
                break;
            case 2:
                this.payMachineHandler_lvl2 = payMachineHandler;
        }
    }

    private void setSensorHandler(SensorHandler sensorHandler, String lvlAndDir) {
        switch (lvlAndDir) {
            case "0up":
                this.sensorHandler_lvl0_up = sensorHandler;
                break;
            case "0dn":
                this.sensorHandler_lvl0_dn = sensorHandler;
                break;
            case "1up":
                this.sensorHandler_lvl1_up = sensorHandler;
                break;
            case "1dn":
                this.sensorHandler_lvl1_dn = sensorHandler;
                break;
            case "2up":
                this.sensorHandler_lvl2_up = sensorHandler;
                break;
            case "2dn":
                this.sensorHandler_lvl2_dn = sensorHandler;
                break;
        }

    }
} // PCSEmulatorStarter
