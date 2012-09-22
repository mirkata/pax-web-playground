package com.objectiflune.servlets.purl;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.web.extender.whiteboard.ExtenderConstants;
import org.ops4j.pax.web.extender.whiteboard.ResourceMapping;
import org.ops4j.pax.web.extender.whiteboard.runtime.DefaultResourceMapping;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private static BundleContext context;
	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(Activator.class);
	private ServiceRegistration<?> m_servletReg;
	private ServiceRegistration<?> m_resourcesReg;
	private ServiceRegistration<?> m_filterReg;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		Dictionary<String, String> props = new Hashtable<String,String>();
		props.put("alias", "/miro");
		m_servletReg = bundleContext.registerService(Servlet.class.getName(), new PurlServlet("/miro"), props);

		DefaultResourceMapping resourceMapping = new DefaultResourceMapping();
		resourceMapping.setAlias("/");
		resourceMapping.setPath("/www");
		m_resourcesReg = bundleContext.registerService(
				ResourceMapping.class.getName(),
				resourceMapping, null);

		try {
			// register a filter
			props = new Hashtable<String,String>();
			props.put(ExtenderConstants.PROPERTY_URL_PATTERNS, "/miro/*");
			m_filterReg = bundleContext.registerService(
					Filter.class.getName(), new PurlFilter(),
					props);
		} catch (NoClassDefFoundError ignore) {
			// in this case most probably that we do not have a
			// servlet
			// version >= 2.3
			// required by our filter
			LOG.warn("Cannot start filter example (javax.servlet version?): "
					+ ignore.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		if (m_servletReg != null) {
			m_servletReg.unregister();
			m_servletReg = null;
		}
		if (m_resourcesReg != null) {
			m_resourcesReg.unregister();
			m_resourcesReg = null;
		}
		if (m_filterReg != null) {
			m_filterReg.unregister();
			m_filterReg = null;
		}
		Activator.context = null;
	}

}
