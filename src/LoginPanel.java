import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * شاشة تسجيل الدخول. مسؤولة فقط عن العرض وجمع الإدخال (Username/Password)،
 * ولا تتعامل مباشرة مع AuthManager - الربط الفعلي يتم من الخارج عبر addLoginListener،
 * حفاظًا على فصل الواجهة عن منطق الأعمال.
 */
public class LoginPanel extends JPanel {

    private static final int CARD_WIDTH = 440;

    private final IconInputField usernameField;
    private final IconInputField passwordField;
    private final RoundedButton loginButton;
    private final Card errorCard;
    private final JLabel errorLabel;

    public LoginPanel(){
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);
        setOpaque(true);

        JPanel centerColumn = new JPanel();
        centerColumn.setOpaque(false);
        centerColumn.setLayout(new BoxLayout(centerColumn, BoxLayout.Y_AXIS));
        // بلا ارتفاع ثابت: الارتفاع يُحسب من المحتوى، فيبقى التوسيط مضبوطًا
        // سواء ظهرت رسالة الخطأ أم لا (الارتفاع الثابت كان يُزيح البطاقة عن المنتصف)
        centerColumn.setMaximumSize(new Dimension(CARD_WIDTH, Integer.MAX_VALUE));
        centerColumn.setPreferredSize(null);

        // ===== البطاقة الرئيسية =====
        Card card = new Card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setCornerRadius(Theme.RADIUS_LG);
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 32, 40));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // أيقونة التطبيق في مربع أزرق فاتح
        JPanel iconBox = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xDBE3FE));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS_MD, Theme.RADIUS_MD));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(56, 56));
        iconBox.setMaximumSize(new Dimension(56, 56));
        iconBox.setLayout(new GridBagLayout());
        JLabel iconLabel = new JLabel(Icons.archiveBox(26, Theme.PRIMARY_CONTAINER));
        iconBox.add(iconLabel);
        iconBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("<html><div style='text-align:center;width:330px;'>Inventory &amp; Shipment Management</div></html>", SwingConstants.CENTER);
        title.setFont(Theme.headlineLg().deriveFont(26f));
        title.setForeground(Theme.ON_SURFACE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("<html><div style='text-align:center;width:320px;'>Enter your credentials to access the warehouse portal</div></html>", SwingConstants.CENTER);
        subtitle.setFont(Theme.bodyMd());
        subtitle.setForeground(Theme.ON_SURFACE_VARIANT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // نلفّ التسمية في صف بتخطيط BorderLayout (نفس نمط صف Password) حتى تُحاذى لليسار.
        // إضافتها مباشرة لـ BoxLayout كانت تجعلها في المنتصف، لأن المحاذاة الافتراضية
        // لأي JComponent داخل BoxLayout هي CENTER_ALIGNMENT وليست اليسار.
        JPanel usernameHeaderRow = new JPanel(new BorderLayout());
        usernameHeaderRow.setOpaque(false);
        usernameHeaderRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameHeaderRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        usernameHeaderRow.add(fieldLabel("Username"), BorderLayout.WEST);

        usernameField = new IconInputField(Icons.user(18, Theme.OUTLINE), "Enter username", false);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel passwordHeaderRow = new JPanel(new BorderLayout());
        passwordHeaderRow.setOpaque(false);
        passwordHeaderRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordHeaderRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        passwordHeaderRow.add(fieldLabel("Password"), BorderLayout.WEST);
        JLabel forgotLink = new JLabel("Forgot password?");
        forgotLink.setFont(Theme.bodySm());
        forgotLink.setForeground(Theme.ACCENT_BLUE);
        passwordHeaderRow.add(forgotLink, BorderLayout.EAST);

        passwordField = new IconInputField(Icons.lock(18, Theme.OUTLINE), "Enter password", true);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        javax.swing.JButton eyeToggle = new javax.swing.JButton(Icons.eye(18, Theme.OUTLINE));
        eyeToggle.setBorderPainted(false);
        eyeToggle.setContentAreaFilled(false);
        eyeToggle.setFocusPainted(false);
        eyeToggle.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        eyeToggle.addActionListener(e -> {
            javax.swing.JPasswordField pf = (javax.swing.JPasswordField) passwordField.getField();
            pf.setEchoChar(pf.getEchoChar() == 0 ? '•' : (char) 0);
        });
        passwordField.setTrailingComponent(eyeToggle);

        loginButton = new RoundedButton("Login", RoundedButton.Variant.PRIMARY);
        loginButton.setIcon(Icons.arrowRight(16, Color.WHITE));
        loginButton.setHorizontalTextPosition(SwingConstants.LEFT);
        loginButton.setIconTextGap(8);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginButton.setPreferredSize(new Dimension(100, 46));

        // ===== بطاقة رسالة الخطأ (مخفية افتراضيًا) =====
        errorCard = new Card();
        errorCard.setCardBackground(Theme.ERROR_BG);
        errorCard.setCardBorderColor(Theme.ERROR);
        errorCard.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        errorCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        errorCard.add(new JLabel(Icons.warningTriangle(16, Theme.ERROR_TEXT)));
        errorLabel = new JLabel("Invalid username or password.");
        errorLabel.setFont(Theme.bodySm());
        errorLabel.setForeground(Theme.ERROR_TEXT);
        errorCard.add(errorLabel);
        errorCard.setVisible(false);

        card.add(iconBox);
        card.add(Box.createVerticalStrut(16));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(usernameHeaderRow);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));
        card.add(passwordHeaderRow);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(16));
        card.add(errorCard);

        JLabel helpText = new JLabel("Need help accessing your account? Contact IT Support.", SwingConstants.CENTER);
        helpText.setFont(Theme.bodySm());
        helpText.setForeground(Theme.ON_SURFACE_VARIANT);
        helpText.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerColumn.add(card);
        centerColumn.add(Box.createVerticalStrut(16));
        centerColumn.add(helpText);

        add(centerColumn, new GridBagConstraints());
    }

    private JLabel fieldLabel(String text){
        JLabel label = new JLabel(text);
        label.setFont(Theme.bodySm().deriveFont(Font.BOLD));
        label.setForeground(Theme.ON_SURFACE);
        return label;
    }

    public String getUsername(){
        return usernameField.getText();
    }

    public String getPassword(){
        return passwordField.getText();
    }

    public void showError(String message){
        errorLabel.setText(message);
        errorCard.setVisible(true);
        revalidate();
        repaint();
    }

    public void clearError(){
        errorCard.setVisible(false);
        revalidate();
        repaint();
    }

    public void addLoginListener(ActionListener listener){
        loginButton.addActionListener(listener);
        // Enter في حقل اسم المستخدم ينقل التركيز لكلمة المرور (ولا يُرسل النموذج)،
        // وEnter في حقل كلمة المرور هو الذي يُنفّذ تسجيل الدخول فعليًا.
        usernameField.getField().addActionListener(e -> passwordField.getField().requestFocusInWindow());
        passwordField.getField().addActionListener(listener);
    }

    // ===== مؤشر التحميل أثناء محاولة تسجيل الدخول =====
    public void setLoading(boolean loading){
        loginButton.setLoading(loading);
        if (loading){
            loginButton.setIcon(null);
        } else {
            loginButton.setIcon(Icons.arrowRight(16, Color.WHITE));
        }
    }

    // يضع التركيز على حقل اسم المستخدم فور ظهور الشاشة
    public void focusFirstField(){
        javax.swing.SwingUtilities.invokeLater(() -> usernameField.getField().requestFocusInWindow());
    }
}
