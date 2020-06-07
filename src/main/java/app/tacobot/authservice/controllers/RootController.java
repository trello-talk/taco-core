package app.tacobot.authservice.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
	@GetMapping("/")
	public void rootController(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession();
		response.sendRedirect("/discordoauth");
	}
}
