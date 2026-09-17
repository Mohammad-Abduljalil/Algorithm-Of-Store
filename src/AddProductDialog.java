import javax.swing.JDialog;
import java.awt.Frame;

/**
 * غلاف رقيق حول AddProductForm، مسؤوليته إدارة سلوك النافذة المنبثقة فقط.
 */
public class AddProductDialog extends JDialog {

    private final AddProductForm form;
    private boolean confirmed = false;

    public AddProductDialog(Frame owner){
        this(owner, new String[]{"Electronics", "Office", "Hardware", "Uncategorized"});
    }

    public AddProductDialog(Frame owner, String[] availableCategories){
        super(owner, "Add New Product", true);
        setUndecorated(true);
        setSize(480, 560);
        setLocationRelativeTo(owner);

        form = new AddProductForm(availableCategories);
        form.setOnCancel(this::dispose);
        form.setOnConfirm(() -> { confirmed = true; dispose(); });

        setContentPane(form);
    }

    public void configureForEdit(int id, String name, int price, int quantity, int threshold, String category){
        form.configureForEdit(id, name, price, quantity, threshold, category);
        setTitle("Edit Product");
    }

    public boolean isConfirmed(){ return confirmed; }
    public String getProductId(){ return form.getProductId(); }
    public String getProductName(){ return form.getProductName(); }
    public String getPrice(){ return form.getPrice(); }
    public String getQuantity(){ return form.getQuantity(); }
    public String getThreshold(){ return form.getThreshold(); }
    public String getCategory(){ return form.getCategory(); }
}
