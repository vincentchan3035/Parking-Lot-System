package PCS.VacancyDispHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;

//================================================================
// VacancyDispHandler
public class VacancyDispHandler extends AppThread {
    protected final MBox pcsCore;
    private DisplayStatus displayStatus;
    private int num_Level;


    /**
     * Constructor of vacancy display Handler
     * @param id the name of the AppThread
     * @param appKickstarter a reference of AppKickstarter
     */

    //------------------------------------------------------------
    // VacancyDispHandler
    public VacancyDispHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        displayStatus=DisplayStatus.DisplayShowing;
    }
    /**
     * run the handler
     */

    //------------------------------------------------------------
    // run
    @Override
    public void run() {
        Thread.currentThread().setName(id);
        log.info(id + ": starting...");

        Msg BeginMsg = mbox.receive();
        processMsg(BeginMsg);

        for (boolean quit = false; !quit;) {


            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            quit = processMsg(msg);


        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    }//run

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
            case DisplayUpdateRequest:  handleDisplayUpdateRequest(msg);   break;
            case DisplayUpdateReply:    handleDisplayUpdateReply(msg);   break;
            case Poll:		   handlePollReq();	     break;
            case PollAck:	   handlePollAck();	     break;
            case Terminate:	   quit = true;          break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;

    }

    /**
     * Handle display update request
     * @param msg the message received from different mbox
     */


    //------------------------------------------------------------
    //handleDisplayUpdateRequest
    protected void handleDisplayUpdateRequest(Msg msg) {
        log.info(id + ": Vacancy update request received. Send signal to the vacancy display");

        sendUpdateRequest();

    } //handleDisplayUpdateRequest

    /**
     * Handle display update reply
     * @param msg the message received from hardware
     */

    //------------------------------------------------------------
    //handleDisplayUpdateReply
    protected void handleDisplayUpdateReply(Msg msg) {
        log.info(id + ": Vacancy Display update reply received, Update is finished!");

    } //handleDisplayUpdateReply

    /**
     * Handle poll ACK
     */

    //------------------------------------------------------------
    // handlePollAck
    protected final void handlePollAck() {
        log.info(id + ": poll ack received.  Send poll ack to PCS Core.");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is working!"));
    } // handlePollAck


    /**
     * Handle Poll request
     */
    //------------------------------------------------------------
    // handlePollReq
    protected final void handlePollReq() {
        log.info(id + ": poll request received.  Send poll request to hardware.");
        sendPollReq();
    } // handlePollReq


    //------------------------------------------------------------
    // sendPollReq
    protected void sendUpdateRequest() {
        // fixme: send vacancy number request to hardware
        log.info(id + ": (Hardware) Vacancy number request received");
    } // sendPollReq


    //------------------------------------------------------------
    // sendPollReq
    protected void sendPollReq() {
        // fixme: send poll request to hardware
        log.info(id + ": poll request received");
    } // sendPollReq




    //------------------------------------------------------------
    // Vacancy Display Status
    private enum DisplayStatus{
        DisplayUpdating,
        DisplayShowing,
    }


}
