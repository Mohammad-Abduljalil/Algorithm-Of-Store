import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * شاشة لوحة التحكم الرئيسية (Dashboard)، مربوطة ببيانات حقيقية من AppContext:
 * عدد المنتجات، قيمة المخزون، تنبيهات المخزون المنخفض، عدد الشحنات، جدول أدنى مخزون فعلي،
 * وآخر عمليات من سجل AuditLog الحقيقي.
 */
public class DashboardPanel extends JPanel {

    private final AppContext ctx;

    public DashboardPanel(AppContext ctx){
        this.ctx = ctx;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(pageHeader());
        content.add(Box.createVerticalStrut(32));
        content.add(kpiRow());
        content.add(Box.createVerticalStrut(20));
        content.add(bentoGrid());

        content.add(Box.createVerticalGlue()); // يدفع المحتوى للأعلى بدل تمدّد البطاقات عموديًا
        add(content, BorderLayout.CENTER);
    }

    // يُعاد بناء الشاشة بالكامل عند العودة إليها (بيانات قد تكون تغيّرت)
    public void refresh(){
        removeAll();
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(pageHeader());
        content.add(Box.createVerticalStrut(32));
        content.add(kpiRow());
        content.add(Box.createVerticalStrut(20));
        content.add(bentoGrid());
        content.add(Box.createVerticalGlue()); // يدفع المحتوى للأعلى بدل تمدّد البطاقات عموديًا
        add(content, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel pageHeader(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Dashboard");
        title.setFont(Theme.display());
        title.setForeground(Theme.ON_SURFACE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Overview of your inventory and daily operations.");
        subtitle.setFont(Theme.bodyMd());
        subtitle.setForeground(Theme.ON_SURFACE_VARIANT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        return panel;
    }

    // ===================== حساب البيانات الحقيقية =====================

    private int computeTotalProducts(){
        return ctx.productManagement.listOfProduct.size();
    }

    private float computeStoreValue(){
        float total = 0;
        for (Product p : ctx.productManagement.listOfProduct){
            total += p.getQuantityOfProduct() * p.getPriceOfProduct();
        }
        return total;
    }

    private List<Product> computeLowStockProducts(){
        List<Product> result = new ArrayList<>();
        for (Product p : ctx.productManagement.listOfProduct){
            if (p.isLowStock()) result.add(p);
        }
        return result;
    }

    private int computeActiveShipments(){
        return ctx.shipmentsRegisters.getListOfShipment() == null ? 0 : ctx.shipmentsRegisters.getListOfShipment().size();
    }

    private JPanel kpiRow(){
        JPanel row = new JPanel(new GridLayout(1, 4, 20, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        int totalProducts = computeTotalProducts();
        float storeValue = computeStoreValue();
        int lowStockCount = computeLowStockProducts().size();
        int activeShipments = computeActiveShipments();

        row.add(new KpiCard("Total Products", String.valueOf(totalProducts), Icons.archiveBox(20, Theme.PRIMARY),
                Theme.PRIMARY_FIXED, Theme.PRIMARY, false));
        row.add(new KpiCard("Store Value", String.format("$%,.2f", storeValue), Icons.dollarCircle(20, Theme.SECONDARY_LINK),
                Theme.SECONDARY_FIXED, Theme.SECONDARY_LINK, false));
        row.add(new KpiCard("Low Stock Alerts", String.valueOf(lowStockCount), Icons.warningTriangle(20, Theme.ERROR_TEXT),
                new Color(Theme.ERROR.getRed(), Theme.ERROR.getGreen(), Theme.ERROR.getBlue(), 25), Theme.ERROR_TEXT, lowStockCount > 0));
        row.add(new KpiCard("Active Shipments", String.valueOf(activeShipments), Icons.truck(20, Theme.TERTIARY),
                Theme.TERTIARY_FIXED, Theme.TERTIARY, false));
        return row;
    }

    private JPanel bentoGrid(){
        JPanel grid = new JPanel(new BorderLayout(20, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        grid.add(lowStockWidget(), BorderLayout.CENTER);

        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.setPreferredSize(new Dimension(340, 400));
        rightWrap.setMinimumSize(new Dimension(260, 200));
        rightWrap.add(recentActivityWidget(), BorderLayout.CENTER);
        grid.add(rightWrap, BorderLayout.EAST);

        return grid;
    }

    // ===================== ودجت: المنتجات منخفضة المخزون (بيانات حقيقية) =====================
    private Card lowStockWidget(){
        Card card = new Card();
        card.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.SURFACE_CONTAINER_HIGH),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        JLabel title = new JLabel("Low Stock Products");
        title.setFont(Theme.headlineMd());
        title.setForeground(Theme.ON_SURFACE);
        header.add(title, BorderLayout.WEST);
        JLabel viewAll = new JLabel("View All");
        viewAll.setFont(Theme.labelMd());
        viewAll.setForeground(Theme.SECONDARY_LINK);
        header.add(viewAll, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        JPanel tableBody = new JPanel();
        tableBody.setOpaque(false);
        tableBody.setLayout(new BoxLayout(tableBody, BoxLayout.Y_AXIS));

        List<Product> lowStock = computeLowStockProducts();
        if (lowStock.isEmpty()){
            tableBody.add(emptyStateLabel("No products are currently low on stock. Good job!"));
        } else {
            tableBody.add(lowStockHeaderRow());
            int shown = 0;
            for (Product p : lowStock){
                if (shown >= 5) break; // نعرض أول 5 فقط في الودجت المصغّرة
                tableBody.add(lowStockDataRow(p.getNameOfProduct(), String.valueOf(p.getQuantityOfProduct()),
                        String.valueOf(p.getMinimumStockThreshold())));
                shown++;
            }
        }

        card.add(tableBody, BorderLayout.CENTER);
        return card;
    }

    private JLabel emptyStateLabel(String text){
        JLabel label = new JLabel(text);
        label.setFont(Theme.bodySm());
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        label.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        return label;
    }

    private JPanel lowStockHeaderRow(){
        JPanel row = new JPanel(new GridLayout(1, 3));
        row.setBackground(Theme.SURFACE_CONTAINER_LOW);
        row.setOpaque(true);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.SURFACE_CONTAINER_HIGH),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.add(columnLabel("Product Name", SwingConstants.LEFT));
        row.add(columnLabel("Quantity", SwingConstants.LEFT));
        row.add(columnLabel("Threshold", SwingConstants.RIGHT));
        return row;
    }

    private JLabel columnLabel(String text, int alignment){
        JLabel label = new JLabel(text, alignment);
        label.setFont(Theme.labelMd());
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        return label;
    }

    private JPanel lowStockDataRow(String name, String quantity, String threshold){
        JPanel row = new JPanel(new GridLayout(1, 3));
        row.setBackground(Theme.SURFACE_CONTAINER_LOWEST);
        row.setOpaque(true);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(Theme.bodyMd().deriveFont(java.awt.Font.BOLD));
        nameLabel.setForeground(Theme.ON_SURFACE);
        row.add(nameLabel);

        JPanel qtyWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        qtyWrap.setOpaque(false);
        qtyWrap.add(new DotBadge(quantity, Theme.ERROR_BG, Theme.ERROR_TEXT));
        row.add(qtyWrap);

        JLabel thresholdLabel = new JLabel(threshold, SwingConstants.RIGHT);
        thresholdLabel.setFont(Theme.monoLabel());
        thresholdLabel.setForeground(Theme.ON_SURFACE_VARIANT);
        row.add(thresholdLabel);

        return row;
    }

    // ===================== ودجت: النشاط الأخير (من AuditLog الحقيقي) =====================
    private Card recentActivityWidget(){
        Card card = new Card();
        card.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.SURFACE_CONTAINER_HIGH),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        JLabel title = new JLabel("Recent Activity");
        title.setFont(Theme.headlineMd());
        title.setForeground(Theme.ON_SURFACE);
        header.add(title, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        List<String> recent = AuditLog.getRecentEntries(5);
        if (recent.isEmpty()){
            body.add(emptyStateLabel("No recent activity yet."));
        } else {
            for (int i = 0; i < recent.size(); i++){
                String[] parts = recent.get(i).split("\\s*\\|\\s*", 4);
                // الصيغة: timestamp | username | action | [details]
                String username = parts.length > 1 ? parts[1] : "?";
                String action = parts.length > 2 ? parts[2] : recent.get(i);
                String result = parts.length > 3 ? parts[3] : "";
                boolean negative = result.startsWith("DENIED") || result.startsWith("FAILED");
                body.add(activityItem(username, action, relativeTime(parts.length > 0 ? parts[0] : ""),
                        negative ? Theme.ERROR : Theme.PRIMARY));
                if (i < recent.size() - 1) body.add(Box.createVerticalStrut(16));
            }
        }
        card.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.SURFACE_CONTAINER_HIGH),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        JLabel footerLink = new JLabel("View Full Log", SwingConstants.CENTER);
        footerLink.setFont(Theme.labelMd());
        footerLink.setForeground(Theme.SECONDARY_LINK);
        footerLink.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setLayout(new BorderLayout());
        footer.add(footerLink, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    // تحويل تقريبي بسيط لطابع زمني نصي إلى عرض "منذ..." (بدون مكتبات خارجية إضافية)
    private String relativeTime(String timestamp){
        try {
            java.time.LocalDateTime then = java.time.LocalDateTime.parse(timestamp.replace(" ", "T"));
            java.time.Duration d = java.time.Duration.between(then, java.time.LocalDateTime.now());
            long minutes = d.toMinutes();
            if (minutes < 1) return "just now";
            if (minutes < 60) return minutes + " min ago";
            long hours = d.toHours();
            if (hours < 24) return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
            return d.toDays() + " day" + (d.toDays() == 1 ? "" : "s") + " ago";
        } catch (Exception e){
            return timestamp;
        }
    }

    private JPanel activityItem(String boldPrefix, String text, String time, Color dotColor){
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel dotWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        dotWrap.setOpaque(false);
        JPanel dot = new JPanel(){
            @Override protected void paintComponent(java.awt.Graphics g){
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(10, 10));
        dotWrap.add(dot);
        row.add(dotWrap, BorderLayout.WEST);

        JPanel textCol = new JPanel();
        textCol.setOpaque(false);
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        String html = boldPrefix != null
                ? "<html><b>" + boldPrefix + "</b> " + text + "</html>"
                : "<html>" + text + "</html>";
        JLabel textLabel = new JLabel(html);
        textLabel.setFont(Theme.bodySm());
        textLabel.setForeground(Theme.ON_SURFACE);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(Theme.labelMd());
        timeLabel.setForeground(Theme.ON_SURFACE_VARIANT);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textCol.add(textLabel);
        textCol.add(Box.createVerticalStrut(2));
        textCol.add(timeLabel);
        row.add(textCol, BorderLayout.CENTER);

        return row;
    }
}
