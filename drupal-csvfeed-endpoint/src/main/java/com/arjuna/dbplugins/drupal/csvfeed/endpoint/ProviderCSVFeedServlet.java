/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.drupal.csvfeed.endpoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.arjuna.dbplugins.drupal.csvfeed.dataflownodes.CommonDefs;
import com.arjuna.dbplugins.drupal.csvfeed.dataflownodes.ProviderCSVFeedJunction;

@WebServlet(CommonDefs.CSVFEED_SERVICE_PATH)
public class ProviderCSVFeedServlet extends HttpServlet
{
	private static final long serialVersionUID = -3645269692852995742L;

	private static final Logger logger = Logger.getLogger(ProviderCSVFeedServlet.class.getName());

    public ProviderCSVFeedServlet()
    {
        logger.log(Level.FINE, "ProviderCSVFeedServlet");
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
        throws ServletException, IOException
    {
        logger.log(Level.FINE, "ProviderCSVFeedServlet.doGet");

        try
        {
            if (_providerCSVFeedJunction != null)
            {
                String id = httpServletRequest.getPathInfo().substring(1);

                logger.log(Level.FINE, "ProviderCSVFeedServlet.doGet: id = " + id);

                httpServletResponse.setContentType("text/csv");
                if (id != null)
                {
                    String csv = _providerCSVFeedJunction.withdraw(id);

                    if (csv != null)
                    {
                        PrintWriter writer = httpServletResponse.getWriter();

                        writer.print(csv);

                        writer.close();
                    }
                    else
                        logger.log(Level.FINE, "ProviderCSVFeedServlet no content");
                }
                else
                {
                    logger.log(Level.WARNING, "ProviderCSVFeedServlet no id");
                    httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
            else
            {
                logger.log(Level.WARNING, "ProviderCSVFeedServlet no provider XML feed junction");
                httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        catch (IOException ioException)
        {
            logger.log(Level.WARNING, "ProviderCSVFeedServlet ", ioException);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "ProviderCSVFeedServlet ", throwable);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @EJB
    private ProviderCSVFeedJunction _providerCSVFeedJunction;
}
