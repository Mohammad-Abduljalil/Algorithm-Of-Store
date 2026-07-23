import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ShipmentsRegisters {
    private ArrayList<Shipment> listOfShipment;
    HashMap<Integer, Shipment> shipmentMap;
    private Scanner scan=new Scanner(System.in);
    private BSTTree bstTree=new BSTTree();
    private HeapPriority heapPriority;
    Orders orders;

    public ShipmentsRegisters(Orders orders){
        listOfShipment=new ArrayList<>();
        shipmentMap = new HashMap<>();
        this.orders=orders;
    }

    private Shipment ReadInfoOfShipment(){
        System.out.println("Please Enter The ID of Shipment : ");
        int id=scan.nextInt();
        Shipment shipment=new Shipment(id);
        System.out.println("Please Enter The Destination of Shipment : ");
        shipment.setShipmentDestination(scan.next());
        do {
            System.out.println("Please Enter the Maximum budget of Shipment : ");
            shipment.setMaximumBudgetOfShipment(scan.nextFloat());
        }while (shipment.getMaximumBudgetOfShipment()<0);
        System.out.println("Please Enter The Delivery Date of Shipment : ");
        LocalDate customDate;
        do {
            System.out.println("Please Enter The  Delivery Year  of Shipment : ");
            int year= scan.nextInt();
            int month;
            do {
                System.out.println("Please Enter The Delivery Month of Shipment : ");
                month= scan.nextInt();
            }while (month>12 || month<1);
            int day;
            do {
                System.out.println("Please Enter The Delivery Day of Shipment : ");
                day= scan.nextInt();
            }while(day>31 || day<1);
            customDate=LocalDate.of(year,month,day);
        }while(customDate.isBefore(LocalDate.now()));
        shipment.setDeliveryDate(customDate.getYear()+"/"+customDate.getMonth()+"/"+customDate.getDayOfMonth());
        return shipment;
    }

    public void addShipment(ProductManagement productManagement){
        Shipment shipment=ReadInfoOfShipment();
        if (shipmentMap.containsKey(shipment.getShipmentId())){
            System.out.println(" This Shipment ID already exists! Please try again with a different ID. ");
            return;
        }
        listOfShipment.add(shipment);
        System.out.println("Add a products to this Shipment : ");
        shipment.addProductToShipment(productManagement);
        bstTree.insertShipment(shipment);
        shipmentMap.put(shipment.getShipmentId(), shipment);
        this.orders.toAddNewOrder(shipment);
    }

    void deleteShipmentWithPriority(){
        if (listOfShipment.isEmpty()){
            System.out.println("There is no Order");
            return;
        }
        Order order=this.orders.deleteOrderWithPriority();
        bstTree.deleteShipmentByID(order.getShipment().getShipmentId());
        shipmentMap.remove(order.getShipment().getShipmentId());
        listOfShipment.remove(order.shipment);
        System.out.println("The Shipment removed successfully ");
    }

    public void updateTheDeliveryDateById(){
        System.out.println("Please Enter The Id of Shipment : ");
        int id=scan.nextInt();
        Shipment shipment=shipmentMap.get(id);
        if (shipment!=null) {
            System.out.println("Please Enter The Delivery Date of Shipment : ");
            LocalDate customDate;
            do {
                System.out.println("Please Enter The  Delivery Year  of Shipment : ");
                int year= scan.nextInt();
                int month;
                do {
                    System.out.println("Please Enter The Delivery Month of Shipment : ");
                    month= scan.nextInt();
                }while (month>12 || month<1);
                int day;
                do {
                    System.out.println("Please Enter The Delivery Day of Shipment : ");
                    day= scan.nextInt();
                }while(day>31 || day<1);
                customDate=LocalDate.of(year,month,day);
            }while(customDate.isBefore(LocalDate.now()));
            shipment.setDeliveryDate(customDate.getYear()+"/"+customDate.getMonth()+"/"+customDate.getDayOfMonth());
            this.orders.editShipmentInOrder(shipment,shipment.getShipmentId());
        }
        else
            System.out.println("this Shipment doesn't exist! ");
    }


    public Shipment searchShipmentByID(){
        System.out.println("Please Enter The Id of Shipment : ");
        int id=scan.nextInt();
        Shipment shipment=shipmentMap.get(id);
        if (shipment==null){
            System.out.println(" this Shipment isn't excite!  ");
        }
        else{
            System.out.println(shipment);
        }
        return shipment;
    }

    public void addProductToShipmentById(ProductManagement productManagement){
        if (listOfShipment.isEmpty()){
            System.out.println(" There is no shipment! ");
            return;
        }
        System.out.println();
        for (Shipment shipment:listOfShipment){
            System.out.print(shipment.getShipmentId()+" ");
        }
        System.out.println();
        int id;
        Shipment shipment;
        do {
            System.out.println("Please Enter The ID of Shipment : ");
            id=scan.nextInt();
            shipment=shipmentMap.get(id);
        }while(shipment==null);
        shipment.addProductToShipment(productManagement);
        this.orders.editShipmentInOrder(shipment,shipment.getShipmentId());
    }

    public void printAllShipments(){
        if (listOfShipment.isEmpty()){
            System.out.println(" There is no Shipment! ");
            return;
        }
        System.out.println();
        int count=1;
        for (Shipment shipment:listOfShipment){
            System.out.println(count+++"- "+shipment);
        }
        System.out.println();
    }

    public void highCostShipments(){
        if (listOfShipment.isEmpty()){
            System.out.println(" There is no Shipments! ");
            return;
        }
        ArrayList<Shipment> tempListShipments = new ArrayList<>(listOfShipment);
        ArrayList<Shipment> maxShipmentsCost=new ArrayList<>();
        Shipment Max=tempListShipments.get(0);
        if (tempListShipments.size()>3){
            for (int i=0;i<3;i++){
                for (Shipment shipment:tempListShipments){
                    if (shipment.getShipmentCost()>Max.getShipmentCost()){
                        Max=shipment;
                    }
                }
                maxShipmentsCost.add(Max);
                tempListShipments.remove(Max);
            }
            System.out.println("Maximum Shipment Cost is : ");
            System.out.println("1- "+maxShipmentsCost.get(0));
            System.out.println("2- "+maxShipmentsCost.get(1));
            System.out.println("3- "+maxShipmentsCost.get(2));
        }

        else{
            int count=1;
            System.out.println("Maximum Shipment Cost is : ");
            for (Shipment ship:tempListShipments) {
                for (Shipment shipment : tempListShipments) {
                    if (shipment.getShipmentCost()>Max.getShipmentCost())
                        Max=shipment;
                }
                System.out.println(count + "- " + Max);
                count++;
                tempListShipments.remove(Max);
            }
        }
    }

   public void saveToFile(String path){
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (Shipment s : listOfShipment){
                StringBuilder productsPart = new StringBuilder();
                for (Product p : s.listOfProducts){
                    if (productsPart.length() > 0) productsPart.append("|");
                    productsPart.append(p.getID()).append(":").append(p.getNameOfProduct())
                            .append(":").append(p.getPriceOfProduct()).append(":").append(p.getQuantityOfProduct());
                }
                writer.println(s.getShipmentId() + "," + s.getShipmentDestination() + "," +
                        s.getMaximumBudgetOfShipment() + "," + s.getDeliveryDate() + "," + productsPart);
            }
        } catch (IOException e){
            System.out.println(" Error while saving shipments: " + e.getMessage());
        }
    }

    public void loadFromFile(String path){
        File file = new File(path);
        if (!file.exists()){
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null){
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                int id = Integer.parseInt(parts[0]);
                String destination = parts[1];
                float budget = Float.parseFloat(parts[2]);
                String deliveryDate = parts[3];
                String productsPart = parts.length > 4 ? parts[4] : "";

                if (shipmentMap.containsKey(id)) continue;

                Shipment shipment = new Shipment(id);
                shipment.setShipmentDestination(destination);
                shipment.setMaximumBudgetOfShipment(budget);
                shipment.setDeliveryDate(deliveryDate);

                if (!productsPart.isEmpty()){
                    for (String entry : productsPart.split("\\|")){
                        String[] pf = entry.split(":", -1);
                        int pid = Integer.parseInt(pf[0]);
                        String pname = pf[1];
                        int pprice = Integer.parseInt(pf[2]);
                        int pqty = Integer.parseInt(pf[3]);

                        Product snapshotProduct = new Product(pid);
                        snapshotProduct.setNameOfProduct(pname);
                        snapshotProduct.setPriceOfProduct(pprice);
                        snapshotProduct.setQuantityOfProduct(pqty);
                        shipment.listOfProducts.add(snapshotProduct);
                    }
                }
                shipment.recalculateShipmentCost();

                listOfShipment.add(shipment);
                bstTree.insertShipment(shipment);
                shipmentMap.put(id, shipment);
            }
        } catch (IOException e){
            System.out.println(" Error while loading shipments: " + e.getMessage());
        }
    }

}
