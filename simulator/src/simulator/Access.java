package simulator;

public class Access {
	
	private int cycle, address;
	boolean read;
	private int requester;
	boolean receive;
	public Block.MSIState state;
	
	public Access(int cycle, int address, boolean read) {
		this.cycle = cycle;
		this.address = address;
		this.read = read;
	}
	
	public Access(int cycle, int address, boolean read, int requester) {
		this.cycle = cycle;
		this.address = address;
		this.read = read;
		this.requester = requester;
	}
	
	public int getRequester(){
		return this.requester;
	}
        
	public int getCycle() {
		return cycle;
	}
	
	public int getAddress() {
		return address;
	}
	
	public boolean accessType(){
		return read;
	}

}