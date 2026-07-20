import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class ShipmentsRegisters {
    private ArrayList<Shipment> listOfShipment;
    private Scanner scan=new Scanner(System.in);
    private BSTTree bstTree=new BSTTree();
    private HeapPriority heapPriority;
    Orders orders;

    public ShipmentsRegisters(Orders orders){
        listOfShipment=new ArrayList<>();
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
        listOfShipment.add(shipment);
        System.out.println("Add a products to this Shipment : ");
        shipment.addProductToShipment(productManagement);
        bstTree.Insert(listOfShipment.get(0),shipment);
        this.orders.toAddNewOrder(shipment);
    }

    void deleteShipmentWithPriority(){
        if (listOfShipment.isEmpty()){
            System.out.println("There is no Order");
            return;
        }
        Order order=this.orders.deleteOrderWithPriority();
        bstTree.deleteShipment(listOfShipment.get(0),order.getShipment().getShipmentId());
        listOfShipment.remove(order.shipment);
        System.out.println("The Shipment removed successfully ");
    }

    public void updateTheDeliveryDateById(){
        System.out.println("Please Enter The Id of Shipment : ");
        int id=scan.nextInt();
        Shipment shipment=bstTree.searchShipmentByID(listOfShipment.get(0),id);
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
            listOfShipment.get(listOfShipment.indexOf(shipment)).setDeliveryDate(customDate.getYear()+"/"+customDate.getMonth()+"/"+customDate.getDayOfMonth());
            this.orders.editShipmentInOrder(shipment,shipment.getShipmentId());
        }
        else
            System.out.println("this Shipment doesn't exist! ");
    }


    public Shipment searchShipmentByID(){
        System.out.println("Please Enter The Id of Shipment : ");
        int id=scan.nextInt();
        Shipment shipment=bstTree.searchShipmentByID(listOfShipment.get(0),id);
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
            shipment=bstTree.searchShipmentByID(listOfShipment.get(0),id);
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


}
