import javax.swing.JDialog;
import java.awt.Frame;

/**
 * غلاف رقيق (Thin Wrapper) حول RegisterEmployeeForm، مسؤوليته الوحيدة هي إدارة
 * سلوك النافذة المنبثقة نفسها (فتح/إغلاق/توسيط)، بينما كل منطق العرض الفعلي
 * موجود في RegisterEmployeeForm القابلة للاختبار بمعزل عن JDialog.
 */
public class RegisterEmployeeDialog extends JDialog {

    private final RegisterEmployeeForm form;
    private boolean confirmed = false;

    public RegisterEmployeeDialog(Frame owner){
        super(owner, "Register New Employee", true);
        setUndecorated(true);
        setSize(460, 520);
        setLocationRelativeTo(owner);

        form = new RegisterEmployeeForm();
        form.setOnCancel(this::dispose);
        form.setOnConfirm(() -> { confirmed = true; dispose(); });

        setContentPane(form);
    }

    public boolean isConfirmed(){ return confirmed; }
    public String getUsername(){ return form.getUsername(); }
    public String getPassword(){ return form.getPassword(); }
    public Role getSelectedRole(){ return form.getSelectedRole(); }
}
