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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * شاشة التقارير (Analytics Overview)، مطابقة لكود Stitch:
 * عنوان + زري "آخر 30 يوم"/"تصدير" -> شبكة Bento من 4 ودجات (رسم بياني كبير + بطاقة تكلفة + قائمة مرتّبة + أشرطة تقدّم).
 */
public class ReportsPanel extends JPanel {

    private final AppContext ctx;

    public ReportsPanel(AppContext ctx){
        this.ctx = ctx;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(pageHeader());
        content.add(Box.createVerticalStrut(24));
        content.add(topRow());
        content.add(Box.createVerticalStrut(20));
        content.add(bottomRow());

        content.add(Box.createVerticalGlue()); // يدفع المحتوى للأعلى بدل تمدّد البطاقات عموديًا
        add(content, BorderLayout.CENTER);
    }

    // ===================== حساب البيانات الحقيقية =====================

    private float computeStoreValue(){
        float total = 0;
        for (Product p : ctx.productManagement.listOfProduct){
            total += p.getQuantityOfProduct() * p.getPriceOfProduct();
        }
        return total;
    }

    private float computeTotalOrdersCost(){
        float total = 0;
        for (Order o : ctx.orders.listOfOrder){
            total += o.getShipment().getShipmentCost();
        }
        return total;
    }

    private List<Shipment> computeTopShipments(int n){
        List<Shipment> sorted = new java.util.ArrayList<>(ctx.shipmentsRegisters.getListOfShipment() == null
                ? java.util.Collections.emptyList() : ctx.shipmentsRegisters.getListOfShipment());
        sorted.sort((a, b) -> Float.compare(b.getShipmentCost(), a.getShipmentCost()));
        return sorted.subList(0, Math.min(n, sorted.size()));
    }

    private List<ReportModels.CategoryEntry> computeCategoryBreakdown(){
        List<ReportModels.CategoryEntry> result = new java.util.ArrayList<>();
        Color[] palette = { new Color(0x60A5FA), new Color(0xC084FC), new Color(0x34D399),
                new Color(0xFBBF24), new Color(0xF472B6), new Color(0x818CF8) };
        int grandTotal = 0;
        java.util.Map<String, Integer> unitsByCategory = new java.util.LinkedHashMap<>();
        for (java.util.Map.Entry<String, java.util.ArrayList<Product>> entry : ctx.productManagement.categoryMap.entrySet()){
            int units = 0;
            for (Product p : entry.getValue()) units += p.getQuantityOfProduct();
            unitsByCategory.put(entry.getKey(), units);
            grandTotal += units;
        }
        int i = 0;
        for (java.util.Map.Entry<String, Integer> entry : unitsByCategory.entrySet()){
            int percent = grandTotal == 0 ? 0 : Math.round(entry.getValue() * 100f / grandTotal);
            result.add(new ReportModels.CategoryEntry(entry.getKey(), entry.getValue(), percent, palette[i % palette.length]));
            i++;
        }
        return result;
    }

    private JPanel pageHeader(){
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel titleCol = new JPanel();
        titleCol.setOpaque(false);
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Analytics Overview");
        title.setFont(Theme.display());
        title.setForeground(Theme.ON_SURFACE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Key performance metrics across inventory and shipments.");
        subtitle.setFont(Theme.bodyLg());
        subtitle.setForeground(Theme.ON_SURFACE_VARIANT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleCol.add(title);
        titleCol.add(Box.createVerticalStrut(4));
        titleCol.add(subtitle);
        row.add(titleCol, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        RoundedButton lastDays = new RoundedButton("Last 30 Days", RoundedButton.Variant.SECONDARY);
        lastDays.setIcon(Icons.calendar(14, Theme.PRIMARY_CONTAINER));
        lastDays.setIconTextGap(8);
        RoundedButton export = new RoundedButton("Export", Theme.SECONDARY_LINK, Color.WHITE);
        export.setIcon(Icons.download(14, Color.WHITE));
        export.setIconTextGap(8);
        actions.add(lastDays);
        actions.add(export);
        row.add(actions, BorderLayout.EAST);

        return row;
    }

    private JPanel topRow(){
        JPanel row = new JPanel(new BorderLayout(20, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        row.add(storeValueChartCard(), BorderLayout.CENTER);

        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.setPreferredSize(new Dimension(330, 400));
        rightWrap.setMinimumSize(new Dimension(250, 200));
        rightWrap.add(totalOrdersCostCard(), BorderLayout.CENTER);
        row.add(rightWrap, BorderLayout.EAST);

        return row;
    }

    private JPanel bottomRow(){
        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        row.add(topShipmentsCard());
        row.add(categoryBreakdownCard());
        return row;
    }

    // ===================== Widget 1: Store Value Over Time =====================
    private Card storeValueChartCard(){
        Card card = new Card();
        card.setCornerRadius(Theme.RADIUS_MD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JPanel titleCol = new JPanel();
        titleCol.setOpaque(false);
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Store Value Over Time");
        title.setFont(Theme.headlineMd());
        title.setForeground(Theme.PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Total inventory value trend");
        subtitle.setFont(Theme.bodySm());
        subtitle.setForeground(Theme.ON_SURFACE_VARIANT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleCol.add(title);
        titleCol.add(subtitle);
        top.add(titleCol, BorderLayout.WEST);
        top.add(new IconButton(Icons.moreVert(18, Theme.ON_SURFACE_VARIANT)), BorderLayout.EAST);

        JPanel valueRow = new JPanel();
        valueRow.setOpaque(false);
        valueRow.setLayout(new BoxLayout(valueRow, BoxLayout.Y_AXIS));
        valueRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueRow.setBorder(BorderFactory.createEmptyBorder(16, 0, 8, 0));
        JLabel value = new JLabel(String.format("$%,.2f", computeStoreValue()));
        value.setFont(Theme.display());
        value.setForeground(Theme.ON_SURFACE);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel trendRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        trendRow.setOpaque(false);
        trendRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel trendText = new JLabel("Current value (historical trend not tracked yet)");
        trendText.setFont(Theme.labelMd());
        trendText.setForeground(Theme.ON_SURFACE_VARIANT);
        trendRow.add(trendText);
        valueRow.add(value);
        valueRow.add(trendRow);

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(top);
        topSection.add(valueRow);

        card.add(topSection, BorderLayout.NORTH);

        LineChartWidget chart = new LineChartWidget();
        chart.setPreferredSize(new Dimension(400, 260));
        card.add(chart, BorderLayout.CENTER);

        return card;
    }

    // ===================== Widget 4: Total Orders Cost =====================
    private Card totalOrdersCostCard(){
        Card card = new Card();
        card.setCornerRadius(Theme.RADIUS_MD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("Total Orders Cost");
        title.setFont(Theme.headlineMd());
        title.setForeground(Theme.PRIMARY);
        top.add(title, BorderLayout.WEST);

        JPanel iconCircle = coloredCircle(40, Theme.PRIMARY_FIXED);
        iconCircle.add(new JLabel(Icons.info(18, Theme.PRIMARY)));
        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        iconWrap.setOpaque(false);
        iconWrap.add(iconCircle);
        top.add(iconWrap, BorderLayout.EAST);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(top);
        center.add(Box.createVerticalStrut(16));
        JLabel value = new JLabel(String.format("$%,.2f", computeTotalOrdersCost()));
        value.setFont(Theme.display());
        value.setForeground(Theme.ON_SURFACE);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(value);
        card.add(center, BorderLayout.NORTH);

        Card infoBox = new Card();
        infoBox.setCardBackground(new Color(0xECFDF5));
        infoBox.setCardBorderColor(new Color(0xD1FAE5));
        infoBox.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel infoIcon = new JLabel(Icons.trendingUp(14, Theme.SUCCESS_TEXT));
        infoBox.add(infoIcon);
        JPanel infoText = new JPanel();
        infoText.setOpaque(false);
        infoText.setLayout(new BoxLayout(infoText, BoxLayout.Y_AXIS));
        JLabel line1 = new JLabel(ctx.orders.listOfOrder.size() + " active order" + (ctx.orders.listOfOrder.size() == 1 ? "" : "s"));
        line1.setFont(Theme.bodySm().deriveFont(Font.BOLD));
        line1.setForeground(new Color(0x065F46));
        JLabel line2 = new JLabel("Sum of all shipment costs currently on order");
        line2.setFont(Theme.labelMd());
        line2.setForeground(new Color(0x059669));
        infoText.add(line1);
        infoText.add(line2);
        infoBox.add(infoText);

        JPanel bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.setOpaque(false);
        bottomWrap.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        bottomWrap.add(infoBox, BorderLayout.NORTH);
        card.add(bottomWrap, BorderLayout.SOUTH);

        return card;
    }

    private JPanel coloredCircle(int size, Color bg){
        JPanel circle = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(size, size));
        circle.setLayout(new java.awt.GridBagLayout());
        return circle;
    }

    // ===================== Widget 2: Top 3 High-Cost Shipments =====================
    private Card topShipmentsCard(){
        Card card = new Card();
        card.setCornerRadius(Theme.RADIUS_MD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        JLabel title = new JLabel("Top 3 High-Cost Shipments");
        title.setFont(Theme.headlineMd());
        title.setForeground(Theme.PRIMARY);
        header.add(title, BorderLayout.WEST);
        JLabel viewAll = new JLabel("View All");
        viewAll.setFont(Theme.labelMd());
        viewAll.setForeground(Theme.SECONDARY_LINK);
        header.add(viewAll, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        List<Shipment> topShipments = computeTopShipments(3);
        if (topShipments.isEmpty()){
            JLabel empty = new JLabel("No shipments yet.");
            empty.setFont(Theme.bodySm());
            empty.setForeground(Theme.ON_SURFACE_VARIANT);
            empty.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
            list.add(empty);
        } else {
            Color[][] medalColors = {
                    { new Color(0xFEF3C7), new Color(0xF59E0B), new Color(0xB45309) }, // ذهبي
                    { new Color(0xF3F4F6), new Color(0x9CA3AF), new Color(0x4B5563) }, // فضي
                    { new Color(0xFFEDD5), new Color(0xD97706), new Color(0x92400E) }, // برونزي
            };
            for (int i = 0; i < topShipments.size(); i++){
                Shipment s = topShipments.get(i);
                Color[] medal = medalColors[Math.min(i, medalColors.length - 1)];
                // لا يوجد حقل "حالة" فعلي في نموذج Shipment، فنعرض حالة محايدة صادقة بدل اختلاق بيانات
                ReportModels.TopShipmentEntry entry = new ReportModels.TopShipmentEntry(
                        i + 1, s.getShipmentDestination(), "#" + s.getShipmentId(),
                        s.listOfProducts.isEmpty() ? "No products" : (s.listOfProducts.size() + " product line(s)"),
                        String.format("$%,.2f", s.getShipmentCost()), "Scheduled",
                        medal[0], medal[1], medal[2], Badge.Semantic.NEUTRAL);
                list.add(topShipmentItem(entry));
                if (i < topShipments.size() - 1){
                    JPanel divider = new JPanel();
                    divider.setBackground(Theme.CARD_BORDER);
                    divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    divider.setBorder(BorderFactory.createEmptyBorder(0, 56, 0, 0));
                    list.add(Box.createVerticalStrut(8));
                    list.add(divider);
                    list.add(Box.createVerticalStrut(8));
                }
            }
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel topShipmentItem(ReportModels.TopShipmentEntry entry){
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JPanel medal = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(entry.medalBg);
                g2.fill(new Ellipse2D.Float(1, 1, getWidth() - 2, getHeight() - 2));
                g2.setColor(entry.medalBorder);
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.draw(new Ellipse2D.Float(1, 1, getWidth() - 2, getHeight() - 2));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        medal.setOpaque(false);
        medal.setPreferredSize(new Dimension(40, 40));
        medal.setLayout(new java.awt.GridBagLayout());
        JLabel rankLabel = new JLabel(String.valueOf(entry.rank));
        rankLabel.setFont(Theme.bodyMd().deriveFont(Font.BOLD));
        rankLabel.setForeground(entry.medalText);
        medal.add(rankLabel);
        row.add(medal, BorderLayout.WEST);

        JPanel infoCol = new JPanel();
        infoCol.setOpaque(false);
        infoCol.setLayout(new BoxLayout(infoCol, BoxLayout.Y_AXIS));
        JLabel destLabel = new JLabel(entry.destination);
        destLabel.setFont(Theme.bodyMd().deriveFont(Font.BOLD));
        destLabel.setForeground(Theme.PRIMARY);
        destLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel idLabel = new JLabel("ID: " + entry.shipmentId + " \u2022 " + entry.category);
        idLabel.setFont(Theme.labelMd());
        idLabel.setForeground(Theme.ON_SURFACE_VARIANT);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCol.add(destLabel);
        infoCol.add(idLabel);
        row.add(infoCol, BorderLayout.CENTER);

        JPanel rightCol = new JPanel();
        rightCol.setOpaque(false);
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        JLabel costLabel = new JLabel(entry.cost, SwingConstants.RIGHT);
        costLabel.setFont(Theme.headlineMd());
        costLabel.setForeground(Theme.PRIMARY);
        costLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2));
        statusWrap.setOpaque(false);
        statusWrap.setAlignmentX(Component.RIGHT_ALIGNMENT);
        statusWrap.add(new Badge(entry.status, entry.statusSemantic));
        rightCol.add(costLabel);
        rightCol.add(statusWrap);
        row.add(rightCol, BorderLayout.EAST);

        return row;
    }

    // ===================== Widget 3: Products by Category =====================
    private Card categoryBreakdownCard(){
        Card card = new Card();
        card.setCornerRadius(Theme.RADIUS_MD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Products by Category");
        title.setFont(Theme.headlineMd());
        title.setForeground(Theme.PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        card.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        List<ReportModels.CategoryEntry> categories = computeCategoryBreakdown();
        if (categories.isEmpty()){
            JLabel empty = new JLabel("No categorized products yet.");
            empty.setFont(Theme.bodySm());
            empty.setForeground(Theme.ON_SURFACE_VARIANT);
            list.add(empty);
        } else {
            for (int i = 0; i < categories.size(); i++){
                list.add(categoryBar(categories.get(i)));
                if (i < categories.size() - 1) list.add(Box.createVerticalStrut(20));
            }
        }
        JPanel centered = new JPanel(new BorderLayout());
        centered.setOpaque(false);
        centered.add(list, BorderLayout.CENTER);
        card.add(centered, BorderLayout.CENTER);

        return card;
    }

    private JPanel categoryBar(ReportModels.CategoryEntry entry){
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel labelRow = new JPanel(new BorderLayout());
        labelRow.setOpaque(false);
        labelRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JPanel dot = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(entry.color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(12, 12));
        left.add(dot);
        JLabel nameLabel = new JLabel(entry.name);
        nameLabel.setFont(Theme.bodySm().deriveFont(Font.BOLD));
        nameLabel.setForeground(Theme.ON_SURFACE);
        left.add(nameLabel);
        labelRow.add(left, BorderLayout.WEST);

        JLabel countLabel = new JLabel(String.format("%,d (%d%%)", entry.count, entry.percent), SwingConstants.RIGHT);
        countLabel.setFont(Theme.bodySm().deriveFont(Font.BOLD));
        countLabel.setForeground(Theme.PRIMARY);
        labelRow.add(countLabel, BorderLayout.EAST);

        col.add(labelRow);
        col.add(Box.createVerticalStrut(8));

        JPanel barTrack = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.SURFACE_CONTAINER_HIGH);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
                int fillW = (int) (getWidth() * entry.percent / 100.0);
                g2.setColor(entry.color);
                g2.fill(new RoundRectangle2D.Float(0, 0, fillW, getHeight(), getHeight(), getHeight()));
                g2.dispose();
            }
        };
        barTrack.setOpaque(false);
        barTrack.setAlignmentX(Component.LEFT_ALIGNMENT);
        barTrack.setPreferredSize(new Dimension(100, 10));
        barTrack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        col.add(barTrack);

        return col;
    }
}
