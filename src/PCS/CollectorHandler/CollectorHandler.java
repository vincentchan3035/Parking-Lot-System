package PCS.CollectorHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;

public class CollectorHandler extends AppThread{
    protected final MBox pcsCore;
    private CollectorStatus collectorStatus;

    /**
     * Handle the Message Process
     * @param id the ID of Collector Handler
     * @param appKickstarter the reference of  AppKickstarter(including Msg, timer function)
     * @return the boolean variable, true or false
     */

    public CollectorHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        CollectorStatus collectorStatus = CollectorStatus.CollectorAlarmOff;
    }

    /**
     * run the thread
     */
    public void run() {
        Thread.currentThread().setName(id);
        log.info(id + ": starting...");

        for (boolean quit = false; !quit;) {
            if(getMBox().getMsgCnt() > 0) {
                Msg msg = mbox.receive();
                log.fine(id + ": message received: [" + msg + "].");
                quit = processMsg(msg);
            }

            if(staffComeToHandle()){
                collectorStatus = CollectorStatus.CollectorAlarmOff;
            }
        }
        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run

    /**
     * Handle the Message Process
     * @param msg the message received from different mbox
     * @return the boolean variable, true or false (whether quit situation is true or false)
     */
    protected boolean processMsg(Msg msg) {

        boolean quit = false;

        switch (msg.getType()) {
            case TicketInsertRequest:
                TicketInsertRequest(msg);
                break;
            case ValidInsertRequest:
                handleValidInsertRequest(msg);
            case InvalidInsertRequest:
                handleInvalidInsertRequest(msg);
                break;
            case Poll:
                handlePollReq();
                break;
            case PollAck:
                handlePollAck();
                break;
            case Terminate:
                quit = true;
                break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }

        return quit;
    }
    //-----------------------------------------------------------------
    /**
     * Handle the ticket insert request
     * @param msg the message received from  mbox
     */
    protected void TicketInsertRequest(Msg msg){
        //fixme collector detected ticket inserted
        //inputTicketId
        log.info(id+": Ticket insert detected");
        int ticketID = Integer.parseInt(msg.getDetails());
        log.info(id+": need to send data to PCS");
        sendTicketInfoSignal(msg.getDetails());
    }

    protected boolean staffComeToHandle(){
        //fixme staff interacted with collector
        return true;
    }

    /**
     * Handle the valid ticket insert request, just have a notification
     * @param msg the message received from  mbox
     */
    protected void handleValidInsertRequest(Msg msg){
        log.info(id + ": Ticket is valid. Wait until other ticket being insert");
        //pcs should handle exitGate open operation
    }

    /**
     * Handle the invalid ticket insert request, send signal to emulator
     * @param msg the message received from  mbox
     */
    protected void handleInvalidInsertRequest(Msg msg){
        log.info(id + ": invalid ticketInfo received, alert!!");
        sendAlertSignal();
    }

    protected final void handlePollAck() {
        log.info(id + ": poll ack received.  Send poll ack to PCS Core.");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is working!"));
    } // handlePollAck

    protected final void handlePollReq() {
        log.info(id + ": poll request received.  Send poll request to hardware.");
        sendPollReq();
    } // handlePollReq

    //------------------------------------------------------------
    /**
     * send ticket info to PCS
     * @param ticketID the id received from emulator
     */
    protected void sendTicketInfoSignal(String ticketID) { //send the ticket id to pcs
        // fixme: send ticket info signal to pcs
        log.info(id + ": sending ticket information to PCS for verification");
        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketInsertRequest,ticketID));
    }

    /**
     * send signal to emulator and change the state
     */
    protected void sendAlertSignal() {
        log.info(id + ": ticket is invalid, sending alert signal to hardware");
        // fixme: send alert signal to hardware(alarm)
        collectorStatus = CollectorStatus.CollectorAlarmOn;
    }

    protected void sendPollReq() {
        // fixme: send gate poll request to hardware
        log.info(id + ": poll request received");
    } // sendPollReq

    private enum CollectorStatus {
        CollectorAlarmOff,
        CollectorAlarmOn,
    }
}
