package model.card.expression;

import model.card.chain.DrawCardEvent;
import model.chain.EffectChain;
import model.state.GameState;

public class TargetDrawCardExpression extends CardExpression {

	private int amount;

	public TargetDrawCardExpression() {
		this(1);
	}

	public TargetDrawCardExpression(int amount) {
		this.amount = amount;
	}

	@Override
	public void handle(long playerID, long targetID, GameState state, EffectChain chain) {
		chain.addWheneverEvent(new DrawCardEvent(playerID, targetID, amount));
	}

}
