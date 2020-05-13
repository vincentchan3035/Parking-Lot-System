package PCS.PCSCore;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

//======================================================================
// PCSCore
public class PCSCore extends AppThread {
    protected MBox entranceGateMBox;
    protected MBox exitGateMBox;
    protected MBox vacancyDispMBox;
    protected MBox dispatcherMBox;
    protected MBox collectorMBox;
    protected MBox payMachineMBox_lvl0;
    protected MBox payMachineMBox_lvl1;
    protected MBox payMachineMBox_lvl2;

    protected MBox sensor0up_MBox;
    protected MBox sensor0dn_MBox;
    protected MBox sensor1up_MBox;
    protected MBox sensor1dn_MBox;
    protected MBox sensor2up_MBox;
    protected MBox sensor2dn_MBox;

    protected final int pollTime;
    protected final int PollTimerID = 1;
    protected int num_Level;
    protected int level00;
    protected int level01;
    protected int level02;
    protected int level03;
    protected final int openCloseGateTime;        // for demo only!!!
    protected final int OpenGateTimerID = 2;        // for demo only!!!
    protected final int CloseGateTimerID = 3;
    protected boolean gateIsClosed = true;        // for demo only!!!
    protected int ticketNum = 0;
    protected String[] enteringTimeArray;
    protected boolean[] paymentCheck;

    //------------------------------------------------------------
    // PCSCore

    /**
     * The constructor of PCSCore
     * @param id the name of the appThread
     * @param appKickstarter a reference to our AppKickstarter
     * @throws Exception any Exception
     */
    public PCSCore(String id, AppKickstarter appKickstarter) throws Exception {
        super(id, appKickstarter);
        num_Level = 4;
        level00 = Integer.parseInt(appKickstarter.getProperty("ParkingCfg.AvailableSpaces.level00"));
        level01 = Integer.parseInt(appKickstarter.getProperty("ParkingCfg.AvailableSpaces.level01"));
        level02 = Integer.parseInt(appKickstarter.getProperty("ParkingCfg.AvailableSpaces.level02"));
        level03 = Integer.parseInt(appKickstarter.getProperty("ParkingCfg.AvailableSpaces.level03"));
        this.pollTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.PollTime"));
        enteringTimeArray= new String[100];
        paymentCheck= new boolean[100];
        Arrays.fill(enteringTimeArray, "");
        Arrays.fill(paymentCheck, false);
        openCloseGateTime=Integer.parseInt(appKickstarter.getProperty("PCSCore.OpenCloseGateTime"));

    } // PCSCore

    /**
     *  Run the Thread
     */
    //------------------------------------------------------------
    // run
    public void run() {
        Thread.currentThread().setName(id);

        Timer.setTimer(id, mbox, pollTime, PollTimerID);

        log.info(id + ": starting...");

        entranceGateMBox = appKickstarter.getThread("EntranceGateHandler").getMBox();
        exitGateMBox = appKickstarter.getThread("ExitGateHandler").getMBox();
        vacancyDispMBox = appKickstarter.getThread("VacancyDispHandler").getMBox();
        dispatcherMBox = appKickstarter.getThread("DispatcherHandler").getMBox();
        collectorMBox = appKickstarter.getThread("CollectorHandler").getMBox();

        payMachineMBox_lvl0 = appKickstarter.getThread("PayMachineHandler-lvl0").getMBox();
        payMachineMBox_lvl1 = appKickstarter.getThread("PayMachineHandler-lvl1").getMBox();
        payMachineMBox_lvl2 = appKickstarter.getThread("PayMachineHandler-lvl2").getMBox();

      sensor0up_MBox = appKickstarter.getThread("SensorHandler-lvl0 (up)").getMBox();
        sensor0dn_MBox = appKickstarter.getThread("SensorHandler-lvl0 (dn)").getMBox();
       sensor1up_MBox = appKickstarter.getThread("SensorHandler-lvl1 (up)").getMBox();
      sensor1dn_MBox = appKickstarter.getThread("SensorHandler-lvl1 (dn)").getMBox();
      sensor2up_MBox = appKickstarter.getThread("SensorHandler-lvl2 (up)").getMBox();
      sensor2dn_MBox = appKickstarter.getThread("SensorHandler-lvl2 (dn)").getMBox();


        sendVacancyUpdateRequest();
        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            quit=processMsg(msg);

        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run

    /**
     * handle the Message Process
     * @param msg the message received from different mbox
     * @return the boolean variable, true or false
     */
    protected boolean processMsg(Msg msg){
        boolean quit=false;
        switch (msg.getType()) {
            case TimesUp:
                handleTimesUp(msg);
                break;
            case TicketRequest:
                log.info(id + ": Ticket Request received.");

                enteringTimeArray[ticketNum]=enteringTime(LocalDateTime.now());
                log.info("Time is added to array[" + ticketNum + "]");
                dispatcherMBox.send(new Msg(id, mbox, Msg.Type.TicketRequestReply, String.format("Ticket No: %05d, Entering Time: %s", ticketNum,enteringTimeArray[ticketNum]))); // return ticket information
                ticketNum++;

                break;
            case TicketRemoved:
                log.info(id + ": Ticket Removed.");
                entranceGateMBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, "Ticket Removed"));  // Call the entrance gate to open
                Timer.setTimer(id, mbox, openCloseGateTime, CloseGateTimerID);

            case GateOpenReply:
                log.info(id + ": Gate is opened.");
                gateIsClosed = false;
                break;

            case GateCloseReply:
                log.info(id + ": Gate is closed.");
                gateIsClosed = true;
                break;

            case CarDetected:
                log.info(id + ": Car detected signal received.");
                handleCarDetected(msg);
                break;

            case PayRequest:
                log.info(id + ": receive the pay request.");
                String fee = handlePayRequest(msg);
                break;

            case SuccessfullyPaid:
                log.info(id + ": Paid signal received. Ticket ID is "+msg.getDetails());
                int TicketID=Integer.parseInt(msg.getDetails());
                paymentCheck[TicketID]=true;
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
                log.info("PollAck from " + msg.getDetails());
                break;

            case Terminate:
                quit = true;
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }

        return quit;
    }

    /**
     *  Handle the Payment Request
     * @param msg The Payment Request Message
     * @return The fee
     */
    protected String handlePayRequest(Msg msg){
        int fee=-1;
        String payMachineInfo[]=msg.getDetails().split("I");

        int ticketID = Integer.parseInt(payMachineInfo[0]);

        String inputtedTime=payMachineInfo[1];

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime inputtedDateTime = LocalDateTime.parse(inputtedTime, format);

        log.info(id+":    "+inputtedDateTime+"   "+"   "+LocalDateTime.now());

        if(payMachineInfo[1].equals(enteringTimeArray[ticketID])){

        java.time.Duration duration = java.time.Duration.between(inputtedDateTime,LocalDateTime.now());

        int Minutes= (int) duration.toMinutes();

        fee= Minutes;
        }

      /*  if(checkPaid(ticketID))
            return "paid I" + Integer.toString(fee);*/

        return Integer.toString(fee);
    }

    protected boolean checkPaid(int ticketID){
        return paymentCheck[ticketID];
    }

    //------------------------------------------------------------
    //send payResponse to payMachine
    protected void sendMessage(Msg msg,String fee){
        System.out.println("in sendMessage");
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

    /**
     * Handle the Car Detection
     * @param msg The Message from sensors of each level
     */
    //------------------------------------------------------------
    // handleCarDetected
    protected void handleCarDetected(Msg msg) {
        switch (msg.getDetails()) {
            case "0up":
                level00 += 1;
                level01 -= 1;
                sendVacancyUpdateRequest();
                break;
            case "0dn":
                level00 -= 1;
                level01 += 1;
                sendVacancyUpdateRequest();
                break;
            case "1up":
                level01 += 1;
                level02 -= 1;
                sendVacancyUpdateRequest();
                break;
            case "1dn":
                level01 -= 1;
                level02 += 1;
                sendVacancyUpdateRequest();
                break;
            case "2up":
                level02 += 1;
                level03 -= 1;
                sendVacancyUpdateRequest();
                break;
            case "2dn":
                level02 -= 1;
                level03 += 1;
                sendVacancyUpdateRequest();
                break;
        }
    }

    /**
     * Send the request for Vacancy Display
     */
    //------------------------------------------------------------
    // sendVacancyUpdateRequest
    protected void sendVacancyUpdateRequest() {
        log.info(id + ": Send vacancy update signal to Vacancy Display");
        vacancyDispMBox.send(new Msg(id, mbox, Msg.Type.DisplayUpdateRequest, num_Level + "-" + level00 + "-" + level01 + "-" + level02+"-"+level03));
    }// sendVacancyUpdateRequest

    /**
     * Handle the Times Up situation
     * @param msg The TimesUp Message
     */
    //------------------------------------------------------------
    // handleTimesUp
    protected void handleTimesUp(Msg msg) {
        log.info("------------------------------------------------------------");
        switch (Timer.getTimesUpMsgTimerId(msg)) {
            case PollTimerID:
                log.info("Poll: " + msg.getDetails());

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
                log.info("Timer up, close the gate: "+msg.getDetails());
                entranceGateMBox.send(new Msg(id, mbox, Msg.Type.GateCloseRequest, "Please close the gate"));

            case CloseGateTimerID:
                log.info("Timer up, close the gate: "+msg.getDetails());
                exitGateMBox.send(new Msg(id, mbox, Msg.Type.GateCloseRequest, "Please close the gate"));
            default:
                log.severe(id + ": why am I receiving a timeout with timer id " + Timer.getTimesUpMsgTimerId(msg));
                break;
        }
    } // handleTimesUp



    /**
     * Formatting the entering time format
     * @param currentDateTime The current entering time
     * @return The formatted entering time
     */
    //-----------------------------------------------------------
    // enteringTime
    protected static String enteringTime(LocalDateTime currentDateTime) {
        int year = currentDateTime.getYear();
        int month = currentDateTime.getMonthValue();
        int day = currentDateTime.getDayOfMonth();
        int hour = currentDateTime.getHour();
        int minute = currentDateTime.getMinute();
        int second = currentDateTime.getSecond();

        return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
    } // enteringTime
} // PCSCore
