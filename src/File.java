public class File {
    String fileName;
    Allocation allocation;
    long sizeOnDisk;
    long size;
    Directory parent;
    File(String FileName,long Size,Allocation Allocation,Directory parent){
        this.fileName = FileName;
        this.size = Size;
        this.allocation = Allocation;
        this.sizeOnDisk = Allocation.getAllocationSize();
        this.parent = parent;
    }
    void delete() {
        allocation.release();
        parent.removeFromList(this);
    }

    public String getName() {
        return fileName;
    }
    @Override
    public String toString(){
        return fileName + " " + size + "KB " + allocation.toString();
    }
}
