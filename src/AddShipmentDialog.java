import javax.swing.JDialog;
import java.awt.Frame;

/**
 * غلاف رقيق حول AddShipmentForm، مسؤوليته إدارة سلوك النافذة المنبثقة فقط.
 */
public class AddShipmentDialog extends JDialog {

    private final AddShipmentForm form;
    private boolean confirmed = false;

    public AddShipmentDialog(Frame owner){
        super(owner, "Add Shipment", true);
        setUndecorated(true);
        setSize(460, 520);
        setLocationRelativeTo(owner);

        form = new AddShipmentForm();
        form.setOnCancel(this::dispose);
        form.setOnConfirm(() -> { confirmed = true; dispose(); });

        setContentPane(form);
    }

    public boolean isConfirmed(){ return confirmed; }
    public String getShipmentId(){ return form.getShipmentId(); }
    public String getDestination(){ return form.getDestination(); }
    public String getBudget(){ return form.getBudget(); }
    public int getYear(){ return form.getYear(); }
    public int getMonth(){ return form.getMonth(); }
    public int getDay(){ return form.getDay(); }
    public int getPriority(){ return form.getPriority(); }
}
