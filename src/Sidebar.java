import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * الشريط الجانبي الثابت (240px)، مطابق لكود Stitch: شعار التطبيق، قائمة تنقل بـ 6 عناصر
 * (آخرها "Settings" مثبّت أسفل القائمة عبر mt-auto)، وبطاقة ملف المستخدم الحالي أسفل كل شيء.
 */
public class Sidebar extends JPanel {

    public static final String KEY_DASHBOARD = "dashboard";
    public static final String KEY_PRODUCTS = "products";
    public static final String KEY_SHIPMENTS = "shipments";
    public static final String KEY_REPORTS = "reports";
    public static final String KEY_ADMINISTRATION = "administration";
    public static final String KEY_SETTINGS = "settings";

    private final Map<String, SidebarNavItem> items = new LinkedHashMap<>();
    private String activeKey = KEY_DASHBOARD;

    public Sidebar(String userName, Role userRole){
        setPreferredSize(new Dimension(240, 100)); // العرض ثابت والارتفاع يتمدّد مع النافذة
        setMinimumSize(new Dimension(240, 100));
        setMaximumSize(new Dimension(240, Integer.MAX_VALUE));
        setBackground(Theme.PRIMARY_CONTAINER);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        add(brandBlock());
        add(Box.createVerticalStrut(8));

        addNavItem(KEY_DASHBOARD, "Dashboard", Icons.dashboardGrid(20, Color.WHITE));
        addNavItem(KEY_PRODUCTS, "Products", Icons.archiveBox(20, Theme.ON_PRIMARY_CONTAINER));
        addNavItem(KEY_SHIPMENTS, "Shipments & Orders", Icons.truck(20, Theme.ON_PRIMARY_CONTAINER));
        addNavItem(KEY_REPORTS, "Reports", Icons.barChart(20, Theme.ON_PRIMARY_CONTAINER));
        addNavItem(KEY_ADMINISTRATION, "Administration", Icons.shield(20, Theme.ON_PRIMARY_CONTAINER));

        add(Box.createVerticalGlue()); // يدفع "Settings" للأسفل (يطابق mt-auto في الكود الأصلي)
        addNavItem(KEY_SETTINGS, "Settings", Icons.gear(20, Theme.ON_PRIMARY_CONTAINER));

        add(Box.createVerticalStrut(16));
        add(profileBlock(userName, userRole));

        setActive(KEY_DASHBOARD);
    }

    private JPanel brandBlock(){
        JPanel outer = new JPanel(new java.awt.BorderLayout(10, 0));
        outer.setOpaque(false);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 20, 24, 20));
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel logoCircle = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.SECONDARY_LINK);
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoCircle.setOpaque(false);
        logoCircle.setPreferredSize(new Dimension(32, 32));
        logoCircle.setLayout(new java.awt.GridBagLayout());
        JLabel logoLetter = new JLabel("L");
        logoLetter.setFont(Theme.headlineMd());
        logoLetter.setForeground(Color.WHITE);
        logoCircle.add(logoLetter);

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("LogisticsPro");
        title.setFont(Theme.headlineMd());
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Enterprise Suite");
        subtitle.setFont(Theme.labelMd());
        subtitle.setForeground(Theme.ON_PRIMARY_CONTAINER);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(2));
        panel.add(subtitle);

        outer.add(logoCircle, java.awt.BorderLayout.WEST);
        outer.add(panel, java.awt.BorderLayout.CENTER);
        return outer;
    }

    private void addNavItem(String key, String text, javax.swing.Icon icon){
        SidebarNavItem item = new SidebarNavItem(key, text, icon, key.equals(activeKey));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new java.awt.BorderLayout());
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 12, 4, 12));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        wrap.add(item, java.awt.BorderLayout.CENTER);
        add(wrap);
        items.put(key, item);
    }

    public void setActive(String key){
        this.activeKey = key;
        for (Map.Entry<String, SidebarNavItem> entry : items.entrySet()){
            entry.getValue().setActive(entry.getKey().equals(key));
        }
    }

    public void addNavigationListener(Consumer<String> onNavigate){
        for (SidebarNavItem item : items.values()){
            item.addNavigationListener(onNavigate);
        }
    }

    private JPanel profileBlock(String userName, Role role){
        JPanel panel = new JPanel(new java.awt.BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 0, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel avatar = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.hex("#545f73")); // surface-tint
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setLayout(new java.awt.GridBagLayout());
        avatar.add(new JLabel(Icons.user(18, Color.WHITE)));

        JPanel textCol = new JPanel();
        textCol.setOpaque(false);
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(Theme.labelMd());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Badge roleBadge = new Badge(role == Role.ADMIN ? "Admin" : "Employee", Badge.Semantic.INFO);
        roleBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(roleBadge);
        badgeWrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        textCol.add(nameLabel);
        textCol.add(Box.createVerticalStrut(4));
        textCol.add(badgeWrap);

        panel.add(avatar, java.awt.BorderLayout.WEST);
        panel.add(textCol, java.awt.BorderLayout.CENTER);
        return panel;
    }
}
