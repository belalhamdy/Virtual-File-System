import javafx.util.Pair;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Navigator {
    final private Directory root;

    Navigator() {
        this.root = new Directory("root", null);
    }

    Pair<Directory, String> seperateLastEntry(String path) throws FileNotFoundException {
        Path p = Paths.get(path);
        Directory cur = root;
        if (!p.getName(0).toString().equals("root")) return null;
        for (int i = 1; i < p.getNameCount() - 1; i++) {
            cur = cur.getSubDirectoryByName(p.getName(i).toString());
            if (cur == null) throw new FileNotFoundException(p.getName(i).toString() + " directory does not exit");
        }
        return new Pair<>(cur, p.getFileName().toString());
    }

    Directory navigateToDirectory(String path) throws FileNotFoundException {
        Pair<Directory, String> ret = seperateLastEntry(path);
        Directory dir = ret.getKey().getSubDirectoryByName(ret.getValue());
        if (dir == null) throw new FileNotFoundException(ret.getValue() + " directory does not exit");
        return dir;
    }

    File navigateToFile(String path) throws FileNotFoundException {
        Pair<Directory, String> ret = seperateLastEntry(path);
        File file = ret.getKey().getSubFileByName(ret.getValue());
        if (file == null) throw new FileNotFoundException(ret.getValue() + " file does not exit");
        return file;
    }

    public Directory getRoot() {
        return root;
    }

    private void printTree(PrintStream out, String prefix, File node, boolean lastChild) {
        out.print(prefix);

        out.print(lastChild ? "└──" : "├──");

        out.println(node.toString());
    }

    private void printTree(PrintStream out, String prefix, Directory node, boolean lastChild) {
        out.print(prefix);

        out.print(lastChild ? "└──" : "├──");

        out.println(node.toString());

        int overallCount = node.getSubFilesCount() + node.getSubDirectoriesCount();

        for (int i = 0; i < node.getSubDirectoriesCount(); i++) {
            --overallCount;
            printTree(out, prefix + (lastChild ? "    " : "│   "), node.getSubDirectoryByIndex(i), overallCount == 0);
        }
        for (int i = 0; i < node.getSubFilesCount(); i++) {
            --overallCount;
            printTree(out, prefix + (lastChild ? "    " : "│   "), node.getSubFileByIndex(i), overallCount == 0);
        }
    }

    public void printVirtualFileSystem(PrintStream out) {
        printTree(out, "", root, true);
    }
}
