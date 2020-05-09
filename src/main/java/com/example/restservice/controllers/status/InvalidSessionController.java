package com.example.restservice.controllers.status;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvalidSessionController {
	@GetMapping("/invalidsession")
	public String invalidSession(HttpServletRequest request) {
		request.getSession().invalidate();
		return "Invalid Session. Please try again.";
	}
}
