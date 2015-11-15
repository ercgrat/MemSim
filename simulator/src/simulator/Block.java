package simulator;

public class Block {

    private static final long wordSize = 32;
    private static final long pageSize = 12;

    public enum MSIState {
        MODIFIED, SHARED, INVALID
    }

    static long page(long address, long p) {
        return (address / (long) Math.pow(2, pageSize)) % (long) Math.pow(2, p);
    }
	
    static long L1cacheIndex(long address, long b, long n, long a) {
        return (address / (long) Math.pow(2, b)) % (long) Math.pow(2, n - a - b);
    }

    static long L1tag(long address, long p, long b, long n, long a) {
        return address / (long) Math.pow(2, n - a);
    }

    static long L2cacheIndex(long address, long p, long b, long n, long a) {
        long indexSize = n - (a + b);
        long withoutBlock = address / (long) Math.pow(2, b);
        if (indexSize + b > pageSize) {
            long lowerSegmentSize = pageSize - b;
            long upperSegmentSize = (indexSize + b) - pageSize;
            long withoutUpperBits = withoutBlock % (long) Math.pow(2, (lowerSegmentSize + p + upperSegmentSize));
            long lowerSegment = withoutUpperBits % (long) Math.pow(2, lowerSegmentSize);
            long upperSegment = (withoutUpperBits / (long) Math.pow(2, lowerSegmentSize + p)) * (long) Math.pow(2, lowerSegmentSize);
            return lowerSegment + upperSegment;
        } else {
            return withoutBlock % (long) Math.pow(2, indexSize);
        }
    }

    static long L2tag(long address, long p, long b, long n, long a) {
        long indexSize = n - (a + b);
        long tagSize = wordSize - (p + indexSize + b);
        if (indexSize + b > pageSize) {
            return address / (long) Math.pow(2, wordSize - tagSize);
        } else {
            long withoutIndexAndBlock = address / (long) Math.pow(2, indexSize + b);
            long lowerSegment = withoutIndexAndBlock % (long) Math.pow(2, pageSize - (indexSize + b));
            long upperSegment = withoutIndexAndBlock / (long) Math.pow(2, p + (pageSize - (indexSize + b)));
            return lowerSegment + upperSegment;
        }
    }

    static long manhattanDistance(long tile1, long tile2, long p) {
        long height = (long) Math.pow(2, p / 2);
        long width = (long) Math.pow(2, p) / height;
        long heightDiff = Math.abs(((tile1 / width) - (tile2 / width)));
        long widthDiff = Math.abs(((tile1 % width) - (tile2 % width)));
        return heightDiff + widthDiff + 1;
    }
}