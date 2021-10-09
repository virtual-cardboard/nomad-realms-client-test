package event.game;

import model.GameState;
import model.actor.CardPlayer;

public class DrawCardEvent extends CardEffectEvent {

	private int num;
	private CardPlayer target;

	public DrawCardEvent(CardPlayer source, CardPlayer target, int num) {
		super(source);
		this.target = target;
		this.num = num;
	}

	public int num() {
		return num;
	}

	public CardPlayer target() {
		return target;
	}

	@Override
	public void process(GameState state) {
		state.cardDeck(target);
		state.cardHand(target);
	}

}
