import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * شاشة إدارة المنتجات الكاملة، مطابقة لكود Stitch:
 * عنوان الصفحة -> شريط أدوات (بحث + فلتر تصنيف + زر إضافة) -> جدول بيانات -> تذييل ترقيم.
 * البيانات المعروضة حاليًا نموذجية (Mock) للمعاينة البصرية، وستُربَط بـ ProductManagement الحقيقية لاحقًا.
 */
public class ProductsPanel extends JPanel {

    private final JPanel tableRowsContainer;
    private Consumer<ProductRow> onEdit;
    private Consumer<ProductRow> onDelete;
    private Runnable onAddProduct;

    // ===== حالة الفلترة (بحث + تصنيف + مخزون منخفض) =====
    private List<ProductRow> allRows = new ArrayList<>(); // النسخة الكاملة قبل أي فلترة
    private IconInputField searchField;
    private JComboBox<String> categoryFilter;
    private javax.swing.JCheckBox lowStockOnly;
    private final JLabel resultCountLabel = new JLabel();

    public ProductsPanel(){
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(pageHeader());
        content.add(Box.createVerticalStrut(24));
        content.add(toolbar());
        content.add(Box.createVerticalStrut(20));

        Card tableCard = new Card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(tableHeaderRow(), BorderLayout.NORTH);

        tableRowsContainer = new JPanel();
        tableRowsContainer.setOpaque(false);
        tableRowsContainer.setLayout(new BoxLayout(tableRowsContainer, BoxLayout.Y_AXIS));
        tableCard.add(tableRowsContainer, BorderLayout.CENTER);

        tableCard.add(paginationFooter(), BorderLayout.SOUTH);
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(tableCard);

        content.add(Box.createVerticalGlue()); // يدفع المحتوى للأعلى بدل تمدّد البطاقات عموديًا
        add(content, BorderLayout.CENTER);

        // بيانات نموذجية مطابقة للقطة الشاشة، لعرض الشكل النهائي
        setRows(sampleData());
    }

    public static List<ProductRow> sampleData(){
        List<ProductRow> rows = new ArrayList<>();
        rows.add(new ProductRow("#1042", "ThinkPad X1 Carbon", "Electronics", "$1,299.00", "45", false));
        rows.add(new ProductRow("#2091", "Ergonomic Mesh Chair", "Office", "$245.50", "8", true));
        rows.add(new ProductRow("#3305", "Industrial Shelving Unit", "Hardware", "$450.00", "112", false));
        return rows;
    }

    public void setRows(List<ProductRow> rows){
        this.allRows = new ArrayList<>(rows);
        refreshCategoryChoices();
        applyFilters();
    }

    // يُعيد بناء خيارات قائمة التصنيفات من البيانات الفعلية (العملية 21: عرض كل التصنيفات)
    private void refreshCategoryChoices(){
        if (categoryFilter == null) return;
        Object selected = categoryFilter.getSelectedItem();
        java.util.LinkedHashSet<String> names = new java.util.LinkedHashSet<>();
        names.add("All Categories");
        for (ProductRow r : allRows) names.add(r.category);
        categoryFilter.setModel(new javax.swing.DefaultComboBoxModel<>(names.toArray(new String[0])));
        if (selected != null && names.contains(selected)) categoryFilter.setSelectedItem(selected);
    }

    // العمليات 3 (بحث بالاسم/الرقم) + 22 (منتجات تصنيف معيّن) + 20 (المخزون المنخفض)
    private void applyFilters(){
        String query = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        String category = categoryFilter == null ? "All Categories" : String.valueOf(categoryFilter.getSelectedItem());
        boolean onlyLow = lowStockOnly != null && lowStockOnly.isSelected();

        List<ProductRow> visible = new ArrayList<>();
        for (ProductRow r : allRows){
            boolean matchesQuery = query.isEmpty()
                    || r.name.toLowerCase().contains(query)
                    || r.id.toLowerCase().contains(query);
            boolean matchesCategory = "All Categories".equals(category) || r.category.equals(category);
            boolean matchesLow = !onlyLow || r.lowStock;
            if (matchesQuery && matchesCategory && matchesLow) visible.add(r);
        }

        tableRowsContainer.removeAll();
        if (visible.isEmpty()){
            JLabel empty = new JLabel(allRows.isEmpty()
                    ? "No products yet. Use \"Add Product\" to create one."
                    : "No products match the current search or filter.");
            empty.setFont(Theme.bodySm());
            empty.setForeground(Theme.ON_SURFACE_VARIANT);
            empty.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrap.setBackground(Theme.SURFACE_CONTAINER_LOWEST);
            wrap.setOpaque(true);
            wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            wrap.add(empty);
            tableRowsContainer.add(wrap);
        } else {
            for (int i = 0; i < visible.size(); i++){
                tableRowsContainer.add(dataRow(visible.get(i), i % 2 == 1));
            }
        }
        resultCountLabel.setText("Showing " + visible.size() + " of " + allRows.size() + " products");
        tableRowsContainer.revalidate();
        tableRowsContainer.repaint();
    }

    public void setOnAddProduct(Runnable r){ this.onAddProduct = r; }
    public void setOnEdit(Consumer<ProductRow> c){ this.onEdit = c; }
    public void setOnDelete(Consumer<ProductRow> c){ this.onDelete = c; }

    private JPanel pageHeader(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Products");
        title.setFont(Theme.display());
        title.setForeground(Theme.ON_SURFACE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        return panel;
    }

    private Card toolbar(){
        Card card = new Card();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        searchField = new IconInputField(Icons.search(16, Theme.ON_SURFACE_VARIANT), "Search by product name or ID...", false);
        searchField.setPreferredSize(new Dimension(280, 40));
        // البحث يعمل فوريًا مع كل حرف يُكتب (بلا زر بحث منفصل)
        searchField.getField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e){ applyFilters(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e){ applyFilters(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e){ applyFilters(); }
        });
        left.add(searchField);

        categoryFilter = new JComboBox<>(new String[]{"All Categories"});
        categoryFilter.setFont(Theme.bodyMd());
        categoryFilter.setPreferredSize(new Dimension(170, 40));
        categoryFilter.addActionListener(e -> applyFilters());
        left.add(categoryFilter);

        lowStockOnly = new javax.swing.JCheckBox("Low stock only");
        lowStockOnly.setFont(Theme.bodySm());
        lowStockOnly.setForeground(Theme.ON_SURFACE_VARIANT);
        lowStockOnly.setOpaque(false);
        lowStockOnly.addActionListener(e -> applyFilters());
        left.add(lowStockOnly);

        card.add(left, BorderLayout.WEST);

        RoundedButton addButton = new RoundedButton("Add Product", RoundedButton.Variant.PRIMARY);
        addButton.setIcon(Icons.plus(16, java.awt.Color.WHITE));
        addButton.setIconTextGap(8);
        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightWrap.setOpaque(false);
        rightWrap.add(addButton);
        card.add(rightWrap, BorderLayout.EAST);

        addButton.addActionListener(e -> { if (onAddProduct != null) onAddProduct.run(); });

        return card;
    }

    private JPanel tableHeaderRow(){
        JPanel row = new JPanel(new java.awt.GridLayout(1, 7, 12, 0));
        row.setBackground(Theme.SURFACE_CONTAINER_LOW);
        row.setOpaque(true);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.SURFACE_CONTAINER_HIGH),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        row.add(colLabel("ID", SwingConstants.LEFT));
        row.add(colLabel("NAME", SwingConstants.LEFT));
        row.add(colLabel("CATEGORY", SwingConstants.LEFT));
        row.add(colLabel("PRICE", SwingConstants.RIGHT));
        row.add(colLabel("QUANTITY", SwingConstants.RIGHT));
        row.add(colLabel("STATUS", SwingConstants.LEFT));
        row.add(colLabel("ACTIONS", SwingConstants.RIGHT));
        return row;
    }

    private JLabel colLabel(String text, int align){
        JLabel label = new JLabel(text, align);
        label.setFont(Theme.labelMd());
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        return label;
    }

    private JPanel dataRow(ProductRow data, boolean tinted){
        JPanel row = new JPanel(new java.awt.GridLayout(1, 7, 12, 0));
        row.setBackground(tinted ? Theme.SURFACE_CONTAINER_LOW : Theme.SURFACE_CONTAINER_LOWEST);
        row.setOpaque(true);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JLabel idLabel = new JLabel(data.id);
        idLabel.setFont(Theme.monoLabel());
        idLabel.setForeground(Theme.ON_SURFACE_VARIANT);
        row.add(idLabel);

        JLabel nameLabel = new JLabel(data.name);
        nameLabel.setFont(Theme.bodyMd().deriveFont(Font.BOLD));
        nameLabel.setForeground(Theme.ON_SURFACE);
        row.add(nameLabel);

        JPanel catWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        catWrap.setOpaque(false);
        catWrap.add(new CategoryBadge(data.category));
        row.add(catWrap);

        JLabel priceLabel = new JLabel(data.price, SwingConstants.RIGHT);
        priceLabel.setFont(Theme.bodyMd());
        priceLabel.setForeground(Theme.ON_SURFACE);
        row.add(priceLabel);

        JLabel qtyLabel = new JLabel(data.quantity, SwingConstants.RIGHT);
        qtyLabel.setFont(Theme.bodyMd());
        qtyLabel.setForeground(Theme.ON_SURFACE);
        row.add(qtyLabel);

        JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusWrap.setOpaque(false);
        statusWrap.add(new Badge(data.lowStock ? "Low Stock" : "In Stock",
                data.lowStock ? Badge.Semantic.WARNING : Badge.Semantic.SUCCESS));
        row.add(statusWrap);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        actions.setOpaque(false);
        IconButton editBtn = new IconButton(Icons.pencil(16, Theme.ON_SURFACE_VARIANT));
        IconButton deleteBtn = new IconButton(Icons.trash(16, Theme.ON_SURFACE_VARIANT), Theme.ERROR_BG);
        editBtn.addActionListener(e -> { if (onEdit != null) onEdit.accept(data); });
        deleteBtn.addActionListener(e -> { if (onDelete != null) onDelete.accept(data); });
        actions.add(editBtn);
        actions.add(deleteBtn);
        row.add(actions);

        return row;
    }

    private JPanel paginationFooter(){
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.SURFACE_CONTAINER_LOW);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.SURFACE_CONTAINER_HIGH),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        resultCountLabel.setFont(Theme.bodySm());
        resultCountLabel.setForeground(Theme.ON_SURFACE_VARIANT);
        footer.add(resultCountLabel, BorderLayout.WEST);

        return footer;
    }
}
