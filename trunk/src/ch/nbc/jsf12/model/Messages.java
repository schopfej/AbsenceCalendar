/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.model;

import java.util.HashSet;
import java.util.Set;

public class Messages {

	private final Set<Message> messages = new HashSet<Message>();

	public void addMessage(Message message) {
		messages.add(message);
	}

	public void addAllMessages(Set<Message> messages) {
		this.messages.addAll(messages);
	}

	public void addAllMessages(Messages messages) {
		if (messages == null) {
			return;
		}
		this.messages.addAll(messages.getMessages());
	}

	public Set<Message> getInfoMessages() {
		Set<Message> result = new HashSet<Message>();
		for (Message msg : messages) {
			if (msg.getType() == Message.TYPE.INFO) {
				result.add(msg);
			}
		}
		return result;
	}

	public Set<Message> getWarnMessages() {
		Set<Message> result = new HashSet<Message>();
		for (Message msg : messages) {
			if (msg.getType() == Message.TYPE.WARN) {
				result.add(msg);
			}
		}
		return result;
	}

	public Set<Message> getErrorMessages() {
		Set<Message> result = new HashSet<Message>();
		for (Message msg : messages) {
			if (msg.getType() == Message.TYPE.ERROR) {
				result.add(msg);
			}
		}
		return result;
	}

	public Set<Message> getMessages() {
		return messages;
	}

}
