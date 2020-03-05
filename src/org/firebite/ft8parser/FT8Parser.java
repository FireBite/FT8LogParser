package org.firebite.ft8parser;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class FT8Parser {
	public static final int  MAX_FRAMES          = 15;
	public static final int  MIN_SIGNAL_STRENGHT = -25;
	public static final int  MAX_SIGNAL_STRENGHT = 5;
	public static final int  SIGNAL_STEP         = (MAX_SIGNAL_STRENGHT - MIN_SIGNAL_STRENGHT) / 7;
	public static final char SIGNAL_STRENGTH_CHARS[] = {' ', '_', '▄', '█'};
	
	public static final void main(String[] args) throws IOException{
		MessageDB msgDB = new MessageDB();
		GridLocator.setHomeLocator("JO90");
		
		// Read log file and load to MessageDB
		try (BufferedReader br = new BufferedReader(new FileReader("data/example.txt"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	Message msg = Message.parse(line);
				msgDB.add(msg);
		    }
		}
		
		int totalFrames = (msgDB.getEndTime() - msgDB.getStartTime()) / 15 + 1;
		
		System.out.printf("FT8LogParser \n\n");
		System.out.printf("Messages total: %d\n", msgDB.Messages.size());
		System.out.printf("Identified:     %d\n\n", msgDB.Locators.size());
		System.out.println(new String(new char[totalFrames]).replace('\0', '═'));
		
		// Get identified stations and present reports
		for (String callsign: msgDB.getCallsigns()) {
			ArrayList<SignalReport> reports = msgDB.SignalReports.get(callsign);
			GridLocator grid = msgDB.Locators.get(callsign);
			float awgPower = 0;
			
			// Generate bargraphs
			char[][] bargraph = new char[2][totalFrames];
			Arrays.fill(bargraph[0], ' ');
			Arrays.fill(bargraph[1], ' ');
			
			for (SignalReport r: reports) {
				int frame = (r.Time - msgDB.getStartTime()) / 15;
				awgPower += r.DB;
				awgPower /= 2;
				
				// Calculate signal strength representation
				int signalStrenght = Math.max(0, Math.min(((r.DB - MIN_SIGNAL_STRENGHT) / SIGNAL_STEP), 7));
				
				bargraph[0][frame] = SIGNAL_STRENGTH_CHARS[signalMinMax(signalStrenght - 4)];
				bargraph[1][frame] = SIGNAL_STRENGTH_CHARS[signalMinMax(signalStrenght)];
			}
			
			// Print details
			System.out.print(new String(bargraph[0]));
			System.out.printf(" Call: %s\n", callsign);
			
			System.out.print(new String(bargraph[1]));
			System.out.printf(" Grid: %s (%dkm)\n", grid.toString(), Math.round(grid.getDistance()));
			
			System.out.println("═" + (Math.signum(awgPower) >= 0 ? "+" : "-") + new DecimalFormat("00.00").format(Math.abs(awgPower)) + new String(new char[totalFrames-7]).replace('\0', '═'));
		}
	}
	
	private static final int signalMinMax(int n) {
		return Math.max(0, Math.min(3, n));
	}
}
