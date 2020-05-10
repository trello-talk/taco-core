package com.example.restservice.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@RestController
public class RootController extends ControllerBase {
	@GetMapping("/")
	public void rootController(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession();
		response.sendRedirect("/discordoauth");
	}
}
