package be.museomix.wihap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {
	
	@RequestMapping(value = "/{spotId}", method = RequestMethod.GET)
	public String processSpot(@PathVariable String spotId, ModelMap model) {
		return PageResolver.getPageIndex(spotId);
	}
	
	@RequestMapping(value = PageResolver.VIEW_PATH+ "{spotId}", method = RequestMethod.GET)
	public String processView(@PathVariable String spotId, ModelMap model) {
		model.addAttribute("message", "You are in spot: " + spotId);
		return PageResolver.getPageIndex(spotId);
	}

}