package org.firebite.ft8parser;

import java.util.*;

public class MessageDB {
	public ArrayList<Message> Messages;
	public HashMap<String, GridLocator> Locators;
	public HashMap<String, ArrayList<SignalReport>> SignalReports;
	
	public MessageDB(){
		Messages = new ArrayList<Message>();
		Locators = new HashMap<String, GridLocator>();
		SignalReports = new HashMap<String, ArrayList<SignalReport>>();
	}
	
	public void add(Message msg) {
		// Reject invalid messages
		if(msg == Message.Unknown)
			return;
		
		Messages.add(msg);
		
		// Add discovered location
		if(msg.Locator.isValid() && !Locators.containsKey(msg.Callsign))
			Locators.put(msg.Callsign, msg.Locator);
		
		// Setup SignalReports if discovered for first time
		if(!SignalReports.containsKey(msg.Callsign))
			SignalReports.put(msg.Callsign, new ArrayList<SignalReport>());
		
		// Otherwise add entry
		else {
			ArrayList<SignalReport> arr = SignalReports.get(msg.Callsign);
			arr.add(new SignalReport(msg.Time, msg.DB));
			SignalReports.put(msg.Callsign, arr);
		}
		
		
	}
	
	public int getStartTime() {
		return Messages.get(0).Time;
	}
	
	public int getEndTime() {
		return Messages.get(Messages.size()-1).Time;
	}
	
	public Set<String> getCallsigns(){
		return Locators.keySet();
	}
}
