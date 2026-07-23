import java.util.Scanner;

public class Store {

    ProductManagement productManagement=new ProductManagement();

    Orders orders=new Orders();

    ShipmentsRegisters shipmentsRegisters=new ShipmentsRegisters(orders);

    AuthManager authManager=new AuthManager();

    Scanner scan=new Scanner(System.in);



    void printStore(){
        System.out.println("1- Add a new Product ");
        System.out.println("2- Print All Products ");
        System.out.println("3- Search about Product ");
        System.out.println("4- Update the Price of Product ");
        System.out.println("5- Update the Quantity of Product ");
        System.out.println("6- Delete a Product ");
        System.out.println("7- Add a new Shipment in Order ");
        System.out.println("8- Add a Product to Shipment ");
        System.out.println("9- Print All Shipments ");
        System.out.println("10- Search about  Shipment ");
        System.out.println("11- Update Delivery Date of Shipment ");
        System.out.println("12- Return the Order with a high priority ");
        System.out.println("13- Update the priority of Order ");
        System.out.println("14- Value Of store ");
        System.out.println("15- high Shipment cost ");
        System.out.println("16- All cost of Orders ");
        System.out.println("17- Save Data (Admin only) ");
        System.out.println("18- Register New Employee (Admin only) ");
        System.out.println("19- Change My Password ");
        System.out.println("20- Print Low Stock Products ");
        System.out.println("21- Print All Categories ");
        System.out.println("22- Print Products By Category ");
        System.out.println("23- Print Audit Log (Admin only) ");
        System.out.println("24- Exit ");
    }

    boolean requireAdmin(User currentUser, String actionName){
        if (currentUser.getRole() != Role.ADMIN){
            System.out.println(" Access Denied! This action requires Admin privileges. ");
            AuditLog.record(currentUser.getUsername(), actionName, "DENIED - insufficient privileges (role=" + currentUser.getRole() + ")");
            return false;
        }
        return true;
    }

    void startStore(){
        Persistence.loadAll(productManagement, shipmentsRegisters, orders);

        User currentUser = authManager.login();
        if (currentUser == null){
            System.out.println(" Too many failed login attempts. Goodbye. ");
            return;
        }

        while(true){
            printStore();
            System.out.println("Enter a Number : ");
            int valueToChose= scan.nextInt();
            switch (valueToChose){
                case 1:
                    if (requireAdmin(currentUser, "Add Product")){
                        productManagement.addNewProduct();
                        AuditLog.record(currentUser.getUsername(), "Add Product", "SUCCESS");
                    }
                    break;
                case 2:
                    productManagement.printProducts();
                    break;
                case 3:
                    productManagement.searchProductByID();
                    break;
                case 4:
                    if (requireAdmin(currentUser, "Update Product Price")){
                        productManagement.updateThePriceOfProductByID();
                        AuditLog.record(currentUser.getUsername(), "Update Product Price", "SUCCESS");
                    }
                    break;
                case 5:
                    if (requireAdmin(currentUser, "Update Product Quantity")){
                        productManagement.updateTheQuantityOfProductByID();
                        AuditLog.record(currentUser.getUsername(), "Update Product Quantity", "SUCCESS");
                    }
                    break;
                case 6:
                    if (requireAdmin(currentUser, "Delete Product")){
                        productManagement.deleteProductByID();
                        AuditLog.record(currentUser.getUsername(), "Delete Product", "SUCCESS");
                    }
                    break;
                case 7:
                    shipmentsRegisters.addShipment(productManagement);
                    AuditLog.record(currentUser.getUsername(), "Add Shipment", "SUCCESS");
                    break;
                case 8:
                    shipmentsRegisters.addProductToShipmentById(productManagement);
                    AuditLog.record(currentUser.getUsername(), "Add Product To Shipment", "SUCCESS");
                    break;
                case 9:
                    shipmentsRegisters.printAllShipments();
                    break;
                case 10:
                    shipmentsRegisters.searchShipmentByID();
                    break;
                case 11:
                    if (requireAdmin(currentUser, "Update Shipment Delivery Date")){
                        shipmentsRegisters.updateTheDeliveryDateById();
                        AuditLog.record(currentUser.getUsername(), "Update Shipment Delivery Date", "SUCCESS");
                    }
                    break;
                case 12:
                    if (requireAdmin(currentUser, "Delete Shipment (High Priority)")){
                        shipmentsRegisters.deleteShipmentWithPriority();
                        AuditLog.record(currentUser.getUsername(), "Delete Shipment (High Priority)", "SUCCESS");
                    }
                    break;
                case 13:
                    if (requireAdmin(currentUser, "Update Order Priority")){
                        orders.updateThePriorityOfOrderById();
                        AuditLog.record(currentUser.getUsername(), "Update Order Priority", "SUCCESS");
                    }
                    break;
                case 14:
                    if (requireAdmin(currentUser, "View Store Value")) productManagement.storeValue();
                    break;
                case 15:
                    if (requireAdmin(currentUser, "View High Cost Shipments")) shipmentsRegisters.highCostShipments();
                    break;
                case 16:
                    if (requireAdmin(currentUser, "View Total Cost Of Orders")) orders.totalCostOfOrders();
                    break;
                case 17:
                    if (requireAdmin(currentUser, "Manual Save Data")){
                        Persistence.saveAll(productManagement, shipmentsRegisters, orders);
                        AuditLog.record(currentUser.getUsername(), "Manual Save Data", "SUCCESS");
                    }
                    break;
                case 18:
                    if (requireAdmin(currentUser, "Register New Employee")){
                        authManager.registerNewUser();
                        AuditLog.record(currentUser.getUsername(), "Register New Employee", "SUCCESS");
                    }
                    break;
                case 19:
                    boolean passwordChanged = authManager.changePassword(currentUser);
                    AuditLog.record(currentUser.getUsername(), "Change Own Password", passwordChanged ? "SUCCESS" : "FAILED - incorrect current password");
                    break;
                case 20:
                    productManagement.printLowStockProducts();
                    break;
                case 21:
                    productManagement.printAllCategories();
                    break;
                case 22:
                    productManagement.printProductsByCategory();
                    break;
                case 23:
                    if (requireAdmin(currentUser, "View Audit Log")) AuditLog.printLog();
                    break;
                case 24:
                    Persistence.saveAll(productManagement, shipmentsRegisters, orders);
                    AuditLog.record(currentUser.getUsername(), "Logout", "Program exited normally");
                    System.out.println(" Thank you for visit ");
                    return;
                default:
                    System.out.println(" Sorry, a wrong number! ");
                    break;
            }
        }
    }

}
