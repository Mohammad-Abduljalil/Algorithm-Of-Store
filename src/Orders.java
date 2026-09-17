

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Orders {
    HeapPriority heapPriority;
    ArrayList<Order> listOfOrder;
    Scanner scan=new Scanner(System.in);

    public Orders(){
        listOfOrder=new ArrayList<>();
        heapPriority=new HeapPriority();
    }

    void toAddNewOrder(Shipment shipment){
        Order order=new Order();
        order.setShipment(shipment);
        System.out.println("Please Enter The Priority of This Order : ");
        int priority=scan.nextInt();
        order.setPriority(priority);
        listOfOrder.add(order);
        heapPriority.AddOrder(order);
    }

    Order deleteOrderWithPriority(){
        Order order=heapPriority.DeleteRoot();
        listOfOrder.remove(order);
        return order;
    }

    void editShipmentInOrder(Shipment newShipment,int id){
        for (Order order:listOfOrder){
            if (order.getShipment().getShipmentId()==id){
                listOfOrder.get(listOfOrder.indexOf(order)).setShipment(newShipment);
            }
        }
    }

    public void updateThePriorityOfOrderById(){
        if (listOfOrder.isEmpty()){
            System.out.println(" There is no Orders! ");
            return;
        }
        System.out.println("Please Enter the ID Of main.java.Shipment : ");
        int id=scan.nextInt();
        Order order=null;
        for (Order order1:listOfOrder){
            if (order1.shipment.getShipmentId()==id){
                order=order1;
                break;
            }
        }
        if (order==null){
            System.out.println(" this Order doesn't exist! ");
        }
        else{
            System.out.println("Please Enter the new priority of main.java.Shipment : ");
            listOfOrder.get(listOfOrder.indexOf(order)).setPriority(scan.nextInt());
        }
    }

    public void totalCostOfOrders(){
        if (listOfOrder.isEmpty()){
            System.out.println(" There is no Orders! ");
            return;
        }
        float totalCost=0;
        for (Order order:listOfOrder){
            totalCost+=order.getShipment().getShipmentCost();
        }
        System.out.println("Total cost of Orders is : "+totalCost);
    }

    @Override
    public String toString() {
        return "Orders{" +
                " heapPriority = " + heapPriority +
                " ,  Orders = " + listOfOrder +
                '}';
    }

    // ===================== الحفظ والاسترجاع (Persistence) =====================
    // صيغة السطر: shipmentId,priority
    public void saveToFile(String path){
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (Order o : listOfOrder){
                writer.println(o.getShipment().getShipmentId() + "," + o.getPriority());
            }
        } catch (IOException e){
            System.out.println(" Error while saving orders: " + e.getMessage());
        }
    }

    // يحتاج ShipmentsRegisters محمّلة مسبقًا للربط بين shipmentId والشحنة الفعلية
    public void loadFromFile(String path, ShipmentsRegisters shipmentsRegisters){
        File file = new File(path);
        if (!file.exists()){
            return; // لا يوجد ملف بيانات سابق - أمر طبيعي في أول تشغيل
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null){
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                int shipmentId = Integer.parseInt(parts[0]);
                int priority = Integer.parseInt(parts[1]);

                Shipment shipment = shipmentsRegisters.shipmentMap.get(shipmentId);
                if (shipment == null) continue; // شحنة غير موجودة (بيانات غير متسقة) - تخطّ بأمان

                Order order = new Order();
                order.setShipment(shipment);
                order.setPriority(priority);
                listOfOrder.add(order);
                heapPriority.AddOrder(order);
            }
        } catch (IOException e){
            System.out.println(" Error while loading orders: " + e.getMessage());
        }
    }

    // ===================== دوال صديقة للواجهة الرسومية (GUI) =====================

    public String addOrderGui(Shipment shipment, int priority){
        if (priority < 1 || priority > 10) return "Priority must be between 1 and 10.";
        Order order = new Order();
        order.setShipment(shipment);
        order.setPriority(priority);
        listOfOrder.add(order);
        heapPriority.AddOrder(order);
        return null;
    }

    // تعديل أولوية طلب مرتبط بشحنة معيّنة (العملية 13 في قائمة الكونسول)
    public String updatePriorityGui(int shipmentId, int newPriority){
        if (newPriority < 1 || newPriority > 10) return "Priority must be between 1 and 10.";
        for (Order o : listOfOrder){
            if (o.getShipment().getShipmentId() == shipmentId){
                o.setPriority(newPriority);
                heapPriority.buildMaxHeapify(); // نُعيد ترتيب الكومة بعد تغيير الأولوية
                return null;
            }
        }
        return "No order found for this shipment.";
    }

    // يُرجع (ويحذف) الطلب الأعلى أولوية عبر الكومة - العملية 12 في قائمة الكونسول
    public Order returnHighestPriorityOrderGui(){
        if (listOfOrder.isEmpty()) return null;
        Order order = heapPriority.DeleteRoot();
        listOfOrder.remove(order);
        return order;
    }

    // يحذف الطلب المرتبط بشحنة معيّنة (وليس بالضرورة الأعلى أولوية، بخلاف نسخة الكونسول)
    public void deleteOrderByShipmentIdGui(int shipmentId){
        Order toRemove = null;
        for (Order o : listOfOrder){
            if (o.getShipment().getShipmentId() == shipmentId){
                toRemove = o;
                break;
            }
        }
        if (toRemove != null){
            listOfOrder.remove(toRemove);
            heapPriority.arrayList.remove(toRemove);
            heapPriority.buildMaxHeapify();
        }
    }
}
