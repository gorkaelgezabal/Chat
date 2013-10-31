package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
	private long timestamp;
	private String text;
	private String from;
	private String to;
	
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {
			Message otherMsg = (Message)obj;
			
			return this.timestamp == otherMsg.timestamp &&
				   this.text.equals(otherMsg.text) &&
				   this.from.equals(otherMsg.from) &&
				   this.to.equals(otherMsg.to);
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "[" + dateFormatter.format(new Date(this.timestamp)) + "] '" + 
	           this.from + " -> " + this.to + " : " + this.text; 
				
	}
}