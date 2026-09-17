/**
 * كائن سياق مركزي (App Context) يحمل مراجع كل كلاسات منطق الأعمال + المستخدم الحالي المسجَّل دخوله.
 * كل شاشة Swing تستقبل نفس الكائن بدل تمرير كل كلاس (ProductManagement, Orders...) يدويًا لكل شاشة.
 */
public class AppContext {
    public final ProductManagement productManagement;
    public final ShipmentsRegisters shipmentsRegisters;
    public final Orders orders;
    public final AuthManager authManager;
    public User currentUser;

    public AppContext(){
        this.productManagement = new ProductManagement();
        this.orders = new Orders();
        this.shipmentsRegisters = new ShipmentsRegisters(orders);
        this.authManager = new AuthManager();
    }

    public boolean isAdmin(){
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }
}
