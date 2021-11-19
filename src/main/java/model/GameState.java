package model;

import static model.card.CardRarity.ARCANE;
import static model.card.CardRarity.BASIC;
import static model.card.CardType.ACTION;
import static model.card.CardType.CANTRIP;
import static model.card.effect.CardTargetType.CHARACTER;
import static model.card.effect.CardTargetType.TILE;
import static model.tile.TileType.GRASS;
import static model.tile.TileType.SAND;
import static model.tile.TileType.WATER;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import common.math.Vector2l;
import model.actor.Actor;
import model.actor.CardPlayer;
import model.actor.HealthActor;
import model.actor.Nomad;
import model.card.CardDashboard;
import model.card.GameCard;
import model.card.effect.CardEffect;
import model.card.effect.DealDamageExpression;
import model.card.effect.DrawCardExpression;
import model.card.effect.TeleportExpression;
import model.chain.ChainHeap;
import model.tile.TileChunk;
import model.tile.TileMap;
import model.tile.TileType;

public class GameState {

	private TileMap tileMap;
	private Map<Long, Actor> actors = new HashMap<>();
	private Map<Long, GameCard> cards = new HashMap<>();
	private Map<Long, CardPlayer> cardPlayers = new HashMap<>();
	private Map<CardPlayer, CardDashboard> dashboards = new HashMap<>();
	private ChainHeap chainHeap = new ChainHeap();

	public GameState() {
		TileType[][] chunk1 = {
				{ GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ GRASS, SAND, SAND, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ SAND, WATER, WATER, WATER, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ SAND, GRASS, WATER, WATER, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ SAND, WATER, WATER, WATER, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ GRASS, SAND, SAND, SAND, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ SAND, GRASS, WATER, WATER, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ SAND, WATER, WATER, WATER, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ GRASS, SAND, SAND, SAND, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ SAND, GRASS, WATER, WATER, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ SAND, WATER, WATER, WATER, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ GRASS, SAND, SAND, SAND, SAND, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS },
				{ GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS, GRASS }
		};
		tileMap = new TileMap();
		tileMap.addChunk(new TileChunk(new Vector2l(), chunk1));
		tileMap.addChunk(new TileChunk(new Vector2l(1, 0), chunk1));
		tileMap.addChunk(new TileChunk(new Vector2l(0, 1), chunk1));
		tileMap.addChunk(new TileChunk(new Vector2l(1, 1), chunk1));
		Nomad n1 = new Nomad();
		n1.pos().translate(800, 760);
		Nomad n2 = new Nomad();
		n2.pos().translate(900, 600);
		add(n1);
		add(n2);
		dashboards.put(n1, new CardDashboard());
		dashboards.put(n2, new CardDashboard());
		fillDeck(n1);
		fillDeck(n2);
	}

	private void fillDeck(Nomad n) {
		GameCard extraPrep = new GameCard("Extra preparation", ACTION, BASIC, new CardEffect(null, a -> true, new DrawCardExpression(2)), 3, "Draw 2.");
		GameCard meteor = new GameCard("Meteor", ACTION, BASIC, new CardEffect(TILE, a -> true, new DrawCardExpression()), 1,
				"Deal 8 to all characters within radius 3 of target tile.");
		GameCard zap = new GameCard("Zap", CANTRIP, BASIC, new CardEffect(CHARACTER, a -> a instanceof HealthActor, new DealDamageExpression(3)), 0, "Deal 3.");
		GameCard teleport = new GameCard("Teleport", CANTRIP, ARCANE, new CardEffect(TILE, a -> true, new TeleportExpression()), 0,
				"Teleport to target tile within radius 4.");
		CardDashboard dashboard = dashboard(n);
		add(extraPrep);
		add(meteor);
		add(zap);
		add(teleport);
		dashboard.hand().addTop(meteor);
		dashboard.hand().addTop(extraPrep);
		dashboard.hand().addTop(zap);
		for (int i = 0; i < 4; i++) {
			GameCard extraPrepCopy = extraPrep.copy();
			dashboard.deck().addTop(extraPrepCopy);
			add(extraPrepCopy);
		}
		for (int i = 0; i < 4; i++) {
			GameCard zapCopy = zap.copy();
			dashboard.deck().addTop(zapCopy);
			add(zapCopy);
		}

	}

	public TileMap tileMap() {
		return tileMap;
	}

	public CardDashboard dashboard(CardPlayer cardPlayer) {
		return dashboards.get(cardPlayer);
	}

	public void add(Actor actor) {
		actor.addTo(actors, cardPlayers, cards);
	}

	public Actor actor(Long id) {
		return actors.get(id);
	}

	public GameCard card(long cardID) {
		return cards.get(cardID);
	}

	public Collection<Actor> actors() {
		return actors.values();
	}

	public CardPlayer cardPlayer(Long id) {
		return cardPlayers.get(id);
	}

	public ChainHeap chainHeap() {
		return chainHeap;
	}

}
