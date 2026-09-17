import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

/**
 * بطاقة مؤشر أداء (KPI): تسمية علوية بأحرف كبيرة، أيقونة دائرية ملوّنة، وقيمة كبيرة.
 * تدعم نمط "تنبيه" (alertStyle) حيث تُصبغ البطاقة كاملة بلون تحذيري (مثل "Low Stock Alerts").
 */
public class KpiCard extends Card {

    public KpiCard(String label, String value, Icon icon, Color iconCircleBg, Color iconColor, boolean alertStyle){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24)); // p-lg = 24px

        if (alertStyle){
            setCardBackground(Theme.ERROR_BG);
            setCardBorderColor(new Color(Theme.ERROR.getRed(), Theme.ERROR.getGreen(), Theme.ERROR.getBlue(), 50));
        }

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel labelText = new JLabel(label.toUpperCase());
        labelText.setFont(Theme.labelMd());
        labelText.setForeground(alertStyle ? Theme.ERROR_TEXT : Theme.ON_SURFACE_VARIANT);
        // CENTER بدل WEST: التسمية تأخذ المساحة المتبقية بعد الأيقونة، وتُختصر تلقائيًا بـ "..."
        // بدل أن تتمدّد فوق الأيقونة عند تضييق النافذة.
        labelText.setMinimumSize(new Dimension(10, 16));
        topRow.add(labelText, BorderLayout.CENTER);

        JPanel iconCircle = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconCircleBg);
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(40, 40));
        iconCircle.setLayout(new java.awt.GridBagLayout());
        iconCircle.add(new JLabel(icon));
        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        iconWrap.setOpaque(false);
        iconWrap.setPreferredSize(new Dimension(44, 40));
        iconWrap.add(iconCircle);
        topRow.add(iconWrap, BorderLayout.EAST);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(Theme.headlineLg());
        valueLabel.setForeground(alertStyle ? Theme.ERROR_TEXT : Theme.ON_SURFACE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setMinimumSize(new Dimension(10, 30));

        add(topRow);
        add(Box.createVerticalStrut(16));
        add(valueLabel);
    }
}
