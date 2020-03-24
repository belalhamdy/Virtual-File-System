import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

public class Directory {
    String DirectoryName;
    Directory parent;
    final List<File> subFiles = new ArrayList<>();
    final List<Directory> subDirectories = new ArrayList<>();
    long sizeOnDisk = 0, size = 0;

    Directory(String DirectoryName, Directory parent) {
        this.DirectoryName = DirectoryName;
    }

    void add(File file) {
        subFiles.add(file);
        sizeOnDisk += file.sizeOnDisk;
        size += file.size;
    }

    void add(Directory dir) {
        subDirectories.add(dir);
        sizeOnDisk += dir.sizeOnDisk;
        size += dir.size;
    }

    void changeSize(long differenceOnDisk, long differenceOnSize) {
        sizeOnDisk -= differenceOnDisk;
        size -= differenceOnSize;
        if (parent != null)
            parent.changeSize(differenceOnDisk, differenceOnSize);

    }

    void delete() throws FileSystemException {
        for (File file : subFiles) {
            file.delete();
        }
        for (Directory dir : subDirectories) {
            dir.delete();
        }
        parent.removeFromList(this);
    }

    void removeFromList(File file) {
        subFiles.remove(file);
        changeSize(file.sizeOnDisk, file.size);
    }

    void removeFromList(Directory dir) {
        subDirectories.remove(dir);
        changeSize(dir.sizeOnDisk, dir.size);
    }


}
