package com.example.restservice.controllers;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public class ControllerBase {
	protected String getBaseUri(HttpServletRequest request) throws MalformedURLException {

		URL reqURL = new URL(request.getRequestURL().toString());
		return reqURL.getProtocol() + "://" + reqURL.getAuthority();
	}
}
