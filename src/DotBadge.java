import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * شارة دائرية (Pill) تحتوي نقطة صغيرة ملوّنة قبل الرقم/النص، تطابق شارات الكمية
 * في جدول "Low Stock Products" بالتصميم الأصلي.
 */
public class DotBadge extends JLabel {

    private final Color bg;
    private final Color fg;

    public DotBadge(String text, Color bg, Color fg){
        super("  " + text); // مسافة تفسح مكانًا للنقطة المرسومة يدويًا قبل النص
        this.bg = bg;
        this.fg = fg;
        setFont(Theme.monoLabel());
        setForeground(fg);
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 18, 4, 10));
    }

    @Override
    public Dimension getPreferredSize(){
        FontMetrics fm = getFontMetrics(getFont());
        int textWidth = fm.stringWidth(getText());
        return new Dimension(textWidth + getInsets().left + getInsets().right, fm.getHeight() + 8);
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS_FULL, Theme.RADIUS_FULL));
        g2.setColor(fg);
        g2.fill(new Ellipse2D.Float(9, getHeight() / 2f - 2.5f, 5, 5));
        g2.dispose();
        super.paintComponent(g);
    }
}
