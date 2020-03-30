import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class IDisk {
    private String diskName;
    final private long diskSize;
    final private long blockSize;
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
        List<Integer> indices = allocation.getIndividualBlocks();
        for (Integer blockIdx : indices)
            releaseDiskSpace(blockIdx);

        releaseUsingAlgorithm(indices);
    }

    public Allocation fromString(String s, long blockSize) {
        List<Integer> lst = new ArrayList<>();

        Pattern pat = Pattern.compile("([0-9]+) *- *([0-9]+)");
        for (String sub : s.split(" *, *")) {
            Matcher match = pat.matcher(sub);
            if (match.matches()) {
                int start = Integer.parseInt(match.group(1));
                int end = Integer.parseInt(match.group(2));
                for (int i = start; i <= end; i++) {
                    lst.add(i);
                    fillDiskSpace(i);
                }
            } else {
                int idx = Integer.parseInt(sub);
                lst.add(idx);
                fillDiskSpace(idx);
            }
        }
        return new Allocation(lst, blockSize, this);
    }

    abstract List<Integer> allocateUsingAlgorithm(long sizeInBlocks);

    abstract void releaseUsingAlgorithm(List<Integer> indices);

    public long getBlockSize() {
        return blockSize;
    }
}
