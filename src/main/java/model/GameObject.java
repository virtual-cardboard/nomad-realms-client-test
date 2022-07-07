package model;

import derealizer.SerializationReader;
import derealizer.SerializationWriter;
import derealizer.format.SerializationPojo;
import math.IDGenerator;
import model.id.ID;
import model.state.GameState;

/**
 * Any object in the game that can be visually represented.
 *
 * @author Jay
 */
public abstract class GameObject implements SerializationPojo<ModelSerializationFormats> {

	protected long id;

	public GameObject() {
		id = genID();
	}

	public GameObject(long id) {
		this.id = id;
	}

	protected long genID() {
		return IDGenerator.genID();
	}

	public abstract ID id();

	public final long longID() {
		return id;
	}

	public abstract GameObject copy();

	public abstract String description();

	public abstract void addTo(GameState state);

	@Override
	public void read(SerializationReader reader) {
		this.id = reader.readLong();
	}

	@Override
	public void write(SerializationWriter writer) {
		writer.consume(id);
	}

}
