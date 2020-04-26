import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSystem {
    private FileSystem() {

    }

    public static boolean hasInvalidCharacters(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        final char[] notValid = {'[', ']', '#', '/', '\\'};
        for (char s : notValid)
            if (str.indexOf(s) != -1) return true;

        return false;
    }

    public static boolean NameIsNotValid(String name, boolean isDirectory) {
        if (hasInvalidCharacters(name)) return true;

        if (isDirectory) return name.contains(".");
        return false;

    }

    public static void createFile(Directory parent, String name, long size, IDisk disk) throws Exception {
        File f = new File(name, size, disk.allocate(size), parent);
        parent.add(f);
    }

    public static void createFile(Directory parent, String name, long size, Allocation alloc) throws Exception {
        File f = new File(name, size, alloc, parent);
        parent.add(f);
    }

    public static void deleteFile(File file) {
        file.delete();
    }

    public static Directory createDirectory(Directory parent, String name) throws Exception {
        Directory p = new Directory(name, parent);
        parent.add(p);
        return p;
    }

    public static void deleteDirectory(Directory dir) {
        dir.delete();
    }

    private static void recurseSaveVFS(BufferedWriter bw, Directory f) throws IOException {
        bw.write("[" + f.getName() + "]");
        bw.newLine();

        for (int i = 0; i < f.getSubDirectoriesCount(); ++i) {
            recurseSaveVFS(bw, f.getSubDirectoryByIndex(i));
        }
        for (int i = 0; i < f.getSubFilesCount(); ++i) {
            File file = f.getSubFileByIndex(i);
            bw.write(file.getName() + "#" + file.size + "#" + file.allocation.toString());
            bw.newLine();
        }

        bw.write("[#" + f.getName() + "]");
        bw.newLine();
    }

    private static void recurseSaveCapabilities(BufferedWriter bw, Directory f, String pathThusFar) throws IOException {
        String myPath = pathThusFar + f.getName();
        if (!f.permissions.isEmpty()) {
            bw.write(myPath);
            for (Permission v : f.permissions) {
                bw.write("#");
                bw.write(v.toString());
            }
            bw.newLine();
        }

        for (int i = 0; i < f.getSubDirectoriesCount(); ++i) {
            recurseSaveCapabilities(bw, f.getSubDirectoryByIndex(i), myPath);
        }
    }

    public static void saveVFS(String filePath, Directory root) throws IOException {
        FileOutputStream fstream = new FileOutputStream(filePath);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fstream));
        recurseSaveVFS(bw, root);
        bw.flush();

        fstream.close();

    }

    public static void saveCapabilities(String filePath, Directory root) throws IOException {
        FileOutputStream fstream = new FileOutputStream(filePath);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fstream));
        recurseSaveCapabilities(bw, root, "");
        bw.flush();

        fstream.close();
    }

    public static void saveAdditionalData(String filePath, IDisk disk) throws Exception {
        FileOutputStream fstream = new FileOutputStream(filePath);
        StringBuilder out = new StringBuilder("Free Blocks: " + disk.getEmptyBlocks() + "\nAllocated Blocks: " + disk.getAllocatedBlocks() + "\nAllocated Blocks: ");
        for (boolean item : disk.getDiskSpace()) {
            if (item) out.append('1');
            else out.append('0');
        }
        fstream.write(out.toString().getBytes());

        fstream.close();

    }

    public static void loadVFS(String filePath, Directory root, IDisk dsk) throws Exception {

        FileInputStream fstream = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        Pattern dirPat = Pattern.compile("\\[(.+)]");
        Pattern filePat = Pattern.compile("(.+)#([0-9]+)#(.+)");

        String strLine;
        root.delete();

        Directory currentDir = root;
        boolean first = false;
        while ((strLine = br.readLine()) != null) {
            Matcher mat = dirPat.matcher(strLine);
            if (mat.matches()) {
                if (!first) {
                    first = true;
                    continue;
                }
                String name = mat.group(1);
                if (name.charAt(0) == '#') {
                    currentDir = currentDir.getParent();
                } else {
                    currentDir = FileSystem.createDirectory(currentDir, name);
                }
            } else {
                mat = filePat.matcher(strLine);
                if (mat.matches()) {
                    String name = mat.group(1);
                    long size = Long.parseLong(mat.group(2));
                    String allocationStr = mat.group(3);
                    FileSystem.createFile(currentDir, name, size, dsk.fromString(allocationStr, dsk.getBlockSize()));
                }
            }
        }

        fstream.close();
    }

    public static void loadCapabilities(String filePath, Navigator nv) throws Exception {
        FileInputStream fstream = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        Pattern pat = Pattern.compile("([^#]*)#(.*)");

        String strLine;

        while ((strLine = br.readLine()) != null) {
            Matcher mat = pat.matcher(strLine);
            if (mat.matches()) {
                String path = mat.group(1);
                String permissions = mat.group(2);
                Directory d = nv.navigateToDirectory(path);

                for (String curr : permissions.split("#"))
                    d.grant(Permission.fromString(curr));

            }
        }

        fstream.close();
    }

    public static void saveUsers(String filePath) throws IOException{
        FileOutputStream fstream = new FileOutputStream(filePath);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fstream));
        bw.write(User.getAdmin().toString());
        for(User v : User.getUsers()){
            bw.write(v.toString());
            bw.newLine();
        }
        bw.flush();
        fstream.close();
    }

    public static void loadUsers(String filePath) throws IOException{
        FileInputStream fstream = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        User.getUsers().clear();
        while ((strLine = br.readLine()) != null) {
            User.getUsers().add(User.fromString(strLine));
        }

        fstream.close();
    }
}
