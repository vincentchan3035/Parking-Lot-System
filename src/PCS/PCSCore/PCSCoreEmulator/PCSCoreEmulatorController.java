package PCS.PCSCore.PCSCoreEmulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.logging.Logger;


//------------------------------------------------------------
// PCSCoreEmulatorController
public class PCSCoreEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private PCSCoreEmulator pcsCoreEmulator;
    private MBox PCSCoreMBox;
    public TextArea PCSCoreTextArea;
    private int lineNo = 0;
    public Button autoPollButton;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, PCSCoreEmulator pcsCoreEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.pcsCoreEmulator = pcsCoreEmulator;
        this.PCSCoreMBox = appKickstarter.getThread("PCSCore").getMBox();

    } // initialize


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {


            case "Poll Request":
                appendTextArea("Send poll request.");
                PCSCoreMBox.send(new Msg(id, null, Msg.Type.Poll, ""));
                break;


            case "Auto Poll: On":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: Off"));
                PCSCoreMBox.send(new Msg(id, null, Msg.Type.PCSCoreEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            case "Auto Poll: Off":
                Platform.runLater(() -> autoPollButton.setText("Auto Poll: On"));
                PCSCoreMBox.send(new Msg(id, null, Msg.Type.PCSCoreEmulatorAutoPollToggle, "ToggleAutoPoll"));
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    //------------------------------------------------------------
    // appendTextArea
    protected void appendTextArea(String status) {
        Platform.runLater(() -> PCSCoreTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea
}
