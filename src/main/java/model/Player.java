package model;

import derealizer.Derealizable;
import derealizer.SerializationReader;
import derealizer.SerializationWriter;

public class Player implements Derealizable {

	private long uuid;
	private String username;

	public Player() {
	}

	public Player(long uuid, String username) {
		this.uuid = uuid;
		this.username = username;
	}

	public Player(byte[] bytes) {
		read(new SerializationReader(bytes));
	}

	@Override
	public void read(SerializationReader reader) {
		this.uuid = reader.readLong();
		this.username = reader.readStringUtf8();
	}

	@Override
	public void write(SerializationWriter writer) {
		writer.consume(uuid);
		writer.consume(username);
	}

	public long uuid() {
		return uuid;
	}

	public String username() {
		return username;
	}

}
