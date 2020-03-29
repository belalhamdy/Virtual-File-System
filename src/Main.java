import javafx.util.Pair;

import java.util.Scanner;

public class Main {
    static Scanner in = new Scanner(System.in);
    static FileSystem fs = new FileSystem(new ContiguousAllocationDisk("My disk", 1024, 1));
    static Navigator ng = new Navigator();

    /*
        CreateFile root/file.txt 100
        CreateFolder root/folder1
        DeleteFile root/folder1/file.txt
        DeleteFolder root/folder1
        DisplayDiskStatus                   //(Empty space, allocated space, empty blocks, allocated blocks)
        DisplayDiskStructure	            //tree structure of disk
        Exit
    */
    public static void main(String[] args) {
        while (true) {
            System.out.print("cmd: ");
            String input = in.nextLine();
            String[] ret = input.split(" ");

            String cmd = ret[0];

            argumentVerification(ret, cmd);
            Pair<Directory, String> p;
            Directory d;
            File f;

            switch (cmd) {
                case "CreateFile":
                    int sz = Integer.parseInt(ret[2]);
                    p = ng.seperateLastEntry(ret[1]);
                    fs.createFile(p.getKey(), p.getValue(), sz);

                    break;
                case "CreateFolder":
                    p = ng.seperateLastEntry(ret[1]);
                    fs.createDirectory(p.getKey(), p.getValue());
                    break;

                case "DeleteFile":
                    f = ng.navigateToFile(ret[1]);
                    fs.deleteFile(f);

                    break;
                case "DeleteFolder":
                    d = ng.navigateToDirectory(ret[1]);
                    fs.deleteDirectory(d);

                    break;
                case "DisplayDiskStatus":
                    System.out.println("Allocated Blocks: " + fs.getDisk().getAllocatedBlocks());
                    System.out.println("Allocated Spaces: " + fs.getDisk().getAllocatedSpace());
                    System.out.println("Free Blocks: " + fs.getDisk().getEmptyBlocks());
                    System.out.println("Free Spaces: " + fs.getDisk().getEmptySpace());
                    break;
                case "DisplayDiskStructure":
                    break;
                case "Exit":
                    return;
                default:
                    System.out.println("bad input");
                    break;
            }
        }
    }

    private static void argumentVerification(String[] ret, String cmd) {
        switch (cmd) {
            case "CreateFile":
                if (ret.length != 3) {
                    System.out.println(cmd + " takes 2 parameters.");
                    break;
                }
                try {
                    Integer.parseInt(ret[2]);
                } catch (Exception ignored) {
                    System.out.println("size is not integer");
                }
                break;
            case "CreateFolder":
            case "DeleteFile":
            case "DeleteFolder":
                if (ret.length != 2) {
                    System.out.println(cmd + " takes one parameter.");
                    break;
                }
                break;
            case "DisplayDiskStatus":
            case "DisplayDiskStructure":
            case "Exit":
                if (ret.length != 1) {
                    System.out.println(cmd + " takes no parameters.");
                    break;
                }
                break;
        }
    }
}