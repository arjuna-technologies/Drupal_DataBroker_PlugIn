/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.drupal.xmlfeed;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.arjuna.databroker.data.DataFlowNode;
import com.arjuna.databroker.data.DataFlowNodeFactory;
import com.arjuna.databroker.data.DataService;
import com.arjuna.databroker.data.DataSink;
import com.arjuna.databroker.data.DataSource;
import com.arjuna.databroker.data.InvalidClassException;
import com.arjuna.databroker.data.InvalidMetaPropertyException;
import com.arjuna.databroker.data.InvalidNameException;
import com.arjuna.databroker.data.InvalidPropertyException;
import com.arjuna.databroker.data.MissingMetaPropertyException;
import com.arjuna.databroker.data.MissingPropertyException;
import com.arjuna.dbplugins.drupal.xmlfeed.dataflownodes.ProviderXMLFeedDataService;

public class XMLFeedDataFlowNodeFactory implements DataFlowNodeFactory
{
    public XMLFeedDataFlowNodeFactory(String name, Map<String, String> properties)
    {
        _name       = name;
        _properties = properties;
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return _properties;
    }

    @Override
    public List<Class<? extends DataFlowNode>> getClasses()
    {
        List<Class<? extends DataFlowNode>> classes = new LinkedList<Class<? extends DataFlowNode>>();

        classes.add(DataService.class);

        return classes;
    }

    @Override
    public <T extends DataFlowNode> List<String> getMetaPropertyNames(Class<T> dataFlowNodeClass)
        throws InvalidClassException
    {
        if (dataFlowNodeClass.equals(DataService.class))
        {
            List<String> metaPropertyNames = new LinkedList<String>();

            return metaPropertyNames;
        }
        else
            throw new InvalidClassException("Unsupported class", dataFlowNodeClass.getName());
    }

    @Override
    public <T extends DataFlowNode> List<String> getPropertyNames(Class<T> dataFlowNodeClass, Map<String, String> metaProperties)
        throws InvalidClassException, InvalidMetaPropertyException, MissingMetaPropertyException
    {
        if (dataFlowNodeClass.equals(DataService.class))
        {
            List<String> propertyNames = new LinkedList<String>();

            propertyNames.add(ProviderXMLFeedDataService.ENDPOINTPATH_PROPERTYNAME);

            return propertyNames;
        }
        else
            throw new InvalidClassException("Unsupported class", dataFlowNodeClass.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataFlowNode> T createDataFlowNode(String name, Class<T> dataFlowNodeClass, Map<String, String> metaProperties, Map<String, String> properties)
        throws InvalidClassException, InvalidNameException, InvalidMetaPropertyException, MissingMetaPropertyException, InvalidPropertyException, MissingPropertyException
    {
        if (dataFlowNodeClass.equals(DataSource.class))
        {
            if ((metaProperties.size() == 1) && metaProperties.containsKey("Type"))
            {
                String type = metaProperties.get("Type");
                if (type.equals("Pull"))
                    return (T) new PullWebServiceDataSource(name, properties);
                else if (type.equals("Acceptor"))
                    return (T) new AcceptorWebServiceDataSource(name, properties);
                else
                    throw new InvalidMetaPropertyException("Expecting value of 'Type' meta property 'Pull' or 'Acceptor'", "Type", type);
            }
            else
                throw new InvalidMetaPropertyException("No metaproperties expected", null, null);
        }
        else if (dataFlowNodeClass.equals(DataSink.class))
        {
            if ((metaProperties.size() == 1) && metaProperties.containsKey("Type"))
            {
                String type = metaProperties.get("Type");
                if (type.equals("Push"))
                    return (T) new PushWebServiceDataSink(name, properties);
                else if (type.equals("Provider"))
                    return (T) new ProviderXMLFeedDataService(name, properties);
                else
                    throw new InvalidMetaPropertyException("Expecting value of 'Type' meta property 'Push' or 'Provider'", "Type", type);
            }
            else
                throw new InvalidMetaPropertyException("No metaproperties expected", null, null);
        }
        else
            throw new InvalidClassException("Unsupported class", dataFlowNodeClass.getName());
    }

    private String              _name;
    private Map<String, String> _properties;
}