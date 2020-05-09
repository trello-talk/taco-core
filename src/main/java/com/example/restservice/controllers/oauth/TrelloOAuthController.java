package com.example.restservice.controllers.oauth;

import com.example.restservice.controllers.ControllerBase;
import com.github.scribejava.apis.TrelloApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.concurrent.ExecutionException;

@RestController
public class TrelloOAuthController extends ControllerBase {
	@Value("${trello.api_key}")
	private String apiKey;

	@Value("${trello.api_secret}")
	private String apiSecret;

	@Value("${trello.app_name}")
	private String appName;

	@Value("${discord.user_agent}")
	private String userAgent;

	private OAuth10aService service;
	private OAuth1RequestToken requestToken;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@GetMapping("/trellooauth")
	public RedirectView trelloOauth(HttpServletRequest request) throws InterruptedException,
			ExecutionException,
			IOException {

		HttpSession session = request.getSession(false);

		if (session == null) {
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl(getBaseUri(request) + "/invalidsession");
			return redirectView;
		}

		if (service == null)
		{
			service = new ServiceBuilder(apiKey)
					.apiSecret(apiSecret)
					.callback(getBaseUri(request)+"/trelloredirect")
					.build(TrelloApi.instance());
			requestToken = service.getRequestToken();
		}

		RedirectView redirectView = new RedirectView();
		String authUrl = service.getAuthorizationUrl(requestToken) + "&name=" + appName;
		redirectView.setUrl(authUrl);
		return redirectView;
	}

	@GetMapping("/trelloredirect")
	public RedirectView trelloRedirect(HttpServletRequest request,
							   @RequestParam(value = "oauth_verifier", defaultValue = "") String verifier) throws InterruptedException, ExecutionException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null) {
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl(getBaseUri(request) + "/invalidsession");
			return redirectView;
		}

		String discordToken = (String) session.getAttribute("token");
		String discordRefresh = (String) session.getAttribute("refresh");

		if (discordToken == null || discordRefresh == null) {
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl(getBaseUri(request) + "/invalidsession");
			return redirectView;
		}

		OAuth1AccessToken accessToken;
		try
		{
			accessToken = service.getAccessToken(requestToken, verifier);
		} catch (OAuthException e) {
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl(getBaseUri(request) + "/failed");
			return redirectView;
		}
		String trelloToken = accessToken.getToken();
		String trelloID = getTrelloID(trelloToken);
		String userID = getDiscordID(discordToken);

		jdbcTemplate.update(
				"INSERT INTO \"Users\" " +
						"(\"userID\", \"trelloToken\", \"trelloID\", \"discordToken\", \"discordRefresh\")" +
				"VALUES (?, ?, ?, ?, ?)", userID, trelloToken, trelloID, discordToken, discordRefresh);

		RedirectView redirectView = new RedirectView();
		redirectView.setUrl(getBaseUri(request) + "/success");
		return redirectView;
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
