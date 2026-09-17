import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * نقطة انطلاق الواجهة الرسومية (بديل عن Store.java الخاصة بالكونسول، والتي تبقى تعمل كما هي).
 * تُنشئ AppContext واحدًا مشتركًا بين كل الشاشات، وتُدير التنقل بين تسجيل الدخول والتطبيق الرئيسي.
 */
public class SwingApp {

    public static void main(String[] args){
        SwingUtilities.invokeLater(SwingApp::start);
    }

    private static void start(){
        AppContext ctx = new AppContext();
        Persistence.loadAll(ctx.productManagement, ctx.shipmentsRegisters, ctx.orders);

        JFrame frame = new JFrame("LogisticsPro - Inventory & Shipment Management");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // نتحكم بالحفظ يدويًا قبل الإغلاق
        frame.setSize(1440, 900);
        frame.setMinimumSize(new java.awt.Dimension(1100, 700));
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e){
                Persistence.saveAll(ctx.productManagement, ctx.shipmentsRegisters, ctx.orders);
                if (ctx.currentUser != null){
                    AuditLog.record(ctx.currentUser.getUsername(), "Logout", "Application closed");
                }
                frame.dispose();
                System.exit(0);
            }
        });

        showLoginScreen(frame, ctx);
        frame.setVisible(true);
    }

    private static void showLoginScreen(JFrame frame, AppContext ctx){
        LoginPanel loginPanel = new LoginPanel();
        loginPanel.addLoginListener(e -> {
            String username = loginPanel.getUsername();
            String password = loginPanel.getPassword();
            loginPanel.clearError();
            loginPanel.setLoading(true);

            // نُنفّذ التحقق خارج خيط الواجهة (EDT) حتى تظهر دائرة التحميل فعليًا
            // بدل تجمّد الواجهة، ثم نعود للـ EDT لتحديث الشاشة.
            new javax.swing.SwingWorker<User, Void>() {
                @Override protected User doInBackground(){
                    try { Thread.sleep(250); } catch (InterruptedException ignored) {}
                    return ctx.authManager.loginGui(username, password);
                }
                @Override protected void done(){
                    loginPanel.setLoading(false);
                    User user = null;
                    try { user = get(); } catch (Exception ignored) {}
                    if (user != null){
                        ctx.currentUser = user;
                        showMainApp(frame, ctx);
                    } else {
                        loginPanel.showError("Invalid username or password.");
                    }
                }
            }.execute();
        });
        frame.setContentPane(loginPanel);
        frame.revalidate();
        frame.repaint();
        loginPanel.focusFirstField(); // التركيز يبدأ من اسم المستخدم
    }

    private static void showMainApp(JFrame frame, AppContext ctx){
        MainAppShell shell = new MainAppShell(ctx.currentUser.getUsername(), ctx.currentUser.getRole());

        DashboardPanel dashboard = new DashboardPanel(ctx);
        shell.registerScreen(Sidebar.KEY_DASHBOARD, dashboard);

        SettingsPanel settingsPanel = new SettingsPanel();
        settingsPanel.addSaveListener(e -> {
            String error = ctx.authManager.changePasswordGui(ctx.currentUser,
                    settingsPanel.getCurrentPassword(), settingsPanel.getNewPassword());
            if (error != null){
                settingsPanel.showError(error);
            } else if (!settingsPanel.getNewPassword().equals(settingsPanel.getConfirmPassword())){
                settingsPanel.showError("New password and confirmation do not match.");
            } else {
                settingsPanel.showSuccess();
            }
        });
        shell.registerScreen(Sidebar.KEY_SETTINGS, settingsPanel);

        // "Administration" في الشريط الجانبي عنصر واحد فقط، فندمج المستخدمين وسجل العمليات
        // بتبويبين داخل شاشة واحدة (AdministrationPanel) بدل تفريع الشريط الجانبي نفسه.
        shell.registerScreen(Sidebar.KEY_ADMINISTRATION, new AdministrationPanel(ctx));

        // الشاشات الباقية ستُربط تباعًا؛ نضع الآن عناصر نائبة بسيطة حتى يعمل التنقل بالكامل من اليوم الأول
        // ===== Products: عرض/إضافة/تعديل/حذف حقيقي مربوط بـ ProductManagement =====
        ProductsPanel productsPanel = new ProductsPanel();
        Runnable[] refreshProducts = new Runnable[1]; // مرجع ذاتي حتى تقدر الأزرار تُعيد بناء نفس الشاشة بعد أي تعديل
        refreshProducts[0] = () -> {
            productsPanel.setRows(buildProductRows(ctx));
            dashboard.refresh(); // عدد المنتجات/القيمة/تنبيهات المخزون تتأثر بأي تعديل هنا
        };
        productsPanel.setRows(buildProductRows(ctx));

        productsPanel.setOnAddProduct(() -> {
            if (!requireAdminGui(frame, ctx, "Add Product")) return;
            AddProductDialog dialog = new AddProductDialog(frame, currentCategoryNames(ctx));
            dialog.setVisible(true);
            if (dialog.isConfirmed()){
                String error = validateAndAddProduct(ctx, dialog);
                if (error != null){
                    javax.swing.JOptionPane.showMessageDialog(frame, error, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                } else {
                    AuditLog.record(ctx.currentUser.getUsername(), "Add Product", "SUCCESS - GUI");
                    refreshProducts[0].run();
                }
            }
        });

        productsPanel.setOnEdit(row -> {
            if (!requireAdminGui(frame, ctx, "Update Product")) return;
            int id = Integer.parseInt(row.id.replace("#", ""));
            Product product = ctx.productManagement.productMap.get(id);
            if (product == null) return;
            AddProductDialog dialog = new AddProductDialog(frame, currentCategoryNames(ctx));
            dialog.configureForEdit(id, product.getNameOfProduct(), product.getPriceOfProduct(),
                    product.getQuantityOfProduct(), product.getMinimumStockThreshold(), product.getCategory());
            dialog.setVisible(true);
            if (dialog.isConfirmed()){
                String error = validateAndUpdateProduct(ctx, id, dialog);
                if (error != null){
                    javax.swing.JOptionPane.showMessageDialog(frame, error, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                } else {
                    AuditLog.record(ctx.currentUser.getUsername(), "Update Product", "SUCCESS - GUI");
                    refreshProducts[0].run();
                }
            }
        });

        productsPanel.setOnDelete(row -> {
            if (!requireAdminGui(frame, ctx, "Delete Product")) return;
            int id = Integer.parseInt(row.id.replace("#", ""));
            int confirm = javax.swing.JOptionPane.showConfirmDialog(frame,
                    "Delete product \"" + row.name + "\"? This cannot be undone.",
                    "Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
            if (confirm == javax.swing.JOptionPane.YES_OPTION){
                String error = ctx.productManagement.deleteProductGui(id);
                if (error != null){
                    javax.swing.JOptionPane.showMessageDialog(frame, error, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                } else {
                    AuditLog.record(ctx.currentUser.getUsername(), "Delete Product", "SUCCESS - GUI");
                    refreshProducts[0].run();
                }
            }
        });

        shell.registerScreen(Sidebar.KEY_PRODUCTS, productsPanel);
        // ===== Shipments & Orders: عرض/إضافة/حذف حقيقي + إضافة منتجات لشحنة =====
        ShipmentsPanel shipmentsPanel = new ShipmentsPanel();
        Runnable[] refreshShipments = new Runnable[1];
        refreshShipments[0] = () -> {
            shipmentsPanel.setRows(buildShipmentRows(ctx), null);
            dashboard.refresh();
        };
        shipmentsPanel.setRows(buildShipmentRows(ctx), null);

        shipmentsPanel.setOnAddShipment(() -> {
            AddShipmentDialog dialog = new AddShipmentDialog(frame);
            dialog.setVisible(true);
            if (dialog.isConfirmed()){
                String error = validateAndAddShipment(ctx, dialog);
                if (error != null){
                    javax.swing.JOptionPane.showMessageDialog(frame, error, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                } else {
                    AuditLog.record(ctx.currentUser.getUsername(), "Add Shipment", "SUCCESS - GUI");
                    refreshShipments[0].run();
                }
            }
        });

        shipmentsPanel.setOnAddProductToShipment(row -> {
            if (ctx.productManagement.listOfProduct.isEmpty()){
                javax.swing.JOptionPane.showMessageDialog(frame, "There are no products in inventory yet.", "No Products", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            String[] choices = ctx.productManagement.listOfProduct.stream()
                    .map(p -> p.getID() + " - " + p.getNameOfProduct() + " (stock: " + p.getQuantityOfProduct() + ")")
                    .toArray(String[]::new);
            AddProductToShipmentDialog dialog = new AddProductToShipmentDialog(frame, choices);
            dialog.setVisible(true);
            if (dialog.isConfirmed()){
                try {
                    int shipmentId = Integer.parseInt(row.id.replace("#", ""));
                    int productId = Integer.parseInt(dialog.getSelectedProduct().split(" - ")[0].trim());
                    int quantity = Integer.parseInt(dialog.getQuantity().trim());
                    String error = ctx.shipmentsRegisters.addProductToShipmentGui(shipmentId, productId, quantity, ctx.productManagement);
                    if (error != null){
                        javax.swing.JOptionPane.showMessageDialog(frame, error, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    } else {
                        AuditLog.record(ctx.currentUser.getUsername(), "Add Product To Shipment", "SUCCESS - GUI");
                        refreshShipments[0].run();
                    }
                } catch (NumberFormatException ex){
                    javax.swing.JOptionPane.showMessageDialog(frame, "Quantity must be a valid number.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        shipmentsPanel.setOnDeleteShipment(row -> {
            if (!requireAdminGui(frame, ctx, "Delete Shipment")) return;
            int confirm = javax.swing.JOptionPane.showConfirmDialog(frame,
                    "Delete shipment " + row.id + " to " + row.destination + "? This cannot be undone.",
                    "Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
            if (confirm == javax.swing.JOptionPane.YES_OPTION){
                int id = Integer.parseInt(row.id.replace("#", ""));
                ctx.orders.deleteOrderByShipmentIdGui(id);
                String error = ctx.shipmentsRegisters.deleteShipmentGui(id);
                if (error != null){
                    javax.swing.JOptionPane.showMessageDialog(frame, error, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                } else {
                    AuditLog.record(ctx.currentUser.getUsername(), "Delete Shipment", "SUCCESS - GUI");
                    refreshShipments[0].run();
                }
            }
        });

        // العملية 11: تعديل تاريخ تسليم الشحنة
        shipmentsPanel.setOnEditDeliveryDate(row -> {
            if (!requireAdminGui(frame, ctx, "Update Shipment Delivery Date")) return;
            int id = Integer.parseInt(row.id.replace("#", ""));
            java.time.LocalDate picked = askForDate(frame, "Update Delivery Date - " + row.id);
            if (picked == null) return;
            String error = ctx.shipmentsRegisters.updateDeliveryDateGui(id, picked.getYear(), picked.getMonthValue(), picked.getDayOfMonth());
            if (error != null){
                javax.swing.JOptionPane.showMessageDialog(frame, error, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            } else {
                AuditLog.record(ctx.currentUser.getUsername(), "Update Shipment Delivery Date", "SUCCESS - GUI");
                refreshShipments[0].run();
            }
        });

        // العملية 13: تعديل أولوية الطلب المرتبط بالشحنة
        shipmentsPanel.setOnEditPriority(row -> {
            if (!requireAdminGui(frame, ctx, "Update Order Priority")) return;
            int id = Integer.parseInt(row.id.replace("#", ""));
            Integer[] choices = new Integer[10];
            for (int i = 0; i < 10; i++) choices[i] = i + 1;
            Object picked = javax.swing.JOptionPane.showInputDialog(frame,
                    "Select the new priority for order of shipment " + row.id + " :",
                    "Update Order Priority", javax.swing.JOptionPane.QUESTION_MESSAGE, null, choices, row.priority);
            if (picked == null) return;
            String error = ctx.orders.updatePriorityGui(id, (Integer) picked);
            if (error != null){
                javax.swing.JOptionPane.showMessageDialog(frame, error, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            } else {
                AuditLog.record(ctx.currentUser.getUsername(), "Update Order Priority", "SUCCESS - GUI");
                refreshShipments[0].run();
            }
        });

        // العملية 12: إرجاع (وحذف) الطلب الأعلى أولوية عبر الكومة
        shipmentsPanel.setOnReturnHighestPriority(() -> {
            if (!requireAdminGui(frame, ctx, "Return Top Priority Order")) return;
            if (ctx.orders.listOfOrder.isEmpty()){
                javax.swing.JOptionPane.showMessageDialog(frame, "There are no orders to return.",
                        "No Orders", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            Order top = ctx.orders.returnHighestPriorityOrderGui();
            if (top == null) return;
            int shipmentId = top.getShipment().getShipmentId();
            ctx.shipmentsRegisters.deleteShipmentGui(shipmentId);
            AuditLog.record(ctx.currentUser.getUsername(), "Return Top Priority Order",
                    "SUCCESS - GUI - shipment #" + shipmentId + " (priority " + top.getPriority() + ")");
            javax.swing.JOptionPane.showMessageDialog(frame,
                    "Returned shipment #" + shipmentId + " to " + top.getShipment().getShipmentDestination()
                            + "\nPriority: " + top.getPriority(),
                    "Top Priority Order Returned", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            refreshShipments[0].run();
        });

        shell.registerScreen(Sidebar.KEY_SHIPMENTS, shipmentsPanel);
        shell.registerScreen(Sidebar.KEY_REPORTS, new ReportsPanel(ctx));

        shell.getHeaderBar().setSaveButtonVisible(ctx.isAdmin());
        shell.getHeaderBar().addSaveListener(e -> {
            if (!requireAdminGui(frame, ctx, "Manual Save Data")) return;
            Persistence.saveAll(ctx.productManagement, ctx.shipmentsRegisters, ctx.orders);
            AuditLog.record(ctx.currentUser.getUsername(), "Manual Save Data", "SUCCESS - GUI");
            javax.swing.JOptionPane.showMessageDialog(frame, "All data has been saved successfully.",
                    "Saved", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        });

        shell.getHeaderBar().addLogoutListener(e -> {
            Persistence.saveAll(ctx.productManagement, ctx.shipmentsRegisters, ctx.orders);
            AuditLog.record(ctx.currentUser.getUsername(), "Logout", "GUI session ended");
            ctx.currentUser = null;
            showLoginScreen(frame, ctx);
        });

        frame.setContentPane(shell);
        shell.showScreen(Sidebar.KEY_DASHBOARD);
        frame.revalidate();
        frame.repaint();
    }

    // تُحقّق من صلاحية Admin قبل تنفيذ عملية حسّاسة، وتُسجّل الرفض في AuditLog تمامًا كنسخة الكونسول
    private static boolean requireAdminGui(JFrame frame, AppContext ctx, String actionName){
        if (!ctx.isAdmin()){
            AuditLog.record(ctx.currentUser.getUsername(), actionName,
                    "DENIED - insufficient privileges (role=" + ctx.currentUser.getRole() + ")");
            javax.swing.JOptionPane.showMessageDialog(frame,
                    "Access Denied! This action requires Admin privileges.",
                    "Access Denied", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private static java.util.List<ProductRow> buildProductRows(AppContext ctx){
        java.util.List<ProductRow> rows = new java.util.ArrayList<>();
        for (Product p : ctx.productManagement.listOfProduct){
            rows.add(new ProductRow(
                    "#" + p.getID(),
                    p.getNameOfProduct(),
                    p.getCategory(),
                    String.format("$%,.2f", (float) p.getPriceOfProduct()),
                    String.valueOf(p.getQuantityOfProduct()),
                    p.isLowStock()
            ));
        }
        return rows;
    }

    private static String[] currentCategoryNames(AppContext ctx){
        java.util.Set<String> names = new java.util.LinkedHashSet<>(ctx.productManagement.categoryMap.keySet());
        names.add("Uncategorized");
        names.add("Electronics");
        names.add("Office");
        names.add("Hardware");
        return names.toArray(new String[0]);
    }

    private static String validateAndAddProduct(AppContext ctx, AddProductDialog dialog){
        int id, price, quantity, threshold;
        try {
            id = Integer.parseInt(dialog.getProductId().trim());
            price = Integer.parseInt(dialog.getPrice().trim());
            quantity = Integer.parseInt(dialog.getQuantity().trim());
            threshold = Integer.parseInt(dialog.getThreshold().trim());
        } catch (NumberFormatException ex){
            return "ID, Price, Quantity, and Threshold must all be valid numbers.";
        }
        if (dialog.getProductName() == null || dialog.getProductName().isBlank()){
            return "Product name cannot be empty.";
        }
        return ctx.productManagement.addNewProductGui(id, dialog.getProductName().trim(), price, quantity, threshold, dialog.getCategory());
    }

    // نافذة بسيطة لاختيار تاريخ (سنة/شهر/يوم) - تُستخدم في تعديل تاريخ التسليم
    private static java.time.LocalDate askForDate(JFrame frame, String title){
        int currentYear = java.time.Year.now().getValue();
        Integer[] years = new Integer[6];
        for (int i = 0; i < 6; i++) years[i] = currentYear + i;
        javax.swing.JComboBox<Integer> yearBox = new javax.swing.JComboBox<>(years);
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        javax.swing.JComboBox<String> monthBox = new javax.swing.JComboBox<>(months);
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) days[i] = i + 1;
        javax.swing.JComboBox<Integer> dayBox = new javax.swing.JComboBox<>(days);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(1, 3, 8, 0));
        panel.add(yearBox);
        panel.add(monthBox);
        panel.add(dayBox);

        int result = javax.swing.JOptionPane.showConfirmDialog(frame, panel, title,
                javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE);
        if (result != javax.swing.JOptionPane.OK_OPTION) return null;
        try {
            return java.time.LocalDate.of((Integer) yearBox.getSelectedItem(),
                    monthBox.getSelectedIndex() + 1, (Integer) dayBox.getSelectedItem());
        } catch (Exception e){
            javax.swing.JOptionPane.showMessageDialog(frame, "Invalid date.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private static java.util.List<ShipmentRow> buildShipmentRows(AppContext ctx){
        java.util.List<ShipmentRow> rows = new java.util.ArrayList<>();
        java.util.List<Shipment> shipments = ctx.shipmentsRegisters.getListOfShipment();
        if (shipments == null) return rows;
        for (Shipment s : shipments){
            int priority = 1;
            for (Order o : ctx.orders.listOfOrder){
                if (o.getShipment().getShipmentId() == s.getShipmentId()){
                    priority = o.getPriority();
                    break;
                }
            }
            java.util.List<ShipmentRow.ShipmentProductLine> lines = new java.util.ArrayList<>();
            for (Product p : s.listOfProducts){
                float subtotal = p.getQuantityOfProduct() * p.getPriceOfProduct();
                lines.add(new ShipmentRow.ShipmentProductLine(p.getNameOfProduct(), String.valueOf(p.getQuantityOfProduct()),
                        String.format("$%,.2f", (float) p.getPriceOfProduct()), String.format("$%,.2f", subtotal)));
            }
            rows.add(new ShipmentRow("#" + s.getShipmentId(), s.getShipmentDestination(), s.getDeliveryDate(),
                    String.format("$%,.2f", s.getMaximumBudgetOfShipment()), String.format("$%,.2f", s.getShipmentCost()),
                    priority, lines));
        }
        return rows;
    }

    private static String validateAndAddShipment(AppContext ctx, AddShipmentDialog dialog){
        int id;
        float budget;
        try {
            id = Integer.parseInt(dialog.getShipmentId().replaceAll("[^0-9]", ""));
            budget = Float.parseFloat(dialog.getBudget().trim());
        } catch (NumberFormatException ex){
            return "Shipment ID and Budget must be valid numbers (ID can include a prefix like 'SH-').";
        }
        if (dialog.getDestination() == null || dialog.getDestination().isBlank()){
            return "Destination cannot be empty.";
        }
        String error = ctx.shipmentsRegisters.addShipmentGui(id, dialog.getDestination().trim(), budget,
                dialog.getYear(), dialog.getMonth(), dialog.getDay());
        if (error != null) return error;

        Shipment created = ctx.shipmentsRegisters.shipmentMap.get(id);
        ctx.orders.addOrderGui(created, dialog.getPriority());
        return null;
    }

    private static String validateAndUpdateProduct(AppContext ctx, int id, AddProductDialog dialog){
        int price, quantity, threshold;
        try {
            price = Integer.parseInt(dialog.getPrice().trim());
            quantity = Integer.parseInt(dialog.getQuantity().trim());
            threshold = Integer.parseInt(dialog.getThreshold().trim());
        } catch (NumberFormatException ex){
            return "Price, Quantity, and Threshold must all be valid numbers.";
        }
        return ctx.productManagement.updateProductGui(id, price, quantity, threshold, dialog.getCategory());
    }
}
