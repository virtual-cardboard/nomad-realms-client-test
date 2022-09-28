package model.state;

import static model.GameObject.UNSET_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import derealizer.Derealizable;
import derealizer.Derealizer;
import derealizer.SerializationReader;
import derealizer.SerializationWriter;
import engine.common.math.Vector2i;
import model.GameObject;
import model.actor.Actor;
import model.actor.ActorEnum;
import model.actor.ItemActor;
import model.actor.health.Structure;
import model.actor.health.cardplayer.CardPlayer;
import model.actor.health.cardplayer.NpcActor;
import model.card.CardDashboard;
import model.card.WorldCard;
import model.chain.ChainHeap;
import model.hidden.HiddenGameObject;
import model.world.WorldMap;
import model.world.tile.Tile;

public class GameState implements Derealizable {

	private Map<Long, WorldCard> cards = new HashMap<>();
	private Map<Long, Actor> actors = new HashMap<>();
	private Map<Long, HiddenGameObject> hiddens = new HashMap<>();

	private transient List<CardPlayer> cardPlayers = new ArrayList<>();
	private transient List<NpcActor> npcs = new ArrayList<>();
	private transient List<Structure> structures = new ArrayList<>();
	private transient Map<Vector2i, List<Actor>> chunkToActors = new HashMap<>();
	private transient Map<Vector2i, List<Structure>> chunkToStructures = new HashMap<>();

	private transient WorldMap worldMap = new WorldMap();
	private ChainHeap chainHeap = new ChainHeap();

	public GameState() {
	}

	public GameState(byte[] bytes) {
		read(new SerializationReader(bytes));
	}

	@SuppressWarnings("unchecked")
	public <T extends GameObject> T getCorresponding(T object) {
		T corresponding = null;
		if (object instanceof Tile) {
			Tile tile = (Tile) object;
			corresponding = (T) worldMap().chunk(tile.longID()).tile(Tile.tileCoords(tile.longID()));
		} else {
			GameObject actor = object;
			corresponding = (T) actor(actor.longID());
		}
		return corresponding;
	}

	public Map<Long, HiddenGameObject> hiddens() {
		return hiddens;
	}

	public WorldMap worldMap() {
		return worldMap;
	}

	/**
	 * Should not be called by {@link GameObject#addTo(GameState)}
	 */
	public void add(GameObject object) {
		if (object.longID() == UNSET_ID) {
			throw new IllegalStateException("A " + object.getClass().getSimpleName() + " with an unset id cannot be added to a game state!\n" +
					"Maybe you forgot to generate its Id?");
		}
		object.addTo(this);
	}

	public void remove(GameObject object) {
		if (object.longID() == UNSET_ID) {
			throw new IllegalStateException("A " + object.getClass().getSimpleName() + " with an unset id cannot be removed from a game state!\n" +
					"Maybe you forgot to generate its Id?");
		}
		object.removeFrom(this);
	}

	public WorldCard card(long cardID) {
		return cards().get(cardID);
	}

	public Actor actor(long id) {
		return actors.get(id);
	}

	public Map<Long, Actor> actors() {
		return actors;
	}

	public List<CardPlayer> cardPlayers() {
		return cardPlayers;
	}

	public List<NpcActor> npcs() {
		return npcs;
	}

	public List<Structure> structures() {
		return structures;
	}

	public CardPlayer cardPlayer(Long id) {
		return (CardPlayer) actors.get(id);
	}

	public Structure structure(Long id) {
		return (Structure) actors.get(id);
	}

	public ItemActor item(Long id) {
		return (ItemActor) actors.get(id);
	}

	public List<Actor> actors(Vector2i key) {
		return chunkToActors().get(key);
	}

	public List<Actor> getActorsAroundChunk(Vector2i chunkPos) {
		List<Actor> actors = new ArrayList<>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				List<Actor> a = actors(chunkPos.add(j, i));
				if (a != null) {
					actors.addAll(a);
				}
			}
		}
		return actors;
	}

	public List<Structure> structures(Vector2i key) {
		return chunkToStructures().get(key);
	}

	public ChainHeap chainHeap() {
		return chainHeap;
	}

	public GameState copy() {
		GameState copy = new GameState();
		actors.forEach((Long id, Actor actor) -> {
			copy.add(actor.copy());
		});
		cardPlayers.forEach((CardPlayer player) -> {
			CardDashboard dashboard = player.cardDashboard().copy();
			copy.getCorresponding(player).setCardDashboard(dashboard);
			dashboard.hand().forEach(copy::add);
			dashboard.deck().forEach(copy::add);
			dashboard.discard().forEach(copy::add);
			dashboard.queue().forEach(cardPlayedEvent -> copy.add(cardPlayedEvent.cardID().getFrom(this)));
			if (player.cardDashboard().task() != null) {
				dashboard.setTask(player.cardDashboard().task().copy());
			}
		});
		copy.worldMap = worldMap.copy();
		copy.chainHeap = chainHeap.copy();
		return copy;
	}

	@Override
	public void read(SerializationReader reader) {
		for (byte i0 = 0, numElements0 = reader.readByte(); i0 < numElements0; i0++) {
			WorldCard pojo1 = new WorldCard();
			pojo1.read(reader);
			cards.put(pojo1.longID(), pojo1);
		}
		for (byte i0 = 0, numElements0 = reader.readByte(); i0 < numElements0; i0++) {
			Actor pojo1 = (Actor) Derealizer.recursiveRead(reader, ActorEnum.class);
			actors.put(pojo1.longID(), pojo1);
		}
	}

	@Override
	public void write(SerializationWriter writer) {
		writer.consume((byte) cards.size());
		cards.forEach((id, card) -> card.write(writer));
		writer.consume((byte) actors.size());
		actors.forEach((id, card) -> card.write(writer));
	}

	public Map<Vector2i, List<Actor>> chunkToActors() {
		return chunkToActors;
	}

	public Map<Vector2i, List<Structure>> chunkToStructures() {
		return chunkToStructures;
	}

	public Map<Long, WorldCard> cards() {
		return cards;
	}

}
