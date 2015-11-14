package simulator;

public class Block {

    private static final int wordSize = 32;
    private static final int pageSize = 12;

    public enum MSIState {
        MODIFIED, SHARED, INVALID
    }

    static int page(int address, int p) {
        return (address / (int) Math.pow(2, pageSize)) % (int) Math.pow(2, p);
    }
    
    static int reconstructAddress(int tag, int L2index, int cacheIndex, int p, int b, int n, int a) {
        return 0;
    }

    static int L1cacheIndex(int address, int b, int n, int a) {
        return (address / (int) Math.pow(2, b)) % (int) Math.pow(2, n - a - b);
    }

    static int L1tag(int address, int p, int b, int n, int a) {
        return address / (int) Math.pow(2, n - a);
    }

    static int L2cacheIndex(int address, int p, int b, int n, int a) {
        int indexSize = n - (a + b);
        int withoutBlock = address / (int) Math.pow(2, b);
        if (indexSize + b > pageSize) {
            int lowerSegmentSize = pageSize - b;
            int upperSegmentSize = (indexSize + b) - pageSize;
            int withoutUpperBits = withoutBlock % (int) Math.pow(2, (lowerSegmentSize + p + upperSegmentSize));
            int lowerSegment = withoutUpperBits % (int) Math.pow(2, lowerSegmentSize);
            int upperSegment = (withoutUpperBits / (int) Math.pow(2, lowerSegmentSize + p)) * (int) Math.pow(2, lowerSegmentSize);
            return lowerSegment + upperSegment;
        } else {
            return withoutBlock % (int) Math.pow(2, indexSize);
        }
    }

    static int L2tag(int address, int p, int b, int n, int a) {
        int indexSize = n - (a + b);
        int tagSize = wordSize - (p + indexSize + b);
        if (indexSize + b > pageSize) {
            return address / (int) Math.pow(2, wordSize - tagSize);
        } else {
            int withoutIndexAndBlock = address / (int) Math.pow(2, indexSize + b);
            int lowerSegment = withoutIndexAndBlock % (int) Math.pow(2, pageSize - (indexSize + b));
            int upperSegment = withoutIndexAndBlock / (int) Math.pow(2, p + (pageSize - (indexSize + b)));
            return lowerSegment + upperSegment;
        }
    }

    static int manhattanDistance(int tile1, int tile2, int p) {
        int height = (int) Math.pow(2, p / 2);
        int width = (int) Math.pow(2, p) / height;
        int heightDiff = Math.abs(((tile1 / width) - (tile2 / width)));
        int widthDiff = Math.abs(((tile1 % width) - (tile2 % width)));
        return heightDiff + widthDiff + 1;
    }
}