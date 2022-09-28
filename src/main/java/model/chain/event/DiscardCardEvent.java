package model.chain.event;

import static math.IntegerRandom.randomInt;

import engine.common.ContextQueues;
import math.IdGenerators;
import model.actor.health.cardplayer.CardPlayer;
import model.card.CardDashboard;
import model.card.CardZone;
import model.card.WorldCard;
import model.id.CardPlayerId;
import model.state.GameState;

public class DiscardCardEvent extends FixedTimeChainEvent {

	private CardPlayerId targetID;
	private int amount;

	public DiscardCardEvent(CardPlayerId playerID, CardPlayerId targetID, int amount) {
		super(playerID);
		this.targetID = targetID;
		this.amount = amount;
	}

	@Override
	public void process(long tick, GameState state, IdGenerators idGenerators, ContextQueues contextQueues) {
		CardPlayer target = targetID.getFrom(state);
		CardDashboard dashboard = target.cardDashboard();
		CardZone hand = dashboard.hand();
		for (int i = 0; i < amount && !hand.empty(); i++) {
			WorldCard card = hand.remove(randomInt(hand.size()));
			dashboard.discard().addTop(card);
		}
	}

	@Override
	public int priority() {
		return 8;
	}

	@Override
	public int processTime() {
		return 5;
	}

	@Override
	public boolean cancelled(GameState state) {
		return super.cancelled(state) || targetID.getFrom(state).shouldRemove();
	}

	@Override
	public String textureName() {
		return "discard_card";
	}

}
