import javafx.util.Pair;

import java.util.Scanner;

public class Main {
    static Scanner in = new Scanner(System.in);
    static IDisk disk = new IndexedAllocationDisk("My disk", 1024, 1);
    static Navigator ng = new Navigator();
    final static String structureFileName = "data.vfs";
    final static String additionalDataFileName = "dataInformation.txt";
    final static String usersFileName = "users.txt";
    final static String capabilitiesFileName = "capabilities.txt";

    enum Command {
        CreateFile(2),
        CreateFolder(1),
        DeleteFile(1),
        DeleteFolder(1),
        DisplayDiskStatus(0),
        DisplayDiskStructure(0),
        Exit(0),

        CreateUser(2),
        Grant(3),
        Login(2),
        DeleteUser(1),
        TellUser(0);


        private int argCnt;

        Command(int argCnt) {
            this.argCnt = argCnt;
        }

        public int getArgCnt() {
            return argCnt;
        }
    }

    public static void main(String[] args) {
        try {
            FileSystem.loadVFS(structureFileName, ng.getRoot(), disk);
            FileSystem.loadUsers(usersFileName);
            FileSystem.loadCapabilities(capabilitiesFileName, ng);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Couldn't locate Virtual File System data, missing files will be created.");
        }

        input_loop:
        while (true) {
            System.out.print("cmd $" + User.getCurrentUser().getName() + ": ");
            String input = in.nextLine();
            String[] ret = input.split(" ");

            if (!argumentVerification(ret)) continue;

            Command cmd = Command.valueOf(ret[0]);

            Pair<Directory, String> p;
            Directory d;
            File f;

            switch (cmd) {
                case CreateFile:
                    int sz = Integer.parseInt(ret[2]);
                    try {
                        p = ng.separateLastEntry(ret[1]);
                        FileSystem.createFile(p.getKey(), p.getValue(), sz, disk);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }

                    break;
                case CreateFolder:
                    try {
                        p = ng.separateLastEntry(ret[1]);
                        FileSystem.createDirectory(p.getKey(), p.getValue());
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;

                case DeleteFile:
                    try {
                        f = ng.navigateToFile(ret[1], GrantType.Delete);
                        FileSystem.deleteFile(f);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case DeleteFolder:
                    try {
                        d = ng.navigateToDirectory(ret[1], GrantType.Delete);
                        FileSystem.deleteDirectory(d);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case DisplayDiskStatus:
                    System.out.println("Allocated Blocks: " + disk.getAllocatedBlocks());
                    System.out.println("Allocated Spaces: " + disk.getAllocatedSpace());
                    System.out.println("Free Blocks: " + disk.getEmptyBlocks());
                    System.out.println("Free Spaces: " + disk.getEmptySpace());
                    break;
                case DisplayDiskStructure:
                    ng.printVirtualFileSystem(System.out);
                    break;
                case Exit:
                    break input_loop;

                case CreateUser:
                    try {
                        User.createUser(ret[1], ret[2]);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case Grant:
                    try {
                        if (!User.getCurrentUser().isAdmin()) {
                            System.out.println("Only admin can execute this command.");
                            break;
                        }
                        if (!User.userExists(ret[1])) {
                            System.out.println("User " + ret[1] + " doesn't exist.");
                            break;
                        }
                        if (ret[2].equals("root")) d = ng.getRoot();
                        else d = ng.navigateToDirectory(ret[2], GrantType.AllAccess);
                        if(d.grant(new Permission(ret[1], ret[3])))
                            throw new Exception("Previous grant is replaced with new one.");
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case Login:
                    try {
                        User.login(ret[1], ret[2]);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case DeleteUser:
                    try {
                        User.deleteUser(ret[1]);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case TellUser:
                    System.out.println("Current Username is: " + User.getCurrentUser().getName());
                    break;
                default:
                    System.out.println("bad input");
                    break;
            }
        }
        try {
            FileSystem.saveVFS(structureFileName, ng.getRoot());
            FileSystem.saveAdditionalData(additionalDataFileName, disk);
            FileSystem.saveUsers(usersFileName);
            FileSystem.saveCapabilities(capabilitiesFileName, ng.getRoot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean argumentVerification(String[] ret) {
        String cmdString = ret[0];
        try {
            Command cmdVal = Command.valueOf(cmdString);
            if (ret.length != cmdVal.argCnt + 1) {
                if(cmdVal.argCnt == 1) System.out.println(cmdString + " takes only 1 parameter.");
                else System.out.println(cmdString + " takes " + cmdVal.argCnt + " parameters.");
                return false;
            }
            if (ret.length == 3 && cmdVal != Command.CreateUser && cmdVal != Command.Login) {
                try {
                    Integer.parseInt(ret[2]);
                } catch (Exception ignored) {
                    System.out.println("third argument must be an integer.");
                    return false;
                }
            }
            return true;
        } catch (Exception ignored) {
            System.out.println("Invalid command.");
            return false;
        }
    }

}