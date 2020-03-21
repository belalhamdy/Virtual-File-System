import java.util.List;

public class Allocation {
    List<Integer> Blocks;
    long AllocationSize;

    Allocation(List<Integer> Blocks, long BlockSize) {
        this.Blocks = Blocks;
        this.AllocationSize = this.Blocks.size() * BlockSize;
    }

    long getAllocationSize() {
        return AllocationSize;
    }
}
