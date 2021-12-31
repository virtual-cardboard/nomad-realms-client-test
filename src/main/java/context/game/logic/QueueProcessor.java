package context.game.logic;

import static model.card.expression.CardTargetType.CHARACTER;
import static model.card.expression.CardTargetType.TILE;

import context.game.logic.handler.CardResolvedEventHandler;
import event.game.logicprocessing.CardPlayedEvent;
import event.game.logicprocessing.CardResolvedEvent;
import model.actor.CardPlayer;
import model.actor.GameObject;
import model.card.CardDashboard;
import model.card.CardQueue;
import model.card.GameCard;
import model.state.GameState;
import model.tile.Tile;

/**
 * Processes card queues of {@link CardPlayer}s.
 * 
 * @author Jay
 *
 */
public class QueueProcessor {

	private CardResolvedEventHandler cardResolvedEventHandler;

	public QueueProcessor(CardResolvedEventHandler cardResolvedEventHandler) {
		this.cardResolvedEventHandler = cardResolvedEventHandler;
	}

	public void processAll(GameState state) {
		for (CardPlayer cardPlayer : state.cardPlayers()) {
			CardQueue queue = cardPlayer.cardDashboard().queue();
			if (!queue.empty() && !queue.locked()) {
				CardPlayedEvent first = queue.first();
				CardPlayer player = state.cardPlayer(first.playerID());
				GameCard card = state.card(first.cardID());
				if (queue.tickCount() == card.cost() * 10) {
					queue.resetTicks();
					queue.poll();
					if (card.effect().condition.test(player, getTarget(card, state, first.targetID()))) {
						CardResolvedEvent cre = new CardResolvedEvent(first.playerID(), first.cardID(), first.targetID());
						cardResolvedEventHandler.accept(cre);
						queue.setLocked(true);
					} else {
						CardDashboard dashboard = cardPlayer.cardDashboard();
						if (!dashboard.hand().full()) {
							dashboard.hand().add(card);
						} else {
							dashboard.discard().add(card);
						}
					}
				} else {
					queue.increaseTick();
				}
			}
		}
	}

	private GameObject getTarget(GameCard card, GameState state, long targetID) {
		if (card.effect().targetType == TILE) {
			return state.worldMap().chunk(targetID).tile(Tile.tileCoords(targetID));
		} else if (card.effect().targetType == CHARACTER) {
			return state.actor(targetID);
		} else {
			return state.card(targetID);
		}
	}

}
