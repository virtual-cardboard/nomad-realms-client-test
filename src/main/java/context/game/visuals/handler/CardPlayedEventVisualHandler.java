package context.game.visuals.handler;

import static model.card.CardType.CANTRIP;
import static model.card.CardType.TASK;

import java.util.function.Consumer;

import context.game.NomadsGameData;
import context.game.visuals.gui.dashboard.CardDashboardGui;
import context.game.visuals.gui.dashboard.WorldCardGui;
import context.visuals.gui.RootGui;
import event.game.logicprocessing.CardPlayedEvent;
import model.card.WorldCard;

public class CardPlayedEventVisualHandler implements Consumer<CardPlayedEvent> {

	private NomadsGameData data;
	private CardDashboardGui dashboardGui;
	private RootGui rootGui;

	public CardPlayedEventVisualHandler(NomadsGameData data, CardDashboardGui dashboardGui, RootGui rootGui) {
		this.data = data;
		this.dashboardGui = dashboardGui;
		this.rootGui = rootGui;
	}

	@Override
	public void accept(CardPlayedEvent t) {
		if (t.playerID() != data.playerID()) {
			return;
		}
		WorldCardGui cardGui = dashboardGui.getCardGui(t.cardID());
		cardGui.setLockPos(false);
		cardGui.setLockTargetPos(false);
		cardGui.unhover(data.settings());
		dashboardGui.hand().removeCardGui(cardGui);
		WorldCard card = t.cardID().getFrom(data.previousState());
		if (card.type() == CANTRIP || card.type() == TASK) {
			dashboardGui.discard().addCardGui(cardGui);
		} else {
			dashboardGui.queue().addCardGui(0, cardGui);
		}
		dashboardGui.resetTargetPositions(rootGui.dimensions(), data.settings());
	}

}
