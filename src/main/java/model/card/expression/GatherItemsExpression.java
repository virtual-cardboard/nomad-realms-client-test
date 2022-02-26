package model.card.expression;

import java.util.List;

import model.actor.CardPlayer;
import model.card.CardTag;
import model.card.chain.GatherItemsEvent;
import model.chain.EffectChain;
import model.id.ID;
import model.state.GameState;

public class GatherItemsExpression extends CardExpression {

	private int radius;

	public GatherItemsExpression(int radius) {
		this.radius = radius;
	}

	@Override
	public void handle(ID<? extends CardPlayer> playerID, ID<?> targetID, GameState state, EffectChain chain) {
		chain.addWheneverEvent(new GatherItemsEvent(playerID, radius));
	}

	@Override
	public void populateTags(List<CardTag> tags) {
		tags.add(CardTag.GATHERS_ITEMS);
	}

}
