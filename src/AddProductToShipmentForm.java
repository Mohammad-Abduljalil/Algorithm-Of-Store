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

/**
 * نموذج بسيط لإضافة منتج موجود لشحنة موجودة (قائمة منتجات + حقل كمية).
 */
public class AddProductToShipmentForm extends Card {

    private final JComboBox<String> productBox;
    private final IconInputField quantityField;
    private Runnable onCancel;
    private Runnable onConfirm;

    public AddProductToShipmentForm(String[] productChoices){
        setCornerRadius(Theme.RADIUS_MD);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        JLabel title = new JLabel("Add Product to Shipment");
        title.setFont(Theme.headlineMd());
        title.setForeground(Theme.ON_SURFACE);
        header.add(title, BorderLayout.WEST);
        IconButton closeBtn = new IconButton(Icons.close(16, Theme.ON_SURFACE_VARIANT));
        closeBtn.setPreferredSize(new Dimension(28, 28));
        closeBtn.addActionListener(e -> { if (onCancel != null) onCancel.run(); });
        header.add(closeBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        productBox = new JComboBox<>(productChoices);
        productBox.setFont(Theme.bodyMd());
        form.add(labeledField("Product", productBox));
        form.add(Box.createVerticalStrut(16));

        quantityField = new IconInputField(null, "0", false);
        form.add(labeledField("Quantity", quantityField));

        add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        RoundedButton cancelBtn = new RoundedButton("Cancel", RoundedButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> { if (onCancel != null) onCancel.run(); });
        RoundedButton addBtn = new RoundedButton("Add", RoundedButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> { if (onConfirm != null) onConfirm.run(); });
        footer.add(cancelBtn);
        footer.add(addBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel labeledField(String labelText, javax.swing.JComponent field){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.labelMd());
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(100, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        return panel;
    }

    public void setOnCancel(Runnable r){ this.onCancel = r; }
    public void setOnConfirm(Runnable r){ this.onConfirm = r; }
    public String getSelectedProduct(){ return (String) productBox.getSelectedItem(); }
    public String getQuantity(){ return quantityField.getText(); }
}
