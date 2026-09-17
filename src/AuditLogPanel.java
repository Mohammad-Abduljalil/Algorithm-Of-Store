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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * شاشة سجل العمليات (Admin Only)، مطابقة لكود Stitch: شريط فلترة + جدول بطابع "طرفية"
 * (خط Monospace، صفوف مضغوطة)، مع تلوين خفيف لصفوف العمليات المرفوضة (DENIED).
 */
public class AuditLogPanel extends JPanel {

    private final JPanel tableRowsContainer;

    public AuditLogPanel(){
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

        tableCard.add(paginationFooter(), BorderLayout.SOUTH);
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(tableCard);

        content.add(Box.createVerticalGlue()); // يدفع المحتوى للأعلى بدل تمدّد البطاقات عموديًا
        add(content, BorderLayout.CENTER);

        setRows(sampleData());
    }

    public static List<AuditLogRow> sampleData(){
        List<AuditLogRow> rows = new ArrayList<>();
        rows.add(new AuditLogRow("2026-07-21 14:32:01", "admin", "Delete Product", null,
                "SUCCESS", Theme.TERTIARY_FIXED, Theme.TERTIARY, false));
        rows.add(new AuditLogRow("2026-07-21 14:28:44", "bob", "Delete Product", "insufficient privileges (role=EMPLOYEE)",
                "DENIED", Theme.SURFACE_CONTAINER_HIGH, Theme.OUTLINE, true));
        rows.add(new AuditLogRow("2026-07-21 14:15:20", "admin", "Update Shipment Delivery Date", null,
                "SUCCESS", Theme.PRIMARY_FIXED, Theme.PRIMARY, false));
        rows.add(new AuditLogRow("2026-07-21 13:55:09", "unknown", "Login attempt 1/3", null,
                "FAILED", new Color(0xE0E7FF), new Color(0x3730A3), false));
        rows.add(new AuditLogRow("2026-07-21 13:10:42", "admin", "Change Own Password", null,
                "SUCCESS", Theme.TERTIARY_FIXED, Theme.TERTIARY, false));
        rows.add(new AuditLogRow("2026-07-21 12:45:11", "bob", "View Audit Log", "insufficient privileges (role=EMPLOYEE)",
                "DENIED", Theme.SURFACE_CONTAINER_HIGH, Theme.OUTLINE, true));
        rows.add(new AuditLogRow("2026-07-21 11:30:05", "admin", "Manual Save Data", null,
                "SUCCESS", Theme.TERTIARY_FIXED, Theme.TERTIARY, false));
        rows.add(new AuditLogRow("2026-07-21 10:05:22", "admin", "Register New Employee", null,
                "SUCCESS", Theme.TERTIARY_FIXED, Theme.TERTIARY, false));
        return rows;
    }

    public void setRows(List<AuditLogRow> rows){
        tableRowsContainer.removeAll();
        for (AuditLogRow row : rows){
            tableRowsContainer.add(dataRow(row));
        }
        tableRowsContainer.revalidate();
        tableRowsContainer.repaint();
    }

    private JPanel toolbar(){
        Card card = new Card();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        IconInputField filter = new IconInputField(Icons.search(16, Theme.OUTLINE), "Filter by action, user, or ID...", false);
        filter.setPreferredSize(new Dimension(360, 40));
        card.add(filter, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        RoundedButton lastDays = new RoundedButton("Last 7 Days", RoundedButton.Variant.SECONDARY);
        lastDays.setIcon(Icons.calendar(14, Theme.PRIMARY_CONTAINER));
        lastDays.setIconTextGap(8);
        RoundedButton export = new RoundedButton("Export", RoundedButton.Variant.SECONDARY);
        export.setIcon(Icons.download(14, Theme.PRIMARY_CONTAINER));
        export.setIconTextGap(8);
        right.add(lastDays);
        right.add(export);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private JPanel tableHeaderRow(){
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setBackground(Theme.SURFACE_CONTAINER_LOW);
        row.setOpaque(true);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        row.add(colLabel("TIMESTAMP", SwingConstants.LEFT));
        row.add(colLabel("USER", SwingConstants.LEFT));
        row.add(colLabel("ACTION DETAILS", SwingConstants.LEFT));
        row.add(colLabel("RESULT", SwingConstants.RIGHT));
        return row;
    }

    private JLabel colLabel(String text, int align){
        JLabel label = new JLabel(text, align);
        label.setFont(Theme.labelMd());
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        return label;
    }

    private JPanel dataRow(AuditLogRow data){
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        Color bg = data.highlightRow
                ? new Color(Theme.ERROR.getRed(), Theme.ERROR.getGreen(), Theme.ERROR.getBlue(), 12)
                : Theme.SURFACE_CONTAINER_LOWEST;
        row.setBackground(bg);
        row.setOpaque(true);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel timeLabel = new JLabel(data.timestamp);
        timeLabel.setFont(Theme.monoLabel());
        timeLabel.setForeground(Theme.OUTLINE);
        row.add(timeLabel);

        JPanel userWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        userWrap.setOpaque(false);
        JPanel avatar = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(data.avatarBg);
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(24, 24));
        avatar.setLayout(new java.awt.GridBagLayout());
        JLabel avatarLabel = new JLabel(data.username.length() >= 2 ? data.username.substring(0, 2).toUpperCase() : data.username.toUpperCase());
        avatarLabel.setFont(Theme.labelMd().deriveFont(9f));
        avatarLabel.setForeground(data.avatarFg);
        avatar.add(avatarLabel);
        userWrap.add(avatar);
        JLabel userLabel = new JLabel(data.username);
        userLabel.setFont(Theme.monoLabel());
        userLabel.setForeground(data.highlightRow ? Theme.ERROR : Theme.ON_SURFACE);
        userWrap.add(userLabel);
        row.add(userWrap);

        String actionHtml = "<html>" + data.actionDetails +
                (data.extraDetail != null ? " <font color='#75777D' size='-1'>[" + data.extraDetail + "]</font>" : "") + "</html>";
        JLabel actionLabel = new JLabel(actionHtml);
        actionLabel.setFont(Theme.monoLabel());
        actionLabel.setForeground(Theme.ON_SURFACE);
        row.add(actionLabel);

        JPanel resultWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        resultWrap.setOpaque(false);
        Badge.Semantic semantic;
        if ("DENIED".equals(data.result)) semantic = Badge.Semantic.ERROR;
        else if ("FAILED".equals(data.result)) semantic = Badge.Semantic.WARNING;
        else semantic = Badge.Semantic.SUCCESS;
        resultWrap.add(new Badge(data.result, semantic));
        row.add(resultWrap);

        return row;
    }

    private JPanel paginationFooter(){
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.SURFACE_CONTAINER_LOWEST);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel info = new JLabel("Showing entries from this session's audit_log.csv");
        info.setFont(Theme.bodySm());
        info.setForeground(Theme.ON_SURFACE_VARIANT);
        footer.add(info, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttons.setOpaque(false);
        RoundedButton prev = new RoundedButton("Prev", RoundedButton.Variant.SECONDARY);
        prev.setEnabled(false);
        RoundedButton next = new RoundedButton("Next", RoundedButton.Variant.SECONDARY);
        buttons.add(prev);
        buttons.add(next);
        footer.add(buttons, BorderLayout.EAST);

        return footer;
    }
}
