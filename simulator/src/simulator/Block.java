package simulator;

public class Block {

	private final int wordSize = 32;
	private final int pageSize = 12;

	private int address;
	
	public Block(int address) {
		this.address = address;
	}
	
	public int page(int p) {
		return (address / (int)Math.pow(2, pageSize)) % (int)Math.pow(2, p);
	}
	
	public int cacheIndex(int b, int n, int a, int p) {
		int indexSize = n - a;
		int withoutBlock = address / (int)Math.pow(2, b);
		if(indexSize + b > pageSize) {
			int lowerSegmentSize = pageSize - b;
			int upperSegmentSize = (indexSize + b) - pageSize;
			int withoutUpperBits = withoutBlock % (int)Math.pow(2, (lowerSegmentSize + p + upperSegmentSize));
			int lowerSegment = withoutUpperBits % (int)Math.pow(2, lowerSegmentSize);
			int upperSegment = (withoutUpperBits / (int)Math.pow(2, lowerSegmentSize + p)) * (int)Math.pow(2, lowerSegmentSize);
			return lowerSegment + upperSegment;			
		} else {
			return withoutBlock % (int)Math.pow(2, indexSize);
		}
	}
	
}