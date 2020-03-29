public class FileSystem {
    final private IDisk disk;

    FileSystem(IDisk disk) {
        this.disk = disk;
    }

    void createFile(Directory parent, String name, long size) {
        File f = new File(name, size, disk.allocate(size), parent);
        parent.add(f);
    }

    void deleteFile(File file) {
        file.delete();
    }

    void createDirectory(Directory parent, String name) {
        Directory p = new Directory(name, parent);
        parent.add(p);
    }

    void deleteDirectory(Directory dir) {
        dir.delete();
    }

    IDisk getDisk() {
        return disk;
    }
}
