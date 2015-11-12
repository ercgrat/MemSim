package simulator;

public class Access {

    private int cycle, address;
    boolean read;
    public Block.MSIState state;
    

    public Access(int cycle, int address, boolean read) {
	this.cycle = cycle;
	this.address = address;
	this.read = read;
    }

    public int getCycle() {
	return cycle;
    }

    public int getAddress() {
	return address;
    }

    public boolean accessType() {
	return read;
    }
    
    public void setState(){
	if(read)
	    state = Block.MSIState.SHARED;
	else
	    state = Block.MSIState.MODIFIED;
    }
}