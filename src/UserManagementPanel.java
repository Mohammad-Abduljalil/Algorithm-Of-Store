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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * شاشة إدارة المستخدمين (Admin Only)، مطابقة لكود Stitch:
 * عنوان + شارة "Admin Only" + زر تسجيل موظف جديد -> جدول مستخدمين -> تذييل ترقيم.
 */
public class UserManagementPanel extends JPanel {

    private final JPanel tableRowsContainer;
    private Runnable onRegisterEmployee;
    private Consumer<UserRow> onResetPassword;

    public UserManagementPanel(){
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(pageHeader());
        content.add(Box.createVerticalStrut(24));

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

    public static List<UserRow> sampleData(){
        List<UserRow> rows = new ArrayList<>();
        rows.add(new UserRow("JD", "jdoe_admin", Role.ADMIN, true, Theme.TERTIARY_FIXED, Theme.TERTIARY));
        rows.add(new UserRow("AS", "asmith_ops", Role.EMPLOYEE, true, Theme.SURFACE_CONTAINER_HIGH, Theme.ON_SURFACE_VARIANT));
        rows.add(new UserRow("MK", "mkelly_warehouse", Role.EMPLOYEE, true, Theme.SURFACE_CONTAINER_HIGH, Theme.ON_SURFACE_VARIANT));
        return rows;
    }

    public void setRows(List<UserRow> rows){
        tableRowsContainer.removeAll();
        for (UserRow row : rows){
            tableRowsContainer.add(dataRow(row));
        }
        tableRowsContainer.revalidate();
        tableRowsContainer.repaint();
    }

    public void setOnRegisterEmployee(Runnable r){ this.onRegisterEmployee = r; }
    public void setOnResetPassword(Consumer<UserRow> c){ this.onResetPassword = c; }

    private JPanel pageHeader(){
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel titleCol = new JPanel();
        titleCol.setOpaque(false);
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel title = new JLabel("User Management");
        title.setFont(Theme.display());
        title.setForeground(Theme.ON_SURFACE);
        titleRow.add(title);
        Badge adminOnly = new Badge("ADMIN ONLY", Badge.Semantic.ERROR);
        titleRow.add(adminOnly);
        titleCol.add(titleRow);

        JLabel subtitle = new JLabel("Manage enterprise employee access and roles.");
        subtitle.setFont(Theme.bodyMd());
        subtitle.setForeground(Theme.ON_SURFACE_VARIANT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleCol.add(subtitle);

        row.add(titleCol, BorderLayout.WEST);

        RoundedButton registerBtn = new RoundedButton("Register New Employee", Theme.SECONDARY_LINK, Color.WHITE);
        registerBtn.setIcon(Icons.personAdd(16, Color.WHITE));
        registerBtn.setIconTextGap(8);
        registerBtn.addActionListener(e -> { if (onRegisterEmployee != null) onRegisterEmployee.run(); });
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.add(registerBtn);
        row.add(btnWrap, BorderLayout.EAST);

        return row;
    }

    private JPanel tableHeaderRow(){
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Theme.SURFACE);
        row.setOpaque(true);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.SURFACE_CONTAINER_HIGH),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JPanel avatarSpacer = new JPanel();
        avatarSpacer.setOpaque(false);
        avatarSpacer.setPreferredSize(new Dimension(48, 10));
        row.add(avatarSpacer, BorderLayout.WEST);

        JPanel cols = new JPanel(new GridLayout(1, 3, 12, 0));
        cols.setOpaque(false);
        cols.add(colLabel("Username", SwingConstants.LEFT));
        cols.add(colLabel("Role", SwingConstants.LEFT));
        cols.add(colLabel("Status", SwingConstants.LEFT));
        row.add(cols, BorderLayout.CENTER);

        JLabel actionsLabel = colLabel("ACTIONS", SwingConstants.RIGHT);
        actionsLabel.setPreferredSize(new Dimension(80, 20));
        row.add(actionsLabel, BorderLayout.EAST);

        return row;
    }

    private JLabel colLabel(String text, int align){
        JLabel label = new JLabel(text, align);
        label.setFont(Theme.labelMd());
        label.setForeground(Theme.ON_SURFACE_VARIANT);
        return label;
    }

    private JPanel dataRow(UserRow data){
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Theme.SURFACE_CONTAINER_LOWEST);
        row.setOpaque(true);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

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
        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setLayout(new java.awt.GridBagLayout());
        JLabel initialsLabel = new JLabel(data.initials);
        initialsLabel.setFont(Theme.labelMd());
        initialsLabel.setForeground(data.avatarFg);
        avatar.add(initialsLabel);
        JPanel avatarWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        avatarWrap.setOpaque(false);
        avatarWrap.setPreferredSize(new Dimension(48, 36));
        avatarWrap.add(avatar);
        row.add(avatarWrap, BorderLayout.WEST);

        JPanel cols = new JPanel(new GridLayout(1, 3, 12, 0));
        cols.setOpaque(false);

        JLabel usernameLabel = new JLabel(data.username);
        usernameLabel.setFont(Theme.bodyMd().deriveFont(Font.BOLD));
        usernameLabel.setForeground(Theme.ON_SURFACE);
        cols.add(usernameLabel);

        JPanel roleWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        roleWrap.setOpaque(false);
        roleWrap.add(new Badge(data.role == Role.ADMIN ? "Admin" : "Employee",
                data.role == Role.ADMIN ? Badge.Semantic.INFO : Badge.Semantic.NEUTRAL));
        cols.add(roleWrap);

        JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        statusWrap.setOpaque(false);
        JPanel dot = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(data.active ? Theme.SUCCESS_TEXT : Theme.OUTLINE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(8, 8));
        statusWrap.add(dot);
        JLabel statusLabel = new JLabel(data.active ? "Active" : "Inactive");
        statusLabel.setFont(Theme.bodySm());
        statusLabel.setForeground(Theme.ON_SURFACE_VARIANT);
        statusWrap.add(statusLabel);
        cols.add(statusWrap);

        row.add(cols, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.setPreferredSize(new Dimension(80, 30));
        IconButton resetBtn = new IconButton(Icons.lockReset(16, Theme.ON_SURFACE_VARIANT), Theme.SECONDARY_FIXED);
        resetBtn.setToolTipText("Reset Password");
        resetBtn.addActionListener(e -> { if (onResetPassword != null) onResetPassword.accept(data); });
        actions.add(resetBtn);
        row.add(actions, BorderLayout.EAST);

        return row;
    }

    private JPanel paginationFooter(){
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.SURFACE);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.SURFACE_CONTAINER_HIGH),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel info = new JLabel("Showing 1 to 3 of 42 entries");
        info.setFont(Theme.bodySm());
        info.setForeground(Theme.ON_SURFACE_VARIANT);
        footer.add(info, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
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
