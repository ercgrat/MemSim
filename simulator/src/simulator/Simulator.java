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
	
	static int p, n1, n2, b, a1, a2, C, d, d1;
	static ArrayList<Tile> tiles;
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("Not enough arguments. Run with this format:\n\tjava Simulator <config> <trace file>");
		}
		
        parseInput(args);
    }
	
	public static void parseInput(String[] args) {
		tiles = new ArrayList<Tile>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			if(!br.ready()) {
				return;
			}
			br.readLine();
			p = Integer.parseInt(br.readLine().split(" ")[2]);
			n1 = Integer.parseInt(br.readLine().split(" ")[2]);
			n2 = Integer.parseInt(br.readLine().split(" ")[2]);
			b = Integer.parseInt(br.readLine().split(" ")[2]);
			a1 = Integer.parseInt(br.readLine().split(" ")[2]);
			a2 = Integer.parseInt(br.readLine().split(" ")[2]);
			C = Integer.parseInt(br.readLine().split(" ")[2]);
			d = Integer.parseInt(br.readLine().split(" ")[2]);
			d1 = Integer.parseInt(br.readLine().split(" ")[2]);
			br.close();
		} catch(Exception e) {
			System.out.println(e);
		}
		
		for(int i = 0; i < (int)Math.pow(2,p); i++) {
			tiles.add(new Tile(tiles, p, b, n1, n2, a1, a2, d, d1, C));
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[1]));
			while(br.ready()) {
				String[] line = br.readLine().split("\t");
				int cycle = Integer.parseInt(line[0]);
				int core = Integer.parseInt(line[1]);
				boolean read = line[2].equals("0");
				int address = Integer.parseInt(line[3].substring(2), 16);
				Access access = new Access(cycle, address, read);
				tiles.get(core).addAccess(access);
			}
			br.close();
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}
