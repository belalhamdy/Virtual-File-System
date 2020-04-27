public class File {
    String fileName;
    Allocation allocation;
    long sizeOnDisk;
    long size;
    Directory parent;
    File(String fileName,long size,Allocation allocation,Directory parent) throws Exception {
        if (FileSystem.NameIsNotValid(fileName,false)) throw new Exception("Invalid File Name.");

        this.fileName = fileName;
        this.size = size;
        this.allocation = allocation;
        this.sizeOnDisk = allocation.getAllocationSize();
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
