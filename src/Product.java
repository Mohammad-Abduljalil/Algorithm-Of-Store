
import java.util.Scanner;

public class Product {
    private static final int DEFAULT_MINIMUM_STOCK_THRESHOLD = 5;
    public static final String DEFAULT_CATEGORY = "Uncategorized";

    private final int ID;
    private  int priceOfProduct,quantityOfProduct,hight;
    private int minimumStockThreshold = DEFAULT_MINIMUM_STOCK_THRESHOLD;
    private String nameOfProduct;
    private String category = DEFAULT_CATEGORY;
    Product left,right;
    Scanner scan=new Scanner(System.in);

    public Product(int id){
        ID = id;
    }

    public void updateTheCategory(){
        System.out.println("Please Enter the Category of Product (e.g. Electronics, Food, Clothes) : ");
        this.category = scan.next();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = (category == null || category.isBlank()) ? DEFAULT_CATEGORY : category;
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

    public void updateTheMinimumStockThreshold(){
        do {
            System.out.println("Please Enter the Minimum Stock Threshold (alert when quantity drops to this level or below) : ");
            this.minimumStockThreshold = scan.nextInt();
        } while (this.minimumStockThreshold < 0);
    }

    public int getMinimumStockThreshold() {
        return minimumStockThreshold;
    }

    public void setMinimumStockThreshold(int minimumStockThreshold) {
        this.minimumStockThreshold = minimumStockThreshold;
    }

    public boolean isLowStock() {
        return this.quantityOfProduct <= this.minimumStockThreshold;
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
