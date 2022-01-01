package model.actor;

import java.util.List;
import java.util.Map;

import common.math.Vector2i;
import math.IDGenerator;
import model.card.GameCard;
import model.state.GameState;

/**
 * Any object in the game that can be visually represented.
 * 
 * @author Jay
 */
public abstract class GameObject {

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

	public long id() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}

	public abstract GameObject copy(GameState gameState);

	public abstract String description();

	public void addTo(Map<Long, Actor> actors, Map<Long, CardPlayer> cardPlayers, Map<Long, GameCard> cards, Map<Vector2i, List<Actor>> chunkToActors) {
	}

}
