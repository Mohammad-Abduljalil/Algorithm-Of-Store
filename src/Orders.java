

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
}
