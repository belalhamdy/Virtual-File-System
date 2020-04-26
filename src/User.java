import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class User {
    private final static String adminName = "admin";
    private final static String adminPassword = "admin";

    private static User admin = new User(adminName, adminPassword);
    private static User currentUser = admin; // It is said that the default user will be the admin, don't save the admin in user files save only "users" List
    private static List<User> users = new ArrayList<>();

    private String name, password;

    private User(String name, String password) {
        this.name = name.toLowerCase();
        this.password = password;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static List<User> getUsers() {
        return users;
    }

    public static void createUser(String name, String password) throws Exception {
        if (name.equals(adminName)) throw new Exception("Cannot create user.. this is the admin's name.");
        if (!currentUser.name.equals(adminName)) throw new Exception("Cannot create user.. Admin only can create.");
        if (users.stream().anyMatch((user) -> user.name.equals(name))) throw new Exception("Cannot create user.. a user with same name already exists.");



        users.add(new User(name, password));
    }

    public static void deleteUser(String name) throws Exception {
        if (!currentUser.name.equals(adminName)) throw new Exception("This command can be done by Admin only");
        if(!users.removeIf(user -> user.name.equals(name))) throw new Exception("Cannot delete user.. user doesn't exist.");
    }

    public static void login(String name, String password) throws Exception {
        String currName = name.toLowerCase();
        if (currName.equals(adminName) && password.equals(adminPassword)) {
            currentUser = admin;
            return;
        }
        for (User user : users)
            if (user.name.equals(currName) && user.password.equals(password)) {
                currentUser = user;
                return;
            }
        throw new Exception("Invalid username or password");
    }

    public String getName() {
        return name;
    }
    public String getPassword(){
        return password;
    }



}
