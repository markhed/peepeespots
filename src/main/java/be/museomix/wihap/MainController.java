package be.museomix.wihap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class MainController {
	
	private static final Map<String, Integer> USERS = new HashMap<String, Integer>();
	private String currentWinner;
	
	@RequestMapping(value = LOGIN_PAGE, method = RequestMethod.GET)
	public String handleLogIn(HttpServletResponse response) {
		return LOGIN_PAGE;
	}
	
	@RequestMapping(value = LOGIN_CONFIRMATION_PAGE, method = RequestMethod.GET)
	public String handleLogInConfirm(@RequestParam String user, HttpServletResponse response) {
		addUser(user);
		Cookie cookie = new Cookie("user", user);
		cookie.setMaxAge(24*60*60);
		cookie.setPath("/");
		response.addCookie(cookie);
		
		return LOGIN_CONFIRMATION_PAGE;
	}
	
	@RequestMapping(value = "/" + S0_PATH, method = RequestMethod.GET)
	public String handle_Spot0(HttpServletRequest request, ModelMap model) {
		return handleSpotReadingInternal(request, model, S0_PATH);
	}
	
	@RequestMapping(value = "/" + S1_PATH, method = RequestMethod.GET)
	public String handle_Spot1(HttpServletRequest request, ModelMap model) {
		return handleSpotReadingInternal(request, model, S1_PATH);
	}
	
	@RequestMapping(value = "/" + S2_PATH, method = RequestMethod.GET)
	public String handle_Spot2(HttpServletRequest request, ModelMap model) {
		return handleSpotReadingInternal(request, model, S2_PATH);
	}
	
	@RequestMapping(value = "/" + S3_PATH, method = RequestMethod.GET)
	public String handle_Spot3(HttpServletRequest request, ModelMap model) {
		return handleSpotReadingInternal(request, model, S3_PATH);
	}
	
	@RequestMapping(value = "/" + S4_PATH, method = RequestMethod.GET)
	public String handle_Spot4(HttpServletRequest request, ModelMap model) {
		return handleSpotReadingInternal(request, model, S4_PATH);
	}
	
	@RequestMapping(value = "/" + S5_PATH, method = RequestMethod.GET)
	public String handle_Spot5(HttpServletRequest request, ModelMap model) {
		return handleSpotReadingInternal(request, model, S5_PATH);
	}
	
	private String handleSpotReadingInternal(HttpServletRequest request, ModelMap model, String spotPath) {
		String user = determineUser(request);
		if (user == null) {
			return LOGIN_PAGE;
		}
		
		String spotPage = getPageForPath(spotPath);
		if (!isUserAllowedInPage(user, spotPage)) {
			return PROHIBITED_PAGE;
		}
			
		model.addAttribute("user", user);
		model.addAttribute("points", USERS.get(user));
		return spotPage;
	}
	
	@RequestMapping(value = "/" +"{viewId}", method = RequestMethod.GET)
	public String handleViewForSpot(
			@CookieValue(value="user", required = false) String user, @PathVariable String viewId, ModelMap model) {
		if (user == null) {
			return LOGIN_PAGE;
		}
		
		model.addAttribute("user", user);
		return getPageForPath(viewId);
	}
	
	
	
	// FOR CLEARING SPOTS - start
	@RequestMapping(value = "/" + S0_PATH + "_" + CLEARING_KEY, method = RequestMethod.GET)
	public String handleClearing_Spot0(HttpServletRequest request) {
		return handleClearingSpotInternal(request, S0_PATH, 1);
	}
	
	@RequestMapping(value = "/" + S1_PATH + "_" + CLEARING_KEY, method = RequestMethod.GET)
	public String handleClearing_Spot1(HttpServletRequest request) {
		return handleClearingSpotInternal(request, S1_PATH, 2);
	}
	
	@RequestMapping(value = "/" + S2_PATH + "_" + CLEARING_KEY, method = RequestMethod.GET)
	public String handleClearing_Spot2(HttpServletRequest request) {
		return handleClearingSpotInternal(request, S2_PATH, 3);
	}
	
	@RequestMapping(value = "/" + S3_PATH + "_" + CLEARING_KEY, method = RequestMethod.GET)
	public String handleClearing_Spot3(HttpServletRequest request) {
		return handleClearingSpotInternal(request, S3_PATH, 4);
	}
	
	@RequestMapping(value = "/" + S4_PATH + "_" + CLEARING_KEY, method = RequestMethod.GET)
	public String handleClearing_Spot4(HttpServletRequest request) {
		return handleClearingSpotInternal(request, S4_PATH, 5);
	}
	
	@RequestMapping(value = "/" + S5_PATH + "_" + CLEARING_KEY, method = RequestMethod.GET)
	public String handleClearing_Spot5(HttpServletRequest request) {
		return handleClearingSpotInternal(request, S5_PATH, 6);
	}
	
	private String handleClearingSpotInternal(HttpServletRequest request, String spotPath, int pointsForClearing) {
		String user = determineUser(request);
		if (user == null) {
			return LOGIN_PAGE;
		}
		
		updateUser(user, pointsForClearing);
		String spotClearPage = getPageForPath(spotPath);
		return String.format("%s_%s", spotClearPage, CLEARING_KEY);
	}
	// FOR CLEARING SPOTS - end

	
	@RequestMapping(value = CHECK_WINNER_PAGE, method = RequestMethod.GET)
	public String handleWinner(
			@CookieValue(value="user", required = false) String user, HttpServletResponse response, ModelMap model) {
		if (currentWinner == null) {
			return CHECK_WINNER_PAGE;
		}
		
		model.addAttribute("user", user);
		currentWinner = null;
		return WINNER_PAGE;	
	}
	

	
	@RequestMapping(method = RequestMethod.GET)
	public String handleDefault() {
		return LOGIN_PAGE;
	}
	
	
	private boolean isUserAllowedInPage(String user, String pageRequested) {
		int requiredPoints = getRequiredPointsForPage(pageRequested);
		Integer currentPoints = USERS.get(user);
		
		if (currentPoints == requiredPoints) {
			return true;
		} else if (currentPoints > requiredPoints) {
			return true; // reconsider this
		} else {
			return false;
		}
	}
	
	private static String determineUser(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookie.getName().equals("user")) {
				return cookie.getValue();
			}
		}
				
		return null;
	}

	private static  int getRequiredPointsForPage(String pageRequested) {
		switch(pageRequested) {
			case S0_PAGE:
				return 0;
			case S1_PAGE:
				return 1;
			case S2_PAGE:
				return 2;
			case S3_PAGE:
				return 3;
			case S4_PAGE:
				return 4;
			case S5_PAGE:
				return 5;
			case WINNER_PAGE:
				return 6
						;
			default:
				return 0;
		}
	}
	
	
	private void addUser(String user) {
		USERS.put(user, 0);
	}
	
	private void updateUser(final String user, int newPoints) {
		Integer currentPoints = USERS.get(user);
		if (currentPoints == null || currentPoints >= newPoints) {
			return;
		}
		
		USERS.put(user, newPoints);
		if (isWinningPoints(newPoints)) {
			final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		    executorService.scheduleAtFixedRate(new Runnable() {
		        @Override
		        public void run() {
		        	currentWinner = user;
		        }
		    }, 0, 2, TimeUnit.SECONDS);
		}
	}
	
	private static boolean isWinningPoints(int points){
		return points >= 6;
	}
	
	static final String S0_PAGE = "s0asiev9uq6gp79sh";
	static final String S1_PAGE = "s1u0o0t2a5yzt14i1";
	static final String S2_PAGE = "s2p9n9xeyv1yeg658";
	static final String S3_PAGE = "s3nyzpj48pasg1wf4";
	static final String S4_PAGE = "s4xmwcmveythghyv4";
	static final String S5_PAGE = "s5542i5ysa97z4maj";
	
	static final String CLEARING_KEY = "clearing";
	
	static final String S0_PATH = "s0asiev9uq6gp79sh";
	static final String S1_PATH = "s1u0o0t2a5yzt14i1";
	static final String S2_PATH = "s2p9n9xeyv1yeg658";
	static final String S3_PATH = "s3nyzpj48pasg1wf4";
	static final String S4_PATH = "s4xmwcmveythghyv4";
	static final String S5_PATH = "s5542i5ysa97z4maj";
	
	static final String LOGIN_PAGE = "login";
	static final String LOGIN_CONFIRMATION_PAGE = "login_confirmed";
	
	static final String PROHIBITED_PAGE = "prohibited";
	
	static final String CHECK_WINNER_PAGE = "winner_check";
	static final String WINNER_PAGE = "winner";
	
	private static Map<String, String> knownSpots = new HashMap<String, String>();
	
	static {
		knownSpots.put(S0_PAGE, S0_PATH);
		knownSpots.put(S1_PAGE, S1_PATH);
		knownSpots.put(S2_PAGE, S2_PATH);
		knownSpots.put(S3_PAGE, S3_PATH);
		knownSpots.put(S4_PAGE, S4_PATH);
		knownSpots.put(S5_PAGE, S5_PATH);
	}
	
	public static String getPageForPath(String path) {
		String pageView = knownSpots.get(path);
		if (pageView != null) {
			return pageView;
		} else {
			return path;
		}
	}

}