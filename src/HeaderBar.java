import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

/**
 * الرأس العلوي الثابت (64px ارتفاع)، مطابق لكود Stitch:
 * عنوان التطبيق ثابت على اليسار، وأيقونتا بحث/خروج على اليمين.
 * (عنوان الصفحة الفعلي - "Dashboard" مثلاً - يظهر داخل منطقة المحتوى نفسها، وليس هنا).
 */
public class HeaderBar extends JPanel {

    private final IconButton logoutButton;
    private final IconButton searchButton;
    private final RoundedButton saveButton; // العملية 17: حفظ البيانات يدويًا (Admin فقط)

    public HeaderBar(){
        setLayout(new BorderLayout());
        setBackground(Theme.SURFACE);
        setOpaque(true);
        setPreferredSize(new Dimension(100, 64));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER)); // بديل بسيط عن الظل الناعم

        JLabel title = new JLabel("Inventory & Shipment Management");
        title.setFont(Theme.headlineMd());
        title.setForeground(Theme.PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 0));
        add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));

        saveButton = new RoundedButton("Save Data", RoundedButton.Variant.SECONDARY);
        saveButton.setIcon(Icons.download(14, Theme.PRIMARY_CONTAINER));
        saveButton.setIconTextGap(6);
        saveButton.setToolTipText("Save all data to disk now (Admin only)");
        saveButton.setVisible(false); // يظهر فقط للمستخدم Admin
        actions.add(saveButton);

        searchButton = new IconButton(Icons.search(20, Theme.ON_SURFACE_VARIANT));
        logoutButton = new IconButton(Icons.logout(20, Theme.ON_SURFACE_VARIANT));
        actions.add(searchButton);
        actions.add(logoutButton);
        add(actions, BorderLayout.EAST);
    }

    public void addLogoutListener(ActionListener listener){
        logoutButton.addActionListener(listener);
    }

    public void addSearchListener(ActionListener listener){
        searchButton.addActionListener(listener);
    }

    public void addSaveListener(ActionListener listener){
        saveButton.addActionListener(listener);
    }

    // يُظهر زر الحفظ للمشرف فقط، تمامًا كقيد "Admin only" في نسخة الكونسول
    public void setSaveButtonVisible(boolean visible){
        saveButton.setVisible(visible);
    }

    public RoundedButton getSaveButton(){
        return saveButton;
    }
}
