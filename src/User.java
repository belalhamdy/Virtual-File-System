import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class User {
    private final static String adminName = "admin";
    private final static String adminPassword = "admin";

    private static User admin = new User(adminName, adminPassword);
    private static User currentUser = admin; // It is said that the default user will be the admin, don't save the admin in user files save only "users" List
    private static List<User> users = new ArrayList<>();
    private static List<String> deletedUsers = new ArrayList<>();

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

        if (!currentUser.name.equals(adminName)) throw new Exception("Cannot create user.. Admin only can create.");
        if (name.equals(adminName)) throw new Exception("Cannot create user.. this is the admin's name.");

        if (FileSystem.hasInvalidCharacters(name) || FileSystem.hasInvalidCharacters(password)) throw new Exception("Invalid characters encountered in name or password.");

        if (userExists(name))
            throw new Exception("Cannot create user.. a user with same name already exists.");
        boolean warning = deletedUsers.stream().anyMatch(curr -> curr.equals(name));

        users.add(new User(name, password));
        if (warning)
            throw new Exception("Warning.. a user with same name is deleted recently.. The previous grants will be assigned to the new user with new password.");
    }

    public static void deleteUser(String name) throws Exception {
        if (!currentUser.name.equals(adminName)) throw new Exception("This command can be done by Admin only.");
        if (name.equals(adminName))  throw new Exception("Admin cannot be deleted.");

        if (!users.removeIf(user -> user.name.equals(name)))
            throw new Exception("Cannot delete user.. user doesn't exist.");
        deletedUsers.add(name);
    }

    public static void login(String name, String password) throws Exception {
        String currName = name.toLowerCase();
        if (currName.equals(getCurrentUser().getName())) throw new Exception("This user already logged in.");
        if (currName.equals(adminName) && password.equals(adminPassword)) {
            currentUser = admin;
            return;
        }
        for (User user : users)
            if (user.name.equals(currName) && user.password.equals(password)) {
                currentUser = user;
                return;
            }
        throw new Exception("Invalid username or password.");
    }

    public static User getAdmin() {
        return admin;
    }

    public static boolean userExists(String name) {
        return users.stream().anyMatch((user) -> user.name.equals(name));
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString(){
        return getName() + "#" + getPassword();
    }
    public static User fromString(String str){
        String[] split = str.split("#");
        return new User(split[0],split[1]);
    }

    public boolean isAdmin() {
        return getName().equals(adminName);
    }
}
