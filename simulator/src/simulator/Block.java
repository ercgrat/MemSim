package simulator;

public class Block {

	private static final int wordSize = 32;
	private static final int pageSize = 12;
	
	public enum MSIState {
		MODIFIED, SHARED, INVALID
	}
	
	static int page(int address, int p) {
		return (address / (int)Math.pow(2, pageSize)) % (int)Math.pow(2, p);
	}
	
	static int cacheIndex(int address, int p, int b, int n, int a) {
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
	
	static int tag(int address, int p, int b, int n, int a) {
		int indexSize = n - a;
		int tagSize = wordSize - (p + indexSize + b);
		if(indexSize + b > pageSize) {
			return address / (int)Math.pow(2, wordSize - tagSize);
		} else {
			int withoutIndexAndBlock = address / (int)Math.pow(2, indexSize + b);
			int lowerSegment = withoutIndexAndBlock % (int)Math.pow(2, pageSize - (indexSize + b));
			int upperSegment = withoutIndexAndBlock / (int) Math.pow(2, p + (pageSize - (indexSize + b)));
			return lowerSegment + upperSegment;
		}
	}
	
}