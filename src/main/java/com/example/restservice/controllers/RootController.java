package com.example.restservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;

@RestController
public class RootController extends ControllerBase {
	@GetMapping("/")
	public RedirectView rootController(HttpServletRequest request) throws MalformedURLException {
		request.getSession();
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl(getBaseUri(request) + "/discordoauth");
		return redirectView;
	}
}
