import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * حقل إدخال يجمع أيقونة بادئة (مثل شخص/قفل) مع النص داخل نفس الصندوق الدائري الموحّد،
 * تمامًا كما تظهر شاشة تسجيل الدخول في التصميم. يدعم أيضًا مكوّنًا لاحقًا اختياريًا
 * (مثل أيقونة "إظهار كلمة المرور" أو سهم قائمة منسدلة).
 */
public class IconInputField extends JPanel {

    private final JTextField field;
    private final String placeholder;
    private boolean focused = false;
    private JComponent trailingComponent;

    public IconInputField(Icon leadingIcon, String placeholder, boolean isPassword){
        this.placeholder = placeholder;
        setOpaque(false);
        setLayout(new BorderLayout(6, 0));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));

        if (leadingIcon != null){
            JLabel iconLabel = new JLabel(leadingIcon);
            iconLabel.setOpaque(false);
            add(iconLabel, BorderLayout.WEST);
        }

        field = isPassword ? new JPasswordField() : new JTextField();
        field.setOpaque(false);
        field.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 4, 10, 4));
        field.setFont(Theme.bodyMd());
        field.setForeground(Theme.ON_SURFACE);
        field.setCaretColor(Theme.ACCENT_BLUE);
        add(field, BorderLayout.CENTER);

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e){ focused = true; repaint(); }
            @Override public void focusLost(FocusEvent e){ focused = false; repaint(); }
        });
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e){ repaint(); }
            @Override public void removeUpdate(DocumentEvent e){ repaint(); }
            @Override public void changedUpdate(DocumentEvent e){ repaint(); }
        });
    }

    public void setTrailingComponent(JComponent component){
        if (this.trailingComponent != null){
            remove(this.trailingComponent);
        }
        this.trailingComponent = component;
        if (component != null){
            component.setOpaque(false);
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrap.setOpaque(false);
            wrap.add(component);
            add(wrap, BorderLayout.EAST);
        }
        revalidate();
        repaint();
    }

    public String getText(){
        if (field instanceof JPasswordField){
            return new String(((JPasswordField) field).getPassword());
        }
        return field.getText();
    }

    public JTextField getField(){
        return field;
    }

    @Override
    public java.awt.Dimension getPreferredSize(){
        java.awt.Dimension d = super.getPreferredSize();
        return new java.awt.Dimension(Math.max(d.width, 200), 44);
    }

    private boolean isEmpty(){
        return getText().isEmpty();
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (focused){
            g2.setColor(new Color(Theme.ACCENT_BLUE.getRed(), Theme.ACCENT_BLUE.getGreen(), Theme.ACCENT_BLUE.getBlue(), 40));
            g2.fill(new RoundRectangle2D.Float(-2, -2, getWidth() + 4, getHeight() + 4, Theme.RADIUS_DEFAULT + 2, Theme.RADIUS_DEFAULT + 2));
        }

        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS_DEFAULT, Theme.RADIUS_DEFAULT));

        g2.setColor(focused ? Theme.ACCENT_BLUE : Theme.INPUT_BORDER);
        g2.setStroke(new java.awt.BasicStroke(focused ? 2f : 1f));
        float inset = focused ? 1f : 0.5f;
        g2.draw(new RoundRectangle2D.Float(inset, inset, getWidth() - inset * 2, getHeight() - inset * 2, Theme.RADIUS_DEFAULT, Theme.RADIUS_DEFAULT));
        g2.dispose();

        super.paintComponent(g);

        if (isEmpty() && placeholder != null){
            Graphics2D pg = (Graphics2D) g.create();
            pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pg.setColor(Theme.OUTLINE);
            pg.setFont(field.getFont());
            FontMetrics fm = pg.getFontMetrics();
            int textX = field.getX() + 4;
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            pg.drawString(placeholder, textX, textY);
            pg.dispose();
        }
    }
}
