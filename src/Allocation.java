import java.util.List;

public class Allocation {
    List<Integer> Blocks;
    long AllocationSize;
    IDisk disk;
    Allocation(List<Integer> Blocks, long BlockSize, IDisk disk) {
        this.Blocks = Blocks;
        this.AllocationSize = this.Blocks.size() * BlockSize;
        this.disk = disk;
    }
    void release(){
        disk.release(this);
    }
    long getAllocationSize() {
        return AllocationSize;
    }
}
