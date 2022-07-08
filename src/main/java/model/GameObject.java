package model;

import derealizer.SerializationReader;
import derealizer.SerializationWriter;
import derealizer.format.Serializable;
import math.IdGenerator;
import model.id.Id;
import model.state.GameState;

/**
 * Any object in the game that can be visually represented.
 *
 * @author Jay
 */
public abstract class GameObject implements Serializable {

	/**
	 * When a GameObject has its id equal to UNSET_ID, then its id hasn't been set.
	 */
	public static final long UNSET_ID = -401;

	/**
	 * Unique long representing this {@link GameObject}.
	 * <p>
	 * When creating a new <code>GameObject</code>, the id should either be generated using an {@link IdGenerator} or it
	 * should be read from a byte array (when the object is being deserialized).
	 */
	protected long id = UNSET_ID;

	public GameObject() {
	}

	public GameObject(long id) {
		this.id = id;
	}

	public abstract Id id();

	public final long longID() {
		return id;
	}

	public void setId(long id) {
		if (id != UNSET_ID) {

		}
		this.id = id;
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
