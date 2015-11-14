package simulator;

public class L2Cache {

    int p, b, n, a;
    L2CacheEntry[][] cache;

    public L2Cache(int p, int b, int n, int a) {
        this.p = p;
        this.b = b;
        this.n = n;
        this.a = a;

        cache = new L2CacheEntry[(int) Math.pow(2, a)][(int) Math.pow(2, n - a - b)];
    }
}