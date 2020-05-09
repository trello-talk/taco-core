package com.example.restservice.controllers.status;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FailedController {
	@GetMapping("/failed")
	public String authFailed(HttpServletRequest request) {
		request.getSession().invalidate();
		System.out.println("invalidate");
		return "Authorization Failed. You may close this tab";
	}
}
