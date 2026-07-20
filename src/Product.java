
import java.util.Scanner;

public class Product {
    private final int ID;
    private  int priceOfProduct,quantityOfProduct,hight;
    private String nameOfProduct;
    Product left,right;
    Scanner scan=new Scanner(System.in);

    public Product(int id){
        ID = id;
    }

    public void updateThePriceOfProduct(){
        do {
            System.out.println("Please Enter A positive Price : ");
            this.priceOfProduct= scan.nextInt();
        }while(this.priceOfProduct<0);
    }

    public void updateTheQuantityOfProduct(){
        do {
            System.out.println("Please Enter The Quantity Of Product : ");
            this.quantityOfProduct= scan.nextInt();
        }while(this.quantityOfProduct<0);
    }

    public void updateTheQuantityOfProduct(int Quantity){
        this.quantityOfProduct+=Quantity;
    }

    public int getID() {
        return ID;
    }

    public String getNameOfProduct() {
        return nameOfProduct;
    }

    public void setNameOfProduct(String nameOfProduct) {
        this.nameOfProduct = nameOfProduct;
    }

    public void setQuantityOfProduct(int quantityOfProduct) {
        this.quantityOfProduct = quantityOfProduct;
    }

    public int getPriceOfProduct() {
        return priceOfProduct;
    }

    public int getQuantityOfProduct() {
        return quantityOfProduct;
    }

    public int getHight() {
        return hight;
    }

    public void setHight(int hight) {
        this.hight = hight;
    }

    public void setPriceOfProduct(int priceOfProduct) {
        this.priceOfProduct = priceOfProduct;
    }

    @Override
    public String toString() {
        return "Product { \n" +
                " nameOfProduct = ' " + nameOfProduct + '\'' +
                "\n quantityOfProduct = " + quantityOfProduct +
                "\n priceOfProduct = " + priceOfProduct +
                "\n ID = " + ID +
                '}';
    }
}
