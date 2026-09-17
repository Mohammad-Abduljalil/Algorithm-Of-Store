import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * حقل نص بحواف دائرية يطابق قسم "Input Fields" في DESIGN.md:
 * حدود رفيعة رمادية افتراضيًا، وعند التركيز تتحول الحدود للأزرق مع توهج ناعم حولها.
 * يدعم نصًا توضيحيًا (Placeholder) يظهر فقط عند الفراغ وعدم التركيز.
 */
public class RoundedTextField extends JTextField {

    private String placeholder;
    private boolean focused = false;

    public RoundedTextField(String placeholder){
        this.placeholder = placeholder;
        commonSetup();
    }

    protected void commonSetup(){
        setOpaque(false);
        setFont(Theme.bodyMd());
        setForeground(Theme.ON_SURFACE);
        setBorder(new EmptyBorder(10, 14, 10, 14)); // مساحة داخلية تتيح مكانًا للحواف الدائرية المرسومة يدويًا
        setCaretColor(Theme.ACCENT_BLUE);

        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e){ focused = true; repaint(); }
            @Override public void focusLost(FocusEvent e){ focused = false; repaint(); }
        });

        getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e){ repaint(); }
            @Override public void removeUpdate(DocumentEvent e){ repaint(); }
            @Override public void changedUpdate(DocumentEvent e){ repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // توهج ناعم حول الحقل عند التركيز (محاكاة box-shadow في DESIGN.md)
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

        // رسم النص التوضيحي (Placeholder) لو الحقل فارغ وغير مُركَّز عليه
        if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty()){
            Graphics2D pg = (Graphics2D) g.create();
            pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pg.setColor(Theme.OUTLINE);
            pg.setFont(getFont());
            FontMetrics fm = pg.getFontMetrics();
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            pg.drawString(placeholder, getInsets().left, textY);
            pg.dispose();
        }
    }

    // نسخة كلمة المرور من نفس المكوّن (لإخفاء النص المُدخَل)
    public static class Password extends JPasswordField {
        private String placeholder;
        private boolean focused = false;

        public Password(String placeholder){
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(Theme.bodyMd());
            setForeground(Theme.ON_SURFACE);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setCaretColor(Theme.ACCENT_BLUE);
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e){ focused = true; repaint(); }
                @Override public void focusLost(FocusEvent e){ focused = false; repaint(); }
            });
            getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e){ repaint(); }
                @Override public void removeUpdate(DocumentEvent e){ repaint(); }
                @Override public void changedUpdate(DocumentEvent e){ repaint(); }
            });
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

            if (getPassword().length == 0 && placeholder != null && !placeholder.isEmpty()){
                Graphics2D pg = (Graphics2D) g.create();
                pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                pg.setColor(Theme.OUTLINE);
                pg.setFont(getFont());
                FontMetrics fm = pg.getFontMetrics();
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                pg.drawString(placeholder, getInsets().left, textY);
                pg.dispose();
            }
        }
    }
}
