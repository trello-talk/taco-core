package com.example.restservice.controllers;

import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

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
