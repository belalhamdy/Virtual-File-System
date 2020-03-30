import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Allocation {
    private List<Integer> Blocks;
    private List<Pair<Integer, Integer>> ranges;
    private long AllocationSize;
    private IDisk disk;

    Allocation(List<Integer> Blocks, long BlockSize, IDisk disk) {
        this.Blocks = Blocks;
        this.AllocationSize = this.Blocks.size() * BlockSize;
        this.disk = disk;

        generateRangedBlocks();
    }

    private void generateRangedBlocks() {
        ranges = new ArrayList<>();
        Blocks.sort(Integer::compareTo);
        int start = Blocks.get(0);
        int sz = 1;
        for (int i = 1; i < Blocks.size(); i++) {
            int current = Blocks.get(i);
            if (current == start + sz)
                ++sz;
            else {
                ranges.add(new Pair<>(start, start + sz - 1));
                start = current;
                sz = 1;
            }
        }
        ranges.add(new Pair<>(start, start + sz - 1));
    }

    void release() {
        disk.release(this);
    }

    long getAllocationSize() {
        return AllocationSize;
    }

    @Override
    public String toString() {
        return this.ranges
                .stream()
                .map(p -> {
                    if (p.getValue().equals(p.getKey())) return p.getKey().toString();
                    else return p.getKey().toString() + "-" + p.getValue().toString();
                })
                .collect(Collectors.joining(", "));
    }

    public List<Integer> getIndividualBlocks() {
        return this.Blocks;
    }

    public List<Pair<Integer, Integer>> getRangedBlocks() {
        return this.ranges;
    }
}
