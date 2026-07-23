import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class Tests {

    static int passed = 0, failed = 0;

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
        System.out.println("Final result: " + passed + " Success / " + failed + " Failed");
        System.out.println("=====================================");
    }


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

    static void test1_AVL_DeleteTwoChildrenPreservesLeftSubtree() {
        System.out.println("\n--- Test 1: Delete node with two children ---");
        AVLTree tree = new AVLTree();
        int[] ids = {50, 30, 70, 20, 40, 60, 80}; // perfectly balanced tree
        for (int id : ids) tree.insertProduct(newProduct(id));

        // 50 has two children (30 and 70) => deletion uses the successor
        tree.deleteProductByID(50);

        List<Integer> result = new ArrayList<>();
        inOrderIds(tree.root, result);

        List<Integer> expected = new ArrayList<>();
        for (int id : ids) if (id != 50) expected.add(id);
        expected.sort(null);

        check("AVL Delete",
                result.equals(expected),
                "Expect=" + expected + " But result=" + result);
    }

    static void test2_AVL_RootUpdatesAfterRotation() {
        System.out.println("\n--- Test 2: Refresh root after rotation ---");
        AVLTree tree = new AVLTree();
        tree.insertProduct(newProduct(10));
        tree.insertProduct(newProduct(20));
        tree.insertProduct(newProduct(30));

        check("AVL Root - root becomes 20 after rotation",
                tree.root != null && tree.root.getID() == 20,
                "Actually root = " + (tree.root == null ? "null" : tree.root.getID()));

        check("AVL Root - search for 10 works via the new root",
                tree.searchProductByID(10) != null, "10 doesn't exist!");
        check("AVL Root - search for 30 works via the new root",
                tree.searchProductByID(30) != null, "30 doesn't exist!");
    }

    static void test3_BST_DuplicateInsertDoesNotBreakTree() {
        System.out.println("\n--- Test 3: Duplicate ID in BST ---");
        BSTTree tree = new BSTTree();
        tree.insertShipment(new Shipment(100));
        tree.insertShipment(new Shipment(100));

        check("BST Duplicate - after duplicate insertion the root is not null",
                tree.root != null, "The root became null!");
        check("BST Duplicate - the original element exists",
                tree.searchShipmentByID(100) != null, "Element 100 does not exist!");
    }


    static void test4_BST_DeleteTwoChildrenPreservesLeftSubtree() {
        System.out.println("\n--- Test 4: Delete a node with two children ---");
        BSTTree tree = new BSTTree();
        int[] ids = {50, 30, 70, 20, 40, 60, 80};
        for (int id : ids) tree.insertShipment(new Shipment(id));

        tree.deleteShipmentByID(50);

        List<Integer> result = new ArrayList<>();
        inOrderIdsShip(tree.root, result);

        List<Integer> expected = new ArrayList<>();
        for (int id : ids) if (id != 50) expected.add(id);
        expected.sort(null);

        check("BST Delete - all elements exist after deletion (20,30,40)",
                result.equals(expected),
                "expected=" + expected + " but result=" + result);
    }

    static void test5_Store_InvalidMenuDoesNotCrash() {
        System.out.println("\n--- Test 5: Invalid menu option in Store (after default Admin login) ---");
        // Clean user data first to ensure a known default Admin account (admin/admin123) is created.
        deleteRecursively(new File("store_data"));
        // Simulate: login as admin/admin123 -> invalid option (999) -> exit (24)
        String simulatedInput = "admin\nadmin123\n999\n24\n";
        java.io.InputStream originalIn = System.in;
        System.setIn(new SlowInputStream(simulatedInput));
        try {
            new Store().startStore();
            check("Store Menu - login succeeded, program continued after invalid number and exited normally (24)",
                    true, "");
        } catch (Exception e) {
            check("Store Menu - login succeeded, program continued after invalid number and exited normally (24)",
                    false, "Exception occurred: " + e);
        } finally {
            System.setIn(originalIn);
            deleteRecursively(new File("store_data")); // clean up after test
        }
    }

    static void test6_ShipmentsRegisters_DuplicateIdNotAddedToList() {
        System.out.println("\n--- Test 6: Duplicate Shipment ID in the list ---");

        // First shipment ID=100 (completes successfully, needs Order priority later)
        // Second shipment with same ID=100 (should be rejected before reaching priority request)
        String simulatedInput =
                "100\nTestDest\n50\n2030\n1\n1\n5\n" +   // addShipment #1
                        "100\nTestDest2\n60\n2030\n2\n2\n";       // addShipment #2 (duplicate)

        java.io.InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        // System.in must be set *before* creating objects, because their Scanner fields
        // are initialized inside the constructor and bind to System.in at that specific moment.
        System.setIn(new SlowInputStream(simulatedInput));

        Orders orders = new Orders();
        ProductManagement pm = new ProductManagement(); // intentionally empty to simplify input
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
        check("Shipments - duplicate shipment was not added to the list (only one occurrence for ID=100)",
                occurrences == 1,
                "Number of occurrences of 'Shipment ID = 100' in the list = " + occurrences + " (should be 1)\n" + output);
        check("Shipments - duplicate rejection message appeared",
                output.contains("already exists"), "Expected rejection message did not appear\n" + output);
    }

    // ---------- Test 7: Duplicate Product ID is not added twice to the ProductManagement list ----------
    static void test7_ProductManagement_DuplicateIdNotAddedToList() {
        System.out.println("\n--- Test 7: Duplicate Product ID in the list ---");
        String simulatedInput =
                "10\nTestName\n5\n5\n3\nCatA\n" +    // addNewProduct #1
                        "10\nTestName2\n7\n7\n3\nCatA\n";     // addNewProduct #2 (duplicate)

        java.io.InputStream originalIn = System.in;
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
        check("Products - duplicate product was not added to the list (only one occurrence for ID=10)",
                occurrences == 1,
                "Number of occurrences of 'ID = 10}' in the list = " + occurrences + " (should be 1)\n" + output);
        check("Products - duplicate rejection message appeared",
                output.contains("already exists"), "Expected rejection message did not appear\n" + output);
    }

    // ---------- Test 8: Deleting a product removes it from productMap as well (not only from list and tree) ----------
    static void test8_ProductManagement_DeleteRemovesFromMap() {
        System.out.println("\n--- Test 8: Synchronization of product deletion with HashMap ---");

        // Two products: delete the first (20) and ensure the second (30) still exists and the first is completely gone
        String simulatedInput =
                "20\nNameA\n5\n5\n3\nCatA\n" +   // addNewProduct #1 (ID=20)
                        "30\nNameB\n5\n5\n3\nCatA\n" +   // addNewProduct #2 (ID=30)
                        "20\n" +                 // deleteProductByID -> deletes ID=20
                        "20\n";                  // searchProductByID -> searches for ID=20 (deleted)

        java.io.InputStream originalIn = System.in;
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
            searchResult = pm.searchProductByID(); // must return null after deletion
            mapStillHasKey = pm.productMap.containsKey(20);
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }

        check("Products - search after deletion returns null (no longer in productMap)",
                searchResult == null, "Expected null but got: " + searchResult);
        check("Products - productMap.containsKey(20) became false after deletion",
                !mapStillHasKey, "The ID still exists in productMap despite deletion!");
        check("Products - the other element (30) still exists in productMap",
                pm.productMap.containsKey(30), "Element 30 was wrongly lost while deleting 20!");
    }

    // ---------- Test 9: Deleting a shipment removes it from shipmentMap as well ----------
    static void test9_ShipmentsRegisters_DeleteRemovesFromMap() {
        System.out.println("\n--- Test 9: Synchronization of shipment deletion with HashMap ---");

        // One shipment (ID=200) we add with a given priority, then delete via deleteShipmentWithPriority
        String simulatedInput =
                "200\nTestDest\n50\n2030\n1\n1\n5\n" + // addShipment (ID=200), priority=5
                        "200\n";                                 // searchShipmentByID after deletion

        java.io.InputStream originalIn = System.in;
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
            sr.deleteShipmentWithPriority(); // deletes the highest priority (which is the only one here: ID=200)
            searchResult = sr.searchShipmentByID(); // must return null after deletion
            mapStillHasKey = sr.shipmentMap.containsKey(200);
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }

        check("Shipments - search after deletion returns null (no longer in shipmentMap)",
                searchResult == null, "Expected null but got: " + searchResult);
        check("Shipments - shipmentMap.containsKey(200) became false after deletion",
                !mapStillHasKey, "The ID still exists in shipmentMap despite deletion!");
    }

    // ---------- Test 10: Save all data, then load it into new objects and verify consistency ----------
    static void test10_Persistence_SaveAndLoadRoundTrip() {
        System.out.println("\n--- Test 10: Save and Load Round-Trip ---");

        // Clean up any previous data from manual runs before the test
        deleteRecursively(new File("store_data"));

        // Save phase: product (ID=1000) + shipment (ID=2000) takes 5 units from it + order with priority 7
        String simulatedInput =
                "1000\nWidget\n10\n50\n8\nGadgets\n" +          // addNewProduct (threshold=8, category=Gadgets)
                        "2000\nCity\n1000\n2030\n1\n1\n" +   // ReadInfoOfShipment
                        "1000\n5\n2\n" +                       // takeProductByID(id=1000,qty=5) then no more (2)
                        "7\n";                                  // order priority

        java.io.InputStream originalIn = System.in;
        System.setIn(new SlowInputStream(simulatedInput));

        ProductManagement pm1 = new ProductManagement();
        Orders orders1 = new Orders();
        ShipmentsRegisters sr1 = new ShipmentsRegisters(orders1);

        pm1.addNewProduct();
        sr1.addShipment(pm1);

        System.setIn(originalIn);

        Persistence.saveAll(pm1, sr1, orders1);

        // Load phase: completely new empty objects, loaded only from files (without any Scanner input)
        ProductManagement pm2 = new ProductManagement();
        Orders orders2 = new Orders();
        ShipmentsRegisters sr2 = new ShipmentsRegisters(orders2);

        Persistence.loadAll(pm2, sr2, orders2);

        Product loadedProduct = pm2.productMap.get(1000);
        check("Persistence - loaded product exists and has the same name/price",
                loadedProduct != null && loadedProduct.getNameOfProduct().equals("Widget") && loadedProduct.getPriceOfProduct() == 10,
                "Loaded product: " + loadedProduct);
        check("Persistence - product quantity after save/load = 45 (50 - 5 taken for the shipment)",
                loadedProduct != null && loadedProduct.getQuantityOfProduct() == 45,
                "Actual quantity: " + (loadedProduct == null ? "null" : loadedProduct.getQuantityOfProduct()));

        Shipment loadedShipment = sr2.shipmentMap.get(2000);
        check("Persistence - loaded shipment exists and has the same destination",
                loadedShipment != null && "City".equals(loadedShipment.getShipmentDestination()),
                "Loaded shipment: " + loadedShipment);
        check("Persistence - shipment cost was recalculated correctly (5*10=50)",
                loadedShipment != null && loadedShipment.getShipmentCost() == 50f,
                "Actual cost: " + (loadedShipment == null ? "null" : loadedShipment.getShipmentCost()));

        check("Persistence - number of loaded orders = 1",
                orders2.listOfOrder.size() == 1,
                "Actual count: " + orders2.listOfOrder.size());
        if (!orders2.listOfOrder.isEmpty()){
            Order loadedOrder = orders2.listOfOrder.get(0);
            check("Persistence - priority of the loaded order = 7",
                    loadedOrder.getPriority() == 7, "Actual priority: " + loadedOrder.getPriority());
            check("Persistence - loaded order is linked to the same loaded shipment object (not an old reference)",
                    loadedOrder.getShipment() == loadedShipment, "Different reference!");
        }

        // Clean up after test to avoid contaminating real user data when running the program later
        deleteRecursively(new File("store_data"));
    }

    // ---------- Test 11: Login attempts (repeated failures then success) ----------
    static void test11_AuthManager_LoginAttemptsAndSuccess() {
        System.out.println("\n--- Test 11: Login (wrong passwords then correct) ---");
        deleteRecursively(new File("store_data"));

        // 3 consecutive wrong attempts, then a correct attempt later (used in the same object later)
        String simulatedInput =
                "admin\nwrong1\n" + "admin\nwrong2\n" + "admin\nwrong3\n" + // 3 failures
                        "admin\nadmin123\n";                                          // success

        java.io.InputStream originalIn = System.in;
        System.setIn(new SlowInputStream(simulatedInput));

        AuthManager authManager = new AuthManager(); // creates a default admin account automatically

        User failedResult = authManager.login();       // consumes first 6 tokens (3 wrong attempts)
        User successResult = authManager.login();      // consumes last 2 tokens (correct attempt)

        System.setIn(originalIn);
        deleteRecursively(new File("store_data"));

        check("Auth - login fails and returns null after 3 wrong attempts",
                failedResult == null, "Actual result: " + failedResult);
        check("Auth - login succeeds with the default correct password",
                successResult != null && successResult.getUsername().equals("admin") && successResult.getRole() == Role.ADMIN,
                "Actual result: " + successResult);
    }

    // ---------- Test 12: Employee cannot actually execute an Admin-only action ----------
    static void test12_RoleBasedAccess_EmployeeBlockedFromAdminAction() {
        System.out.println("\n--- Test 12: Employee blocked from Admin-only action (adding a product) ---");
        deleteRecursively(new File("store_data"));

        java.io.InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();

        try {
            // Session 1 (default Admin): registers a new employee "bob" then exits
            System.setIn(new SlowInputStream("admin\nadmin123\n18\nbob\nbob123\n2\n24\n"));
            System.setOut(new java.io.PrintStream(capturedOut));
            new Store().startStore();

            // Session 2 (bob as employee): tries to execute "1- Add a new Product" (Admin only) then exits
            System.setIn(new SlowInputStream("bob\nbob123\n1\n24\n"));
            new Store().startStore();
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }
        String output = capturedOut.toString();

        check("Auth - Access Denied message appeared for employee who tried to add a product",
                output.contains("Access Denied"), "Expected denial message did not appear");

        // We verify actually (not just from the message) that no product was added: we load new data from disk
        ProductManagement verifyPm = new ProductManagement();
        Persistence.loadAll(verifyPm, new ShipmentsRegisters(new Orders()), new Orders());
        check("Auth - no product was actually added despite the employee's attempt (the block is real, not just cosmetic)",
                verifyPm.listOfProduct.isEmpty(), "Actual number of products: " + verifyPm.listOfProduct.size());

        deleteRecursively(new File("store_data"));
    }

    // ---------- Test 13: Low stock alert (immediate alert + comprehensive report) ----------
    static void test13_LowStockAlert_TriggersAndReportCorrectly() {
        System.out.println("\n--- Test 13: Low Stock Alert ---");

        // A: quantity=10, threshold=5 -> not low on addition, but becomes low later after updating quantity to 4
        // B: quantity=3, threshold=5 -> low immediately upon addition
        // C: quantity=20, threshold=5 -> stays above threshold throughout the test (should not appear in the report)
        String simulatedInput =
                "1\nProdA\n10\n10\n5\nCatX\n" +
                        "2\nProdB\n10\n3\n5\nCatX\n" +
                        "3\nProdC\n10\n20\n5\nCatX\n" +
                        "1\n4\n"; // updateTheQuantityOfProductByID(ID=1) -> new quantity = 4

        java.io.InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        System.setIn(new SlowInputStream(simulatedInput));

        ProductManagement pm = new ProductManagement();

        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(capturedOut));
        String output;
        try {
            pm.addNewProduct(); // A
            pm.addNewProduct(); // B (low immediately)
            pm.addNewProduct(); // C
            pm.updateTheQuantityOfProductByID(); // A becomes low now
            pm.printLowStockProducts();
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
            output = capturedOut.toString();
        }

        check("LowStock - isLowStock() is true for product A after reducing its quantity (4<=5)",
                pm.productMap.get(1).isLowStock(), "Product A was not considered low even though its quantity is 4 <= its threshold 5");
        check("LowStock - isLowStock() is true for product B since its addition (3<=5)",
                pm.productMap.get(2).isLowStock(), "Product B was not considered low even though its quantity is 3 <= its threshold 5");
        check("LowStock - isLowStock() is false for product C which stays above the threshold (20>5)",
                !pm.productMap.get(3).isLowStock(), "Product C was wrongly considered low stock even though its quantity is 20");

        check("LowStock - immediate alert appeared when adding product B with low quantity",
                output.contains("LOW STOCK ALERT") && output.contains("ProdB"),
                "No immediate alert appeared when adding B\n" + output);

        int lowStockReportLines = countOccurrences(output, "Minimum Threshold");
        check("LowStock - low stock report contains exactly two products (A and B)",
                lowStockReportLines == 2,
                "Number of lines in report = " + lowStockReportLines + " (expected 2)\n" + output);
        check("LowStock - low stock report does not include product C at all",
                !output.substring(output.lastIndexOf("Low Stock Products")).contains("ProdC"),
                "Product C wrongly appeared in the low stock report\n" + output);
    }

    // ---------- Test 14: Product categories (indexing + reports) ----------
    static void test14_Categories_IndexingAndReportsWork() {
        System.out.println("\n--- Test 14: Product Categories (Indexing + Reports) ---");

        // A,B in Electronics and C in Food
        String simulatedInput =
                "1\nProdA\n10\n10\n5\nElectronics\n" +
                        "2\nProdB\n10\n10\n5\nElectronics\n" +
                        "3\nProdC\n10\n10\n5\nFood\n" +
                        "1\n" +      // deleteProductByID -> deletes A
                        "2\n" +      // deleteProductByID -> deletes B (last element in Electronics)
                        "Food\n";    // printProductsByCategory -> asks for the category name

        java.io.InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        System.setIn(new SlowInputStream(simulatedInput));

        ProductManagement pm = new ProductManagement();

        pm.addNewProduct(); // A
        pm.addNewProduct(); // B
        pm.addNewProduct(); // C

        check("Categories - Electronics index contains two products (A,B) after addition",
                pm.categoryMap.containsKey("Electronics") && pm.categoryMap.get("Electronics").size() == 2,
                "Actual content: " + pm.categoryMap.get("Electronics"));
        check("Categories - Food index contains one product (C) after addition",
                pm.categoryMap.containsKey("Food") && pm.categoryMap.get("Food").size() == 1,
                "Actual content: " + pm.categoryMap.get("Food"));

        pm.deleteProductByID(); // deletes A

        check("Categories - Electronics index now contains only one product (B) after deleting A",
                pm.categoryMap.containsKey("Electronics") && pm.categoryMap.get("Electronics").size() == 1,
                "Actual content: " + pm.categoryMap.get("Electronics"));

        pm.deleteProductByID(); // deletes B (last element in Electronics)

        check("Categories - Electronics key was completely removed from the index after deleting the last element in it",
                !pm.categoryMap.containsKey("Electronics"),
                "The key still exists even though its list should be empty!");

        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(capturedOut));
        String output;
        try {
            pm.printProductsByCategory(); // prints all categories then asks for "Food"
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
            output = capturedOut.toString();
        }

        check("Categories - report for Food category contains ProdC",
                output.contains("ProdC"), "ProdC did not appear in the Food category report\n" + output);
        check("Categories - report for Food category does not contain Electronics at all",
                !output.contains("Electronics"), "Electronics category wrongly appeared even though there are no products in it\n" + output);
    }

    // ---------- Test 15: Audit Log records logins (success/failure), successful actions, and denials ----------
    static void test15_AuditLog_RecordsActionsAndDenials() {
        System.out.println("\n--- Test 15: Audit Log ---");
        deleteRecursively(new File("store_data"));

        java.io.InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        java.io.ByteArrayOutputStream capturedOut = new java.io.ByteArrayOutputStream();

        try {
            // Session 1 (Admin): wrong login attempt, then correct, then add product, then register employee, then exit
            System.setIn(new SlowInputStream(
                    "admin\nwrongpass\n" +
                            "admin\nadmin123\n" +
                            "1\n10\nProdX\n5\n5\n5\nCatY\n" + // Add product (Admin only, successful)
                            "18\nbob\nbob123\n2\n" +            // Register new employee
                            "24\n"                                // Exit
            ));
            System.setOut(new java.io.PrintStream(capturedOut));
            new Store().startStore();

            // Session 2 (bob as employee): successful login, attempt to delete product (Admin only -> denied), then exit
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

        check("AuditLog - failed login attempt for admin was logged",
                logContent.contains("admin") && logContent.contains("Login") && logContent.contains("FAILED"),
                "Actual log:\n" + logContent);
        check("AuditLog - successful login for admin was logged",
                logContent.contains("admin | Login | SUCCESS"), "Actual log:\n" + logContent);
        check("AuditLog - successful 'Add Product' operation by admin was logged",
                logContent.contains("admin | Add Product | SUCCESS"), "Actual log:\n" + logContent);
        check("AuditLog - successful 'Register New Employee' operation by admin was logged",
                logContent.contains("admin | Register New Employee | SUCCESS"), "Actual log:\n" + logContent);
        check("AuditLog - successful login for bob was logged",
                logContent.contains("bob | Login | SUCCESS"), "Actual log:\n" + logContent);
        check("AuditLog - denied 'Delete Product' attempt by bob with denial reason was logged",
                logContent.contains("bob | Delete Product | DENIED"), "Actual log:\n" + logContent);
        check("AuditLog - logout was logged for both sessions",
                countOccurrences(logContent, "Logout") == 2, "Actual log:\n" + logContent);

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
}