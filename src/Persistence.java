import java.io.File;

/**
 * ينسّق عملية الحفظ والاسترجاع بين ProductManagement و ShipmentsRegisters و Orders.
 * كل كلاس يعرف كيف يحفظ/يحمّل بياناته الخاصة (Single Responsibility)،
 * وهذا الكلاس فقط يستدعيها بالترتيب الصحيح:
 *   تحميل: منتجات أولاً -> شحنات (تحتاج معرفة منتجاتها) -> طلبات (تحتاج شحنات موجودة)
 *   حفظ: لا يهم الترتيب لأن كل ملف مستقل، لكن نحافظ على نفس الترتيب للتناسق
 */
public class Persistence {

    private static final String DATA_DIR = "store_data";
    private static final String PRODUCTS_FILE = DATA_DIR + File.separator + "products.csv";
    private static final String SHIPMENTS_FILE = DATA_DIR + File.separator + "shipments.csv";
    private static final String ORDERS_FILE = DATA_DIR + File.separator + "orders.csv";

    public static void saveAll(ProductManagement productManagement, ShipmentsRegisters shipmentsRegisters, Orders orders){
        new File(DATA_DIR).mkdirs(); // ينشئ المجلد لو غير موجود، لا يفعل شيء لو موجود بالفعل
        productManagement.saveToFile(PRODUCTS_FILE);
        shipmentsRegisters.saveToFile(SHIPMENTS_FILE);
        orders.saveToFile(ORDERS_FILE);
        System.out.println(" All data has been saved successfully. ");
    }

    public static void loadAll(ProductManagement productManagement, ShipmentsRegisters shipmentsRegisters, Orders orders){
        productManagement.loadFromFile(PRODUCTS_FILE);
        shipmentsRegisters.loadFromFile(SHIPMENTS_FILE);
        orders.loadFromFile(ORDERS_FILE, shipmentsRegisters);
    }
}
