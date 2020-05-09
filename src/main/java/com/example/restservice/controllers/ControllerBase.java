package com.example.restservice.controllers;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

public class ControllerBase {
	protected String getBaseUri(HttpServletRequest request) throws MalformedURLException {

		URL reqURL = new URL(request.getRequestURL().toString());
		return reqURL.getProtocol() + "://" + reqURL.getAuthority();
	}
}
