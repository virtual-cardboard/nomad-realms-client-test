package event.game.logicprocessing.expression;

import java.util.Queue;

import common.event.GameEvent;
import common.math.Vector2f;
import model.GameState;
import model.actor.CardPlayer;
import model.actor.PositionalActor;

public class TeleportEvent extends CardExpressionEvent {

	private PositionalActor target;
	private Vector2f loc;

	public TeleportEvent(CardPlayer source, PositionalActor target, Vector2f loc) {
		super(source);
		this.target = target;
		this.loc = loc;
	}

	public Vector2f loc() {
		return loc.copy();
	}

	public PositionalActor target() {
		return target;
	}

	@Override
	public void process(GameState state, Queue<GameEvent> sync) {
		target.setPos(loc());
	}

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public int processTime() {
		return 1;
	}

}
