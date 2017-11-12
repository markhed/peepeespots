package be.museomix.wihap;

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
	
	private static int COUNTER = 0;
	
	@RequestMapping(value = "/{spotId}", method = RequestMethod.GET)
	public String handleSpotReading(@PathVariable String spotId, HttpServletRequest request) {
		String user = determineUser(request);
		if (user == null) {
			return PageResolver.LOGIN;
		}
		
		return PageResolver.getPageIndex(spotId);
	}
	

	@RequestMapping(value = PageResolver.VIEW_PATH + "{spotId}", method = RequestMethod.GET)
	public String handleViewForSpot(
			@CookieValue(value="user", required = false) String user, @PathVariable String spotId, ModelMap model) {
		if (user == null) {
			return PageResolver.LOGIN;
		}
		COUNTER++;
		
		model.addAttribute("message", "You are in spot: " + spotId);
		model.addAttribute("user", user);
		model.addAttribute("counter", COUNTER);
		return PageResolver.getPageIndex(spotId);
	}
	
	@RequestMapping(value = PageResolver.LOGIN, method = RequestMethod.GET)
	public String handleLogIn(HttpServletResponse response) {
		COUNTER = 0;
		return PageResolver.LOGIN;
	}
	
	@RequestMapping(value = PageResolver.CHECK_WINNER, method = RequestMethod.GET)
	public String handleWinner(
			@CookieValue(value="user", required = false) String user, HttpServletResponse response, ModelMap model) {
		model.addAttribute("user", user);
		model.addAttribute("counter", COUNTER);
		
		if (COUNTER >= 5) {
			COUNTER = 0;
			return PageResolver.WINNER;	
		} else {
			return PageResolver.CHECK_WINNER;
		}
	}
	
	@RequestMapping(value = PageResolver.WELCOME, method = RequestMethod.GET)
	public String handleWelcome(@RequestParam String user, HttpServletResponse response) {
		addUser(user);
		Cookie cookie = new Cookie("user", user);
		cookie.setMaxAge(24*60*60);
		cookie.setPath("/");
		response.addCookie(cookie);
		
		return PageResolver.WELCOME;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String handleDefault() {
		return PageResolver.LOGIN;
	}
	
	private String determineUser(HttpServletRequest request) {
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
	
	private void addUser(String user) {
		//TODO
	}

}