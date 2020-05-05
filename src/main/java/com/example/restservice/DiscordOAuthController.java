package com.example.restservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@RestController
public class DiscordOAuthController {

	private static final String STATE = new BigInteger(130, new SecureRandom()).toString(32);
	private static final String CLIENT_ID = Config.getValue(Config.Key.DISCORD_CLIENT_ID);
	private static final String CLIENT_SECRET = Config.getValue(Config.Key.DISCORD_CLIENT_SECRET);
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

	@GetMapping("/discordredirect")
	public void discordRedirect(String code, String state) throws IOException {

		// TODO(#4): Check that state is the same to protect against XSRF
		String params = "grant_type=authorization_code" +
						"&scope=identify" +
						"&code=" + code +
						"&redirect_uri=" + REDIRECT_URI +
						"&client_id=" + CLIENT_ID +
						"&client_secret=" + CLIENT_SECRET;
		byte[] postData = params.getBytes(StandardCharsets.UTF_8);
		URL url = new URL("https://discordapp.com/api/oauth2/token");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("User-Agent", "Taco Core (https://github.com/trello-talk/taco-core, WIP)");
		conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
		conn.setUseCaches(false);
		try (DataOutputStream output = new DataOutputStream(conn.getOutputStream()))
		{
			output.write(postData);
		}
		conn.connect();
		InputStream input;
		if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
			input = conn.getInputStream();
		} else {
			input = conn.getErrorStream();
		}
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
		String line;
		while ((line = inputReader.readLine()) != null)
		{
			System.out.println(line);
		}
	}
}
