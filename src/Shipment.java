import java.util.ArrayList;
import java.util.Scanner;

public class Shipment {
    private final int shipmentId;
    private String shipmentDestination, deliveryDate;
    private float shipmentCost=0;
    private float maximumBudgetOfShipment =0;
    ArrayList<Product> listOfProducts;
    Shipment left,right;



    public Shipment(int ID){
        this.shipmentId=ID;
        listOfProducts =new ArrayList<>();
    }

    public float getMaximumBudgetOfShipment() {
        return maximumBudgetOfShipment;
    }

    public void setMaximumBudgetOfShipment(float maximumBudgetOfShipment) {
        this.maximumBudgetOfShipment = maximumBudgetOfShipment;
    }

    public String getShipmentDestination() {
        return shipmentDestination;
    }

    public void setShipmentDestination(String shipmentDestination) {
        this.shipmentDestination = shipmentDestination;
    }

    public String getDeliveryDate() {
        return this.deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public float getShipmentCost() {
        return this.shipmentCost;
    }

    public int getShipmentId() {
        return this.shipmentId;
    }

    private void Budget(){
        System.out.println();
        System.out.println("Maximum Budget Of shipment is : "+getMaximumBudgetOfShipment());
        System.out.println("Current Budget Of shipment is : "+getShipmentCost());
        System.out.println("Available Budget For the Shipment is : "+(getMaximumBudgetOfShipment()-getShipmentCost()));
        System.out.println();
    }

    public void addProductToShipment(ProductManagement productManagement) {
        Product product =productManagement.takeProductByID();
        if(product==null){
            return;
        }
        if (product.getQuantityOfProduct() == 0) {
            return;
        } else {
            float futureCost=getShipmentCost()+(product.getQuantityOfProduct()*product.getPriceOfProduct());
            if (futureCost>getMaximumBudgetOfShipment()){
                System.err.print("Sorry,you cannot take this product with this quantity because : ");
                Budget();
                System.out.println(" so,we will return this Order only and you can re-order again! ");
                productManagement.returnQuantity(product.getID(),product.getQuantityOfProduct());
                addProductToShipment(productManagement);
            }
            else {
                System.out.println(" the product has been successfully added to the shipment. ");
                listOfProducts.add(product);
                shipmentCost += product.getQuantityOfProduct() * product.getPriceOfProduct();
                System.out.println("If you want to Add more Product enter 1 : ");
                int valueToCheck=new Scanner(System.in).nextInt();
                if (valueToCheck==1){
                    addProductToShipment(productManagement);
                }
                else {
                    System.out.println(" Thank you to order. ");
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Shipment {\n" +
                " Shipment ID = " + shipmentId +
                "\n Shipment Destination = " + shipmentDestination + '\'' +
                "\n Delivery Date = " + deliveryDate + '\'' +
                "\n Shipment Cost = " + shipmentCost +
                "\n Products = " + listOfProducts +
                '}';
    }
}
