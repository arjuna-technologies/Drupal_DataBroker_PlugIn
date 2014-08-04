/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.drupal.xmlfeed.dataflownodes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import org.risbic.intraconnect.basic.BasicDataConsumer;
import org.risbic.intraconnect.basic.BasicDataProvider;
import org.w3c.dom.Document;
import com.arjuna.databroker.data.DataConsumer;
import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataService;

public class ProviderXMLFeedDataService implements DataService
{
    private static final Logger logger = Logger.getLogger(ProviderXMLFeedDataService.class.getName());

    public static final String XMLFEEDID_PROPERTYNAME = "XML Feed ID";

    public ProviderXMLFeedDataService(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "ProviderXMLFeedDataService: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _dataConsumer = new BasicDataConsumer<Document>(this, "consume", Document.class);
        _dataProvider = new BasicDataProvider<Document>(this);

        _endpointId = properties.get(XMLFEEDID_PROPERTYNAME);

        try
        {
            _providerWebServiceJunction = (ProviderXMLFeedJunction) new InitialContext().lookup("java:global/drupal-plugin-ear-1.0.0p1m1/drupal-xmlfeed-1.0.0p1m1/ProviderXMLFeedJunction");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "ProviderXMLFeedDataService: no providerWebServiceJunction found", throwable);
        }
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.unmodifiableMap(_properties);
    }

    public void consume(Document data)
    {
        logger.log(Level.FINE, "ProviderXMLFeedDataService.consume");

        if (_providerWebServiceJunction != null)
            _providerWebServiceJunction.deposit(_endpointId, data);
        else
            logger.log(Level.WARNING, "ProviderXMLFeedDataService.consume: no providerWebServiceJunction");
    }

    @Override
    public Collection<Class<?>> getDataConsumerDataClasses()
    {
        Set<Class<?>> dataConsumerDataClasses = new HashSet<Class<?>>();

        dataConsumerDataClasses.add(Document.class);

        return dataConsumerDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataConsumer<T> getDataConsumer(Class<T> dataClass)
    {
        if (dataClass == Document.class)
            return (DataConsumer<T>) _dataConsumer;
        else
            return null;
    }

    @Override
    public Collection<Class<?>> getDataProviderDataClasses()
    {
        Set<Class<?>> dataProviderDataClasses = new HashSet<Class<?>>();

        dataProviderDataClasses.add(Document.class);

        return dataProviderDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataProvider<T> getDataProvider(Class<T> dataClass)
    {
        if (dataClass == Document.class)
            return (DataProvider<T>) _dataProvider;
        else
            return null;
    }

    private String _endpointId;

    private String                 _name;
    private Map<String, String>    _properties;
    private DataConsumer<Document> _dataConsumer;
    private DataProvider<Document> _dataProvider;

    private ProviderXMLFeedJunction _providerWebServiceJunction;
}
