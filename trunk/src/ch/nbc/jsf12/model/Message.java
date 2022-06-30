/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class Message {

	public class TYPE {
		public static final int INFO = 1;
		public static final int WARN = 2;
		public static final int ERROR = 3;
		public static final int FATAL = 4;
	}

	private final int type;
	private final String text;
	private final Date timestamp = new Date();

	public Message(int type, String message) {
		if (type == TYPE.INFO || type == TYPE.WARN || type == TYPE.ERROR || type == TYPE.FATAL) {
			this.type = type;
		} else {
			this.type = TYPE.ERROR;
		}
		if (StringUtils.isNotBlank(message)) {
			this.text = message;
		} else {
			this.text = "";
		}
	}

	public String getText() {
		return text;
	}

	public int getType() {
		return type;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + type;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Message other = (Message) obj;
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Message [text=");
		buffer.append(text);
		buffer.append(", type=");
		buffer.append(type);
		buffer.append(", timestamp=");
		buffer.append(timestamp);
		buffer.append("]");
		return buffer.toString();
	}

}
