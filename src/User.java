import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class User {
    private final static String adminName = "Admin";
    private final static String adminPassword = "1234";

    private static User admin = new User(adminName, adminPassword, "11");
    private static User currentUser = admin; // It is said that the default user will be the admin
    private static List<User> users = new ArrayList<>();

    private String name, password, type; // 00 - 10 - 01 - 11

    private User(String name, String password, String type) {
        this.name = name;
        this.password = password;
        this.type = type;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static List<User> getUsers() {
        return users;
    }

    public static void createUser(String name, String password, String type) throws Exception {
        if (name.equals(adminName)) throw new Exception("Cannot create user.. this is the admin's name.");
        for (User user : users)
            if (user.name.equals(name))
                throw new Exception("Cannot create user.. a user with same name already exists.");

        if (!type.matches("[0-1][0-1]"))
            throw new Exception("Invalid type please enter 00 or 01 or 10 or 11 only. ");

        users.add(new User(name, password, type));
    }

    public static void deleteUser(String name) throws Exception {
        if (!currentUser.name.equals(adminName)) throw new Exception("This command can be done by Admin only");

        for (User user : users)
            if (user.name.equals(name)) {
                users.remove(user);
                return;
            }

        throw new Exception("Cannot delete user.. user doesn't exist.");
    }

    public static void login(String name, String password) throws Exception {
        if (name.equals(adminName) && password.equals(adminPassword)){
            currentUser = admin;
            return;
        }
        for (User user : users)
            if (user.name.equals(name) && user.password.equals(password)) {
                currentUser = user;
                return;
            }
        throw new Exception("Invalid username or password");
    }

    public String getName() {
        return name;
    }

    public boolean canCreate() {
        return this.type.charAt(0) == '1';
    }

    public boolean canDelete() {
        return this.type.charAt(1) == '1';
    }

}
