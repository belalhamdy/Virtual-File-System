import javafx.util.Pair;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;


enum GrantType {
    Create, Delete, AllAccess
}

public class Navigator {
    final private Directory root;

    Navigator() {
        this.root = new Directory("root");
    }

    Pair<Directory, String> separateLastEntry(String path, GrantType grantType) throws Exception {

        Path p = Paths.get(path);
        Directory cur = root;
        if (!p.getName(0).toString().equals("root")) {
            checkGrant(cur, grantType); // to remove grant root remove this and line 176 in filesystem
            return null;
        }
        for (int i = 1; i < p.getNameCount() - 1; i++) {
            cur = cur.getSubDirectoryByName(p.getName(i).toString());
            if (cur == null) throw new FileNotFoundException(p.getName(i).toString() + " directory does not exit.");
        }
        checkGrant(cur, grantType);
        return new Pair<>(cur, p.getFileName().toString());
    }

    Directory navigateToDirectory(String path, GrantType grantType) throws Exception {
        Pair<Directory, String> ret = separateLastEntry(path, grantType);
        Directory dir = ret.getKey().getSubDirectoryByName(ret.getValue());

        if (dir == null) throw new FileNotFoundException(ret.getValue() + " directory does not exit.");
        return dir;
    }

    File navigateToFile(String path, GrantType grantType) throws Exception {
        Pair<Directory, String> ret = separateLastEntry(path, grantType);
        File file = ret.getKey().getSubFileByName(ret.getValue());
        if (file == null) throw new FileNotFoundException(ret.getValue() + " file does not exit.");
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

    private void checkGrant(Directory dir, GrantType grantType) throws Exception {
        if (User.getCurrentUser().isAdmin()) return;

        String username = User.getCurrentUser().getName();
        if (grantType == GrantType.Create && !dir.canCreate(username))
            throw new Exception("Invalid Grant.. Current user (" + username + ") cannot create in this directory.");
        if (grantType == GrantType.Delete && !dir.canDelete(username))
            throw new Exception("Invalid Grant.. Current user (" + username + ") cannot delete from this directory.");
    }

    public void printVirtualFileSystem(PrintStream out) {
        printTree(out, "", root, true);
    }
}
