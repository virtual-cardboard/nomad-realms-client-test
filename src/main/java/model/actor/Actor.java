package model.actor;

import static math.IDGenerator.genID;

import java.util.Map;

import common.source.GameSource;

public abstract class Actor implements GameSource {

	protected long id;

	public Actor() {
		id = genID();
	}

	public long id() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public abstract Actor copy();

	public <A extends PositionalActor> A copyTo(A copy) {
		copy.id = id;
		return copy;
	}

	public void addTo(Map<Long, Actor> actors, Map<Long, CardPlayer> cardPlayers) {
		actors.put(id, this);
	}

}
