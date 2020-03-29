import java.util.ArrayList;
import java.util.List;

public class IndexedAllocationDisk extends IDisk {

    IndexedAllocationDisk(String diskName, int diskSizeinBlocks, long blockSize) {
        super(diskName, diskSizeinBlocks, blockSize);
    }

    @Override
    List<Integer> allocateUsingAlgorithm(long sizeInBlocks) {
        if (sizeInBlocks < getEmptyBlocks()) return null;

        List<Integer> ret = new ArrayList<>();
        for (int i = 0; sizeInBlocks > 0 && i < diskSizeInBlocks; i++) {
            if (isBlockFree(i)) {
                ret.add(i);
                --sizeInBlocks;
            }
        }
        return ret;
    }

    @Override
    void releaseUsingAlgorithm(List<Integer> indices) {
        //no need to do anything here
    }
}
