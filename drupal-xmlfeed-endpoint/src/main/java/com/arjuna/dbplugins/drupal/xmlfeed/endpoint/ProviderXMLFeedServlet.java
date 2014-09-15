/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.drupal.xmlfeed.endpoint;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import com.arjuna.dbplugins.drupal.xmlfeed.dataflownodes.CommonDefs;
import com.arjuna.dbplugins.drupal.xmlfeed.dataflownodes.ProviderXMLFeedJunction;

@WebServlet(CommonDefs.XMLFEED_SERVICE_PATH)
public class ProviderXMLFeedServlet extends HttpServlet
{
    private static final long serialVersionUID = -5349864860416735754L;

    private static final Logger logger = Logger.getLogger(ProviderXMLFeedServlet.class.getName());

    public ProviderXMLFeedServlet()
    {
        logger.log(Level.FINE, "ProviderXMLFeedServlet");
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
        throws ServletException, IOException
    {
        logger.log(Level.FINE, "ProviderXMLFeedServlet.doGet");

        try
        {
            if (_providerXMLFeedJunction != null)
            {
                String id = httpServletRequest.getPathInfo().substring(1);

                logger.log(Level.FINE, "ProviderXMLFeedServlet.doGet: id = " + id);

                if (id != null)
                {
                    Document document = _providerXMLFeedJunction.withdraw(id);

                    httpServletResponse.setContentType("text/xml");
                    if (document != null)
                    {
                        DOMSource    domSource    = new DOMSource(document);
                        StreamResult streamResult = new StreamResult(httpServletResponse.getWriter());

                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer        transformer        = transformerFactory.newTransformer();
                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                        transformer.transform(domSource, streamResult);
                    }
                    else
                        logger.log(Level.FINE, "ProviderXMLFeedServlet no content");
                }
                else
                {
                    logger.log(Level.WARNING, "ProviderXMLFeedServlet no id");
                    httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
            else
            {
                logger.log(Level.WARNING, "ProviderXMLFeedServlet no provider XML feed junction");
                httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        catch (IOException ioException)
        {
            logger.log(Level.WARNING, "ProviderXMLFeedServlet ", ioException);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "ProviderXMLFeedServlet ", throwable);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @EJB
    private ProviderXMLFeedJunction _providerXMLFeedJunction;
}
