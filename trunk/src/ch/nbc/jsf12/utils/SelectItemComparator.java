/**
 * Copyright (C) 2012 - NOVO Business Consultants AG
 * 
 * $LastChangedDate: 2012-04-04 11:08:05 +0200 (Mi, 04 Apr 2012) $
 * $LastChangedRevision: 3558 $
 * $LastChangedBy: bes02 $
 */
package ch.nbc.jsf12.utils;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import javax.faces.model.SelectItem;

public class SelectItemComparator implements Comparator<SelectItem> {

	private final Collator collator;

	public SelectItemComparator() {
		Collator collator = Collator.getInstance(Locale.GERMAN);
		collator.setStrength(Collator.SECONDARY);
		this.collator = collator;
	}

	public int compare(SelectItem oneItem, SelectItem anotherItem) {
		String oneLabel = oneItem.getLabel();
		String anotherLabel = anotherItem.getLabel();
		return collator.compare(oneLabel, anotherLabel);
	}
}
