package model.card.expression;

import java.util.List;

import model.card.CardTag;
import model.chain.EffectChain;
import model.state.GameState;

public class AndExpression extends CardExpression {

	private CardExpression cardEffect1;
	private CardExpression cardEffect2;

	public AndExpression(CardExpression cardEffect1, CardExpression cardEffect2) {
		this.cardEffect1 = cardEffect1;
		this.cardEffect2 = cardEffect2;
	}

	@Override
	public void handle(long playerID, long targetID, GameState state, EffectChain chain) {
		cardEffect1.handle(playerID, targetID, state, chain);
		cardEffect2.handle(playerID, targetID, state, chain);
	}

	@Override
	public void populateTags(List<CardTag> tags) {
		cardEffect1.populateTags(tags);
		cardEffect2.populateTags(tags);
	}

}
