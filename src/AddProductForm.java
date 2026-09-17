import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * نموذج "إضافة منتج" ككلاس Card مستقل تمامًا عن JDialog، قابل للاختبار بصريًا بمعزل عنها.
 */
public class AddProductForm extends Card {

    private final IconInputField idField;
    private final IconInputField nameField;
    private final IconInputField priceField;
    private final IconInputField quantityField;
    private final IconInputField thresholdField;
    private final JComboBox<String> categoryBox;
    private final JLabel titleLabel;
    private final RoundedButton confirmBtn;
    private Runnable onCancel;
    private Runnable onConfirm;

    public AddProductForm(){
        this(new String[]{"Electronics", "Office", "Hardware", "Uncategorized"});
    }

    public AddProductForm(String[] availableCategories){
        setCornerRadius(Theme.RADIUS_MD);
        setLayout(new BorderLayout());

        idField = new IconInputField(null, "e.g. 1042", false);
        categoryBox = new JComboBox<>(availableCategories);
        nameField = new IconInputField(null, "Enter product name", false);
        priceField = new IconInputField(null, "0.00", false);
        quantityField = new IconInputField(null, "0", false);
        thresholdField = new IconInputField(null, "10", false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        titleLabel = new JLabel("Add New Product");
        titleLabel.setFont(Theme.headlineMd());
        titleLabel.setForeground(Theme.PRIMARY);
        header.add(titleLabel, BorderLayout.WEST);
        IconButton closeBtn = new IconButton(Icons.close(16, Theme.ON_SURFACE_VARIANT));
        closeBtn.setPreferredSize(new Dimension(28, 28));
        closeBtn.addActionListener(e -> { if (onCancel != null) onCancel.run(); });
        header.add(closeBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setOpaque(false);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        categoryBox.setFont(Theme.bodyMd());
        row1.add(labeledField("Product ID", idField));
        row1.add(labeledField("Category", categoryBox));
        form.add(row1);
        form.add(Box.createVerticalStrut(16));

        JPanel nameWrap = labeledField("Product Name", nameField);
        nameWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(nameWrap);
        form.add(Box.createVerticalStrut(16));

        JPanel row2 = new JPanel(new GridLayout(1, 2, 12, 0));
        row2.setOpaque(false);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        row2.add(labeledField("Price ($)", priceField));
        row2.add(labeledField("Initial Quantity", quantityField));
        form.add(row2);
        form.add(Box.createVerticalStrut(16));

        JPanel thresholdWrap = labeledField("Minimum Stock Threshold", thresholdField);
        thresholdWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel helper = new JLabel("Alert when stock drops to this level or below");
        helper.setFont(Theme.bodySm());
        helper.setForeground(Theme.ON_SURFACE_VARIANT);
        helper.setAlignmentX(Component.LEFT_ALIGNMENT);
        thresholdWrap.add(Box.createVerticalStrut(4));
        thresholdWrap.add(helper);
        form.add(thresholdWrap);

        add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        RoundedButton cancelBtn = new RoundedButton("Cancel", RoundedButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> { if (onCancel != null) onCancel.run(); });
        confirmBtn = new RoundedButton("Add Product", RoundedButton.Variant.PRIMARY);
        confirmBtn.addActionListener(e -> { if (onConfirm != null) onConfirm.run(); });
        footer.add(cancelBtn);
        footer.add(confirmBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel labeledField(String labelText, javax.swing.JComponent field){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.labelMd());
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(100, 40));
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        return panel;
    }

    public void setOnCancel(Runnable r){ this.onCancel = r; }
    public void setOnConfirm(Runnable r){ this.onConfirm = r; }
    public String getProductId(){ return idField.getText(); }
    public String getProductName(){ return nameField.getText(); }
    public String getPrice(){ return priceField.getText(); }
    public String getQuantity(){ return quantityField.getText(); }
    public String getThreshold(){ return thresholdField.getText(); }
    public String getCategory(){ return (String) categoryBox.getSelectedItem(); }

    // تُحوّل النموذج لوضع "تعديل": تعطيل حقل ID (غير قابل للتغيير)، تغيير العنوان/الزر، وتعبئة القيم الحالية
    public void configureForEdit(int id, String name, int price, int quantity, int threshold, String category){
        titleLabel.setText("Edit Product");
        confirmBtn.setText("Save Changes");
        idField.getField().setText(String.valueOf(id));
        idField.getField().setEnabled(false);
        nameField.getField().setText(name);
        priceField.getField().setText(String.valueOf(price));
        quantityField.getField().setText(String.valueOf(quantity));
        thresholdField.getField().setText(String.valueOf(threshold));
        categoryBox.setSelectedItem(category);
    }
}
