package context.game.logic.handler;

import static model.card.CardType.CANTRIP;
import static model.card.CardType.TASK;

import java.util.function.Consumer;

import event.game.logicprocessing.CardPlayedEvent;
import event.game.logicprocessing.CardResolvedEvent;
import model.card.CardDashboard;
import model.card.GameCard;

public class CardPlayedEventHandler implements Consumer<CardPlayedEvent> {

	private CardResolvedEventHandler creHandler;

	public CardPlayedEventHandler(CardResolvedEventHandler creHandler) {
		this.creHandler = creHandler;
	}

	/**
	 * If the card is a cantrip, then it resolves immediately and is handled by
	 * {@link CardResolvedEventHandler}.
	 */
	@Override
	public void accept(CardPlayedEvent event) {
		CardDashboard dashboard = event.player().cardDashboard();
		GameCard card = event.card();
		int index = dashboard.hand().indexOf(card.id());
		dashboard.hand().remove(index);
		if (card.type() == CANTRIP) {
			creHandler.accept(new CardResolvedEvent(event.player(), card, event.target()));
		} else if (card.type() == TASK) {
			if (dashboard.task() != null) {
				dashboard.task().cancel();
			}
			creHandler.accept(new CardResolvedEvent(event.player(), card, event.target()));
		} else {
			dashboard.queue().append(event);
		}
	}

}
