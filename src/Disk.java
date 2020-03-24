public abstract class Disk {
    long diskSize,blockSize;

    Disk(long diskSize,long blockSize){
        this.diskSize = diskSize;
        this.blockSize =blockSize;
    }

    abstract Allocation allocate(File file);
    abstract void release(Allocation allocation);
}
