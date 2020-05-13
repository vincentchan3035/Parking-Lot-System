package PCS.SensorHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;



//======================================================================
// SensorHandler
public class SensorHandler extends AppThread{
    protected final MBox pcsCore;
    protected int level;
    protected String dir;


    /**
     * Constructor of sensor Handler
     * @param id the name of the AppThread
     * @param appKickstarter a reference of AppKickstarter
     * @param level the number of level
     * @param dir the direction sensor
     */

    //------------------------------------------------------------
    // SensorHandler
    public SensorHandler(String id, AppKickstarter appKickstarter, int level, String dir) {
    super(id, appKickstarter);
    pcsCore = appKickstarter.getThread("PCSCore").getMBox();
    this.level=level;
    this.dir =dir;
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
    //processMsg
    protected boolean processMsg(Msg msg){
        boolean quit = false;

        switch (msg.getType()) {
            case Poll: handlePollReq();     break;
            case PollAck: handlePollAck();  break;
            case Terminate:	   quit = true; break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;
    }   // processMsg
    /**
     * Handle the Poll request
     */
    //------------------------------------------------------------
    // handlePollReq
    protected final void handlePollReq() {
        log.info(id + ": poll request received.  Send poll request to hardware.");
        sendPollReq();
    } // handlePollReq

    /**
     * Handle the Poll ACK
     */

    //------------------------------------------------------------
    // handlePollAck
    protected final void handlePollAck() {
        log.info(id + ": poll ack received.  Send poll ack to PCS Core.");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PollAck, ":Level "+level+", dir: "+dir+" Sensor is working."));
    } // handlePollAck


    /**
     * Send the Poll request to sensor emulator
     */
    //------------------------------------------------------------
    // sendPollReq
    protected void sendPollReq() {
        // fixme: send gate poll request to hardware
        log.info(id + ": poll request received");
    } // sendPollReq



}//SensorHandler



