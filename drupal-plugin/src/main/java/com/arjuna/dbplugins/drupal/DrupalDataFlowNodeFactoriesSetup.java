/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.drupal;

import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import com.arjuna.databroker.data.DataFlowNodeFactory;
import com.arjuna.databroker.data.DataFlowNodeFactoryInventory;
import com.arjuna.dbplugins.drupal.xmlfeed.XMLFeedDataFlowNodeFactory;
import com.arjuna.dbplugins.drupal.csvfeed.CSVFeedDataFlowNodeFactory;

@Startup
@Singleton
public class DrupalDataFlowNodeFactoriesSetup
{
    @PostConstruct
    public void setup()
    {
        DataFlowNodeFactory xmlFeedDataFlowNodeFactory = new XMLFeedDataFlowNodeFactory("Drupal XMLFeed Data Flow Node Factories", Collections.<String, String>emptyMap());
        DataFlowNodeFactory csvFeedDataFlowNodeFactory = new CSVFeedDataFlowNodeFactory("Drupal CSVFeed Data Flow Node Factories", Collections.<String, String>emptyMap());

        _dataFlowNodeFactoryInventory.addDataFlowNodeFactory(xmlFeedDataFlowNodeFactory);
        _dataFlowNodeFactoryInventory.addDataFlowNodeFactory(csvFeedDataFlowNodeFactory);
    }

    @PreDestroy
    public void cleanup()
    {
        _dataFlowNodeFactoryInventory.removeDataFlowNodeFactory("Drupal XMLFeed Data Flow Node Factories");
        _dataFlowNodeFactoryInventory.removeDataFlowNodeFactory("Drupal CSVFeed Data Flow Node Factories");
    }

    @EJB(lookup="java:global/databroker/control-core/DataFlowNodeFactoryInventory")
    private DataFlowNodeFactoryInventory _dataFlowNodeFactoryInventory;
}
