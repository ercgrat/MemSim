package simulator;

public class L2CacheEntry extends L1CacheEntry {

    private boolean[] ownerArray;

    public L2CacheEntry(int tag, Block.MSIState state, int p, int address) {
        super(tag, state, address);
        ownerArray = new boolean[(int)Math.pow(2,p)];
    }
    
    public boolean[] getOwnerArray() {
        return ownerArray;
    }
    
    public void removeFromOwners(int tileNum){
        ownerArray[tileNum] = false;
    }
    
    public void addToOwners(int tileNum){
        ownerArray[tileNum] = true;
    }
}