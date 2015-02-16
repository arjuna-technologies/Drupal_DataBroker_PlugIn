/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.drupal.csvfeed.dataflownodes;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;

@Singleton
public class ProviderCSVFeedJunction
{
    private static final Logger logger = Logger.getLogger(ProviderCSVFeedJunction.class.getName());

    public ProviderCSVFeedJunction()
    {
        logger.log(Level.FINE, "ProviderCSVFeedJunction");

        _syncObject  = new Object();
        _documentMap = new HashMap<String, String>();
    }

    public void deposit(String id, String csv)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "ProviderCSVFeedJunction.deposit: " + id);

            _documentMap.put(id, csv);
        }
    }

    public String withdraw(String id)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "ProviderCSVFeedJunction.withdraw: " + id);

            return _documentMap.get(id);
        }
    }

    private Object              _syncObject;
    private Map<String, String> _documentMap;
}
