import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class IDisk {
    private String diskName;
    final private long diskSize, blockSize;
    private long nEmptyBlocks;
    private boolean[] diskSpace; // array that will carry disk data use in your class anyway to fill this array, DONT FILL THIS ARRAY

    final int diskSizeInBlocks;

    IDisk(String diskName, int diskSizeInBlocks, long blockSize) {
        this.diskName = diskName;
        this.diskSize = diskSizeInBlocks * blockSize;
        this.blockSize = blockSize;

        this.diskSizeInBlocks = diskSizeInBlocks;
        this.nEmptyBlocks = diskSizeInBlocks;
        this.diskSpace = new boolean[diskSizeInBlocks];
    }

    boolean isBlockFree(int index){
        return diskSpace[index];
    }
    private void fillDiskSpace(int index) {
        --nEmptyBlocks;
        diskSpace[index] = true;
    }

    private void releaseDiskSpace(int index) {
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
        long sizeInBlocks = (size + blockSize - 1) / blockSize;
        List<Integer> indices = allocateUsingAlgorithm(sizeInBlocks);

        if (indices != null)
            for (Integer blockIdx : indices) fillDiskSpace(blockIdx);

        return new Allocation(indices, blockSize, this);
    }

    void release(Allocation allocation) {
        List<Integer> indices = allocation.Blocks;
        for (Integer blockIdx : indices)
            releaseDiskSpace(blockIdx);

        releaseUsingAlgorithm(indices);
    }

    abstract List<Integer> allocateUsingAlgorithm(long sizeInBlocks);

    abstract void releaseUsingAlgorithm(List<Integer> indices);
}
