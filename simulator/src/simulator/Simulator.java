/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import java.util.*;
import java.io.*;

/**
 *
 * @author zaeem
 */
public class Simulator {

    static long p, n1, n2, b, a1, a2, C, d, d1;
    static ArrayList<Tile> tiles;
    static Statistics stats;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	if (args.length < 2) {
	    System.out.println("Not enough arguments. Run with this format:\n\tjava Simulator <config> <trace file>");
	}
	long maxCycle = 100;
	parseInput(args);

	boolean verbose = false;
	if (args.length == 3 && args[2].equals("-v")) {
	    verbose = true;
	}

	long cycle = 0;
	boolean done = false;
	while (!done) {
	    if(verbose)
		System.out.println("*********cycle " + cycle + "****************");
	    int numDone = 0;
	    for (int i = 0; i < tiles.size(); i++) {
		if (!tiles.get(i).cycle(cycle, i, verbose)) {
		    numDone++;
		    stats.done(cycle, i);
		}
	    }
	    //System.out.println(numDone);
	    if (numDone == tiles.size()) {
		done = true;
	    }
	    cycle++;
	}
	if(verbose)
	    for (int i= 0; i < tiles.size(); i++){
		System.out.println("Printing L1 cache contents of tile " + i);
		tiles.get(i).printL1Cache();
		System.out.println("Printing L2 cache contents of tile " + i);
		tiles.get(i).printL2Cache();
	    }
	float[] avgMissPens = stats.getMissPenalties();
	float[][] hitRates = stats.getHitRates();
	int[] numCycles = stats.numCycles;
	for (int i= 0; i < tiles.size(); i++){
	    System.out.println("Number of cycles to finish execution of tile " + i + " is " + numCycles[i]);
	    if(hitRates[i][0]==-1)
		System.out.println("No accesses in L1 of tile " + i);
	    else
		System.out.println("Hit rate in L1 of tile " + i + " is " + hitRates[i][0]);
	    if(hitRates[i][1]==-1)
		System.out.println("No accesses in L2 of tile " + i);
	    else
		System.out.println("Hit rate in L2 of tile " + i + " is " + hitRates[i][1]);
	    System.out.println("Average miss penalty in L1 of tile " + i + " is " + avgMissPens[i]);
	}
	System.out.println("Total number of control(short) messages is " + stats.controlMsgs);
	System.out.println("Total number of data(long) messages is " + stats.dataMsgs);
    }

    public static void parseInput(String[] args) {
	tiles = new ArrayList<Tile>();

	try {
	    BufferedReader br = new BufferedReader(new FileReader(args[0]));
	    if (!br.ready()) {
		return;
	    }
	    br.readLine();
	    p = Long.parseLong(br.readLine().split(" ")[2]);
	    n1 = Long.parseLong(br.readLine().split(" ")[2]);
	    n2 = Long.parseLong(br.readLine().split(" ")[2]);
	    b = Long.parseLong(br.readLine().split(" ")[2]);
	    a1 = Long.parseLong(br.readLine().split(" ")[2]);
	    a2 = Long.parseLong(br.readLine().split(" ")[2]);
	    C = Long.parseLong(br.readLine().split(" ")[2]);
	    d = Long.parseLong(br.readLine().split(" ")[2]);
	    d1 = Long.parseLong(br.readLine().split(" ")[2]);
	    br.close();
	} catch (Exception e) {
	    System.out.println(e);
	}
	stats = new Statistics(p);
	for (long i = 0; i < (long) Math.pow(2, p); i++) {
	    tiles.add(new Tile(tiles, p, b, n1, n2, a1, a2, d, d1, C, stats));
	}

	try {
	    BufferedReader br = new BufferedReader(new FileReader(args[1]));
	    while (br.ready()) {
		String[] line = br.readLine().split("\t");
		long cycle = Long.parseLong(line[0]);
		int core = Integer.parseInt(line[1]);
		boolean read = line[2].equals("0");
		long address = Long.parseLong(line[3].substring(2).toUpperCase(), 16);
		Access access = new Access(cycle, address, read);
		tiles.get(core).addAccess(access);
	    }
	    br.close();
	} catch (Exception e) {
	    System.out.println(e);
	}
    }
}
