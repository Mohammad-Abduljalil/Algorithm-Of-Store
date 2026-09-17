import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ProductManagement {
    ArrayList<Product> listOfProduct;
    HashMap<Integer, Product> productMap; // فهرس سريع O(1) للبحث بالـ ID، يعمل بالتوازي مع avlTree
    HashMap<String, ArrayList<Product>> categoryMap; // فهرس التصنيفات: اسم التصنيف -> قائمة منتجاته
    Scanner scan=new Scanner(System.in);
    AVLTree avlTree=new AVLTree();
    int quantityOfProducts=0;


    public ProductManagement(){
        listOfProduct =new ArrayList<>();
        productMap = new HashMap<>();
        categoryMap = new HashMap<>();
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
        product.updateTheMinimumStockThreshold();
        product.updateTheCategory();
        return product;
    }

    // يُضيف المنتج لفهرس التصنيفات (يُنشئ قائمة جديدة للتصنيف لو كانت أول مرة يظهر فيها)
    private void addToCategoryIndex(Product product){
        categoryMap.computeIfAbsent(product.getCategory(), k -> new ArrayList<>()).add(product);
    }

    private void removeFromCategoryIndex(Product product){
        ArrayList<Product> list = categoryMap.get(product.getCategory());
        if (list != null){
            list.remove(product);
            if (list.isEmpty()){
                categoryMap.remove(product.getCategory());
            }
        }
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

    // تطبع تحذيرًا فوريًا لو كانت كمية المنتج عند الحد الأدنى أو أقل منه
    private void checkLowStockAndAlert(Product product){
        if (product.isLowStock()){
            System.out.println(" \u26A0 LOW STOCK ALERT: '" + product.getNameOfProduct() + "' (ID=" + product.getID() +
                    ") quantity is " + product.getQuantityOfProduct() +
                    ", at or below its threshold of " + product.getMinimumStockThreshold() + "! ");
        }
    }

    public void addNewProduct(){
        Product product=ReadProductInfo();
        // فحص التكرار بـ O(1) عبر HashMap بدل O(log n) عبر الشجرة
        if (productMap.containsKey(product.getID())){
            System.out.println(" This Product ID already exists! Please try again with a different ID. ");
            this.quantityOfProducts -= product.getQuantityOfProduct(); // التراجع عن الحجز الذي تم في ReadProductInfo
            return;
        }
        listOfProduct.add(product);
        avlTree.insertProduct(product);
        productMap.put(product.getID(), product);
        addToCategoryIndex(product);
        checkLowStockAndAlert(product); // قد تكون الكمية الابتدائية نفسها منخفضة
    }

    public Product searchProductByID(){
        if (listOfProduct.isEmpty()){
            System.out.println(" There is no Product! ");
            return null;
        }
        System.out.println("Please Enter the ID of Product : ");
        int ID=scan.nextInt();
        Product product=productMap.get(ID);
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
        Product product=productMap.get(ID);
        if (product==null){
            System.out.println(" this Product isn't exist!  ");
        }else {
            avlTree.deleteProductByID(product.getID());
            listOfProduct.remove(product);
            productMap.remove(product.getID());
            removeFromCategoryIndex(product);
            this.quantityOfProducts -= product.getQuantityOfProduct();
        }
    }

    public void updateThePriceOfProductByID(){
        printProducts();
        System.out.println("Please Enter the ID of Product : ");
        int ID=scan.nextInt();
        Product product=productMap.get(ID);
        if (product==null){
            System.out.println(" This Product does not exist! ");
        }else{
            product.updateThePriceOfProduct();
            System.out.println(" Updated successfully ");
        }
    }

    public void updateTheQuantityOfProductByID(){
        printProducts();
        System.out.println("Please Enter the ID of Product : ");
        int ID=scan.nextInt();
        Product product=productMap.get(ID);
        if (product==null){
            System.out.println(" This Product does not exist! ");
        }else{
            int thePreviousQuantity=product.getQuantityOfProduct();
            do {
                product.updateTheQuantityOfProduct();
            }while(!checkQuantity(product.getQuantityOfProduct()));
            this.quantityOfProducts-=thePreviousQuantity;
            this.quantityOfProducts+=product.getQuantityOfProduct();
            checkLowStockAndAlert(product);
            System.out.println(" Updated successfully ");
        }

    }

    public void returnQuantity(int ID,int Quantity){
        Product product=productMap.get(ID);
        if (product==null){
            System.out.println("This Product doesn't exist! ");
        }
        else{
            product.updateTheQuantityOfProduct(Quantity);
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
        Product product=productMap.get(id);

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
            product.setQuantityOfProduct(product.getQuantityOfProduct()-quantity);
            tempProduct.setQuantityOfProduct(quantity);
            this.quantityOfProducts-=quantity;
            checkLowStockAndAlert(product);
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

    public void printLowStockProducts(){
        if (listOfProduct.isEmpty()){
            System.out.println(" There is no Products! ");
            return;
        }
        System.out.println("Low Stock Products : ");
        int count=1;
        boolean found=false;
        for (Product product: listOfProduct){
            if (product.isLowStock()){
                System.out.println(count+++"- "+product+" [Minimum Threshold = "+product.getMinimumStockThreshold()+"]");
                found=true;
            }
        }
        if (!found){
            System.out.println(" No products are currently low on stock. Good job! ");
        }
    }

    // يعرض كل التصنيفات الموجودة حاليًا مع عدد منتجات كل تصنيف (O(عدد التصنيفات) عبر categoryMap)
    public void printAllCategories(){
        if (categoryMap.isEmpty()){
            System.out.println(" There is no Products! ");
            return;
        }
        System.out.println("Categories : ");
        int count=1;
        for (String category : categoryMap.keySet()){
            System.out.println(count+++"- "+category+" ("+categoryMap.get(category).size()+" product(s))");
        }
    }

    // يعرض منتجات تصنيف معيّن بحث المستخدم عنه (O(1) للوصول لقائمة التصنيف عبر categoryMap)
    public void printProductsByCategory(){
        if (listOfProduct.isEmpty()){
            System.out.println(" There is no Products! ");
            return;
        }
        printAllCategories();
        System.out.println("Please Enter the Category name : ");
        String category = scan.next();
        ArrayList<Product> products = categoryMap.get(category);
        if (products == null || products.isEmpty()){
            System.out.println(" No products found in this category! ");
            return;
        }
        System.out.println("Products in category '"+category+"' : ");
        int count=1;
        for (Product product : products){
            System.out.println(count+++"- "+product);
        }
    }

    // ===================== الحفظ والاسترجاع (Persistence) =====================
    // صيغة السطر: id,name,price,quantity,minimumStockThreshold,category
    public void saveToFile(String path){
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (Product p : listOfProduct){
                writer.println(p.getID() + "," + p.getNameOfProduct() + "," +
                        p.getPriceOfProduct() + "," + p.getQuantityOfProduct() + "," +
                        p.getMinimumStockThreshold() + "," + p.getCategory());
            }
        } catch (IOException e){
            System.out.println(" Error while saving products: " + e.getMessage());
        }
    }

    public void loadFromFile(String path){
        File file = new File(path);
        if (!file.exists()){
            return; // لا يوجد ملف بيانات سابق (أول تشغيل للبرنامج) - أمر طبيعي
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null){
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                int price = Integer.parseInt(parts[2]);
                int quantity = Integer.parseInt(parts[3]);
                // parts[4] و parts[5] قد لا يكونا موجودين في ملفات محفوظة قبل إضافة هذه الميزات،
                // فنستخدم القيم الافتراضية للمنتج في هذه الحالة (توافق رجعي)
                Integer threshold = (parts.length > 4 && !parts[4].isBlank()) ? Integer.parseInt(parts[4]) : null;
                String category = (parts.length > 5 && !parts[5].isBlank()) ? parts[5] : null;

                if (productMap.containsKey(id)) continue; // تفادي التكرار لو استُدعيت loadFromFile أكثر من مرة

                Product product = new Product(id);
                product.setNameOfProduct(name);
                product.setPriceOfProduct(price);
                product.setQuantityOfProduct(quantity);
                if (threshold != null){
                    product.setMinimumStockThreshold(threshold);
                }
                if (category != null){
                    product.setCategory(category);
                }

                listOfProduct.add(product);
                avlTree.insertProduct(product);
                productMap.put(id, product);
                addToCategoryIndex(product);
                this.quantityOfProducts += quantity;
            }
        } catch (IOException e){
            System.out.println(" Error while loading products: " + e.getMessage());
        }
    }

    // ===================== دوال صديقة للواجهة الرسومية (GUI) =====================
    // نفس منطق addNewProduct/updateThePriceOfProductByID/.../deleteProductByID لكن تستقبل
    // المُدخلات كمعاملات مباشرة بدل Scanner، وتُعيد رسالة خطأ نصية (null = نجاح).

    public String addNewProductGui(int id, String name, int price, int quantity, int threshold, String category){
        if (price < 0) return "Price must be a positive number.";
        if (quantity < 0) return "Quantity must be a positive number.";
        if (!checkQuantity(quantity)) return "Adding this quantity would exceed the maximum capacity of 1000.";
        if (productMap.containsKey(id)) return "This Product ID already exists!";

        Product product = new Product(id);
        product.setNameOfProduct(name);
        product.setPriceOfProduct(price);
        product.setQuantityOfProduct(quantity);
        product.setMinimumStockThreshold(threshold);
        product.setCategory(category);

        listOfProduct.add(product);
        avlTree.insertProduct(product);
        productMap.put(id, product);
        addToCategoryIndex(product);
        this.quantityOfProducts += quantity;
        checkLowStockAndAlert(product);
        return null;
    }

    public String updateProductGui(int id, int newPrice, int newQuantity, int newThreshold, String newCategory){
        Product product = productMap.get(id);
        if (product == null) return "Product not found.";
        if (newPrice < 0) return "Price must be a positive number.";
        if (newQuantity < 0) return "Quantity must be a positive number.";

        int previousQuantity = product.getQuantityOfProduct();
        int delta = newQuantity - previousQuantity;
        if (delta > 0 && !checkQuantity(delta)) return "This quantity would exceed the maximum capacity of 1000.";

        removeFromCategoryIndex(product); // نُزيله من فهرس التصنيف القديم قبل تغييره
        product.setPriceOfProduct(newPrice);
        product.setQuantityOfProduct(newQuantity);
        product.setMinimumStockThreshold(newThreshold);
        product.setCategory(newCategory);
        addToCategoryIndex(product); // ونُعيد إضافته بالتصنيف الجديد

        this.quantityOfProducts += delta;
        checkLowStockAndAlert(product);
        return null;
    }

    public String deleteProductGui(int id){
        Product product = productMap.get(id);
        if (product == null) return "Product not found.";
        avlTree.deleteProductByID(id);
        listOfProduct.remove(product);
        productMap.remove(id);
        removeFromCategoryIndex(product);
        this.quantityOfProducts -= product.getQuantityOfProduct();
        return null;
    }

}
