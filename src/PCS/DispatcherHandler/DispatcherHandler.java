package PCS.DispatcherHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

//======================================================================
// DispatcherHandler
public class DispatcherHandler extends AppThread {
    protected final MBox pcsCore;
    private String entranceInfo;

    /**
     * The constructor of Dispatcher Handler
     * @param id the name of AppThread
     * @param appKickstarter a reference to our AppKickstarter
     */
    //------------------------------------------------------------
    // DispatcherHandler
    public DispatcherHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
    }

    /**
     * Run the Thread
     */
    //------------------------------------------------------------
    // run
    @Override
    public void run() {
        Thread.currentThread().setName(id);
        log.info(id + ": starting...");

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();
            log.fine(id + ": message recevied: [ " + msg + " ].");

            quit = processMsg(msg);

        }
        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    }// run

    /**
     * Handle the Message Process
     * @param msg the message received from different mbox
     * @return the boolean variable, true or false
     */
    //------------------------------------------------------------
    // processMsg
    protected boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
            case TicketRequest:
                handleTicketRequest(msg);
                break;
            case TicketRemoved:
                handleTicketRemoved();
                break;
            case TicketRequestReply:
                handleTicketRequestReply(msg);
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
                log.warning(id + ": unknown message type: [ " + msg + " ].");
        }

        return quit;
    }

    /**
     * Send the message to PCSCore for ticket request
     * @param msg the Ticket Request message
     */
    //------------------------------------------------------------
    // handleTicketRequest
    protected final void handleTicketRequest(Msg msg) {
        log.info(id + ": ticket request received!!");

        log.info(id + ": send a signal to show the request now.");

        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketRequest, "Ticket Request"));
        sendTicketRequestSignal();
    }

    /**
     * Send the message to PCSCore for ticket removed
     */
    //------------------------------------------------------------
    // handleTicketRemoved
    protected final void handleTicketRemoved() {
        log.info(id + ": ticket removed!!");

        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketRemoved, "Ticket Removed"));
        sendTicketRemovedSignal();

    }

    /**
     * Store the entrance information
     * @param msg the entrance information return from PCSCore
     */
    //-----------------------------------------------------------
    // handleTicketRequestReply
    protected void handleTicketRequestReply(Msg msg){
        log.info(id + ": ticket info reply received.");

        setEntranceInfo(msg.getDetails());

    }

    /**
     * Send ticket request signal to hardware
     */
    //-----------------------------------------------------------
    // sendTicketRequestSignal
    protected void sendTicketRequestSignal() {
        // fixme: send ticket request signal to hardware
        log.info(id + ": sending ticket request signal to hardware.");
    }

    /**
     * Send ticket removed signal to hardware
     */
    //------------------------------------------------------------
    // sendTicketRemovedSignal
    protected void sendTicketRemovedSignal() {
        // fixme: send ticket removed signal to hardware
        log.info(id + ": sending ticket removed signal to PCS.");
    }

    /**
     * Handle the poll request
     */
    //------------------------------------------------------------
    // handlePollReq
    protected final void handlePollReq() {
        log.info(id + ": poll request received.  Send poll request to hardware.");
        sendPollReq();
    } // handlePollReq


    /**
     * Send the poll acknowledgement back to PCSCore
     */
    //------------------------------------------------------------
    // handlePollAck
    protected final void handlePollAck() {
        log.info(id + ": poll ack received.  Send poll ack to PCS Core.");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is working!"));
    } // handlePollAck

    /**
     * Send dispatcher poll request to hardware
     */
    //------------------------------------------------------------
    // sendPollReq
    protected void sendPollReq() {
        // fixme: send dispatcher poll request to hardware
        log.info(id + ": poll request received.");
    } // sendPollReq

    /**
     * Get the entrance information
     * @return entrance information
     */
    protected String getEntranceInfo(){
        return entranceInfo;
    }

    /**
     * Return entrance information
     * @param entranceInfo the entrance information
     * @return entrance information
     */
    protected String setEntranceInfo(String entranceInfo){
        return this.entranceInfo = entranceInfo;
    }
}
