package org.firebite.ft8parser;

public class Message {
	public static final Message Unknown = new Message(-1, Integer.MIN_VALUE, Float.MAX_VALUE, Integer.MIN_VALUE, null, null);
	
	public int Time;
	public int DB;
	public float TimeError;
	public int Frequency;
	
	public String Callsign;
	public GridLocator Locator;
	
	public Message(int time, int dB, float timeError, int frequency, String callsign, GridLocator locator) {
		Time = time;
		DB = dB;
		TimeError = timeError;
		Frequency = frequency;
		Callsign = callsign;
		Locator = locator;
	}
	
	public Message(int time, int dB, float timeError, int frequency, String callsign) {
		Time = time;
		DB = dB;
		TimeError = timeError;
		Frequency = frequency;
		Callsign = callsign;
		Locator = GridLocator.Unknown;
	}
	
	// Message always has the following format
	// 0    1    2    3     4    5...
	// UTC  dB   DT   Freq  ~    Content
	//
	// Content (5,6,7)          | Extracted data
	//
	// CQ [PartyA] [Grid]       | PartyA.Grid
	// [PartyA] [PartyB] [Grid] | PartyB.Grid (Trigger only on 4 letter grid)
	// [PartyA] [PartyB] *      | ---                          & ignore RR73)
	public static Message parse(String entry) {
		String[] msg = entry.split(" +");
		
		try {
			int time        = Integer.parseInt(msg[0]);
			int dB          = Integer.parseInt(msg[1]);
			float timeError = Float.parseFloat(msg[2]);
			int frequency   = Integer.parseInt(msg[3]);
			
			String callsign;
			GridLocator locator;
			
			callsign = msg[6];
			locator = GridLocator.tryParse(msg[7]);
			
			return new Message(time, dB, timeError, frequency, callsign, locator);
		}
		catch(Exception e) {
			return Message.Unknown;
		}
	}
	
	public void printDetails() {
		System.out.printf("Metadata: %d %d %f %d\n", Time, DB, TimeError, Frequency);
		System.out.printf("Content:  %s %s\n", Callsign, Locator.toString());
	}
}
