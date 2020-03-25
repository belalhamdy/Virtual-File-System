import java.util.ArrayList;
import java.util.List;

public class ContiguousAllocationDisk extends IDisk {
    // value in disk[i] will answer the question: how much space do i have if i decided to allocate a file from i
    int[] disk;

    ContiguousAllocationDisk(String diskName, long diskSize, long blockSize) {
        super(diskName, diskSize, blockSize);

        disk = new int[diskSizeInBlocks];
        for (int i = 0; i < diskSizeInBlocks; ++i) {
            disk[i] = (diskSizeInBlocks - i);
        }
    }

    int getBestFitIndex(int sizeInBlocks) {
        int minSizeIdx = 0;
        for (int i = 1; i < disk.length; ++i) {
            if (disk[i] >= sizeInBlocks && (disk[i] < disk[minSizeIdx] || disk[minSizeIdx] == 0))
                minSizeIdx = i;
        }
        if (disk[minSizeIdx] < sizeInBlocks) minSizeIdx = -1;

        return minSizeIdx;
    }

    void updateArrayAfterAllocate(int from, int size) {
        for (int i = from - 1; i >= 0; --i) {
            if (disk[i] == 0) break;
            disk[i] -= size;
        }

    }

    @Override
    List<Integer> allocateUsingAlgorithm(int sizeInBlocks) {
        if (sizeInBlocks<=0) return null;

        int minSizeIdx = getBestFitIndex(sizeInBlocks);
        List<Integer> indices = null;
        if (minSizeIdx >= 0) {
            indices = new ArrayList<>();
            for (int i = minSizeIdx; i < (minSizeIdx + sizeInBlocks); ++i) {
                disk[i] = 0;
                indices.add(i);
            }
            updateArrayAfterAllocate(minSizeIdx, indices.size());
        }
        return indices;
    }

    @Override
    void releaseUsingAlgorithm(List<Integer> indices) {
        int from = indices.get(0), to = indices.get(indices.size()-1);

        if (to == this.disk.length - 1) disk[to--] = 1; // if the last block is the last block in the array

        for (int i = to ; i >= 0; --i) {
            if (disk[i] == 0 && i < from) break;
            disk[i] = disk[i+1] + 1;
        }
    }
}
