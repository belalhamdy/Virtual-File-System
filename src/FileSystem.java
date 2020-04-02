import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSystem {
    private FileSystem() {

    }
    public static boolean NameIsNotValid(String name){
        final char[] notValid = {'[' , ']','#','/','\\'};
        if (name == null || name.length() == 0) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            for (char c : notValid) {
                if (c == ch) {
                    return true;
                }
            }
        }
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

    private static void recurse(BufferedWriter bw, Directory f) throws IOException {
        bw.write("[" + f.getName() + "]");
        bw.newLine();

        for (int i = 0; i < f.getSubDirectoriesCount(); ++i) {
            recurse(bw, f.getSubDirectoryByIndex(i));
        }
        for (int i = 0; i < f.getSubFilesCount(); ++i) {
            File file = f.getSubFileByIndex(i);
            bw.write(file.getName() + "#" + file.size + "#" + file.allocation.toString());
            bw.newLine();
        }

        bw.write("[#" + f.getName() + "]");
        bw.newLine();
    }

    public static void saveVFS(String filePath, Directory root) throws IOException {
        FileOutputStream fstream = new FileOutputStream(filePath);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fstream));
        recurse(bw, root);
        bw.flush();

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
}
