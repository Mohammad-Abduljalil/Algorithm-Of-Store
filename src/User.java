public class User {
    private String username;
    private String passwordHash; // لا نُخزّن كلمة المرور الصريحة أبدًا، فقط بصمتها (SHA-256)
    private Role role;

    public User(String username, String passwordHash, Role role){
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername(){
        return username;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }

    public Role getRole(){
        return role;
    }

    public boolean verifyPassword(String plainPassword){
        return this.passwordHash.equals(AuthManager.hashPassword(plainPassword));
    }

    @Override
    public String toString(){
        return "User{username='" + username + "', role=" + role + "}";
    }
}
