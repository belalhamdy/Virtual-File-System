public class Permission {
    public String username;
    String type;

    Permission(String user, String type) throws Exception {
        this.username = user;
        this.type = type;
        if (!type.matches("[0-1]{2}"))
            throw new Exception("Invalid type please enter 00 or 01 or 10 or 11 only. ");

    }

    public boolean canCreate() {
        return this.type.charAt(0) == '1';
    }

    public boolean canDelete() {
        return this.type.charAt(1) == '1';
    }
}
