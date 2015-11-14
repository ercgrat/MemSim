package simulator;

public class L2CacheEntry extends L1CacheEntry {

    private boolean[] ownerArray;

    public L2CacheEntry(int tag, Block.MSIState state, int p) {
        super(tag, state);
        ownerArray = new boolean[(int)Math.pow(2,p)];
    }
    
    public boolean[] getOwnerArray() {
        return ownerArray;
    }
}