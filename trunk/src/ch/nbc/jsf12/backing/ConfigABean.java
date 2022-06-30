/**
 * Copyright (C) 2020 - NOVO Business Consultants AG
 * 
 * $LastChangedDate: $
 * $LastChangedRevision: $
 * $LastChangedBy: $
 */
package ch.nbc.jsf12.backing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import ch.nbc.jsf12.model.Destination;
import ch.nbc.jsf12.utils.SelectItemComparator;

public class ConfigABean {

	private static final Logger logger = Logger.getLogger(ConfigABean.class);

	private final String defaultDestination;
	private final Map<String, Destination> destinationMap;

	public ConfigABean() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("backend-config.properties");
		Properties properties = new Properties();
		try {
			properties.load(input);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}

		String defaultDestination = properties.getProperty("default.destination");
		this.defaultDestination = StringUtils.isBlank(defaultDestination) ? null : defaultDestination;

		Map<String, Destination> destinationMap = new HashMap<String, Destination>();
		if (defaultDestination != null) {
			destinationMap.put(defaultDestination, createDestination(properties, defaultDestination));
		}

		String destinations = properties.getProperty("destinations");
		if (StringUtils.isNotBlank(destinations)) {

			String[] destinationArray = destinations.split(",");
			for (String destId : destinationArray) {
				destinationMap.put(destId, createDestination(properties, destId));
			}
		}
		this.destinationMap = destinationMap;
	}

	private static Destination createDestination(Properties properties, String destId) {
		String keyProvider = "destination." + destId + ".provider";
		String provider = properties.getProperty(keyProvider);
		String keyLabel = "destination." + destId + ".label";
		String label = properties.getProperty(keyLabel);
		return new Destination(destId, provider, label);
	}

	public List<SelectItem> getDestinationItems() {
		List<SelectItem> items = new ArrayList<SelectItem>(4);
		for (Destination destination : destinationMap.values()) {
			String label = destination.getLabel();
			if (label == null) {
				label = destination.getId();
			} else {
				label += " (" + destination.getId() + ")";
			}
			SelectItem item = new SelectItem(destination.getId(), label);
			items.add(item);
		}
		Collections.sort(items, new SelectItemComparator());
		Collections.reverse(items);
		return items;
	}

	public int getDestinationCounter() {
		return destinationMap.size();
	}

	public String getDefaultDestination() {
		return defaultDestination;
	}

	public Map<String, Destination> getDestinationMap() {
		return destinationMap;
	}
}
