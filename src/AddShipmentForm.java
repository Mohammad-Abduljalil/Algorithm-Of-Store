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
import java.time.Year;

/**
 * نموذج "إضافة شحنة" ككلاس Card مستقل تمامًا عن JDialog، قابل للاختبار بصريًا بمعزل عنها.
 */
public class AddShipmentForm extends Card {

    private final IconInputField idField;
    private final IconInputField destinationField;
    private final IconInputField budgetField;
    private final JComboBox<Integer> priorityBox;
    private final JComboBox<Integer> yearBox;
    private final JComboBox<String> monthBox;
    private final JComboBox<Integer> dayBox;
    private Runnable onCancel;
    private Runnable onConfirm;

    public AddShipmentForm(){
        setCornerRadius(Theme.RADIUS_MD);
        setLayout(new BorderLayout());

        idField = new IconInputField(null, "e.g. SH-6000", false);
        destinationField = new IconInputField(null, "City, State", false);
        budgetField = new IconInputField(null, "0.00", false);
        Integer[] priorities = new Integer[10];
        for (int i = 0; i < 10; i++) priorities[i] = i + 1;
        priorityBox = new JComboBox<>(priorities);
        priorityBox.setSelectedItem(5); // قيمة افتراضية متوسطة معقولة
        int currentYear = Year.now().getValue();
        Integer[] years = new Integer[6];
        for (int i = 0; i < 6; i++) years[i] = currentYear + i;
        yearBox = new JComboBox<>(years);
        monthBox = new JComboBox<>(new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"});
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) days[i] = i + 1;
        dayBox = new JComboBox<>(days);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        JLabel title = new JLabel("Add Shipment");
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

        form.add(labeledField("Shipment ID", idField));
        form.add(Box.createVerticalStrut(16));
        form.add(labeledField("Destination", destinationField));
        form.add(Box.createVerticalStrut(16));
        form.add(labeledField("Maximum Budget ($)", budgetField));
        form.add(Box.createVerticalStrut(16));

        priorityBox.setFont(Theme.bodyMd());
        form.add(labeledField("Order Priority (1-10, higher = more urgent)", priorityBox));
        form.add(Box.createVerticalStrut(16));

        JPanel dateLabelPanel = new JPanel();
        dateLabelPanel.setOpaque(false);
        dateLabelPanel.setLayout(new BoxLayout(dateLabelPanel, BoxLayout.Y_AXIS));
        dateLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel dateLabel = new JLabel("Delivery Date");
        dateLabel.setFont(Theme.labelMd());
        dateLabel.setForeground(Theme.ON_SURFACE_VARIANT);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabelPanel.add(dateLabel);
        dateLabelPanel.add(Box.createVerticalStrut(4));

        JPanel dateRow = new JPanel(new GridLayout(1, 3, 8, 0));
        dateRow.setOpaque(false);
        dateRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        yearBox.setFont(Theme.bodyMd());
        monthBox.setFont(Theme.bodyMd());
        dayBox.setFont(Theme.bodyMd());
        dateRow.add(yearBox);
        dateRow.add(monthBox);
        dateRow.add(dayBox);
        dateLabelPanel.add(dateRow);
        form.add(dateLabelPanel);
        form.add(Box.createVerticalStrut(16));

        Card infoNote = new Card();
        infoNote.setCardBackground(Theme.SURFACE_CONTAINER_LOW);
        infoNote.setCardBorderColor(Theme.CARD_BORDER);
        infoNote.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        infoNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoNote.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        infoNote.add(new JLabel(Icons.info(16, Theme.ON_SURFACE_VARIANT)));
        JLabel infoText = new JLabel("<html><div style='width:280px;'>You can add products to this shipment after creating it.</div></html>");
        infoText.setFont(Theme.bodySm());
        infoText.setForeground(Theme.ON_SURFACE_VARIANT);
        infoNote.add(infoText);
        form.add(infoNote);

        add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        RoundedButton cancelBtn = new RoundedButton("Cancel", RoundedButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> { if (onCancel != null) onCancel.run(); });
        RoundedButton addBtn = new RoundedButton("Add Shipment", RoundedButton.Variant.PRIMARY);
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
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        return panel;
    }

    public void setOnCancel(Runnable r){ this.onCancel = r; }
    public void setOnConfirm(Runnable r){ this.onConfirm = r; }
    public String getShipmentId(){ return idField.getText(); }
    public String getDestination(){ return destinationField.getText(); }
    public String getBudget(){ return budgetField.getText(); }
    public int getYear(){ return (Integer) yearBox.getSelectedItem(); }
    public int getMonth(){ return monthBox.getSelectedIndex() + 1; }
    public int getDay(){ return (Integer) dayBox.getSelectedItem(); }
    public int getPriority(){ return (Integer) priorityBox.getSelectedItem(); }
}
