import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

/**
 * زر دائري بأيقونة فقط (بلا نص)، مع تظليل خفيف عند المرور بالماوس.
 * يُستخدم في الرأس العلوي (بحث/خروج) وفي أعمدة الإجراءات بالجداول (تعديل/حذف).
 */
public class IconButton extends JButton {

    private boolean hovered = false;
    private final Color hoverColor;

    public IconButton(Icon icon){
        this(icon, Theme.SURFACE_CONTAINER_HIGH);
    }

    public IconButton(Icon icon, Color hoverColor){
        super(icon);
        this.hoverColor = hoverColor;
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new java.awt.Dimension(36, 36));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e){ hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e){ hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        if (hovered){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hoverColor);
            g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
