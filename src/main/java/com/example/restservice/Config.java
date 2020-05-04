package com.example.restservice;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Config extends Properties {

	private static final File FILE = new File("config.ini");
	private static final Config CFG = new Config();

	public enum Key {

		DISCORD_CLIENT_ID("discord_client_id"),
		DISCORD_REDIRECT_URI("discord_redirect_uri");

		private final String value;

		Key(String value) {

			this.value = value;
		}

		@Override
		public String toString() {

			return value;
		}
	}

	private Config() {

		if (CFG != null)
			throw new IllegalStateException();

		// @formatter:off
		try { this.initialize(); }
		catch (IOException e) { e.printStackTrace(); }
		// @formatter:on
	}

	private void initialize() throws IOException {

		if (FILE.createNewFile())
		{
			List<String> values = new java.util.ArrayList<>();

			for (Key value : Key.values())
			{
				values.add(value.toString() + '=');
			}
			FileUtils.writeLines(FILE, values);
			throw new IllegalStateException("No configuration file found, a template file has been created" +
					".");
		}
		else load(new FileInputStream(FILE.getName()));
	}

	public static String getValue(Key key) {

		return (String) CFG.get(key.value);
	}
}
