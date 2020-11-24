package app.tacobot.authservice.controllers.oauth;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kong.unirest.Unirest;

@RestController
public class DiscordOAuthController {

	@Value("${server.hostname}")
	private String hostname;

	@Value("${discord.client_id}")
	private String clientId;

	@Value("${discord.client_secret}")
	private String clientSecret;

	@Value("${discord.user_agent}")
	private String userAgent;

	@GetMapping("/discordoauth")
	public void discordOauth(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);

		if (session == null) {
			// No session, could be malicous.
			response.sendRedirect("/invalidsession");
			return;
		}

		// State is a 32 bit cryptographically secure random value.
		String state = new BigInteger(130, new SecureRandom()).toString(32);
		session.setAttribute("state", state);

		response.sendRedirect("https://discord.com/oauth2/authorize" +
				"?client_id=" + clientId +
				"&redirect_uri=" + hostname + "/discordredirect" +
				"&state=" + state +
				"&response_type=code" +
				"&scope=identify");
	}

	@GetMapping("/discordredirect")
	public void discordRedirect(HttpServletRequest request, HttpServletResponse response,
										@RequestParam(value = "code", defaultValue = "") String code,
										@RequestParam(value = "state", defaultValue = "") String state) throws IOException {

		HttpSession session = request.getSession(false);

		if (session == null) {
			// No session, could be malicious.
			response.sendRedirect("/invalidsession");
			return;
		}

		String sessionState = (String) session.getAttribute("state");
		if (sessionState != null && !sessionState.equals(state))
		{
			// Incorrect state, so could be a malicious request.
			response.sendRedirect("/invalidsession");
			return;
		}

		JSONObject json = Unirest.post("https://discord.com/api/oauth2/token")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("User-Agent", userAgent)
				.field("grant_type", "authorization_code")
				.field("scope", "identify")
				.field("code", code)
				.field("redirect_uri", hostname + "/discordredirect")
				.field("client_id", clientId)
				.field("client_secret", clientSecret)
				.asJson()
				.getBody()
				.getObject();

		try {
			session.setAttribute("token", json.getString("access_token"));
			session.setAttribute("refresh", json.getString("refresh_token"));
			// Success.
			response.sendRedirect("/trellooauth");
		} catch (JSONException e) {
			// Probably clicked cancel, or an other error occurred.
			response.sendRedirect("/failed");
		}
	}
}
