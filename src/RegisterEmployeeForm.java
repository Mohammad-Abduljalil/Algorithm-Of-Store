import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * نموذج "تسجيل موظف جديد" ككلاس Card مستقل تمامًا عن JDialog (لا يرثها إطلاقًا)،
 * حتى يمكن إنشاؤه ورسمه واختباره بصريًا في أي بيئة (بما فيها بيئة بلا شاشة حقيقية).
 * الأزرار تستقبل دوال Callback خارجية بدل استدعاء dispose() مباشرة، فالنموذج
 * لا "يعرف" أنه سيُعرض داخل نافذة منبثقة على الإطلاق - فصل تام بين العرض والتحكم.
 */
public class RegisterEmployeeForm extends Card {

    private final IconInputField usernameField;
    private final IconInputField passwordField;
    private final SegmentedToggle roleToggle;
    private Runnable onCancel;
    private Runnable onConfirm;

    public RegisterEmployeeForm(){
        setCornerRadius(Theme.RADIUS_MD);
        setLayout(new BorderLayout());

        usernameField = new IconInputField(null, "e.g. jsmith_ops", false);
        passwordField = new IconInputField(null, "\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022", true);
        roleToggle = new SegmentedToggle(new String[]{"Admin", "Employee"}, 1); // Employee افتراضيًا

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        JLabel title = new JLabel("Register New Employee");
        title.setFont(Theme.headlineMd());
        title.setForeground(Theme.ON_SURFACE);
        header.add(title, BorderLayout.WEST);
        IconButton closeBtn = new IconButton(Icons.close(16, Theme.ON_SURFACE_VARIANT));
        closeBtn.setPreferredSize(new Dimension(28, 28));
        closeBtn.addActionListener(e -> { if (onCancel != null) onCancel.run(); });
        header.add(closeBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        form.add(labeledField("Username", usernameField, null));
        form.add(Box.createVerticalStrut(20));

        javax.swing.JButton eyeToggle = new javax.swing.JButton(Icons.eye(16, Theme.ON_SURFACE_VARIANT));
        eyeToggle.setBorderPainted(false);
        eyeToggle.setContentAreaFilled(false);
        eyeToggle.setFocusPainted(false);
        eyeToggle.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        eyeToggle.addActionListener(e -> {
            javax.swing.JPasswordField pf = (javax.swing.JPasswordField) passwordField.getField();
            pf.setEchoChar(pf.getEchoChar() == 0 ? '\u2022' : (char) 0);
        });
        passwordField.setTrailingComponent(eyeToggle);
        form.add(labeledField("Temporary Password", passwordField, "User will be prompted to change this on first login."));
        form.add(Box.createVerticalStrut(20));

        JPanel roleLabelPanel = new JPanel();
        roleLabelPanel.setOpaque(false);
        roleLabelPanel.setLayout(new BoxLayout(roleLabelPanel, BoxLayout.Y_AXIS));
        roleLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel roleLabel = new JLabel("Role Assignment");
        roleLabel.setFont(Theme.labelMd());
        roleLabel.setForeground(Theme.ON_SURFACE_VARIANT);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleLabelPanel.add(roleLabel);
        roleLabelPanel.add(Box.createVerticalStrut(8));
        roleToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleToggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        roleToggle.setPreferredSize(new Dimension(100, 40));
        roleLabelPanel.add(roleToggle);
        form.add(roleLabelPanel);

        add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        RoundedButton cancelBtn = new RoundedButton("Cancel", RoundedButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> { if (onCancel != null) onCancel.run(); });
        RoundedButton createBtn = new RoundedButton("Create User", RoundedButton.Variant.PRIMARY);
        createBtn.addActionListener(e -> { if (onConfirm != null) onConfirm.run(); });
        footer.add(cancelBtn);
        footer.add(createBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel labeledField(String labelText, javax.swing.JComponent field, String helperText){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.labelMd());
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(100, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        if (helperText != null){
            JLabel helper = new JLabel(helperText);
            helper.setFont(Theme.bodySm());
            helper.setForeground(Theme.ON_SURFACE_VARIANT);
            helper.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(Box.createVerticalStrut(6));
            panel.add(helper);
        }
        return panel;
    }

    public void setOnCancel(Runnable r){ this.onCancel = r; }
    public void setOnConfirm(Runnable r){ this.onConfirm = r; }
    public String getUsername(){ return usernameField.getText(); }
    public String getPassword(){ return passwordField.getText(); }
    public Role getSelectedRole(){ return roleToggle.getSelectedIndex() == 0 ? Role.ADMIN : Role.EMPLOYEE; }
}
