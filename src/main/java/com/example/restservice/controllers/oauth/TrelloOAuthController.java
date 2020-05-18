package com.example.restservice.controllers.oauth;

import java.io.*;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.scribejava.apis.TrelloApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

import kong.unirest.Unirest;

@RestController
public class TrelloOAuthController {

	@Value("${server.hostname}")
	private String hostname;

	@Value("${trello.api_key}")
	private String apiKey;

	@Value("${trello.api_secret}")
	private String apiSecret;

	@Value("${trello.app_name}")
	private String appName;

	@Value("${discord.user_agent}")
	private String userAgent;

	private OAuth10aService service;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@GetMapping("/trellooauth")
	public void trelloOauth(HttpServletRequest request, HttpServletResponse response) throws InterruptedException,
			ExecutionException,
			IOException {

		HttpSession session = request.getSession(false);

		if (session == null) {
			// No session, could be malicious.
			response.sendRedirect("/invalidsession");
			return;
		}

		service = new ServiceBuilder(apiKey)
				.apiSecret(apiSecret)
				.callback(hostname + "/trelloredirect")
				.build(TrelloApi.instance());
		OAuth1RequestToken requestToken = service.getRequestToken();

		session.setAttribute("request", requestToken.getToken());
		session.setAttribute("secret", requestToken.getTokenSecret());
		response.sendRedirect(service.getAuthorizationUrl(requestToken) +
				"&name=" + appName +
				"&expiration=never" +
				"&scope=read,write,account");
	}

	@GetMapping("/trelloredirect")
	public void trelloRedirect(HttpServletRequest request, HttpServletResponse response,
							   @RequestParam(value = "oauth_verifier", defaultValue = "") String verifier) throws InterruptedException, ExecutionException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null) {
			// No session, could be malicious.
			response.sendRedirect("/invalidsession");
			return;
		}

		String discordToken = (String) session.getAttribute("token");
		String discordRefresh = (String) session.getAttribute("refresh");
		String requestToken = (String) session.getAttribute("request");
		String requestSecret = (String) session.getAttribute("secret");

		if (discordToken == null || discordRefresh == null || requestToken == null || requestSecret == null) {
			// If one of these attributes isn't set, something is wrong, could be malicous
			response.sendRedirect("/invalidsession");
			return;
		}

		OAuth1AccessToken accessToken;
		try {
			accessToken = service.getAccessToken(new OAuth1RequestToken(requestToken, requestSecret),
					verifier);
		} catch (OAuthException e) {
			response.sendRedirect("/failed");
			return;
		}
		String trelloToken = accessToken.getToken();
		String trelloID = getTrelloID(trelloToken);
		String userID = getDiscordID(discordToken);
		Date now = new Date();

		if (trelloToken == null) {
			response.sendRedirect("/unexpectederror");
			return;
		}

		try {
			jdbcTemplate.update(
					"INSERT INTO users " +
							"(\"userID\", \"trelloToken\", \"trelloID\", \"discordToken\", " +
							"\"discordRefresh\", \"createdAt\", \"updatedAt\")" +
							"VALUES (?, ?, ?, ?, ?, ?, ?)", userID, trelloToken, trelloID, discordToken,
					discordRefresh, now, now);
		} catch (DuplicateKeyException e) {
			response.sendRedirect("/alreadyauthorized");
			return;
		}

		response.sendRedirect("/success");
	}

	private String getTrelloID(String token) {

		return Unirest.get("https://api.trello.com/1/tokens/{token}/member")
				.header("Accept", "application/json")
				.routeParam("token", token)
				.queryString("key", apiKey)
				.queryString("token", token)
				.asJson()
				.getBody()
				.getObject()
				.getString("id");
	}

	private String getDiscordID(String token) {
		return Unirest.get("https://discord.com/api/users/@me")
				.header("Accept", "application/json")
				.header("Authorization", "Bearer " + token)
				.header("User-Agent", userAgent)
				.asJson()
				.getBody()
				.getObject()
				.getString("id");
	}
}
