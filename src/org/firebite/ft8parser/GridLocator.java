package org.firebite.ft8parser;

import java.util.regex.*;

public class GridLocator {
	public static final GridLocator Unknown = new GridLocator("ZZ99");
	
	public static String HomeLocator;	
	private static int homeLongitude = -1;
	private static int homeLatitude = -1;
	
	private static final Pattern matchPattern = Pattern.compile("[A-R]{2}\\d{2}");
	private static final int EARTHRADIUS = 6371; 
	
	private String locator;
	
	public GridLocator(String locator) {
		this.locator = locator.toUpperCase();
	}
	
	// Calculate distance to home assumes earth is a sphere
	public double getDistance() {
		if(homeLongitude == -1)
			throw new IllegalStateException();
		
		double longitudeDiff = Math.toRadians(this.getLongitude() - homeLongitude);
		double latitudeDiff  = Math.toRadians(this.getLatitude() - homeLatitude);
		
		double a = Math.sin(latitudeDiff / 2) * Math.sin(latitudeDiff / 2)
	            + Math.cos(Math.toRadians(homeLatitude)) * Math.cos(Math.toRadians(this.getLatitude()))
	            * Math.sin(longitudeDiff / 2) * Math.sin(longitudeDiff / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return EARTHRADIUS * c;
	}
	
	public int getLongitude() {
		return ((locator.charAt(0) - 65) * 20    // First char represents 20 deg fields
				+ (locator.charAt(2) - 48) * 2)  // Third char represents 2 deg squares
				- 180;                           // Remove 90 deg offset
	}
	
	public int getLatitude() {
		return ((locator.charAt(1) - 65) * 10  // Second char represents 10 deg fields
				+ (locator.charAt(3) - 48))    // Fourth char represents 1 deg squares
				- 90;                          // Remove 90 deg offset
	}
	
	public static void setHomeLocator(String homeLocator) {
		GridLocator home = GridLocator.tryParse(homeLocator);
		
		if(home != GridLocator.Unknown) {
			HomeLocator = homeLocator;
			homeLatitude = home.getLatitude();
			homeLongitude = home.getLongitude();
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	public static GridLocator tryParse(String locator) {
		locator = locator.toUpperCase();
		Matcher m = matchPattern.matcher(locator);
		
		// Is correct grid locator
		if(m.find() && !locator.equals("RR73"))
			return new GridLocator(locator);
		else {
			return GridLocator.Unknown;
		}
	}
	
	public String toString() {
		if(isValid())
			return locator;
		
		return "Unknown";
	}
	
	public Boolean isValid() {
		return !locator.equals("ZZ99");
	}
}
