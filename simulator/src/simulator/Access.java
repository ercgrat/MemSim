package simulator;

public class Access {
	
	private int cycle, address;
	boolean read;
	
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

}