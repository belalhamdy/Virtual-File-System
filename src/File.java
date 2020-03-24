import java.nio.file.FileSystemException;

public class File {
    String FileName;
    Allocation Allocation;
    long sizeOnDisk;
    long size;
    Directory parent;
    File(String FileName,long Size,Allocation Allocation,Directory parent){
        this.FileName = FileName;
        this.size = Size;
        this.Allocation = Allocation;
        this.sizeOnDisk = Allocation.getAllocationSize();
        this.parent = parent;
    }
    void delete() {
        Allocation.release();
        parent.removeFromList(this);
    }

}
