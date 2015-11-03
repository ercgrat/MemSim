package simulator;

public class Access {
	
	private int cycle, address;
	boolean read;
	
	public Access(int cycle, int address, boolean read) {
		this.cycle = cycle;
		this.address = address;
		this.read = read;
	}
	
	public int cycle() {
		return cycle;
	}
	
	public int address() {
		return address;
	}

}