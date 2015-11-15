package simulator;

public class Access {

    private long cycle, address;
    boolean read;
    private Block.MSIState state;
    
    public Access(long cycle, long address, boolean read) {
        this.cycle = cycle;
        this.address = address;
        this.read = read;
    }

    public long getCycle() {
        return cycle;
    }

    public long getAddress() {
        return address;
    }

    public boolean accessType() {
        return read;
    }
    
    public Block.MSIState getState() {
        return state;
    }
    
    public void setState() {
        if(read)
            state = Block.MSIState.SHARED;
        else
            state = Block.MSIState.MODIFIED;
    }
	
	public String toString() {
		return "cycle: " + cycle + ", read: " + read + ", address: " + address;
	}
}