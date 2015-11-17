package simulator;

public class Statistics {
    
    private final long p;
    public int[] numCycles;
    public float[][] hitRates;
    public float[] L1missPenalties;
    public int[] delays;
    public int[] numAccesses;
    public int[] numL2Accesses;
    public int[][] numMisses;
    public int controlMsgs, dataMsgs;
    
    public Statistics(long p) {
	this.p = p;
	controlMsgs = 0;
	dataMsgs = 0;
	delays = new int[(int)Math.pow(2,p)];
	numCycles = new int[(int)Math.pow(2,p)];
	numAccesses = new int[(int)Math.pow(2,p)];
	numL2Accesses = new int[(int)Math.pow(2,p)];
	numMisses = new int[(int)Math.pow(2,p)][2];
	L1missPenalties = new float[(int)Math.pow(2,p)];
	hitRates = new float[(int)Math.pow(2,p)][2];
    }
    
    public void incrementControlMsg(){
	controlMsgs++;
    }
    
    public void incrementDataMsg(){
	dataMsgs++;
    }
    
    public void done(long cycle, int tileNum){
	int cycleNum = (int)cycle;
	if(numCycles[tileNum]==0)
	    numCycles[tileNum] = cycleNum;
    }
    
    public void incrementL2Misses(int tileNum){
	numMisses[tileNum][1]++;
    }
    
    public void incrementL1Misses(int tileNum){
	numMisses[tileNum][0]++;
    }
    
    public void addDelay(long delay, int tileNum){
	delays[tileNum] += (int)delay;
    }
    
    public void incrementL2Accesses(int tileNum){
	numL2Accesses[tileNum]++;
    }
    
    public void incrementL1Accesses(int tileNum){
	numAccesses[tileNum]++;
    }
    
    public float[][] getHitRates(){
	for(int i = 0 ; i < (int)Math.pow(2,p); i++){
	    hitRates[i][0] = numAccesses[i]==0 ? 0 : ((float)numAccesses[i] - (float)numMisses[i][0]) / (float)numAccesses[i];
	    hitRates[i][1] = numL2Accesses[i]==0 ? 0 : ((float)numL2Accesses[i] - (float)numMisses[i][1]) / (float)numL2Accesses[i];
	}
	return hitRates;
    }
    
    public float[] getMissPenalties(){
	for(int i = 0 ; i < (int)Math.pow(2,p); i++)
	    L1missPenalties[i] = numMisses[i][0]==0 ? 0:(float)delays[i] / (float)numMisses[i][0];
	return L1missPenalties;
    }
}
