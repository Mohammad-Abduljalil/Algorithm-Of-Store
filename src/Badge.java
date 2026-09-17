import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * شارة حالة دائرية (Pill/Chip) تطابق قسم "Status Chips" في DESIGN.md:
 * خلفية بلون دلالي خافت (10% تقريبًا) ونص بنفس اللون كامل الشدة.
 * أمثلة: "In Stock" أخضر، "Low Stock" كهرماني، "DENIED" أحمر.
 */
public class Badge extends JLabel {

    public enum Semantic { SUCCESS, WARNING, ERROR, NEUTRAL, INFO }

    public Badge(String text, Semantic semantic){
        super(text);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(Theme.bodySm().deriveFont(java.awt.Font.BOLD));
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 12));

        Color[] colors = colorsFor(semantic);
        setBackground(colors[0]);
        setForeground(colors[1]);
    }

    private Color[] colorsFor(Semantic semantic){
        switch (semantic){
            case SUCCESS: return new Color[]{ Theme.SUCCESS_BG, Theme.SUCCESS_TEXT };
            case WARNING: return new Color[]{ Theme.WARNING_BG, Theme.WARNING_TEXT };
            case ERROR:   return new Color[]{ Theme.ERROR_BG, Theme.ERROR_TEXT };
            case INFO:    return new Color[]{ new Color(0xDBEAFE), new Color(0x2563EB) };
            case NEUTRAL: default: return new Color[]{ Theme.SURFACE_CONTAINER, Theme.ON_SURFACE_VARIANT };
        }
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
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS_FULL, Theme.RADIUS_FULL));
        g2.dispose();
        super.paintComponent(g);
    }
}
