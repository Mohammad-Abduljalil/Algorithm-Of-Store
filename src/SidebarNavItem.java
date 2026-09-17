import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * عنصر تنقل واحد في الشريط الجانبي. عند النشاط: خلفية زرقاء (secondary-container) ونص شبه أبيض.
 * عند عدم النشاط: نص خافت (on-primary-container) يتحول لأبيض كامل + خلفية خفيفة عند المرور بالماوس.
 */
public class SidebarNavItem extends JPanel {

    private final String screenKey;
    private boolean active;
    private boolean hovered = false;
    private final JLabel iconLabel;
    private final JLabel textLabel;
    private final Icon icon;

    public SidebarNavItem(String screenKey, String text, Icon icon, boolean active){
        this.screenKey = screenKey;
        this.active = active;
        this.icon = icon;

        setOpaque(false);
        setLayout(new BorderLayout(12, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        iconLabel = new JLabel(icon);
        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconWrap.setOpaque(false);
        iconWrap.add(iconLabel);
        add(iconWrap, BorderLayout.WEST);

        textLabel = new JLabel(text);
        textLabel.setFont(Theme.labelMd());
        add(textLabel, BorderLayout.CENTER);

        updateColors();

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e){ hovered = true; updateColors(); repaint(); }
            @Override public void mouseExited(MouseEvent e){ hovered = false; updateColors(); repaint(); }
        });
    }

    public String getScreenKey(){
        return screenKey;
    }

    public void setActive(boolean active){
        this.active = active;
        updateColors();
        repaint();
    }

    private void updateColors(){
        Color textColor;
        if (active){
            textColor = Theme.ON_SECONDARY_CONTAINER;
        } else if (hovered){
            textColor = Color.WHITE;
        } else {
            textColor = Theme.ON_PRIMARY_CONTAINER;
        }
        textLabel.setForeground(textColor);
    }

    public void addNavigationListener(Consumer<String> onClick){
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e){ onClick.accept(screenKey); }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (active){
            g2.setColor(Theme.SECONDARY_CONTAINER);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS_DEFAULT, Theme.RADIUS_DEFAULT));
        } else if (hovered){
            g2.setColor(new Color(255, 255, 255, 25));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS_DEFAULT, Theme.RADIUS_DEFAULT));
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
