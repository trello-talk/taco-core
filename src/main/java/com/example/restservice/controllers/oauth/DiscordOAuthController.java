package com.example.restservice.controllers.oauth;

import com.example.restservice.controllers.ControllerBase;
import kong.unirest.Unirest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.SecureRandom;

@RestController
public class DiscordOAuthController extends ControllerBase {

	@Value("${discord.client_id}")
	private String clientId;

	@Value("${discord.client_secret}")
	private String clientSecret;

	@Value("${discord.user_agent}")
	private String userAgent;

	private final String state = new BigInteger(130, new SecureRandom()).toString(32);

	@GetMapping("/discordoauth")
	public RedirectView discordOauth(HttpServletRequest request) throws MalformedURLException {

		HttpSession session = request.getSession(false);

		if (session == null) {
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl(getBaseUri(request) + "/invalidsession");
			return redirectView;
		}

		session.setAttribute("state", state);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("https://discordapp.com/oauth2/authorize" +
				"?client_id=" + clientId +
				"&redirect_uri=" + getBaseUri(request) + "/discordredirect" +
				"&state=" + state +
				"&response_type=code" +
				"&scope=identify");
		return redirectView;
	}

	@GetMapping("/discordredirect")
	public RedirectView discordRedirect(HttpServletRequest request,
										@RequestParam(value = "code", defaultValue = "") String code,
										@RequestParam(value = "state", defaultValue = "") String state) throws IOException {

		HttpSession session = request.getSession(false);

		if (session == null) {
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl(getBaseUri(request) + "/invalidsession");
			return redirectView;
		}

		String sessionState = (String) session.getAttribute("state");
		if (sessionState != null && !sessionState.equals(state))
		{
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl(getBaseUri(request) + "/invalidsession");
			return redirectView;
		}

		JSONObject json = Unirest.post("https://discordapp.com/api/oauth2/token")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("User-Agent", userAgent)
				.field("grant_type", "authorization_code")
				.field("scope", "identify")
				.field("code", code)
				.field("redirect_uri", getBaseUri(request) + "/discordredirect")
				.field("client_id", clientId)
				.field("client_secret", clientSecret)
				.asJson()
				.getBody()
				.getObject();

		RedirectView redirectView = new RedirectView();
		String redirectPath;
		try
		{
			session.setAttribute("token", json.getString("access_token"));
			session.setAttribute("refresh", json.getString("refresh_token"));
			redirectPath = "/trellooauth";
		} catch (JSONException e) {
			redirectPath = "/failed";
		}
		redirectView.setUrl(getBaseUri(request) + redirectPath);
		return redirectView;
	}
}
