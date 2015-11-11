package simulator;

public class Access {

    private int cycle, address;
    boolean read;
    private int requester;
    boolean receive;
    public Block.MSIState state;
    private int blockOwner;
    private boolean isMemAccess;

    public Access(int cycle, int address, boolean read) {
	this.cycle = cycle;
	this.address = address;
	this.read = read;
	this.blockOwner = -1;
	this.isMemAccess = false;
    }

    public Access(int cycle, int address, boolean read, int requester) {
	this.cycle = cycle;
	this.address = address;
	this.read = read;
	this.requester = requester;
	this.blockOwner = -1;
	this.isMemAccess = false;
    }

    public void setToMemAccess() {
	this.isMemAccess = true;
    }

    public boolean isMemAccess() {
	return isMemAccess;
    }

    public int getRequester() {
	return this.requester;
    }

    public int getOwner() {
	return this.blockOwner;
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
}