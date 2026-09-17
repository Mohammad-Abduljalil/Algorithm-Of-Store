import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * شريط تبويبات أفقي بسيط، بخط سفلي أزرق تحت التبويب النشط، يطابق شاشة Settings.
 */
public class TabBar extends JPanel {

    private final String[] tabs;
    private int selectedIndex;
    private final JPanel[] tabPanels;
    private final JLabel[] tabLabels;
    private Consumer<Integer> onChange;

    public TabBar(String[] tabs, int initialSelected){
        this.tabs = tabs;
        this.selectedIndex = initialSelected;
        this.tabPanels = new JPanel[tabs.length];
        this.tabLabels = new JLabel[tabs.length];

        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 24, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER));

        for (int i = 0; i < tabs.length; i++){
            final int index = i;
            JPanel tabPanel = new JPanel(new BorderLayout());
            tabPanel.setOpaque(false);
            tabPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            JLabel label = new JLabel(tabs[i]);
            label.setFont(Theme.bodyMd().deriveFont(java.awt.Font.BOLD));
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            tabPanel.add(label, BorderLayout.CENTER);
            JPanel underline = new JPanel();
            underline.setPreferredSize(new java.awt.Dimension(10, 2));
            tabPanel.add(underline, BorderLayout.SOUTH);
            tabPanel.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e){ setSelectedIndex(index); }
            });
            tabPanels[i] = tabPanel;
            tabLabels[i] = label;
            add(tabPanel);
            updateTabStyle(i, underline);
        }
    }

    private void updateTabStyle(int index, JPanel underline){
        boolean active = index == selectedIndex;
        tabLabels[index].setForeground(active ? Theme.ACCENT_BLUE : Theme.ON_SURFACE_VARIANT);
        underline.setBackground(active ? Theme.ACCENT_BLUE : new java.awt.Color(0, 0, 0, 0));
        underline.setOpaque(active);
    }

    public void setSelectedIndex(int index){
        this.selectedIndex = index;
        for (int i = 0; i < tabPanels.length; i++){
            JPanel underline = (JPanel) tabPanels[i].getComponent(1);
            updateTabStyle(i, underline);
            tabPanels[i].repaint();
        }
        if (onChange != null) onChange.accept(index);
    }

    public void setOnChange(Consumer<Integer> onChange){
        this.onChange = onChange;
    }
}
