package com.example.restservice.controllers.status;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SuccessController {
	@GetMapping("/success")
	public String authFailed(HttpServletRequest request) {
		request.getSession().invalidate();
		return "Authorization Success. You may close this tab";
	}
}
