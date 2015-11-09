package simulator;

public class Access {
	
	private int cycle, address;
	boolean read;
	private int requestor;
        boolean receive;
        public Block.MSIState state;
        
	public Access(int cycle, int address, boolean read) {
		this.cycle = cycle;
		this.address = address;
		this.read = read;
	}
	
        public void setRequestor(int requestor){
            this.requestor = requestor;
        }
        
        public int getRequestor(){
            return this.requestor;
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