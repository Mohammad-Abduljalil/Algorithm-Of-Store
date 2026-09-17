import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * شاشة الإعدادات: تبويبات (Security/Preferences/Notifications) + بطاقة "Change My Password"
 * مركزية تحت تبويب Security. مرتبطة فعليًا بـ AuthManager.changePassword عبر Listener خارجي،
 * وتعرض حالتي النجاح/الفشل بصريًا (Alert أخضر أو رسالة خطأ).
 */
public class SettingsPanel extends JPanel {

    private final IconInputField currentPasswordField;
    private final IconInputField newPasswordField;
    private final IconInputField confirmPasswordField;
    private final Card successAlert;
    private final JLabel errorLabel;
    private final RoundedButton saveButton;

    public SettingsPanel(){
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Settings");
        title.setFont(Theme.display());
        title.setForeground(Theme.ON_SURFACE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(24));

        // أُزيلت تبويبات Preferences/Notifications لأنها كانت بلا وظيفة فعلية،
        // وأُبقي عنوان فرعي واضح بدلها يصف ما تحتويه الشاشة فعلاً.
        JLabel sectionLabel = new JLabel("Security");
        sectionLabel.setFont(Theme.bodyMd().deriveFont(java.awt.Font.BOLD));
        sectionLabel.setForeground(Theme.ACCENT_BLUE);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        content.add(sectionLabel);

        JPanel underline = new JPanel();
        underline.setBackground(Theme.CARD_BORDER);
        underline.setAlignmentX(Component.LEFT_ALIGNMENT);
        underline.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        content.add(underline);
        content.add(Box.createVerticalStrut(28));

        JPanel centerWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        centerWrap.setOpaque(false);
        centerWrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        Card passwordCard = new Card();
        passwordCard.setLayout(new BoxLayout(passwordCard, BoxLayout.Y_AXIS));
        passwordCard.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        // بلا ارتفاع ثابت: الارتفاع السابق (420) كان أصغر من مجموع ارتفاع الحقول،
        // فكان زر "Save Changes" يُقص ولا يظهر إطلاقًا. الآن يُحسب الارتفاع من المحتوى.
        passwordCard.setMaximumSize(new Dimension(560, Integer.MAX_VALUE));

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JPanel iconBox = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.SURFACE_CONTAINER_LOW);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS_DEFAULT, Theme.RADIUS_DEFAULT));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(48, 48));
        iconBox.setLayout(new java.awt.GridBagLayout());
        iconBox.add(new JLabel(Icons.lockReset(22, Theme.ON_SURFACE)));
        header.add(iconBox, BorderLayout.WEST);

        JPanel titleCol = new JPanel();
        titleCol.setOpaque(false);
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        JLabel cardTitle = new JLabel("Change My Password");
        cardTitle.setFont(Theme.headlineMd());
        cardTitle.setForeground(Theme.ON_SURFACE);
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel cardSubtitle = new JLabel("<html><div style='width:340px;'>Update your password to keep your account secure. Use a strong, unique password.</div></html>");
        cardSubtitle.setFont(Theme.bodySm());
        cardSubtitle.setForeground(Theme.ON_SURFACE_VARIANT);
        cardSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleCol.add(cardTitle);
        titleCol.add(cardSubtitle);
        header.add(titleCol, BorderLayout.CENTER);

        passwordCard.add(header);
        passwordCard.add(Box.createVerticalStrut(24));

        currentPasswordField = new IconInputField(null, "Enter current password", true);
        passwordCard.add(fieldBlock("Current Password", currentPasswordField, null));
        passwordCard.add(Box.createVerticalStrut(16));

        newPasswordField = new IconInputField(null, "Enter new password", true);
        passwordCard.add(fieldBlock("New Password", newPasswordField, "Must be at least 8 characters long and include a number or symbol."));
        passwordCard.add(Box.createVerticalStrut(16));

        confirmPasswordField = new IconInputField(null, "Confirm new password", true);
        passwordCard.add(fieldBlock("Confirm New Password", confirmPasswordField, null));
        passwordCard.add(Box.createVerticalStrut(16));

        // ===== تنبيه النجاح (مخفي افتراضيًا) =====
        successAlert = new Card();
        successAlert.setCardBackground(Theme.SUCCESS_BG);
        successAlert.setCardBorderColor(Theme.SUCCESS_TEXT);
        successAlert.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        successAlert.setAlignmentX(Component.LEFT_ALIGNMENT);
        successAlert.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        successAlert.add(new JLabel(Icons.trendingUp(14, Theme.SUCCESS_TEXT)));
        JLabel successText = new JLabel("Password changed successfully.");
        successText.setFont(Theme.bodySm());
        successText.setForeground(Theme.SUCCESS_TEXT);
        successAlert.add(successText);
        successAlert.setVisible(false);
        passwordCard.add(successAlert);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.bodySm());
        errorLabel.setForeground(Theme.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordCard.add(errorLabel);

        passwordCard.add(Box.createVerticalStrut(20));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        RoundedButton cancelBtn = new RoundedButton("Cancel", RoundedButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> clearFields());
        saveButton = new RoundedButton("Save Changes", RoundedButton.Variant.PRIMARY);
        footer.add(cancelBtn);
        footer.add(saveButton);
        passwordCard.add(footer);

        centerWrap.add(passwordCard);
        content.add(centerWrap);

        content.add(Box.createVerticalGlue()); // يدفع المحتوى للأعلى بدل تمدّد البطاقات عموديًا
        add(content, BorderLayout.CENTER);
    }

    private JPanel fieldBlock(String labelText, javax.swing.JComponent field, String helperText){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.labelMd());
        label.setForeground(Theme.ON_SURFACE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(100, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        if (helperText != null){
            JLabel helper = new JLabel("<html><div style='width:440px;'>" + helperText + "</div></html>");
            helper.setFont(Theme.bodySm());
            helper.setForeground(Theme.ON_SURFACE_VARIANT);
            helper.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(Box.createVerticalStrut(6));
            panel.add(helper);
        }
        return panel;
    }

    private void clearFields(){
        currentPasswordField.getField().setText("");
        newPasswordField.getField().setText("");
        confirmPasswordField.getField().setText("");
        successAlert.setVisible(false);
        errorLabel.setText(" ");
        revalidate();
        repaint();
    }

    // تُستدعى من الخارج (عند الربط بـ AuthManager) لعرض حالة النجاح/الفشل الفعلية
    public void showSuccess(){
        successAlert.setVisible(true);
        errorLabel.setText(" ");
        clearPasswordFieldsOnly();
        revalidate();
        repaint();
    }

    public void showError(String message){
        successAlert.setVisible(false);
        errorLabel.setText(message);
        revalidate();
        repaint();
    }

    private void clearPasswordFieldsOnly(){
        currentPasswordField.getField().setText("");
        newPasswordField.getField().setText("");
        confirmPasswordField.getField().setText("");
    }

    public String getCurrentPassword(){ return currentPasswordField.getText(); }
    public String getNewPassword(){ return newPasswordField.getText(); }
    public String getConfirmPassword(){ return confirmPasswordField.getText(); }

    public void addSaveListener(ActionListener listener){
        saveButton.addActionListener(listener);
    }
}
