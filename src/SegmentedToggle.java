import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * تبديل مقسّم بخيارين (مثل "Admin" / "Employee")، يطابق مكوّن "Role Assignment" في التصميم:
 * حاوية رمادية فاتحة، والخيار المُختار يظهر كبطاقة بيضاء بحدود وظل خفيف.
 */
public class SegmentedToggle extends JPanel {

    private final String[] options;
    private int selectedIndex;
    private final JPanel[] segments;
    private Consumer<Integer> onChange;

    public SegmentedToggle(String[] options, int initialSelected){
        this.options = options;
        this.selectedIndex = initialSelected;
        this.segments = new JPanel[options.length];

        setOpaque(false);
        setLayout(new GridLayout(1, options.length, 4, 0));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        for (int i = 0; i < options.length; i++){
            final int index = i;
            JPanel segment = new JPanel(){
                @Override protected void paintComponent(Graphics g){
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (index == selectedIndex){
                        g2.setColor(Color.WHITE);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS_SM, Theme.RADIUS_SM));
                        g2.setColor(Theme.CARD_BORDER);
                        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, Theme.RADIUS_SM, Theme.RADIUS_SM));
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            segment.setOpaque(false);
            segment.setLayout(new java.awt.GridBagLayout());
            segment.setCursor(new Cursor(Cursor.HAND_CURSOR));
            JLabel label = new JLabel(options[i]);
            label.setFont(Theme.labelMd());
            label.setForeground(index == selectedIndex ? Theme.PRIMARY : Theme.ON_SURFACE_VARIANT);
            segment.add(label);
            segment.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e){ setSelectedIndex(index); }
            });
            segments[i] = segment;
            add(segment);
        }
    }

    public void setSelectedIndex(int index){
        this.selectedIndex = index;
        for (int i = 0; i < segments.length; i++){
            JLabel label = (JLabel) segments[i].getComponent(0);
            label.setForeground(i == selectedIndex ? Theme.PRIMARY : Theme.ON_SURFACE_VARIANT);
            segments[i].repaint();
        }
        if (onChange != null) onChange.accept(index);
    }

    public int getSelectedIndex(){
        return selectedIndex;
    }

    public String getSelectedOption(){
        return options[selectedIndex];
    }

    public void setOnChange(Consumer<Integer> onChange){
        this.onChange = onChange;
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.SURFACE_CONTAINER_LOW);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS_DEFAULT, Theme.RADIUS_DEFAULT));
        g2.setColor(Theme.CARD_BORDER);
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, Theme.RADIUS_DEFAULT, Theme.RADIUS_DEFAULT));
        g2.dispose();
        super.paintComponent(g);
    }
}
