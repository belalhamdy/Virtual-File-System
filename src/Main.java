import javafx.util.Pair;

import java.util.Scanner;

public class Main {
    static Scanner in = new Scanner(System.in);
    static IDisk disk = new IndexedAllocationDisk("My disk", 1024, 1);
    static Navigator ng = new Navigator();
    static String structureFileName = "data.vfs";
    static String additionalDataFileName = "dataInformation.txt";

    enum Command {
        CreateFile(2),
        CreateFolder(1),
        DeleteFile(1),
        DeleteFolder(1),
        DisplayDiskStatus(0),
        DisplayDiskStructure(0),
        Exit(0);

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
        } catch (Exception ex) {
            System.out.println("Couldn't locate Virtual File System data, a new VFS will be created.");
        }

        input_loop:
        while (true) {
            System.out.print("cmd: ");
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
                        p = ng.seperateLastEntry(ret[1]);
                        FileSystem.createFile(p.getKey(), p.getValue(), sz, disk);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }

                    break;
                case CreateFolder:
                    try {
                        p = ng.seperateLastEntry(ret[1]);
                        FileSystem.createDirectory(p.getKey(), p.getValue());
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;

                case DeleteFile:
                    try {
                        f = ng.navigateToFile(ret[1]);
                        FileSystem.deleteFile(f);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case DeleteFolder:
                    try {
                        d = ng.navigateToDirectory(ret[1]);
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
                default:
                    System.out.println("bad input");
                    break;
            }
        }
        try {
            FileSystem.saveVFS(structureFileName, ng.getRoot());
            FileSystem.saveAdditionalData(additionalDataFileName, disk);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean argumentVerification(String[] ret) {
        String cmd = ret[0];
        try {
            Command v = Command.valueOf(cmd);
            if (ret.length != v.argCnt + 1) {
                System.out.println(cmd + " takes 2 parameters.");
                return false;
            }
            if (ret.length == 3) {
                try {
                    Integer.parseInt(ret[2]);
                } catch (Exception ignored) {
                    System.out.println("size is not integer");
                    return false;
                }
            }
            return true;
        } catch (Exception ignored) {
            System.out.println("Invalid command");
            return false;
        }
    }

}