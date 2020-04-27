import java.util.ArrayList;
import java.util.List;

public class Directory {
    private String directoryName;
    private Directory parent;
    private final List<File> subFiles = new ArrayList<>();
    private final List<Directory> subDirectories = new ArrayList<>();
    private long sizeOnDisk = 0, size = 0;

    List<Permission> permissions = new ArrayList<>();

    Directory(String directoryName) {
        this.directoryName = directoryName;
        this.parent = null;
    }

    Directory(String directoryName, Directory parent) throws Exception {
        if (FileSystem.NameIsNotValid(directoryName, true)) throw new Exception("Invalid Directory Name.");

        this.parent = parent;
        this.directoryName = directoryName;
    }

    // return if there any previous permissions
    boolean grant(Permission permission) throws Exception {
        boolean ret = permissions.removeIf((p) -> p.username.equals(permission.username));

        if (parent == null && permission.canDelete()) throw new Exception("Grant Rejected.. No one can delete root.");
        permissions.add(permission);

        return ret;
    }

    // If you want the permissions not inherited remove lines 36-38, 44-46 all inclusive
    boolean canCreate(String username) {
        boolean canCreateHere = permissions.stream().anyMatch(permission -> permission.username.equals(username) && permission.canCreate());
        if (parent != null)
            return (canCreateHere | parent.canCreate(username));
        else
            return canCreateHere;
    }

    boolean canDelete(String username) {
        boolean canDeleteHere = permissions.stream().anyMatch(permission -> permission.username.equals(username) && permission.canDelete());
        if (parent != null)
            return (canDeleteHere | parent.canDelete(username));
        else
            return canDeleteHere;
    }

    void add(File file) throws Exception {
        for (File f : subFiles) {
            if (f.fileName.equals(file.fileName))
                throw new Exception("Error There is another a file with the same name in " + directoryName + ".");
        }
        subFiles.add(file);
        sizeOnDisk += file.sizeOnDisk;
        size += file.size;
    }

    void add(Directory dir) throws Exception {
        for (Directory d : subDirectories) {
            if (d.directoryName.equals(dir.directoryName))
                throw new Exception("Error There is another a folder with the same name in " + directoryName + ".");
        }

        subDirectories.add(dir);
        sizeOnDisk += dir.sizeOnDisk;
        size += dir.size;
    }

    private void changeSize(long differenceOnDisk, long differenceOnSize) {
        sizeOnDisk -= differenceOnDisk;
        size -= differenceOnSize;
        if (parent != null)
            parent.changeSize(differenceOnDisk, differenceOnSize);

    }

    void delete() {
        for (File file : subFiles) {
            file.delete();
        }
        for (Directory dir : subDirectories) {
            dir.delete();
        }
        if (parent != null)
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

    public int getSubDirectoriesCount() {
        return subDirectories.size();
    }

    public int getSubFilesCount() {
        return subFiles.size();
    }

    public Directory getSubDirectoryByIndex(int index) {
        return subDirectories.get(index);
    }

    public File getSubFileByIndex(int index) {
        return subFiles.get(index);
    }


    public Directory getSubDirectoryByName(String name) {
        return subDirectories
                .stream()
                .filter(dir -> dir.directoryName.toLowerCase().equals(name.toLowerCase()))
                .findAny()
                .orElse(null);
    }

    public File getSubFileByName(String name) {
        return subFiles
                .stream()
                .filter(file -> file.fileName.toLowerCase().equals(name.toLowerCase()))
                .findAny()
                .orElse(null);
    }

    public String getName() {
        return directoryName;
    }

    @Override
    public String toString() {
        return directoryName;
    }

    public Directory getParent() {
        return parent;
    }
}
