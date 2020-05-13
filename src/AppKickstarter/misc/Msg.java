package AppKickstarter.misc;


//======================================================================
// Msg
public class Msg {
    private String sender;
    private MBox senderMBox;
    private Type type;
    private String details;

    //------------------------------------------------------------
    // Msg
    /**
     * Constructor for a msg.
     * @param sender id of the msg sender (String)
     * @param senderMBox mbox of the msg sender
     * @param type message type
     * @param details details of the msg (free format String)
     */
    public Msg(String sender, MBox senderMBox, Type type, String details) {
	this.sender = sender;
	this.senderMBox = senderMBox;
	this.type = type;
	this.details = details;
    } // Msg


    //------------------------------------------------------------
    // getSender
    /**
     * Returns the id of the msg sender
     * @return the id of the msg sender
     */
    public String getSender()     { return sender; }


    //------------------------------------------------------------
    // getSenderMBox
    /**
     * Returns the mbox of the msg sender
     * @return the mbox of the msg sender
     */
    public MBox   getSenderMBox() { return senderMBox; }


    //------------------------------------------------------------
    // getType
    /**
     * Returns the message type
     * @return the message type
     */
    public Type   getType()       { return type; }


    //------------------------------------------------------------
    // getDetails
    /**
     * Returns the details of the msg
     * @return the details of the msg
     */
    public String getDetails()    { return details; }


    //------------------------------------------------------------
    // toString
    /**
     * Returns the msg as a formatted String
     * @return the msg as a formatted String
     */
    public String toString() {
	return sender + " (" + type + ") -- " + details;
    } // toString


    //------------------------------------------------------------
    // Msg Types

    /**
     * Message Types used in Msg.
     * @see Msg
     */
    public enum Type  // Type
    {
        /** Set a timer */ CancelTimer,
        /** Generic error msg */ Error,
        /** Health poll */ Poll,
        /** Health poll acknowledgement */ PollAck,
        /** Set a timer */ SetTimer,
        /** Terminate the running thread */ Terminate,
        /** Timer clock ticks */ Tick,
        /** PCSCore Emulator AutoPoll */ PCSCoreEmulatorAutoPollToggle,


        /** Vacancy Display update request */ DisplayUpdateRequest,
        /** Vacancy Display update reply */ DisplayUpdateReply,
        /** Toggling Display Emulator AutoPoll */ VacancyDispEmulatorAutoPollToggle,

        /** Sensor Detect cars */ CarDetected,
        /** Toggling Sensor Emulator AutoPoll */ SensorEmulatorAutoPollToggle,

        /** Gate close reply */ GateCloseReply,
        /** Gate close request */ GateCloseRequest,
        /** Toggling Gate Emulator AutoClose */ GateEmulatorAutoCloseToggle,
        /** Toggling Gate Emulator AutoOpen */ GateEmulatorAutoOpenToggle,
        /** Toggling Gate Emulator AutoPoll */ GateEmulatorAutoPollToggle,
        /** Gate open reply */ GateOpenReply,
        /** Gate open request */ GateOpenRequest,

        /** ticket inserted */ TicketInsertRequest,
        /** valid ticket inserted request*/ ValidInsertRequest,
        /** Invalid ticket inserted request*/ InvalidInsertRequest,
        /** Invalid ticket inserted reply*/ InvalidInsertReply,
        /** Toggling Collector Emulator AutoPoll */ CollectorEmulatorAutoPollToggle,
        /** Turn off alarm request */

        /** Dispatcher Ticket removed */ TicketRemoved,
        /** Dispatcher Ticket removed reply */ TicketRemovedReply,
        /** Dispatcher Ticket request */ TicketRequest,
        /** Dispatcher Ticket request reply */ TicketRequestReply,
        /** Toggling Dispatcher Emulator AutoPoll */ DispatcherEmulatorAutoPollToggle,

        /** PayMachine pay request*/ PayRequest,
        /** PayMachine get pay reply*/ PayReply,
        /** Ask emulator to display the fee*/ DisplayFee,
        /** Driver pay successfully*/ SuccessfullyPaid,
        /** PCS core send exit information*/ SuccessfullyPaidReply,
        /** Toggling PayMachine Emulator AutoPoll */ PayMachineEmulatorAutoPollToggle,
        /** Time's up for the timer */ TimesUp,

    }
} // Msg
