import java.util.ArrayList;
import java.util.Scanner;

public class ProductManagement {
    ArrayList<Product> listOfProduct;
    Scanner scan=new Scanner(System.in);
    AVLTree avlTree=new AVLTree();
    int quantityOfProducts=0;


    public ProductManagement(){
        listOfProduct =new ArrayList<>();
    }

    public ArrayList<Product> getListOfProduct() {
        return listOfProduct;
    }

    private Product ReadProductInfo(){
        System.out.println("Enter The ID of Product : ");
        int ID=scan.nextInt();
        Product product=new Product(ID);
        System.out.println("Enter The Name of Product : ");
        String name=scan.next();
        product.setNameOfProduct(name);
        System.out.println("Enter The Price of Product : ");
        product.updateThePriceOfProduct();
        do {//to check quantity with know the max capacity is 1000!
            System.out.println("the quantity of All products : "+this.quantityOfProducts);
            product.updateTheQuantityOfProduct();
        }while(!checkQuantity(product.getQuantityOfProduct()));
        this.quantityOfProducts+=product.getQuantityOfProduct();
        return product;
    }

    // To check quantityOfCapacity+quantityOfNewProduct<=1000
    boolean checkQuantity(int quantityToAdd){
        if ((this.quantityOfProducts+quantityToAdd)<=1000)
            return true;
        else{
            System.out.println(" The max capacity is 1000! ");
            return false;
        }

    }

    public void addNewProduct(){
        Product product=ReadProductInfo();
        listOfProduct.add(product);
        avlTree.Insert(listOfProduct.get(0),product);

    }

    public Product searchProductByID(){
        if (listOfProduct.isEmpty()){
            System.out.println(" There is no Product! ");
            return null;
        }
        System.out.println("Please Enter the ID of Product : ");
        int ID=scan.nextInt();
        Product product=avlTree.searchProductByID(listOfProduct.get(0),ID);
        if (product==null){
            System.out.println(" this Product isn't excite!  ");
        }
        else{
            System.out.println(product);
        }
        return product;
    }

    public void deleteProductByID(){
        if (listOfProduct.isEmpty()){
            System.out.println(" There is no Product! ");
            return;
        }
        printProducts();
        System.out.println("Please Enter the ID of Product : ");
        int ID=scan.nextInt();
        Product product=avlTree.searchProductByID(listOfProduct.get(0),ID);
        if (product==null){
            System.out.println(" this Product isn't exist!  ");
        }else {
            avlTree.deleteProduct(listOfProduct.get(0), product.getID());
            listOfProduct.remove(product);
            this.quantityOfProducts -= product.getQuantityOfProduct();
        }
    }

    public void updateThePriceOfProductByID(){
        printProducts();
        System.out.println("Please Enter the ID of Product : ");
        int ID=scan.nextInt();
        Product product=avlTree.searchProductByID(listOfProduct.get(0),ID);
        if (product==null){
            System.out.println(" This Product does not exist! ");
        }else{
            listOfProduct.get(listOfProduct.indexOf(product)).updateThePriceOfProduct();
            System.out.println(" Updated successfully ");
        }
    }

    public void updateTheQuantityOfProductByID(){
        printProducts();
        System.out.println("Please Enter the ID of Product : ");
        int ID=scan.nextInt();
        Product product=avlTree.searchProductByID(listOfProduct.get(0),ID);
        if (product==null){
            System.out.println(" This Product does not exist! ");
        }else{
            int thePreviousQuantity=product.getQuantityOfProduct();
            do {
                listOfProduct.get(listOfProduct.indexOf(product)).updateTheQuantityOfProduct();
            }while(!checkQuantity(listOfProduct.get(listOfProduct.indexOf(product)).getQuantityOfProduct()));
            this.quantityOfProducts-=thePreviousQuantity;
            this.quantityOfProducts+=product.getQuantityOfProduct();
            System.out.println(" Updated successfully ");
        }

    }

    public void returnQuantity(int ID,int Quantity){
        Product product=avlTree.searchProductByID(listOfProduct.get(0),ID);
        if (product==null){
            System.out.println("This Product doesn't exist! ");
        }
        else{
            listOfProduct.get(listOfProduct.indexOf(product)).updateTheQuantityOfProduct(Quantity);
            this.quantityOfProducts+=Quantity;
        }
    }

    public void printProducts() {
        if(listOfProduct.isEmpty()){
            System.out.println(" There is no Products! ");
            return;
        }
        System.out.println("Products : ");
        int count=1;
        for (Product product: listOfProduct){
            System.out.println(count+++"- "+product);
        }
    }

    public Product takeProductByID(){
        if (listOfProduct.isEmpty()){
            System.out.println(" There is no Product");
            return null;
        }
        printProducts();
        System.out.println("Please Enter The ID of Product : ");
        int id=scan.nextInt();
        Product product=avlTree.searchProductByID(listOfProduct.get(0),id);

        if (product==null){
            System.out.println(" this ID isn't exist! ");
            return null;
        }
        else {
            Product tempProduct=new Product(product.getID());
            tempProduct.setNameOfProduct(product.getNameOfProduct());
            tempProduct.setPriceOfProduct(product.getPriceOfProduct());
            System.out.println("Please Enter the Quantity of "+product.getNameOfProduct()+" : ");
            int quantity;
            do {
                System.out.println("Total Quantity of this product is : "+product.getQuantityOfProduct()+" , Enter a number is less or equal this Quantity! ");
                quantity= scan.nextInt();
            }while (quantity>product.getQuantityOfProduct());
            listOfProduct.get(listOfProduct.indexOf(product)).setQuantityOfProduct(product.getQuantityOfProduct()-quantity);
            tempProduct.setQuantityOfProduct(quantity);
            this.quantityOfProducts-=quantity;
            return tempProduct;
        }
    }

    public float storeValue(){
        float store_Value=0;
        for (Product product: listOfProduct){
            store_Value+=product.getQuantityOfProduct()*product.getPriceOfProduct();
        }
        System.out.println("The Store Value is : "+store_Value);
        return store_Value;
    }

}

