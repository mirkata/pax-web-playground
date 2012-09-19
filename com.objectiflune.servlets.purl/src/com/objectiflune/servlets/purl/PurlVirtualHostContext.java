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
public class PurlVirtualHostContext implements HttpContext {

	private static final Log LOG = LogFactory.getLog(PurlVirtualHostContext.class);
	private final String virtualHost;
	
	public PurlVirtualHostContext(final String virtualHost){
		this.virtualHost = virtualHost;
	}
	
	public boolean handleSecurity(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		LOG.info("Forbiden access!");
		if(!request.getServerName().equalsIgnoreCase(virtualHost)){
			return false;
		}
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
