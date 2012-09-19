package com.objectiflune.servlets.purl;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PurlListener
    implements ServletRequestListener
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( PurlListener.class );

    public void requestInitialized( final ServletRequestEvent sre )
    {
        LOG.info( "Request initialized from ip: " + sre.getServletRequest().getRemoteAddr() );
    }

    public void requestDestroyed( final ServletRequestEvent sre )
    {
        LOG.info( "Request destroyed from ip:" + sre.getServletRequest().getRemoteAddr() );
    }

}
