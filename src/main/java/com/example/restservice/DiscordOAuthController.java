package com.example.restservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigInteger;
import java.security.SecureRandom;

@RestController
public class DiscordOAuthController {

	private static final String STATE = new BigInteger(130, new SecureRandom()).toString(32);
	private static final String CLIENT_ID = Config.getValue(Config.Key.DISCORD_CLIENT_ID);
	private static final String REDIRECT_URI = Config.getValue(Config.Key.DISCORD_REDIRECT_URI);

	@GetMapping("/discordoauth")
	public RedirectView discordOauth() {

		RedirectView redirectView = new RedirectView();
		// TODO(#4): Store state parameter to check in redirect
		redirectView.setUrl("https://discordapp.com/oauth2/authorize" +
				"?client_id=" + CLIENT_ID +
				"&redirect_uri=" + REDIRECT_URI +
				"&state=" + STATE +
				"&response_type=code" +
				"&scope=identify");
		return redirectView;
	}
}
