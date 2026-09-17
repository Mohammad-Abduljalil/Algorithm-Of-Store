import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * شاشة الشحنات والطلبات، مطابقة لكود Stitch: عنوان + شريط بحث/إضافة (بلا بطاقة إطار) + جدول
 * قابل لتوسيع كل صف لعرض منتجاته. الصف الثاني (SH-5502) يبدأ موسّعًا افتراضيًا كما في اللقطة.
 */
public class ShipmentsPanel extends JPanel {

    private final JPanel tableRowsContainer;
    private Runnable onAddShipment;
    private Runnable onReturnHighestPriority;
    private java.util.function.Consumer<ShipmentRow> onDeleteShipment;
    private java.util.function.Consumer<ShipmentRow> onAddProductToShipment;
    private java.util.function.Consumer<ShipmentRow> onEditDeliveryDate;
    private java.util.function.Consumer<ShipmentRow> onEditPriority;

    private List<ShipmentRow> allRows = new ArrayList<>();
    private String expandedId;
    private IconInputField searchField;

    public ShipmentsPanel(){
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(toolbar());
        content.add(Box.createVerticalStrut(20));

        Card tableCard = new Card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(tableHeaderRow(), BorderLayout.NORTH);

        tableRowsContainer = new JPanel();
        tableRowsContainer.setOpaque(false);
        tableRowsContainer.setLayout(new BoxLayout(tableRowsContainer, BoxLayout.Y_AXIS));
        tableCard.add(tableRowsContainer, BorderLayout.CENTER);
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(tableCard);

        content.add(Box.createVerticalGlue()); // يدفع المحتوى للأعلى بدل تمدّد البطاقات عموديًا
        add(content, BorderLayout.CENTER);

        setRows(sampleData(), "SH-5502"); // الشحنة الثانية موسّعة افتراضيًا مطابقةً للقطة
    }

    public static List<ShipmentRow> sampleData(){
        List<ShipmentRow> rows = new ArrayList<>();
        rows.add(new ShipmentRow("#SH-5501", "New York, NY", "Oct 24, 2023", "$15,000.00", "$14,250.00", 9, List.of()));
        List<ShipmentRow.ShipmentProductLine> sfProducts = List.of(
                new ShipmentRow.ShipmentProductLine("ThinkPad X1 Carbon", "2", "$1,299.00", "$2,598.00"),
                new ShipmentRow.ShipmentProductLine("Dell UltraSharp 27\" Monitor", "5", "$450.00", "$2,250.00"),
                new ShipmentRow.ShipmentProductLine("Logitech MX Master 3S", "10", "$99.00", "$990.00")
        );
        rows.add(new ShipmentRow("#SH-5502", "San Francisco, CA", "Oct 26, 2023", "$25,000.00", "$18,400.00", 5, sfProducts));
        rows.add(new ShipmentRow("#SH-5503", "Austin, TX", "Oct 28, 2023", "$8,500.00", "$3,200.00", 2, List.of()));
        rows.add(new ShipmentRow("#SH-5504", "Seattle, WA", "Nov 02, 2023", "$42,000.00", "$11,050.00", 2, List.of()));
        return rows;
    }

    public void setRows(List<ShipmentRow> rows, String initiallyExpandedId){
        this.allRows = new ArrayList<>(rows);
        this.expandedId = initiallyExpandedId;
        applyFilter();
    }

    // العملية 10: البحث عن شحنة بالرقم أو الوجهة (فوري مع كل حرف)
    private void applyFilter(){
        String query = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        tableRowsContainer.removeAll();
        int shown = 0;
        for (ShipmentRow row : allRows){
            boolean matches = query.isEmpty()
                    || row.id.toLowerCase().contains(query)
                    || (row.destination != null && row.destination.toLowerCase().contains(query));
            if (matches){
                tableRowsContainer.add(buildRowGroup(row, row.id.replace("#", "").equals(expandedId)));
                shown++;
            }
        }
        if (shown == 0){
            JLabel empty = new JLabel(allRows.isEmpty()
                    ? "No shipments yet. Use \"Add Shipment\" to create one."
                    : "No shipments match this search.");
            empty.setFont(Theme.bodySm());
            empty.setForeground(Theme.ON_SURFACE_VARIANT);
            empty.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrap.setBackground(Theme.SURFACE_CONTAINER_LOWEST);
            wrap.setOpaque(true);
            wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            wrap.add(empty);
            tableRowsContainer.add(wrap);
        }
        tableRowsContainer.revalidate();
        tableRowsContainer.repaint();
    }

    public void setOnAddShipment(Runnable r){ this.onAddShipment = r; }
    public void setOnReturnHighestPriority(Runnable r){ this.onReturnHighestPriority = r; }
    public void setOnEditDeliveryDate(java.util.function.Consumer<ShipmentRow> c){ this.onEditDeliveryDate = c; }
    public void setOnEditPriority(java.util.function.Consumer<ShipmentRow> c){ this.onEditPriority = c; }
    public void setOnDeleteShipment(java.util.function.Consumer<ShipmentRow> c){ this.onDeleteShipment = c; }
    public void setOnAddProductToShipment(java.util.function.Consumer<ShipmentRow> c){ this.onAddProductToShipment = c; }

    private JPanel pageHeader(){
        JLabel title = new JLabel("Shipments & Orders");
        title.setFont(Theme.display());
        title.setForeground(Theme.ON_SURFACE);
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setOpaque(false);
        wrap.add(title);
        return wrap;
    }

    private JPanel toolbar(){
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        row.add(pageHeader(), BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        searchField = new IconInputField(Icons.search(16, Theme.ON_SURFACE_VARIANT), "Search by ID or destination...", false);
        searchField.setPreferredSize(new Dimension(240, 40));
        searchField.getField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e){ applyFilter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e){ applyFilter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e){ applyFilter(); }
        });
        right.add(searchField);

        RoundedButton returnTopBtn = new RoundedButton("Return Top Priority Order", RoundedButton.Variant.SECONDARY);
        returnTopBtn.setToolTipText("Removes the highest-priority order from the heap and returns its shipment");
        returnTopBtn.addActionListener(e -> { if (onReturnHighestPriority != null) onReturnHighestPriority.run(); });
        right.add(returnTopBtn);
        RoundedButton addBtn = new RoundedButton("Add Shipment", RoundedButton.Variant.PRIMARY);
        addBtn.setIcon(Icons.plus(16, java.awt.Color.WHITE));
        addBtn.setIconTextGap(8);
        addBtn.addActionListener(e -> { if (onAddShipment != null) onAddShipment.run(); });
        right.add(addBtn);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    private JPanel tableHeaderRow(){
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Theme.SURFACE_CONTAINER_LOW);
        row.setOpaque(true);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.SURFACE_CONTAINER_HIGH),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JPanel chevronSpacer = new JPanel();
        chevronSpacer.setOpaque(false);
        chevronSpacer.setPreferredSize(new Dimension(32, 10));
        row.add(chevronSpacer, BorderLayout.WEST);

        JPanel cols = new JPanel(new GridLayout(1, 6, 12, 0));
        cols.setOpaque(false);
        cols.add(colLabel("Shipment ID", SwingConstants.LEFT));
        cols.add(colLabel("Destination", SwingConstants.LEFT));
        cols.add(colLabel("Delivery Date", SwingConstants.LEFT));
        cols.add(colLabel("Budget", SwingConstants.LEFT));
        cols.add(colLabel("Current Cost", SwingConstants.LEFT));
        cols.add(colLabel("Priority", SwingConstants.LEFT));
        row.add(cols, BorderLayout.CENTER);

        JLabel actionsLabel = colLabel("ACTIONS", SwingConstants.RIGHT);
        actionsLabel.setPreferredSize(new Dimension(118, 20));
        row.add(actionsLabel, BorderLayout.EAST);

        return row;
    }

    private JLabel colLabel(String text, int align){
        JLabel label = new JLabel(text, align);
        label.setFont(Theme.labelMd());
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        return label;
    }

    private JPanel buildRowGroup(ShipmentRow data, boolean expandedByDefault){
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        boolean[] expanded = { expandedByDefault };
        JPanel[] subTableHolder = new JPanel[1];

        JLabel chevron = new JLabel(expanded[0] ? Icons.chevronDown(18, Theme.PRIMARY) : Icons.chevronRight(18, Theme.OUTLINE));
        JPanel mainRow = dataRow(data, chevron, expanded[0]);
        mainRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(mainRow);

        JPanel subPanel = subTable(data);
        subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subPanel.setVisible(expanded[0]);
        subTableHolder[0] = subPanel;
        group.add(subPanel);

        MouseAdapter toggle = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e){
                expanded[0] = !expanded[0];
                chevron.setIcon(expanded[0] ? Icons.chevronDown(18, Theme.PRIMARY) : Icons.chevronRight(18, Theme.OUTLINE));
                subTableHolder[0].setVisible(expanded[0]);
                group.revalidate();
                group.repaint();
            }
        };
        chevron.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chevron.addMouseListener(toggle);
        mainRow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainRow.addMouseListener(toggle);

        return group;
    }

    private JPanel dataRow(ShipmentRow data, JLabel chevron, boolean tinted){
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(tinted ? Theme.SURFACE_CONTAINER_LOW : Theme.SURFACE_CONTAINER_LOWEST);
        row.setOpaque(true);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel chevronWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        chevronWrap.setOpaque(false);
        chevronWrap.setPreferredSize(new Dimension(32, 20));
        chevronWrap.add(chevron);
        row.add(chevronWrap, BorderLayout.WEST);

        JPanel cols = new JPanel(new GridLayout(1, 6, 12, 0));
        cols.setOpaque(false);

        JLabel idLabel = new JLabel(data.id);
        idLabel.setFont(Theme.monoLabel().deriveFont(tinted ? Font.BOLD : Font.PLAIN));
        idLabel.setForeground(Theme.PRIMARY);
        cols.add(idLabel);

        cols.add(bodyLabel(data.destination, tinted));
        cols.add(bodyLabel(data.deliveryDate, tinted));
        cols.add(bodyLabel(data.budget, tinted));
        cols.add(bodyLabel(data.currentCost, tinted));

        JPanel priorityWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        priorityWrap.setOpaque(false);
        priorityWrap.add(new PriorityBadge(data.priority));
        cols.add(priorityWrap);

        row.add(cols, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        actions.setOpaque(false);
        actions.setPreferredSize(new Dimension(118, 30));

        IconButton dateBtn = new IconButton(Icons.calendar(16, Theme.ON_SURFACE_VARIANT));
        dateBtn.setToolTipText("Update Delivery Date");
        dateBtn.setPreferredSize(new Dimension(32, 30));
        dateBtn.addActionListener(e -> { if (onEditDeliveryDate != null) onEditDeliveryDate.accept(data); });
        actions.add(dateBtn);

        IconButton priorityBtn = new IconButton(Icons.trendingUp(16, Theme.ON_SURFACE_VARIANT));
        priorityBtn.setToolTipText("Update Order Priority");
        priorityBtn.setPreferredSize(new Dimension(32, 30));
        priorityBtn.addActionListener(e -> { if (onEditPriority != null) onEditPriority.accept(data); });
        actions.add(priorityBtn);

        IconButton deleteBtn = new IconButton(Icons.trash(16, Theme.ON_SURFACE_VARIANT), Theme.ERROR_BG);
        deleteBtn.setToolTipText("Delete Shipment");
        deleteBtn.setPreferredSize(new Dimension(32, 30));
        deleteBtn.addActionListener(e -> { if (onDeleteShipment != null) onDeleteShipment.accept(data); });
        actions.add(deleteBtn);

        row.add(actions, BorderLayout.EAST);

        return row;
    }

    private JLabel bodyLabel(String text, boolean bold){
        JLabel label = new JLabel(text);
        label.setFont(bold ? Theme.bodyMd().deriveFont(Font.BOLD) : Theme.bodyMd());
        label.setForeground(Theme.ON_SURFACE);
        return label;
    }

    private JPanel subTable(ShipmentRow data){
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.SURFACE_CONTAINER_LOW);
        outer.setOpaque(true);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER),
                BorderFactory.createEmptyBorder(16, 64, 16, 16)));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("PRODUCTS IN THIS SHIPMENT");
        label.setFont(Theme.labelMd());
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(label);
        inner.add(Box.createVerticalStrut(8));

        Card innerCard = new Card();
        innerCard.setLayout(new BoxLayout(innerCard, BoxLayout.Y_AXIS));
        innerCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel head = new JPanel(new GridLayout(1, 4, 12, 0));
        head.setBackground(Theme.SURFACE);
        head.setOpaque(true);
        head.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.SURFACE_CONTAINER_HIGH),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        head.add(smallColLabel("Product Name", SwingConstants.LEFT));
        head.add(smallColLabel("Quantity", SwingConstants.CENTER));
        head.add(smallColLabel("Unit Price", SwingConstants.RIGHT));
        head.add(smallColLabel("Subtotal", SwingConstants.RIGHT));
        innerCard.add(head);

        if (data.products.isEmpty()){
            JLabel empty = new JLabel("No products added to this shipment yet.");
            empty.setFont(Theme.bodySm());
            empty.setForeground(Theme.ON_SURFACE_VARIANT);
            empty.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            innerCard.add(empty);
        } else {
            for (ShipmentRow.ShipmentProductLine line : data.products){
                JPanel r = new JPanel(new GridLayout(1, 4, 12, 0));
                r.setBackground(Theme.SURFACE_CONTAINER_LOWEST);
                r.setOpaque(true);
                r.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                JLabel nameL = new JLabel(line.name);
                nameL.setFont(Theme.bodySm().deriveFont(Font.BOLD));
                nameL.setForeground(Theme.ON_SURFACE);
                r.add(nameL);
                JLabel qtyL = new JLabel(line.quantity, SwingConstants.CENTER);
                qtyL.setFont(Theme.bodySm());
                qtyL.setForeground(Theme.ON_SURFACE);
                r.add(qtyL);
                JLabel priceL = new JLabel(line.unitPrice, SwingConstants.RIGHT);
                priceL.setFont(Theme.bodySm());
                priceL.setForeground(Theme.ON_SURFACE_VARIANT);
                r.add(priceL);
                JLabel subL = new JLabel(line.subtotal, SwingConstants.RIGHT);
                subL.setFont(Theme.bodySm().deriveFont(Font.BOLD));
                subL.setForeground(Theme.ON_SURFACE);
                r.add(subL);
                innerCard.add(r);
            }
        }

        inner.add(innerCard);
        inner.add(Box.createVerticalStrut(10));
        RoundedButton addProductBtn = new RoundedButton("+ Add Product to Shipment", RoundedButton.Variant.SECONDARY);
        addProductBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addProductBtn.addActionListener(e -> { if (onAddProductToShipment != null) onAddProductToShipment.accept(data); });
        inner.add(addProductBtn);
        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    private JLabel smallColLabel(String text, int align){
        JLabel label = new JLabel(text, align);
        label.setFont(Theme.labelMd().deriveFont(11f));
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        return label;
    }
}
