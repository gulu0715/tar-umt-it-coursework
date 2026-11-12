package useraccount.model;


public class User extends Person {
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        super(username);
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { 
        return username; 
    }

    public String getPassword() { 
        return password; 
    }

    public String getRole() { 
        return role; 
    }

    public String getId() { 
        return username; 
    }

    public void setUsername(String username) {
        this.username = username;
        setId(username);
    }
    public void setPassword(String password) { 
        this.password = password; 
    }

    public void setRole(String role) { 
        this.role = role; 
    }

    @Override
    public String toString() {
        return String.format("%-15s %-15s %-10s", username, password, role);
    }
}
