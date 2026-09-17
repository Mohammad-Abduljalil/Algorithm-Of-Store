import javax.swing.JDialog;
import java.awt.Frame;

/**
 * غلاف رقيق حول AddProductToShipmentForm.
 */
public class AddProductToShipmentDialog extends JDialog {

    private final AddProductToShipmentForm form;
    private boolean confirmed = false;

    public AddProductToShipmentDialog(Frame owner, String[] productChoices){
        super(owner, "Add Product to Shipment", true);
        setUndecorated(true);
        setSize(420, 320);
        setLocationRelativeTo(owner);

        form = new AddProductToShipmentForm(productChoices);
        form.setOnCancel(this::dispose);
        form.setOnConfirm(() -> { confirmed = true; dispose(); });

        setContentPane(form);
    }

    public boolean isConfirmed(){ return confirmed; }
    public String getSelectedProduct(){ return form.getSelectedProduct(); }
    public String getQuantity(){ return form.getQuantity(); }
}
