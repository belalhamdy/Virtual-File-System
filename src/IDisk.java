import java.util.List;

public abstract class IDisk {
    String diskName;
    long diskSize, blockSize;
    int diskSizeInBlocks, nEmptyBlocks;
    private boolean[] diskSpace; // array that will carry disk data use in your class anyway to fill this array, DONT FILL THIS ARRAY

    IDisk(String diskName, long diskSize, long blockSize) {
        this.diskName = diskName;
        this.diskSize = diskSize;
        this.blockSize = blockSize;

        this.diskSizeInBlocks = (int) Math.ceil(1.0 * diskSize / blockSize);
        this.nEmptyBlocks = diskSizeInBlocks;
        this.diskSpace = new boolean[diskSizeInBlocks];
    }

    void fillDiskSpace(int index) {
        --nEmptyBlocks;
        diskSpace[index] = true;
    }

    void releaseDiskSpace(int index) {
        ++nEmptyBlocks;
        diskSpace[index] = false;
    }

    long getEmptySpace() {
        return getEmptyBlocks() * blockSize;
    }

    long getAllocatedSpace() {
        return getAllocatedBlocks() * blockSize;
    }

    long getEmptyBlocks() {
        return nEmptyBlocks;
    }

    long getAllocatedBlocks() {
        return (diskSizeInBlocks - nEmptyBlocks);
    }

    Allocation allocate(long size) {
        int sizeInBlocks = (int) Math.ceil(1.0 * diskSize / blockSize);
        List<Integer> indices = allocateUsingAlgorithm(sizeInBlocks);

        if(indices != null)
            for (Integer blockIdx : indices) fillDiskSpace(blockIdx);

        return new Allocation(indices,blockSize, this);
    }

    void release(Allocation allocation){
        List<Integer> indices = allocation.Blocks;
        for (Integer blockIdx : indices)
            releaseDiskSpace(blockIdx);

        releaseUsingAlgorithm(indices);
    }

    abstract List<Integer> allocateUsingAlgorithm(int sizeInBlocks);
    abstract void releaseUsingAlgorithm(List<Integer> indices);
}
