import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

public class Directory {
    String DirectoryName;
    final List<File> SubFiles = new ArrayList<>();
    final List<Directory> SubDirectories = new ArrayList<>();
    long SizeOnDisk = 0, Size = 0;

    Directory(String DirectoryName) {
        this.DirectoryName = DirectoryName;
    }

    void add(File file) {
        SubFiles.add(file);
        SizeOnDisk += file.SizeOnDisk;
        Size += file.Size;
    }

    void add(Directory dir) {
        SubDirectories.add(dir);
        SizeOnDisk += dir.SizeOnDisk;
        Size += dir.Size;
    }

    void remove(File file) throws FileSystemException {
        if (SubFiles.remove(file)) {
            SizeOnDisk -= file.SizeOnDisk;
            Size -= file.Size;
        }
        else throw new FileSystemException("File does not exist in directory");
    }

    void remove(Directory dir) throws FileSystemException {
        if (SubDirectories.remove(dir)) {
            SizeOnDisk -= dir.SizeOnDisk;
            Size -= dir.Size;
        }
        else throw new FileSystemException("Directory does not exist in directory");
    }
}
