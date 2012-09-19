package com.objectiflune.servlets.purl;

import java.io.IOException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.http.HttpContext;

/**
 * A http context that always blocks access to resources.
 */
public class PurlContext implements HttpContext {

	private static final Log LOG = LogFactory.getLog(PurlContext.class);

	public boolean handleSecurity(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		LOG.info("Forbiden access!");
		return true;
	}

	public URL getResource(final String name) {
		throw new IllegalStateException(
				"This method should not be possible to be called as the access is denied");
	}

	public String getMimeType(String s) {
		throw new IllegalStateException(
				"This method should not be possible to be called as the access is denied");
	}
}
