import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * بطاقة محتوى بحواف دائرية (تطابق "Level 1 Surface" في DESIGN.md):
 * خلفية بيضاء، حدود رفيعة فاتحة، بلا ظل افتراضيًا (تصميم "هادئ" وليس مسطحًا بالكامل ولا مُبالغًا في العمق).
 */
public class Card extends JPanel {

    private int cornerRadius = Theme.RADIUS_DEFAULT;
    private Color backgroundColorOverride = Theme.SURFACE_CONTAINER_LOWEST;
    private Color borderColorOverride = Theme.CARD_BORDER;

    public Card(){
        setOpaque(false); // نرسم الخلفية يدويًا في paintComponent حتى تكون الزوايا دائرية فعليًا
    }

    public void setCornerRadius(int radius){
        this.cornerRadius = radius;
        repaint();
    }

    public void setCardBackground(Color color){
        this.backgroundColorOverride = color;
        repaint();
    }

    public void setCardBorderColor(Color color){
        this.borderColorOverride = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        RoundRectangle2D.Float shape = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, cornerRadius, cornerRadius);

        g2.setColor(backgroundColorOverride);
        g2.fill(shape);

        if (borderColorOverride != null){
            g2.setColor(borderColorOverride);
            g2.draw(shape);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
