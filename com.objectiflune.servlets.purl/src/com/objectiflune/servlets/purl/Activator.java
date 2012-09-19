package com.objectiflune.servlets.purl;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.concurrent.Executors;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.web.extender.whiteboard.ExtenderConstants;
import org.ops4j.pax.web.extender.whiteboard.ResourceMapping;
import org.ops4j.pax.web.extender.whiteboard.runtime.DefaultResourceMapping;
import org.ops4j.pax.web.service.WebContainer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;

@SuppressWarnings("rawtypes")
public class Activator implements BundleActivator {

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(Activator.class);

	private ServiceRegistration m_rootServletReg;
	private ServiceRegistration m_servletReg;
	private ServiceRegistration m_resourcesReg;
	private ServiceRegistration m_filterReg;
	private ServiceRegistration m_listenerReg;
	private ServiceRegistration m_httpContextReg;
	private ServiceRegistration m_forbiddenServletReg;
	private Thread starter;
	private ServiceReference<WebContainer> webContainerRef;

	@SuppressWarnings("unchecked")
	public void start(final BundleContext bundleContext) throws Exception {

		starter = Executors.defaultThreadFactory().newThread(new Runnable() {

			@Override
			public void run() {				
				try {
					doRun();
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
			}
		
			void doRun() throws Exception{
				int counter = 0;
				boolean started = false;
				while (!started) {

					webContainerRef = bundleContext.getServiceReference(WebContainer.class);
					started = webContainerRef != null;
					if (started) {

						WebContainer container = bundleContext
								.getService(webContainerRef);
						PurlVirtualHostContext context = new PurlVirtualHostContext(
								"miro.objectiflune.local");
						container.setConnectors(Arrays.asList("8989"), context);
						container.setVirtualHosts(
								Arrays.asList("miro.objectiflune.local"),
								context);
						
						Dictionary initparams = new Hashtable();
						initparams.put("virtualHosts",
								"miro.objectiflune.local");

						try {
							container.registerServlet("/", new PurlServlet(
									"/root"), initparams, context);
						} catch (ServletException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NamespaceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Dictionary props;

						// register a custom http context that forbids access
						props = new Hashtable();
						props.put(ExtenderConstants.PROPERTY_HTTP_CONTEXT_ID,
								"forbidden");
						m_httpContextReg = bundleContext.registerService(
								HttpContext.class.getName(), new PurlContext(),
								props);
						// and an servlet that cannot be accessed due to the
						// above
						// context
						props = new Hashtable();
						props.put(ExtenderConstants.PROPERTY_ALIAS,
								"/forbidden");
						props.put(ExtenderConstants.PROPERTY_HTTP_CONTEXT_ID,
								"forbidden");
						m_forbiddenServletReg = bundleContext.registerService(
								Servlet.class.getName(), new PurlServlet(
										"/forbidden"), props);

						props = new Hashtable();
						props.put("alias", "/purl");
						m_servletReg = bundleContext.registerService(
								Servlet.class.getName(), new PurlServlet(
										"/purl"), props);

						props = new Hashtable();
						props.put("alias", "/erik");
						m_servletReg = bundleContext.registerService(
								Servlet.class.getName(), new PurlServlet(
										"/erik"), props);

						props = new Hashtable();
						props.put("alias", "/miro");
						m_servletReg = bundleContext.registerService(
								Servlet.class.getName(), new PurlServlet(
										"/miro"), props);

						props = new Hashtable();
						props.put("alias", "/");
						m_rootServletReg = bundleContext.registerService(
								HttpServlet.class.getName(), new PurlServlet(
										"/root"), props);

						DefaultResourceMapping resourceMapping = new DefaultResourceMapping();
						resourceMapping.setAlias("/purlresource");
						resourceMapping.setPath("/images");
						m_resourcesReg = bundleContext.registerService(
								ResourceMapping.class.getName(),
								resourceMapping, null);

						try {
							// register a filter
							props = new Hashtable();
							props.put(ExtenderConstants.PROPERTY_URL_PATTERNS,
									"/purl/filtered/*");
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

						try {
							// register a servlet request listener
							m_listenerReg = bundleContext.registerService(
									EventListener.class.getName(),
									new PurlListener(), null);
						} catch (NoClassDefFoundError ignore) {
							// in this case most probably that we do not have a
							// servlet
							// version >= 2.4
							// required by our request listener
							LOG.warn("Cannot start filter example (javax.servlet version?): "
									+ ignore.getMessage());
						}
					}else{
						// wait, throw exception after 5 retries.
		                if( counter > 10 ) {
		                    throw new Exception( "Could not start the PURLS service, WebContainer service not started or not available." );
		                }
		                else 
		                {
		                   counter++;
		                   Thread.sleep( counter * 1000 );
		                }
					}
				}
			}
		});

		starter.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		if (m_rootServletReg != null) {
			m_rootServletReg.unregister();
			m_rootServletReg = null;
		}
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
		if (m_listenerReg != null) {
			m_listenerReg.unregister();
			m_listenerReg = null;
		}
		if (m_httpContextReg != null) {
			m_httpContextReg.unregister();
			m_httpContextReg = null;
		}
		if (m_forbiddenServletReg != null) {
			m_forbiddenServletReg.unregister();
			m_forbiddenServletReg = null;
		}
		if (starter != null) {
			starter.interrupt();
		}
	}

}
