/*
 * Copyright (c) 2013-2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.drupal.xmlfeed.tmptest;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import com.arjuna.databroker.data.DataConsumer;
import com.arjuna.dbplugins.drupal.xmlfeed.dataflownodes.ProviderXMLFeedDataService;

@Startup
@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DataFlowSetup implements Serializable
{
    private static final Logger logger = Logger.getLogger(DataFlowSetup.class.getName());

    private static final long serialVersionUID = 1124426492239508535L;

    @PostConstruct
    public void setup()
    {
        try
        {
            String              name       = "XML Feed";
            Map<String, String> properties = new HashMap<String, String>();
            properties.put(ProviderXMLFeedDataService.XMLFEEDID_PROPERTYNAME, "test");

            ProviderXMLFeedDataService providerXMLFeedDataService = new ProviderXMLFeedDataService(name, properties);

            FeedSourceThread feedSourceThread = new FeedSourceThread(providerXMLFeedDataService);
            feedSourceThread.start();
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "DataFlowSetup: FeedSourceThread problem ", throwable);
        }
    }

    private class FeedSourceThread extends Thread
    {
        public FeedSourceThread(ProviderXMLFeedDataService providerXMLFeedDataService)
        {
            _providerXMLFeedDataService = providerXMLFeedDataService;
        }

        public void run()
        {
            try
            {
                Thread.sleep(15000);

                DataConsumer<Document> dataConsumer = _providerXMLFeedDataService.getDataConsumer(Document.class);

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder        documentBuilder        = documentBuilderFactory.newDocumentBuilder();
                for (int index = 0; index < 4; index++)
                {
                    Document document     = documentBuilder.newDocument();
                    Element  rootElement  = document.createElement("feed");
                    Element  infoElement  = document.createElement("info");
                    Element  titleElement = document.createElement("title");
                    Element  geoElement   = document.createElement("geo");
                    Text     titleText    = document.createTextNode("101");
                    Text     geoText      = document.createTextNode("{\"type\":\"Point\",\"coordinates\":[-1.6" + index + ",55]}");

                    document.appendChild(rootElement);
                    rootElement.appendChild(infoElement);
                    infoElement.appendChild(titleElement);
                    infoElement.appendChild(geoElement);
                    titleElement.appendChild(titleText);
                    geoElement.appendChild(geoText);

                    dataConsumer.consume(null, document);

                    Thread.sleep(10000);
                }
            }
            catch (Throwable throwable)
            {
                logger.log(Level.WARNING, "FeedSourceThread: run problem ", throwable);
            }
        }

        private ProviderXMLFeedDataService _providerXMLFeedDataService;
    }
}
