import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * اختبارات آلية للتحقق من الإصلاحات الأربعة + إصلاح تكرار الـ ID في القوائم.
 * لا تحتاج تفاعل يدوي (تُحاكي إدخال المستخدم عبر System.in).
 */
public class Tests {

    static int passed = 0, failed = 0;

    /**
     * InputStream يُعيد بايت واحد فقط في كل استدعاء read().
     * ضروري لأن المشروع يُنشئ عدة كائنات Scanner مستقلة (في ShipmentsRegisters,
     * Orders, ProductManagement, Product) وكلها تُغلّف نفس System.in. إذا استخدمنا
     * ByteArrayInputStream عاديًا، فإن أول Scanner يقرأ منه قد "يلتهم" بايتات
     * أكثر مما يحتاج فعليًا في قراءة داخلية واحدة، فتُحرم كائنات Scanner
     * الأخرى من البيانات المتبقية (رغم وجودها منطقيًا) فيحدث تعليق أو خطأ.
     * القراءة بايت-بايت تُحاكي دخل الطرفية التفاعلية الحقيقي وتَحل المشكلة.
     */
    static class SlowInputStream extends InputStream {
        private final byte[] data;
        private int pos = 0;
        SlowInputStream(String s) { this.data = s.getBytes(); }
        @Override public int read() { return pos < data.length ? (data[pos++] & 0xFF) : -1; }
        @Override public int read(byte[] b, int off, int len) {
            if (pos >= data.length) return -1;
            b[off] = data[pos++];
            return 1;
        }
    }

    public static void main(String[] args) {
        test1_AVL_DeleteTwoChildrenPreservesLeftSubtree();
        test2_AVL_RootUpdatesAfterRotation();
        test3_BST_DuplicateInsertDoesNotBreakTree();
        test4_BST_DeleteTwoChildrenPreservesLeftSubtree();
        test5_Store_InvalidMenuDoesNotCrash();
        test6_ShipmentsRegisters_DuplicateIdNotAddedToList();
        test7_ProductManagement_DuplicateIdNotAddedToList();
        test8_ProductManagement_DeleteRemovesFromMap();
        test9_ShipmentsRegisters_DeleteRemovesFromMap();
        test10_Persistence_SaveAndLoadRoundTrip();
        test11_AuthManager_LoginAttemptsAndSuccess();
        test12_RoleBasedAccess_EmployeeBlockedFromAdminAction();
        test13_LowStockAlert_TriggersAndReportCorrectly();
        test14_Categories_IndexingAndReportsWork();
        test15_AuditLog_RecordsActionsAndDenials();

        System.out.println("\n=====================================");
        System.out.println("النتيجة النهائية: " + passed + " ناجح / " + failed + " فاشل");
        System.out.println("=====================================");
    }

    // ---------- أدوات مساعدة ----------

    static void check(String testName, boolean condition, String detail) {
        if (condition) {
            System.out.println("✅ PASS - " + testName);
            passed++;
        } else {
            System.out.println("❌ FAIL - " + testName + " -> " + detail);
            failed++;
        }
    }

    static Product newProduct(int id) {
        Product p = new Product(id);
        p.setNameOfProduct("P" + id);
        p.setPriceOfProduct(10);
        p.setQuantityOfProduct(1);
        return p;
    }

    // نجمع كل الـ IDs بترتيب inorder يدويًا (بدل الاعتماد على toString)
    static void inOrderIds(Product node, List<Integer> out) {
        if (node == null) return;
        inOrderIds(node.left, out);
        out.add(node.getID());
        inOrderIds(node.right, out);
    }

    static void inOrderIdsShip(Shipment node, List<Integer> out) {
        if (node == null) return;
        inOrderIdsShip(node.left, out);
        out.add(node.getShipmentId());
        inOrderIdsShip(node.right, out);
    }

    // ---------- اختبار 1: حذف عقدة AVL لها ابنان (يجب ألا يضيع الفرع الأيسر) ----------
    static void test1_AVL_DeleteTwoChildrenPreservesLeftSubtree() {
        System.out.println("\n--- اختبار 1: حذف AVL لعقدة لها ابنان ---");
        AVLTree tree = new AVLTree();
        int[] ids = {50, 30, 70, 20, 40, 60, 80}; // شجرة متوازنة تمامًا
        for (int id : ids) tree.insertProduct(newProduct(id));

        // 50 لها ابنان (30 و 70) => الحذف يستخدم الخَلَف (successor)
        tree.deleteProductByID(50);

        List<Integer> result = new ArrayList<>();
        inOrderIds(tree.root, result);

        List<Integer> expected = new ArrayList<>();
        for (int id : ids) if (id != 50) expected.add(id);
        expected.sort(null);

        check("AVL Delete - كل العناصر موجودة بعد الحذف (لم يضع 20,30,40)",
                result.equals(expected),
                "المتوقع=" + expected + " لكن الناتج=" + result);
    }

    // ---------- اختبار 2: تحديث جذر AVL تلقائيًا بعد Rotation ----------
    static void test2_AVL_RootUpdatesAfterRotation() {
        System.out.println("\n--- اختبار 2: تحديث الجذر بعد Rotation ---");
        AVLTree tree = new AVLTree();
        // إدخال تصاعدي متتالي => يفرض دوران (rotation) في AVL
        tree.insertProduct(newProduct(10));
        tree.insertProduct(newProduct(20));
        tree.insertProduct(newProduct(30)); // هنا يحدث Left Rotate، الجذر الجديد = 20

        check("AVL Root - الجذر أصبح 20 بعد الدوران",
                tree.root != null && tree.root.getID() == 20,
                "الجذر الفعلي = " + (tree.root == null ? "null" : tree.root.getID()));

        check("AVL Root - البحث عن 10 يعمل عبر الجذر الجديد",
                tree.searchProductByID(10) != null, "لم يتم إيجاد 10");
        check("AVL Root - البحث عن 30 يعمل عبر الجذر الجديد",
                tree.searchProductByID(30) != null, "لم يتم إيجاد 30");
    }

    // ---------- اختبار 3: إدخال ID مكرر في BST لا يُسقط الشجرة ----------
    static void test3_BST_DuplicateInsertDoesNotBreakTree() {
        System.out.println("\n--- اختبار 3: تكرار ID في BST ---");
        BSTTree tree = new BSTTree();
        tree.insertShipment(new Shipment(100));
        tree.insertShipment(new Shipment(100)); // تكرار - يجب ألا يُسقط الشجرة

        check("BST Duplicate - الجذر لم يصبح null بعد إدخال مكرر",
                tree.root != null, "الجذر أصبح null! (نفس الخطأ القديم)");
        check("BST Duplicate - العنصر الأصلي ما زال موجودًا",
                tree.searchShipmentByID(100) != null, "العنصر 100 غير موجود");
    }

    // ---------- اختبار 4: حذف عقدة BST لها ابنان (نفس اختبار 1 لكن للشحنات) ----------
    static void test4_BST_DeleteTwoChildrenPreservesLeftSubtree() {
        System.out.println("\n--- اختبار 4: حذف BST لعقدة لها ابنان ---");
        BSTTree tree = new BSTTree();
        int[] ids = {50, 30, 70, 20, 40, 60, 80};
        for (int id : ids) tree.insertShipment(new Shipment(id));

        tree.deleteShipmentByID(50);

        List<Integer> result = new ArrayList<>();
        inOrderIdsShip(tree.root, result);

        List<Integer> expected = new ArrayList<>();
        for (int id : ids) if (id != 50) expected.add(id);
        expected.sort(null);

        check("BST Delete - كل العناصر موجودة بعد الحذف (لم يضع 20,30,40)",
                result.equals(expected),
                "المتوقع=" + expected + " لكن الناتج=" + result);
    }

    // ---------- اختبار 6: تكرار ID شحنة لا يُضاف مرتين لقائمة ShipmentsRegisters ----------
    static void test6_ShipmentsRegisters_DuplicateIdNotAddedToList() {
        System.out.println("\n--- اختبار 6: تكرار ID شحنة في القائمة ---");

        // شحنة أولى ID=100 (تكتمل بنجاح، وتحتاج أولوية Order لاحقًا)
        // شحنة ثانية بنفس ID=100 (يجب أن تُرفض قبل الوصول لطلب الأولوية)
        String simulatedInput =
                "100\nTestDest\n50\n2030\n1\n1\n5\n" +   // addShipment #1
                "100\nTestDest2\n60\n2030\n2\n2\n";       // addShipment #2 (مكرر)

        InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        // يجب ضبط System.in *قبل* إنشاء الكائنات، لأن حقول Scanner فيها
        // تُهيَّأ داخل الـ constructor وترتبط بـ System.in في تلك اللحظة تحديدًا
        System.setIn(new SlowInputStream(simulatedInput));

        Orders orders = new Orders();
        ProductManagement pm = new ProductManagement(); // فارغة عمدًا لتبسيط الإدخال
        ShipmentsRegisters sr = new ShipmentsRegisters(orders);

        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(capturedOut));
        String output;
        try {
            sr.addShipment(pm);
            sr.addShipment(pm);
            sr.printAllShipments();
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
            output = capturedOut.toString();
        }

        int occurrences = countOccurrences(output, "Shipment ID = 100");
        check("Shipments - الشحنة المكررة لم تُضَف للقائمة (تكرار واحد فقط لـ ID=100)",
                occurrences == 1,
                "عدد ظهور 'Shipment ID = 100' في القائمة = " + occurrences + " (يجب أن يكون 1)\n" + output);
        check("Shipments - ظهرت رسالة رفض التكرار",
                output.contains("already exists"), "لم تظهر رسالة الرفض المتوقعة\n" + output);
    }

    // ---------- اختبار 7: تكرار ID منتج لا يُضاف مرتين لقائمة ProductManagement ----------
    static void test7_ProductManagement_DuplicateIdNotAddedToList() {
        System.out.println("\n--- اختبار 7: تكرار ID منتج في القائمة ---");
        String simulatedInput =
                "10\nTestName\n5\n5\n3\nCatA\n" +    // addNewProduct #1
                "10\nTestName2\n7\n7\n3\nCatA\n";     // addNewProduct #2 (مكرر)

        InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        System.setIn(new SlowInputStream(simulatedInput));

        ProductManagement pm = new ProductManagement();

        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(capturedOut));
        String output;
        try {
            pm.addNewProduct();
            pm.addNewProduct();
            pm.printProducts();
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
            output = capturedOut.toString();
        }

        int occurrences = countOccurrences(output, "ID = 10}");
        check("Products - المنتج المكرر لم يُضَف للقائمة (تكرار واحد فقط لـ ID=10)",
                occurrences == 1,
                "عدد ظهور 'ID = 10}' في القائمة = " + occurrences + " (يجب أن يكون 1)\n" + output);
        check("Products - ظهرت رسالة رفض التكرار",
                output.contains("already exists"), "لم تظهر رسالة الرفض المتوقعة\n" + output);
    }

    // ---------- اختبار 8: حذف منتج يُزيله من productMap أيضًا (وليس فقط من القائمة والشجرة) ----------
    static void test8_ProductManagement_DeleteRemovesFromMap() {
        System.out.println("\n--- اختبار 8: تزامن حذف المنتج مع HashMap ---");

        // منتجان: نحذف الأول (20) ونتأكد أن الثاني (30) ما زال موجودًا وأن الأول اختفى تمامًا
        String simulatedInput =
                "20\nNameA\n5\n5\n3\nCatA\n" +   // addNewProduct #1 (ID=20)
                "30\nNameB\n5\n5\n3\nCatA\n" +   // addNewProduct #2 (ID=30)
                "20\n" +                 // deleteProductByID -> يحذف ID=20
                "20\n";                  // searchProductByID -> يبحث عن ID=20 (المحذوف)

        InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        System.setIn(new SlowInputStream(simulatedInput));

        ProductManagement pm = new ProductManagement();

        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(capturedOut));
        Product searchResult;
        boolean mapStillHasKey;
        try {
            pm.addNewProduct();
            pm.addNewProduct();
            pm.deleteProductByID();
            searchResult = pm.searchProductByID(); // يجب أن يُعيد null بعد الحذف
            mapStillHasKey = pm.productMap.containsKey(20);
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }

        check("Products - البحث بعد الحذف يُعيد null (لم يعد موجودًا في productMap)",
                searchResult == null, "توقعنا null لكن أُعيد: " + searchResult);
        check("Products - productMap.containsKey(20) أصبحت false بعد الحذف",
                !mapStillHasKey, "الـ ID ما زال موجودًا في productMap رغم الحذف!");
        check("Products - العنصر الآخر (30) ما زال موجودًا في productMap",
                pm.productMap.containsKey(30), "فُقد العنصر 30 خطأً أثناء حذف 20!");
    }

    // ---------- اختبار 9: حذف شحنة يُزيلها من shipmentMap أيضًا ----------
    static void test9_ShipmentsRegisters_DeleteRemovesFromMap() {
        System.out.println("\n--- اختبار 9: تزامن حذف الشحنة مع HashMap ---");

        // شحنة واحدة (ID=200) نضيفها بأولوية معينة، ثم نحذفها عبر deleteShipmentWithPriority
        String simulatedInput =
                "200\nTestDest\n50\n2030\n1\n1\n5\n" + // addShipment (ID=200), priority=5
                "200\n";                                 // searchShipmentByID بعد الحذف

        InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        System.setIn(new SlowInputStream(simulatedInput));

        Orders orders = new Orders();
        ProductManagement pm = new ProductManagement();
        ShipmentsRegisters sr = new ShipmentsRegisters(orders);

        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(capturedOut));
        Shipment searchResult;
        boolean mapStillHasKey;
        try {
            sr.addShipment(pm);
            sr.deleteShipmentWithPriority(); // يحذف أعلى أولوية (وهي الوحيدة هنا: ID=200)
            searchResult = sr.searchShipmentByID(); // يجب أن يُعيد null بعد الحذف
            mapStillHasKey = sr.shipmentMap.containsKey(200);
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }

        check("Shipments - البحث بعد الحذف يُعيد null (لم تعد موجودة في shipmentMap)",
                searchResult == null, "توقعنا null لكن أُعيد: " + searchResult);
        check("Shipments - shipmentMap.containsKey(200) أصبحت false بعد الحذف",
                !mapStillHasKey, "الـ ID ما زال موجودًا في shipmentMap رغم الحذف!");
    }

    // ---------- اختبار 10: حفظ كامل البيانات ثم تحميلها في كائنات جديدة والتحقق من التطابق ----------
    static void test10_Persistence_SaveAndLoadRoundTrip() {
        System.out.println("\n--- اختبار 10: حفظ واسترجاع البيانات (Round-Trip) ---");

        // تنظيف أي بيانات سابقة من تشغيلات يدوية قبل الاختبار
        deleteRecursively(new File("store_data"));

        // مرحلة الحفظ: منتج (ID=1000) + شحنة (ID=2000) تأخذ 5 وحدات منه + طلب بأولوية 7
        String simulatedInput =
                "1000\nWidget\n10\n50\n8\nGadgets\n" +          // addNewProduct (threshold=8, category=Gadgets)
                "2000\nCity\n1000\n2030\n1\n1\n" +   // ReadInfoOfShipment
                "1000\n5\n2\n" +                       // takeProductByID(id=1000,qty=5) ثم لا مزيد (2)
                "7\n";                                  // أولوية الطلب

        InputStream originalIn = System.in;
        System.setIn(new SlowInputStream(simulatedInput));

        ProductManagement pm1 = new ProductManagement();
        Orders orders1 = new Orders();
        ShipmentsRegisters sr1 = new ShipmentsRegisters(orders1);

        pm1.addNewProduct();
        sr1.addShipment(pm1);

        System.setIn(originalIn);

        Persistence.saveAll(pm1, sr1, orders1);

        // مرحلة التحميل: كائنات جديدة تمامًا، فارغة، تُحمَّل من الملفات فقط (بدون أي إدخال Scanner)
        ProductManagement pm2 = new ProductManagement();
        Orders orders2 = new Orders();
        ShipmentsRegisters sr2 = new ShipmentsRegisters(orders2);

        Persistence.loadAll(pm2, sr2, orders2);

        Product loadedProduct = pm2.productMap.get(1000);
        check("Persistence - المنتج المحمَّل موجود وبنفس الاسم/السعر",
                loadedProduct != null && loadedProduct.getNameOfProduct().equals("Widget") && loadedProduct.getPriceOfProduct() == 10,
                "المنتج المحمَّل: " + loadedProduct);
        check("Persistence - كمية المنتج بعد الحفظ/التحميل = 45 (50 - 5 المأخوذة للشحنة)",
                loadedProduct != null && loadedProduct.getQuantityOfProduct() == 45,
                "الكمية الفعلية: " + (loadedProduct == null ? "null" : loadedProduct.getQuantityOfProduct()));

        Shipment loadedShipment = sr2.shipmentMap.get(2000);
        check("Persistence - الشحنة المحمَّلة موجودة وبنفس الوجهة",
                loadedShipment != null && "City".equals(loadedShipment.getShipmentDestination()),
                "الشحنة المحمَّلة: " + loadedShipment);
        check("Persistence - تكلفة الشحنة أُعيد حسابها بشكل صحيح (5*10=50)",
                loadedShipment != null && loadedShipment.getShipmentCost() == 50f,
                "التكلفة الفعلية: " + (loadedShipment == null ? "null" : loadedShipment.getShipmentCost()));

        check("Persistence - عدد الطلبات المحمَّلة = 1",
                orders2.listOfOrder.size() == 1,
                "العدد الفعلي: " + orders2.listOfOrder.size());
        if (!orders2.listOfOrder.isEmpty()){
            Order loadedOrder = orders2.listOfOrder.get(0);
            check("Persistence - أولوية الطلب المحمَّل = 7",
                    loadedOrder.getPriority() == 7, "الأولوية الفعلية: " + loadedOrder.getPriority());
            check("Persistence - الطلب المحمَّل مرتبط بنفس كائن الشحنة المحمَّلة (وليس مرجعًا قديمًا)",
                    loadedOrder.getShipment() == loadedShipment, "مرجع مختلف!");
        }

        // تنظيف بعد الاختبار حتى لا تتلوث بيانات المستخدم الحقيقية عند تشغيل البرنامج لاحقًا
        deleteRecursively(new File("store_data"));
    }

    // ---------- اختبار 11: محاولات تسجيل الدخول (فشل متكرر ثم نجاح) ----------
    static void test11_AuthManager_LoginAttemptsAndSuccess() {
        System.out.println("\n--- اختبار 11: تسجيل الدخول (كلمات مرور خاطئة ثم صحيحة) ---");
        deleteRecursively(new File("store_data"));

        // 3 محاولات خاطئة متتالية، ثم محاولة صحيحة بعد ذلك (تُستخدم في نفس الكائن لاحقًا)
        String simulatedInput =
                "admin\nwrong1\n" + "admin\nwrong2\n" + "admin\nwrong3\n" + // فشل 3 مرات
                "admin\nadmin123\n";                                          // نجاح

        InputStream originalIn = System.in;
        System.setIn(new SlowInputStream(simulatedInput));

        AuthManager authManager = new AuthManager(); // يُنشئ حساب admin افتراضي تلقائيًا

        User failedResult = authManager.login();       // يستهلك أول 6 tokens (3 محاولات خاطئة)
        User successResult = authManager.login();      // يستهلك آخر 2 tokens (محاولة صحيحة)

        System.setIn(originalIn);
        deleteRecursively(new File("store_data"));

        check("Auth - تسجيل الدخول يفشل ويُعيد null بعد 3 محاولات خاطئة",
                failedResult == null, "النتيجة الفعلية: " + failedResult);
        check("Auth - تسجيل الدخول ينجح بكلمة المرور الافتراضية الصحيحة",
                successResult != null && successResult.getUsername().equals("admin") && successResult.getRole() == Role.ADMIN,
                "النتيجة الفعلية: " + successResult);
    }

    // ---------- اختبار 12: موظف (Employee) لا يستطيع فعليًا تنفيذ عملية Admin-only ----------
    static void test12_RoleBasedAccess_EmployeeBlockedFromAdminAction() {
        System.out.println("\n--- اختبار 12: منع الموظف من عملية Admin-only (إضافة منتج) ---");
        deleteRecursively(new File("store_data"));

        InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();

        try {
            // الجلسة 1 (Admin الافتراضي): يُسجّل موظفًا جديدًا "bob" ثم يخرج
            System.setIn(new SlowInputStream("admin\nadmin123\n18\nbob\nbob123\n2\n24\n"));
            System.setOut(new java.io.PrintStream(capturedOut));
            new Store().startStore();

            // الجلسة 2 (bob كموظف): يحاول تنفيذ "1- Add a new Product" (Admin only) ثم يخرج
            System.setIn(new SlowInputStream("bob\nbob123\n1\n24\n"));
            new Store().startStore();
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }
        String output = capturedOut.toString();

        check("Auth - ظهرت رسالة رفض الصلاحية لموظف حاول إضافة منتج",
                output.contains("Access Denied"), "لم تظهر رسالة الرفض المتوقعة");

        // نتحقق فعليًا (وليس فقط من الرسالة) أن لا منتج أُضيف: نحمّل بيانات جديدة من القرص
        ProductManagement verifyPm = new ProductManagement();
        Persistence.loadAll(verifyPm, new ShipmentsRegisters(new Orders()), new Orders());
        check("Auth - لم يُضَف أي منتج فعليًا رغم محاولة الموظف (الحظر حقيقي وليس شكليًا فقط)",
                verifyPm.listOfProduct.isEmpty(), "عدد المنتجات الفعلي: " + verifyPm.listOfProduct.size());

        deleteRecursively(new File("store_data"));
    }

    // ---------- اختبار 13: تنبيه انخفاض المخزون (تنبيه فوري + تقرير شامل) ----------
    static void test13_LowStockAlert_TriggersAndReportCorrectly() {
        System.out.println("\n--- اختبار 13: تنبيه انخفاض المخزون ---");

        // A: كمية=10، حد=5 -> ليست منخفضة عند الإضافة، لكن ستُصبح منخفضة لاحقًا بعد تحديث الكمية إلى 4
        // B: كمية=3، حد=5  -> منخفضة فورًا عند الإضافة نفسها
        // C: كمية=20، حد=5 -> تبقى فوق الحد طوال الاختبار (يجب ألا تظهر في التقرير)
        String simulatedInput =
                "1\nProdA\n10\n10\n5\nCatX\n" +
                "2\nProdB\n10\n3\n5\nCatX\n" +
                "3\nProdC\n10\n20\n5\nCatX\n" +
                "1\n4\n"; // updateTheQuantityOfProductByID(ID=1) -> كمية جديدة = 4

        InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        System.setIn(new SlowInputStream(simulatedInput));

        ProductManagement pm = new ProductManagement();

        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(capturedOut));
        String output;
        try {
            pm.addNewProduct(); // A
            pm.addNewProduct(); // B (منخفضة فورًا)
            pm.addNewProduct(); // C
            pm.updateTheQuantityOfProductByID(); // A تصبح منخفضة الآن
            pm.printLowStockProducts();
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
            output = capturedOut.toString();
        }

        check("LowStock - isLowStock() صحيحة للمنتج A بعد تخفيض كميته (4<=5)",
                pm.productMap.get(1).isLowStock(), "المنتج A لم يُعتبر منخفضًا رغم أن كميته 4 <= حده 5");
        check("LowStock - isLowStock() صحيحة للمنتج B منذ إضافته (3<=5)",
                pm.productMap.get(2).isLowStock(), "المنتج B لم يُعتبر منخفضًا رغم أن كميته 3 <= حده 5");
        check("LowStock - isLowStock() خاطئة (false) للمنتج C الذي يبقى فوق الحد (20>5)",
                !pm.productMap.get(3).isLowStock(), "المنتج C اعتُبر خطأً منخفض المخزون رغم أن كميته 20");

        check("LowStock - ظهر تنبيه فوري عند إضافة المنتج B بكمية منخفضة",
                output.contains("LOW STOCK ALERT") && output.contains("ProdB"),
                "لم يظهر تنبيه فوري عند إضافة B\n" + output);

        int lowStockReportLines = countOccurrences(output, "Minimum Threshold");
        check("LowStock - تقرير المنتجات المنخفضة يحتوي على منتجين بالضبط (A و B)",
                lowStockReportLines == 2,
                "عدد الأسطر في التقرير = " + lowStockReportLines + " (المتوقع 2)\n" + output);
        check("LowStock - تقرير المنتجات المنخفضة لا يتضمن المنتج C إطلاقًا",
                !output.substring(output.lastIndexOf("Low Stock Products")).contains("ProdC"),
                "ظهر المنتج C خطأً في تقرير المخزون المنخفض\n" + output);
    }

    // ---------- اختبار 14: فهرسة التصنيفات وتزامنها مع الحذف + التقارير ----------
    static void test14_Categories_IndexingAndReportsWork() {
        System.out.println("\n--- اختبار 14: تصنيفات المنتجات (فهرسة + تقارير) ---");

        // A,B في Electronics و C في Food
        String simulatedInput =
                "1\nProdA\n10\n10\n5\nElectronics\n" +
                "2\nProdB\n10\n10\n5\nElectronics\n" +
                "3\nProdC\n10\n10\n5\nFood\n" +
                "1\n" +      // deleteProductByID -> يحذف A
                "2\n" +      // deleteProductByID -> يحذف B (آخر عنصر في Electronics)
                "Food\n";    // printProductsByCategory -> يسأل عن اسم التصنيف

        InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        System.setIn(new SlowInputStream(simulatedInput));

        ProductManagement pm = new ProductManagement();

        pm.addNewProduct(); // A
        pm.addNewProduct(); // B
        pm.addNewProduct(); // C

        check("Categories - فهرس Electronics يحتوي على منتجين (A,B) بعد الإضافة",
                pm.categoryMap.containsKey("Electronics") && pm.categoryMap.get("Electronics").size() == 2,
                "المحتوى الفعلي: " + pm.categoryMap.get("Electronics"));
        check("Categories - فهرس Food يحتوي على منتج واحد (C) بعد الإضافة",
                pm.categoryMap.containsKey("Food") && pm.categoryMap.get("Food").size() == 1,
                "المحتوى الفعلي: " + pm.categoryMap.get("Food"));

        pm.deleteProductByID(); // يحذف A

        check("Categories - فهرس Electronics أصبح يحتوي على منتج واحد فقط (B) بعد حذف A",
                pm.categoryMap.containsKey("Electronics") && pm.categoryMap.get("Electronics").size() == 1,
                "المحتوى الفعلي: " + pm.categoryMap.get("Electronics"));

        pm.deleteProductByID(); // يحذف B (آخر عنصر في Electronics)

        check("Categories - مفتاح Electronics أُزيل بالكامل من الفهرس بعد حذف آخر عنصر فيه",
                !pm.categoryMap.containsKey("Electronics"),
                "المفتاح ما زال موجودًا رغم أن قائمته يجب أن تكون فارغة!");

        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(capturedOut));
        String output;
        try {
            pm.printProductsByCategory(); // يطبع كل التصنيفات ثم يسأل عن "Food"
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
            output = capturedOut.toString();
        }

        check("Categories - تقرير منتجات تصنيف Food يحتوي على ProdC",
                output.contains("ProdC"), "لم يظهر ProdC في تقرير تصنيف Food\n" + output);
        check("Categories - تقرير منتجات تصنيف Food لا يحتوي على Electronics إطلاقًا",
                !output.contains("Electronics"), "ظهر تصنيف Electronics خطأً رغم عدم وجود منتجات فيه\n" + output);
    }

    // ---------- اختبار 15: سجل العمليات يُسجّل الدخول (نجاح/فشل)، العمليات الناجحة، والرفض ----------
    static void test15_AuditLog_RecordsActionsAndDenials() {
        System.out.println("\n--- اختبار 15: سجل العمليات (Audit Log) ---");
        deleteRecursively(new File("store_data"));

        InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();

        try {
            // الجلسة 1 (Admin): محاولة دخول خاطئة، ثم صحيحة، ثم إضافة منتج، ثم تسجيل موظف، ثم خروج
            System.setIn(new SlowInputStream(
                    "admin\nwrongpass\n" +
                    "admin\nadmin123\n" +
                    "1\n10\nProdX\n5\n5\n5\nCatY\n" + // إضافة منتج (Admin only, ناجحة)
                    "18\nbob\nbob123\n2\n" +            // تسجيل موظف جديد
                    "24\n"                                // خروج
            ));
            System.setOut(new java.io.PrintStream(capturedOut));
            new Store().startStore();

            // الجلسة 2 (bob كموظف): دخول ناجح، محاولة حذف منتج (Admin only -> رفض)، ثم خروج
            System.setIn(new SlowInputStream("bob\nbob123\n6\n24\n"));
            new Store().startStore();
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }

        String logContent;
        try {
            logContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("store_data/audit_log.csv")));
        } catch (java.io.IOException e){
            logContent = "";
        }

        check("AuditLog - سُجّلت محاولة دخول فاشلة لـ admin",
                logContent.contains("admin") && logContent.contains("Login") && logContent.contains("FAILED"),
                "السجل الفعلي:\n" + logContent);
        check("AuditLog - سُجّل دخول ناجح لـ admin",
                logContent.contains("admin | Login | SUCCESS"), "السجل الفعلي:\n" + logContent);
        check("AuditLog - سُجّلت عملية 'Add Product' الناجحة باسم admin",
                logContent.contains("admin | Add Product | SUCCESS"), "السجل الفعلي:\n" + logContent);
        check("AuditLog - سُجّلت عملية 'Register New Employee' الناجحة باسم admin",
                logContent.contains("admin | Register New Employee | SUCCESS"), "السجل الفعلي:\n" + logContent);
        check("AuditLog - سُجّل دخول ناجح لـ bob",
                logContent.contains("bob | Login | SUCCESS"), "السجل الفعلي:\n" + logContent);
        check("AuditLog - سُجّلت محاولة 'Delete Product' المرفوضة باسم bob مع سبب الرفض",
                logContent.contains("bob | Delete Product | DENIED"), "السجل الفعلي:\n" + logContent);
        check("AuditLog - سُجّل خروج (Logout) لكلا الجلستين",
                countOccurrences(logContent, "Logout") == 2, "السجل الفعلي:\n" + logContent);

        deleteRecursively(new File("store_data"));
    }

    static void deleteRecursively(File f){
        if (!f.exists()) return;
        if (f.isDirectory()){
            File[] children = f.listFiles();
            if (children != null) for (File c : children) deleteRecursively(c);
        }
        f.delete();
    }

    static int countOccurrences(String text, String sub) {
        int count = 0, idx = 0;
        while ((idx = text.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
    static void test5_Store_InvalidMenuDoesNotCrash() {
        System.out.println("\n--- اختبار 5: رقم قائمة خاطئ في Store (بعد تسجيل دخول Admin افتراضي) ---");
        // ننظف بيانات المستخدمين أولاً لضمان إنشاء حساب Admin افتراضي معروف (admin/admin123)
        deleteRecursively(new File("store_data"));
        // نحاكي: تسجيل دخول admin/admin123 -> رقم خاطئ (999) -> خروج صحيح (20)
        String simulatedInput = "admin\nadmin123\n999\n24\n";
        InputStream originalIn = System.in;
        System.setIn(new SlowInputStream(simulatedInput));
        try {
            new Store().startStore();
            check("Store Menu - تسجيل الدخول نجح، البرنامج استمر بعد رقم خاطئ ثم خرج بشكل طبيعي (24)",
                    true, "");
        } catch (Exception e) {
            check("Store Menu - تسجيل الدخول نجح، البرنامج استمر بعد رقم خاطئ ثم خرج بشكل طبيعي (24)",
                    false, "حدث استثناء: " + e);
        } finally {
            System.setIn(originalIn);
            deleteRecursively(new File("store_data")); // تنظيف بعد الاختبار
        }
    }
}
