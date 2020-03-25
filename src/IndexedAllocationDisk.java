import java.util.List;

public class IndexedAllocationDisk extends IDisk {

    IndexedAllocationDisk(String diskName, long diskSize, long blockSize) {
        super(diskName, diskSize, blockSize);
    }

    @Override
    List<Integer> allocateUsingAlgorithm(int sizeInBlocks) {
        return null;
    }

    @Override
    void releaseUsingAlgorithm(List<Integer> indices) {

    }
}
