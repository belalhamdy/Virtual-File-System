import javafx.util.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Navigator {
    final private Directory root;

    Navigator() {
        this.root = new Directory("", null);
    }

    Pair<Directory, String> seperateLastEntry(String path) {
        Path p = Paths.get(path);
        Directory cur = root;
        if (!p.getName(0).toString().equals("root")) return null;
        for (int i = 1; i < p.getNameCount() - 1; i++) {
            cur = cur.getSubDirectoryByName(p.getName(i).toString());
            if (cur == null) break;
        }
        return new Pair<>(cur, p.getFileName().toString());
    }

    Directory navigateToDirectory(String path) {
        Pair<Directory, String> ret = seperateLastEntry(path);
        return ret.getKey().getSubDirectoryByName(ret.getValue());
    }

    File navigateToFile(String path) {
        Pair<Directory, String> ret = seperateLastEntry(path);
        return ret.getKey().getSubFileByName(ret.getValue());
    }

    Directory getRoot() {
        return root;
    }
}
