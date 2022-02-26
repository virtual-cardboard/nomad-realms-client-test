package model.card.chain;

import java.util.Queue;

import common.event.GameEvent;
import model.actor.CardPlayer;
import model.id.ID;
import model.id.WorldCardID;
import model.state.GameState;

public final class PlayCardEvent extends ChainEvent {

	private WorldCardID cardID;

	public PlayCardEvent(ID<? extends CardPlayer> playerID, WorldCardID cardID) {
		super(playerID);
		this.cardID = cardID;
	}

	@Override
	public void process(GameState state, Queue<GameEvent> sync) {
	}

	@Override
	public int priority() {
		return Integer.MAX_VALUE - 1;
	}

	@Override
	public boolean checkIsDone(GameState state) {
		return true;
	}

	@Override
	public boolean cancelled(GameState state) {
		return false;
	}

	@Override
	public boolean shouldDisplay() {
		return false;
	}

	public WorldCardID cardID() {
		return cardID;
	}

	@Override
	public String textureName() {
		return "play_card";
	}

}
