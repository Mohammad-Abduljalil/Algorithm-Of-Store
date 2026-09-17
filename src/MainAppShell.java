import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;

/**
 * الغلاف الرئيسي لكل شاشات التطبيق بعد تسجيل الدخول:
 * شريط جانبي ثابت (240px) + رأس علوي ثابت (64px) + منطقة محتوى قابلة للتمرير
 * تُبدَّل شاشاتها عبر CardLayout حسب العنصر المُختار من الشريط الجانبي.
 */
public class MainAppShell extends JPanel {

    private final Sidebar sidebar;
    private final HeaderBar headerBar;
    private final JPanel contentArea;
    private final CardLayout cardLayout;

    public MainAppShell(String userName, Role userRole){
        setLayout(new BorderLayout());

        sidebar = new Sidebar(userName, userRole);
        add(sidebar, BorderLayout.WEST);

        JPanel rightColumn = new JPanel(new BorderLayout());
        headerBar = new HeaderBar();
        rightColumn.add(headerBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        // ScrollableContentPanel بدل JPanel عادي: يمنع تمدّد المحتوى أفقيًا خارج النافذة
        contentArea = new ScrollableContentPanel(cardLayout);
        contentArea.setBackground(Theme.BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        rightColumn.add(scrollPane, BorderLayout.CENTER);

        add(rightColumn, BorderLayout.CENTER);

        sidebar.addNavigationListener(this::showScreen);
    }

    public void registerScreen(String key, JPanel screen){
        contentArea.add(screen, key);
    }

    public void showScreen(String key){
        cardLayout.show(contentArea, key);
        sidebar.setActive(key);
    }

    public HeaderBar getHeaderBar(){
        return headerBar;
    }

    public Sidebar getSidebar(){
        return sidebar;
    }
}
