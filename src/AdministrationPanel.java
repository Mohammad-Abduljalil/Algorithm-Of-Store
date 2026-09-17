import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * شاشة "Administration" الموحّدة: تبويب المستخدمين وتبويب سجل العمليات، كلاهما مربوط
 * ببيانات حقيقية (AuthManager.getAllUsers, AuditLog.getRecentEntries) بدل البيانات النموذجية.
 */
public class AdministrationPanel extends JPanel {

    public AdministrationPanel(AppContext ctx){
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Administration");
        title.setFont(Theme.display());
        title.setForeground(Theme.ON_SURFACE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(24));

        TabBar tabBar = new TabBar(new String[]{"Users", "Audit Log"}, 0);
        tabBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        tabBar.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 44));
        content.add(tabBar);
        content.add(Box.createVerticalStrut(24));

        JPanel cardsWrap = new JPanel(new CardLayout());
        cardsWrap.setOpaque(false);
        cardsWrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        UserManagementPanel usersPanel = new UserManagementPanel();
        usersPanel.setRows(buildRealUserRows(ctx));
        usersPanel.setOnRegisterEmployee(() -> {
            RegisterEmployeeDialog dialog = new RegisterEmployeeDialog(
                    (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            if (dialog.isConfirmed()){
                String error = ctx.authManager.registerNewUserGui(
                        dialog.getUsername(), dialog.getPassword(), dialog.getSelectedRole(), ctx.currentUser.getUsername());
                if (error != null){
                    javax.swing.JOptionPane.showMessageDialog(this, error, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                } else {
                    usersPanel.setRows(buildRealUserRows(ctx));
                }
            }
        });
        cardsWrap.add(usersPanel, "users");

        AuditLogPanel auditPanel = new AuditLogPanel();
        auditPanel.setRows(buildRealAuditRows());
        cardsWrap.add(auditPanel, "audit");

        content.add(cardsWrap);
        content.add(Box.createVerticalGlue()); // يدفع المحتوى للأعلى بدل تمدّد البطاقات عموديًا
        add(content, BorderLayout.CENTER);

        CardLayout cl = (CardLayout) cardsWrap.getLayout();
        tabBar.setOnChange(index -> cl.show(cardsWrap, index == 0 ? "users" : "audit"));
    }

    private List<UserRow> buildRealUserRows(AppContext ctx){
        List<UserRow> rows = new ArrayList<>();
        Color[][] palette = {
                { Theme.TERTIARY_FIXED, Theme.TERTIARY },
                { Theme.PRIMARY_FIXED, Theme.PRIMARY },
                { Theme.SECONDARY_FIXED, Theme.SECONDARY_LINK },
        };
        int i = 0;
        for (User u : ctx.authManager.getAllUsers()){
            Color[] colors = palette[i % palette.length];
            String initials = u.getUsername().length() >= 2 ? u.getUsername().substring(0, 2).toUpperCase() : u.getUsername().toUpperCase();
            rows.add(new UserRow(initials, u.getUsername(), u.getRole(), true, colors[0], colors[1]));
            i++;
        }
        return rows;
    }

    private List<AuditLogRow> buildRealAuditRows(){
        List<AuditLogRow> rows = new ArrayList<>();
        Color[][] palette = {
                { Theme.TERTIARY_FIXED, Theme.TERTIARY },
                { Theme.PRIMARY_FIXED, Theme.PRIMARY },
                { new Color(0xE0E7FF), new Color(0x3730A3) },
        };
        int i = 0;
        for (String line : AuditLog.getRecentEntries(50)){
            String[] parts = line.split("\\s*\\|\\s*", 4);
            String timestamp = parts.length > 0 ? parts[0] : "";
            String username = parts.length > 1 ? parts[1] : "?";
            String action = parts.length > 2 ? parts[2] : line;
            String detail = parts.length > 3 ? parts[3] : "";

            String result;
            String extra;
            if (detail.startsWith("DENIED")){
                result = "DENIED";
                extra = detail.length() > 7 ? detail.substring(7).replaceFirst("^-\\s*", "") : null;
            } else if (detail.startsWith("FAILED")){
                result = "FAILED";
                extra = detail.length() > 6 ? detail.substring(6).trim() : null;
            } else {
                result = "SUCCESS";
                extra = detail.replaceFirst("^SUCCESS\\s*-?\\s*", "");
                if (extra.isBlank()) extra = null;
            }

            Color[] colors = palette[i % palette.length];
            rows.add(new AuditLogRow(timestamp, username, action, extra, result, colors[0], colors[1], result.equals("DENIED")));
            i++;
        }
        return rows;
    }
}
