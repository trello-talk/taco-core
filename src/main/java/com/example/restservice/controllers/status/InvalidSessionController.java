package com.example.restservice.controllers.status;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class InvalidSessionController {
	@GetMapping("/invalidsession")
	public String invalidSession(HttpServletRequest request) {
		request.getSession().invalidate();
		return "Invalid Session. Please try again.";
	}
}
