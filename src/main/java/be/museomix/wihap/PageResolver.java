package be.museomix.wihap;

import java.util.HashSet;
import java.util.Set;

public class PageResolver {
	
	static final String VIEW_PATH = "view/";
	static final String LOGIN = "login";
	static final String WELCOME = "welcome";
	private static final String UNKNOWN_SPOT_FALLBACK = "unknown";
	
	private static Set<String> knownSpots = new HashSet<String>();
	
	private PageResolver(){};
	
	static {
		knownSpots.add("spot1");
		knownSpots.add("spot2");
	}
	
	public static String getPageIndex(String inputSpotId) {
		String spotId = inputSpotId.replaceAll(" ", "").trim().toLowerCase();
		if (knownSpots.contains(spotId)) {
			return VIEW_PATH + spotId;
		} else {
			return UNKNOWN_SPOT_FALLBACK;
		}
	}

}
