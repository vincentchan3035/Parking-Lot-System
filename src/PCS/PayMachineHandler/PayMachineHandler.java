package PCS.PayMachineHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import PCS.CollectorHandler.CollectorHandler;


//======================================================================
// PayMachineHandler
public class PayMachineHandler extends AppThread {
    protected final MBox pcsCore;
    protected String entranceTime;


    //------------------------------------------------------------
    // PayMachineHandler

    /**
     * The constructor of PayMachine Handler
     * @param id the name of AppThread
     * @param appKickstarter  a reference to our AppKickstarter
     */
    public PayMachineHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        entranceTime="";

    } // PayMachineHandler


    //------------------------------------------------------------
    // run

    /**
     * run the thread
     */
    public void run() {
        Thread.currentThread().setName(id);
        log.info(id + ": starting...");

        for (boolean quit = false; !quit;) {

                Msg msg = mbox.receive();

                log.fine(id + ": message received: [" + msg + "].");

                quit = processMsg(msg);

        }
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run

    //------------------------------------------------------------
    // processMsg
    /**
     * Handle the Message Process
     * @param msg the message received from different mbox
     * @return the boolean variable, true or false
     */
    protected boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
            case PayRequest:
                handlePayRequest(msg);
                break;

            case PayReply:
                handlePayReply(msg);
                break;

            case SuccessfullyPaid:
                handleSuccessfullyPaid(msg);
                break;

            case SuccessfullyPaidReply:
                handleSuccessfullyPaidReply(msg);
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
    } // processMsg

    //------------------------------------------------------------
    // handlePayRequest

    /**
     * Send the pay request to PCS core
     * @param msg the message related to the payment( Ticket ID, Entrance Time, the id of the Emulator( for level reference))
     */
    protected void handlePayRequest(Msg msg){
      log.info(id+ ": send payment request to PCS core");
      pcsCore.send(new Msg(id, mbox, Msg.Type.PayRequest, msg.getDetails()));
    }

    //------------------------------------------------------------
    // handlePayReply

    /**
     * Handle the pay reply message of PCS
     * @param msg the fee
     */
    protected void handlePayReply(Msg msg){
        log.info(id+": PayReply received, show the fee on pay machine");
        log.info(id+": the fee is "+ msg.getDetails());
        mbox.send(new Msg(id, null, Msg.Type.DisplayFee, msg.getDetails()));
    }

    //------------------------------------------------------------
    // handleSuccessfullyPaid

    /**
     * Send the successfully paid massage to PCS
     * @param msg ticket id
     */
    protected void handleSuccessfullyPaid(Msg msg){
        log.info(id+ ": Send successfully paid signal to PCS core");
        pcsCore.send(new Msg(id, mbox, Msg.Type.SuccessfullyPaid, msg.getDetails()));
    }// handleSuccessfullyPaid

    //------------------------------------------------------------
    // handleSuccessfullyPaidReply
    protected void handleSuccessfullyPaidReply(Msg msg){

    }// handleSuccessfullyPaidReply

    //------------------------------------------------------------
    // handlePollReq
    /**
     * Handle the poll request
     */
    protected final void handlePollReq() {
        log.info(id + ": poll request received.  Send poll request to hardware.");
        sendPollReq();
    } // handlePollReq

    //------------------------------------------------------------
    // sendPollReq
    /**
     * Send pay machine poll request to hardware
     */
    protected void sendPollReq() {
        // fixme: send gate poll request to hardware
        log.info(id + ": poll request received");
    } // sendPollReq

    //------------------------------------------------------------
    // handlePollAck
    /**
     * Send the poll acknowledgement back to PCSCore
     */
    protected final void handlePollAck() {
        log.info(id + ": poll ack received.  Send poll ack to PCS Core.");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is working!"));
    } // handlePollAck


}
