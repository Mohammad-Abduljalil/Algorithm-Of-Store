import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

public class AuthManager {

    private static final String USERS_FILE = "store_data" + File.separator + "users.csv";
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    HashMap<String, User> users = new HashMap<>();
    Scanner scan = new Scanner(System.in);

    public AuthManager(){
        loadUsers();
        if (users.isEmpty()){
            createDefaultAdmin();
        }
    }

    private void createDefaultAdmin(){
        String defaultUsername = "admin";
        String defaultPassword = "admin123";
        users.put(defaultUsername, new User(defaultUsername, hashPassword(defaultPassword), Role.ADMIN));
        saveUsers();
        System.out.println(" No users found. A default Admin account has been created: ");
        System.out.println(" Username: admin | Password: admin123 ");
        System.out.println(" Please change this password after logging in (menu option: Change My Password). ");
    }

    // SHA-256 مضمون التوفر دائمًا في جافا القياسية، لذا NoSuchAlgorithmException لن تحدث فعليًا هنا
    static String hashPassword(String password){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes){
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public User login(){
        int attempts = 0;
        while (attempts < MAX_LOGIN_ATTEMPTS){
            System.out.println("Username : ");
            String username = scan.next();
            System.out.println("Password : ");
            String password = scan.next();

            User user = users.get(username);
            if (user != null && user.verifyPassword(password)){
                System.out.println(" Welcome, " + username + " (" + user.getRole() + ") ");
                AuditLog.record(username, "Login", "SUCCESS");
                return user;
            }
            attempts++;
            System.out.println(" Invalid username or password. Attempts left: " + (MAX_LOGIN_ATTEMPTS - attempts));
            AuditLog.record(username, "Login", "FAILED (attempt " + attempts + "/" + MAX_LOGIN_ATTEMPTS + ")");
        }
        return null; // فشلت كل المحاولات
    }

    public void registerNewUser(){
        System.out.println("Enter new username : ");
        String username = scan.next();
        if (users.containsKey(username)){
            System.out.println(" This username already exists! ");
            return;
        }
        System.out.println("Enter password for new user : ");
        String password = scan.next();
        System.out.println("Select role (1=Admin, 2=Employee) : ");
        int roleChoice = scan.nextInt();
        Role role = (roleChoice == 1) ? Role.ADMIN : Role.EMPLOYEE;

        users.put(username, new User(username, hashPassword(password), role));
        saveUsers();
        System.out.println(" User registered successfully. ");
    }

    public boolean changePassword(User currentUser){
        System.out.println("Enter your current password : ");
        String current = scan.next();
        if (!currentUser.verifyPassword(current)){
            System.out.println(" Incorrect current password! ");
            return false;
        }
        System.out.println("Enter your new password : ");
        String newPassword = scan.next();
        currentUser.setPasswordHash(hashPassword(newPassword));
        saveUsers();
        System.out.println(" Password changed successfully. ");
        return true;
    }

    // ===================== دوال صديقة للواجهة الرسومية (GUI) =====================
    // نفس منطق login()/registerNewUser()/changePassword() لكن تستقبل المُدخلات كمعاملات
    // مباشرة بدل قراءتها من Scanner، لأن الواجهة الرسومية تجمعها عبر حقول نصية وليس الكونسول.

    public User loginGui(String username, String password){
        User user = users.get(username);
        if (user != null && user.verifyPassword(password)){
            AuditLog.record(username, "Login", "SUCCESS");
            return user;
        }
        AuditLog.record(username == null || username.isBlank() ? "(empty)" : username, "Login", "FAILED");
        return null;
    }

    public int getMaxLoginAttempts(){
        return MAX_LOGIN_ATTEMPTS;
    }

    // تُعيد null لو نجح التسجيل، أو رسالة خطأ نصية لو فشل (اسم مستخدم مكرر مثلًا)
    public String registerNewUserGui(String username, String password, Role role, String actingUsername){
        if (username == null || username.isBlank()){
            return "Username cannot be empty.";
        }
        if (users.containsKey(username)){
            return "This username already exists!";
        }
        users.put(username, new User(username, hashPassword(password), role));
        saveUsers();
        AuditLog.record(actingUsername, "Register New Employee", "SUCCESS - created '" + username + "' (" + role + ")");
        return null;
    }

    // تُعيد null لو نجح التغيير، أو رسالة خطأ نصية لو فشل
    public String changePasswordGui(User currentUser, String currentPassword, String newPassword){
        if (!currentUser.verifyPassword(currentPassword)){
            AuditLog.record(currentUser.getUsername(), "Change Own Password", "FAILED - incorrect current password");
            return "Incorrect current password!";
        }
        if (newPassword == null || newPassword.length() < 8){
            return "New password must be at least 8 characters long.";
        }
        currentUser.setPasswordHash(hashPassword(newPassword));
        saveUsers();
        AuditLog.record(currentUser.getUsername(), "Change Own Password", "SUCCESS");
        return null;
    }

    public java.util.Collection<User> getAllUsers(){
        return users.values();
    }

    void saveUsers(){
        try {
            new File("store_data").mkdirs();
            try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
                for (User u : users.values()){
                    writer.println(u.getUsername() + "," + u.getPasswordHash() + "," + u.getRole());
                }
            }
        } catch (IOException e){
            System.out.println(" Error while saving users: " + e.getMessage());
        }
    }

    void loadUsers(){
        File file = new File(USERS_FILE);
        if (!file.exists()){
            return; // أول تشغيل - لا يوجد ملف مستخدمين بعد
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null){
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                String username = parts[0];
                String passwordHash = parts[1];
                Role role = Role.valueOf(parts[2]);
                users.put(username, new User(username, passwordHash, role));
            }
        } catch (IOException e){
            System.out.println(" Error while loading users: " + e.getMessage());
        }
    }
}
