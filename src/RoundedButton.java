import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * زر بحواف دائرية يطابق قسم "Buttons" في DESIGN.md:
 * PRIMARY: خلفية زرقاء (#2563EB) ونص أبيض - للإجراءات الرئيسية (حفظ، إضافة، دخول).
 * SECONDARY: خلفية بيضاء وحدود رمادية فاتحة ونص كُحلي - للإجراءات الثانوية (إلغاء).
 * DANGER: خلفية حمراء - للإجراءات الحرجة (حذف نهائي)، غير مذكورة صراحة في DESIGN.md لكن تتبع نفس منطق الألوان الدلالية.
 */
public class RoundedButton extends JButton {

    public enum Variant { PRIMARY, SECONDARY, DANGER }

    private final Variant variant;
    private boolean hovered = false;
    private Color customBg = null;
    private Color customFg = null;

    // ===== حالة التحميل (Spinner) =====
    private boolean loading = false;
    private int spinnerAngle = 0;
    private javax.swing.Timer spinnerTimer;
    private String textBeforeLoading;

    /**
     * يُفعّل/يُعطّل مؤشر التحميل الدائري داخل الزر. أثناء التحميل يُعطَّل الزر ويُخفى نصه
     * وتدور دائرة ناقصة مكانه، ثم يعود لحالته الأصلية عند setLoading(false).
     */
    public void setLoading(boolean loading){
        if (this.loading == loading) return;
        this.loading = loading;
        if (loading){
            textBeforeLoading = getText();
            setText("");
            setEnabled(false);
            if (spinnerTimer == null){
                spinnerTimer = new javax.swing.Timer(60, e -> {
                    spinnerAngle = (spinnerAngle + 30) % 360;
                    repaint();
                });
            }
            spinnerTimer.start();
        } else {
            if (spinnerTimer != null) spinnerTimer.stop();
            if (textBeforeLoading != null) setText(textBeforeLoading);
            setEnabled(true);
        }
        repaint();
    }

    public boolean isLoading(){
        return loading;
    }

    public RoundedButton(String text, Variant variant){
        super(text);
        this.variant = variant;
        commonSetup();
    }

    // مُنشئ إضافي لدعم ألوان مخصّصة غير الأنماط الثلاثة الجاهزة (مثل زر Export بأزرق مختلف قليلاً)
    public RoundedButton(String text, Color background, Color foreground){
        super(text);
        this.variant = Variant.PRIMARY;
        this.customBg = background;
        this.customFg = foreground;
        commonSetup();
    }

    private void commonSetup(){
        setFont(Theme.bodyMd().deriveFont(java.awt.Font.BOLD));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.CENTER);
        setMargin(new java.awt.Insets(10, 16, 10, 16)); // padding: 10px 16px كما في DESIGN.md

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e){ hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e){ hovered = false; repaint(); }
        });
    }

    private Color backgroundFor(){
        if (!isEnabled()) return Theme.SURFACE_CONTAINER;
        if (customBg != null) return hovered ? customBg.darker() : customBg;
        switch (variant){
            case PRIMARY: return hovered ? Theme.ACCENT_BLUE_HOVER : Theme.ACCENT_BLUE;
            case DANGER: return hovered ? Theme.ERROR.darker() : Theme.ERROR;
            case SECONDARY: default: return hovered ? Theme.SURFACE_CONTAINER_LOW : Theme.SURFACE_CONTAINER_LOWEST;
        }
    }

    private Color foregroundFor(){
        if (!isEnabled()) return Theme.ON_SURFACE_VARIANT;
        if (customFg != null) return customFg;
        switch (variant){
            case PRIMARY: case DANGER: return Color.WHITE;
            case SECONDARY: default: return Theme.PRIMARY_CONTAINER;
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D.Float shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS_DEFAULT, Theme.RADIUS_DEFAULT);
        g2.setColor(backgroundFor());
        g2.fill(shape);

        if (variant == Variant.SECONDARY){
            g2.setColor(Theme.CARD_BORDER);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, Theme.RADIUS_DEFAULT, Theme.RADIUS_DEFAULT));
        }

        // مؤشر التحميل: قوس دائري ناقص يدور حول مركز الزر
        if (loading){
            int d = Math.min(18, getHeight() - 14);
            int cx = (getWidth() - d) / 2;
            int cy = (getHeight() - d) / 2;
            g2.setColor(new Color(255, 255, 255, 70));
            g2.setStroke(new java.awt.BasicStroke(2.5f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
            g2.drawOval(cx, cy, d, d);
            g2.setColor(customFg != null ? customFg : Color.WHITE);
            g2.draw(new java.awt.geom.Arc2D.Float(cx, cy, d, d, spinnerAngle, 100, java.awt.geom.Arc2D.OPEN));
        }

        g2.dispose();
        setForeground(foregroundFor());
        super.paintComponent(g);
    }
}
