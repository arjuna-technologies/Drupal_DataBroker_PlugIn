/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.drupal.csvfeed.dataflownodes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import com.arjuna.databroker.data.DataConsumer;
import com.arjuna.databroker.data.DataFlow;
import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataService;
import com.arjuna.databroker.data.jee.annotation.DataConsumerInjection;
import com.arjuna.databroker.data.jee.annotation.DataProviderInjection;

public class ProviderCSVFeedDataService implements DataService
{
    private static final Logger logger = Logger.getLogger(ProviderCSVFeedDataService.class.getName());

    public static final String CSVFEEDID_PROPERTYNAME = "CSV Feed ID";

    public ProviderCSVFeedDataService(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "ProviderCSVFeedDataService: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _endpointId = properties.get(CSVFEEDID_PROPERTYNAME);

        try
        {
            _providerCSVServiceJunction = (ProviderCSVFeedJunction) new InitialContext().lookup("java:global/drupal-plugin-ear-1.0.0p1m1/drupal-csvfeed-1.0.0p1m1/ProviderCSVFeedJunction");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "ProviderCSVFeedDataService: no providerWebServiceJunction found", throwable);
        }
    }

    @Override
    public DataFlow getDataFlow()
    {
    	return _dataFlow;
    }

    @Override
    public void setDataFlow(DataFlow dataFlow)
    {
    	_dataFlow = dataFlow;
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public void setName(String name)
    {
        _name = name;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.unmodifiableMap(_properties);
    }

    @Override
    public void setProperties(Map<String, String> properties)
    {
        _properties = properties;
    }

    public void consume(String data)
    {
        logger.log(Level.FINE, "ProviderCSVFeedDataService.consume");

        if (_providerCSVServiceJunction != null)
            _providerCSVServiceJunction.deposit(_endpointId, data);
        else
            logger.log(Level.WARNING, "ProviderCSVFeedDataService.consume: no providerWebServiceJunction");
    }

    @Override
    public Collection<Class<?>> getDataConsumerDataClasses()
    {
        Set<Class<?>> dataConsumerDataClasses = new HashSet<Class<?>>();

        dataConsumerDataClasses.add(String.class);

        return dataConsumerDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataConsumer<T> getDataConsumer(Class<T> dataClass)
    {
        if (dataClass == String.class)
            return (DataConsumer<T>) _dataConsumer;
        else
            return null;
    }

    @Override
    public Collection<Class<?>> getDataProviderDataClasses()
    {
        Set<Class<?>> dataProviderDataClasses = new HashSet<Class<?>>();

        dataProviderDataClasses.add(String.class);

        return dataProviderDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataProvider<T> getDataProvider(Class<T> dataClass)
    {
        if (dataClass == String.class)
            return (DataProvider<T>) _dataProvider;
        else
            return null;
    }

    private String _endpointId;

    private DataFlow             _dataFlow;
    private String               _name;
    private Map<String, String>  _properties;
    @DataConsumerInjection(methodName="consume")
    private DataConsumer<String> _dataConsumer;
    @DataProviderInjection
    private DataProvider<String> _dataProvider;

    private ProviderCSVFeedJunction _providerCSVServiceJunction;
}
