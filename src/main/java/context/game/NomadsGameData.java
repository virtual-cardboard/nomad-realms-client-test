package context.game;

import static model.card.GameCard.CUT_TREE;
import static model.card.GameCard.EXTRA_PREPARATION;
import static model.card.GameCard.GATHER;
import static model.card.GameCard.MOVE;
import static model.card.GameCard.OVERCLOCKED_MACHINERY;
import static model.card.GameCard.REGENESIS;
import static model.card.GameCard.TELEPORT;
import static model.card.GameCard.ZAP;

import app.NomadsSettings;
import common.math.Vector2i;
import context.data.GameData;
import model.actor.Nomad;
import model.actor.npc.VillageFarmer;
import model.actor.resource.TreeActor;
import model.card.CardDashboard;
import model.card.WorldCard;
import model.state.GameState;
import model.state.LimitedStack;

public class NomadsGameData extends GameData {

	private long playerID;
	private LimitedStack<GameState> states = new LimitedStack<>(30);
	private GameState nextState;

	private NomadsSettings settings = new NomadsSettings(48f, 1, 1, 1);

	@Override
	protected void init() {
		GameState state = new GameState();
		states.add(state);
//		Nomad n0 = new Nomad();
//		n0.worldPos().setTilePos(new Vector2i(0, 0));
//		state.add(n0);
//		fillDeck(n0, state);
//		Nomad n1 = new Nomad();
//		n1.worldPos().setTilePos(new Vector2i(2, 1));
//		state.add(n1);
//		fillDeck(n1, state);
//		playerID = n0.id();

//		ItemActor wood = new ItemActor(Item.WOOD);
//		wood.worldPos().setTilePos(new Vector2i(3, 3));
//		state.add(wood);

		VillageFarmer villageFarmer = new VillageFarmer(null);
		WorldCard c4 = new WorldCard(GATHER);
		WorldCard c1 = new WorldCard(CUT_TREE);
		WorldCard c3 = new WorldCard(REGENESIS);
		WorldCard c2 = new WorldCard(EXTRA_PREPARATION);
		villageFarmer.cardDashboard().hand().add(c1);
		villageFarmer.cardDashboard().hand().add(c3);
		villageFarmer.cardDashboard().hand().add(c2);
		villageFarmer.cardDashboard().deck().add(c4);
		villageFarmer.worldPos().setTilePos(new Vector2i(4, 8));
		state.add(c1);
		state.add(c2);
		state.add(c3);
		state.add(c4);
		state.add(villageFarmer);

		int[] x = { 0, 1, 1, 3, 4, 6, 9, 10, 10 };
		int[] y = { 0, 7, 12, 5, 15, 8, 9, 2, 0 };
		for (int i = 0; i < x.length; i++) {
			TreeActor tree = new TreeActor();
			tree.worldPos().setTilePos(new Vector2i(x[i], y[i]));
			state.add(tree);
		}

		states.add(state);
		nextState = state.copy();
	}

	private void fillDeck(Nomad n, GameState state) {
		WorldCard extraPrep = new WorldCard(EXTRA_PREPARATION);
		WorldCard move = new WorldCard(MOVE);
		WorldCard teleport = new WorldCard(TELEPORT);
		WorldCard zap = new WorldCard(ZAP);
		WorldCard overclockedMachinery = new WorldCard(OVERCLOCKED_MACHINERY);

		CardDashboard dashboard = n.cardDashboard();
		state.add(extraPrep);
		WorldCard extraPrepCopy = extraPrep.copyDiffID();
		state.add(extraPrepCopy);
		state.add(zap);
		state.add(move);
		state.add(teleport);
		state.add(overclockedMachinery);
		dashboard.hand().addTop(extraPrep);
		dashboard.hand().addTop(extraPrepCopy);
		dashboard.hand().addTop(zap);
		dashboard.hand().addTop(move);
		dashboard.hand().addTop(teleport);
		dashboard.hand().addTop(overclockedMachinery);
//		for (int i = 0; i < 2; i++) {
//			addCopyTo(zap, n);
//		}
		addCopyTo(teleport, n, state);
		for (int i = 0; i < 4; i++) {
			addCopyTo(extraPrep, n, state);
		}
		dashboard.deck().shuffle(0);
		WorldCard regenesis = new WorldCard(REGENESIS);
		state.add(regenesis);
		dashboard.deck().addBottom(regenesis);
	}

	private void addCopyTo(WorldCard card, Nomad nomad, GameState state) {
		WorldCard copy = card.copyDiffID();
		nomad.cardDashboard().deck().addTop(copy);
		state.add(copy);
	}

	public long playerID() {
		return playerID;
	}

	public void setPlayerID(long playerID) {
		this.playerID = playerID;
	}

	public NomadsSettings settings() {
		return settings;
	}

	public LimitedStack<GameState> states() {
		return states;
	}

	public GameState nextState() {
		return nextState;
	}

	public void setNextState(GameState nextState) {
		this.nextState = nextState;
	}

}
