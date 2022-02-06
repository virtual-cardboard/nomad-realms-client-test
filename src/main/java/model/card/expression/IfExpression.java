package model.card.expression;

import java.util.List;

import model.card.CardTag;
import model.chain.EffectChain;
import model.state.GameState;

public class IfExpression extends CardExpression {

	private PlayerTargetPredicate predicate;
	private CardExpression ifTrue;
	private CardExpression ifFalse;

	public IfExpression(PlayerTargetPredicate predicate, CardExpression ifTrue, CardExpression ifFalse) {
		this.predicate = predicate;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	@Override
	public void handle(long playerID, long targetID, GameState state, EffectChain chain) {
		(predicate.test(state.cardPlayer(playerID), targetID, state) ? ifTrue : ifFalse).handle(playerID, targetID, state, chain);
	}

	@Override
	public void populateTags(List<CardTag> tags) {
		ifTrue.populateTags(tags);
		ifFalse.populateTags(tags);
	}

}
