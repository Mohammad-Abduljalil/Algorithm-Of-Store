import java.util.Scanner;

public class Store {

    ProductManagement productManagement=new ProductManagement();

    Orders orders=new Orders();

    ShipmentsRegisters shipmentsRegisters=new ShipmentsRegisters(orders);

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
        System.out.println("17- Exit ");
    }

    void startStore(){
        while(true){
            printStore();
            System.out.println("Enter a Number : ");
            int valueToChose= scan.nextInt();
            switch (valueToChose){
                case 1:
                    productManagement.addNewProduct();
                    break;
                case 2:
                    productManagement.printProducts();
                    break;
                case 3:
                    productManagement.searchProductByID();
                    break;
                case 4:
                    productManagement.updateThePriceOfProductByID();
                    break;
                case 5:
                    productManagement.updateTheQuantityOfProductByID();
                    break;
                case 6:
                    productManagement.deleteProductByID();
                    break;
                case 7:
                    shipmentsRegisters.addShipment(productManagement);
                    break;
                case 8:
                    shipmentsRegisters.addProductToShipmentById(productManagement);
                    break;
                case 9:
                    shipmentsRegisters.printAllShipments();
                    break;
                case 10:
                    shipmentsRegisters.searchShipmentByID();
                    break;
                case 11:
                    shipmentsRegisters.updateTheDeliveryDateById();
                    break;
                case 12:
                    shipmentsRegisters.deleteShipmentWithPriority();
                    break;
                case 13:
                    orders.updateThePriorityOfOrderById();
                    break;
                case 14:
                    productManagement.storeValue();
                    break;
                case 15:
                    shipmentsRegisters.highCostShipments();
                    break;
                case 16:
                    orders.totalCostOfOrders();
                    break;
                case 17:
                    System.out.println(" Thank you for visit ");
                    return;
                default:
                    System.out.println(" Sorry, a wrong number! ");
                    return;
            }
        }
    }

}
