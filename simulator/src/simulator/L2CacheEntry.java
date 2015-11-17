package simulator;

public class L2CacheEntry extends L1CacheEntry {

    private boolean[] ownerArray;

    public L2CacheEntry(long tag, Block.MSIState state, long p, long address) {
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
    
    public void resetOwners(long p){
	for(int i =0; i<(int)Math.pow(2,p);i++)
	    ownerArray[i] = false;
    }
}