/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.drupal.xmlfeed.dataflownodes;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import org.w3c.dom.Document;

@Singleton
public class ProviderXMLFeedJunction
{
    private static final Logger logger = Logger.getLogger(ProviderXMLFeedJunction.class.getName());

    public ProviderXMLFeedJunction()
    {
        logger.log(Level.FINE, "ProviderWebServiceJunction");

        _syncObject  = new Object();
        _documentMap = new HashMap<String, Document>();
    }

    public void deposit(String id, Document document)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "ProviderWebServiceJunction.deposit: " + id);

            _documentMap.put(id, document);
        }
    }

    public Document withdraw(String id)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "ProviderWebServiceJunction.withdraw: " + id);

            return _documentMap.remove(id);
        }
    }

    private Object                _syncObject;
    private Map<String, Document> _documentMap;
}