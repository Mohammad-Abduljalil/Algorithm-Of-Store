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

    public ArrayList<Shipment> getListOfShipment(){
        return listOfShipment;
    }

    HashMap<Integer, Shipment> shipmentMap; // فهرس سريع O(1) للبحث بالـ ID، يعمل بالتوازي مع bstTree
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
        // فحص التكرار بـ O(1) عبر HashMap بدل O(log n) عبر الشجرة
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

    // ===================== الحفظ والاسترجاع (Persistence) =====================
    // صيغة السطر: id,destination,budget,deliveryDate,productsList
    // productsList = "pid:name:price:qty|pid:name:price:qty|..." (فارغة لو لا منتجات)
    // نُخزّن لقطة (Snapshot) من كل منتج (اسم/سعر وقت الشحن) بدل الاعتماد على وجوده لاحقًا في المخزون،
    // لأن المنتج قد يُحذف من ProductManagement لاحقًا لكن يجب أن تبقى بيانات الشحنة القديمة صحيحة.
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
            return; // لا يوجد ملف بيانات سابق - أمر طبيعي في أول تشغيل
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

                if (shipmentMap.containsKey(id)) continue; // تفادي التكرار

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

    // ===================== دوال صديقة للواجهة الرسومية (GUI) =====================

    public String addShipmentGui(int id, String destination, float budget, int year, int month, int day){
        if (shipmentMap.containsKey(id)) return "This Shipment ID already exists!";
        if (budget < 0) return "Budget must be a positive number.";
        LocalDate date;
        try {
            date = LocalDate.of(year, month, day);
        } catch (Exception e){
            return "Invalid delivery date.";
        }
        if (date.isBefore(LocalDate.now())) return "Delivery date cannot be in the past.";

        Shipment shipment = new Shipment(id);
        shipment.setShipmentDestination(destination);
        shipment.setMaximumBudgetOfShipment(budget);
        shipment.setDeliveryDate(date.getYear() + "/" + date.getMonth() + "/" + date.getDayOfMonth());

        listOfShipment.add(shipment);
        bstTree.insertShipment(shipment);
        shipmentMap.put(id, shipment);
        return null;
    }

    // إضافة منتج لشحنة موجودة (نُنقص المخزون فعليًا ونتحقق من الميزانية، بمعزل عن Scanner)
    public String addProductToShipmentGui(int shipmentId, int productId, int quantity, ProductManagement productManagement){
        Shipment shipment = shipmentMap.get(shipmentId);
        if (shipment == null) return "Shipment not found.";
        Product product = productManagement.productMap.get(productId);
        if (product == null) return "Product not found.";
        if (quantity <= 0) return "Quantity must be greater than zero.";
        if (quantity > product.getQuantityOfProduct()) return "Not enough stock available for this quantity.";

        float futureCost = shipment.getShipmentCost() + (quantity * product.getPriceOfProduct());
        if (futureCost > shipment.getMaximumBudgetOfShipment()){
            return "This would exceed the shipment's maximum budget.";
        }

        Product snapshot = new Product(product.getID());
        snapshot.setNameOfProduct(product.getNameOfProduct());
        snapshot.setPriceOfProduct(product.getPriceOfProduct());
        snapshot.setQuantityOfProduct(quantity);
        shipment.listOfProducts.add(snapshot);
        shipment.recalculateShipmentCost();

        product.setQuantityOfProduct(product.getQuantityOfProduct() - quantity);
        productManagement.quantityOfProducts -= quantity;
        return null;
    }

    // تعديل تاريخ تسليم شحنة موجودة (العملية 11 في قائمة الكونسول)
    public String updateDeliveryDateGui(int id, int year, int month, int day){
        Shipment shipment = shipmentMap.get(id);
        if (shipment == null) return "Shipment not found.";
        LocalDate date;
        try {
            date = LocalDate.of(year, month, day);
        } catch (Exception e){
            return "Invalid delivery date.";
        }
        if (date.isBefore(LocalDate.now())) return "Delivery date cannot be in the past.";
        shipment.setDeliveryDate(date.getYear() + "/" + date.getMonth() + "/" + date.getDayOfMonth());
        this.orders.editShipmentInOrder(shipment, shipment.getShipmentId());
        return null;
    }

    public String deleteShipmentGui(int id){
        Shipment shipment = shipmentMap.get(id);
        if (shipment == null) return "Shipment not found.";
        bstTree.deleteShipmentByID(id);
        listOfShipment.remove(shipment);
        shipmentMap.remove(id);
        return null;
    }

}
