package com.example.restservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class StatusController {
	@GetMapping("/alreadyauthorized")
	public String alreadyAuthorized(HttpServletRequest request) {
		request.getSession().invalidate();
		return "You are already authorized. You may close this tab";
	}

	@GetMapping("/invalidsession")
	public String invalidSession(HttpServletRequest request) {
		request.getSession().invalidate();
		return "Invalid Session. Please try again.";
	}

	@GetMapping("/failed")
	public String authFailed(HttpServletRequest request) {
		request.getSession().invalidate();
		return "Authorization Failed. You may close this tab";
	}

	@GetMapping("/success")
	public String authSuccess(HttpServletRequest request) {
		request.getSession().invalidate();
		return "Authorization Success. You may close this tab";
	}
}
