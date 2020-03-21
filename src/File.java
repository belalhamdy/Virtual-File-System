public class File {
    String FileName;
    Allocation Allocation;
    long SizeOnDisk;
    long Size;
    File(String FileName,long Size,Allocation Allocation){
        this.FileName = FileName;
        this.Size = Size;
        this.Allocation = Allocation;
        this.SizeOnDisk = Allocation.getAllocationSize();
    }

}
