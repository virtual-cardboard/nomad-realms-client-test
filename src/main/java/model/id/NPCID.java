package model.id;

import model.actor.NPCActor;
import model.state.GameState;

public class NPCID extends ID<NPCActor> {

	public NPCID(long id) {
		super(id);
	}

	@Override
	public NPCActor getFrom(GameState state) {
		return (NPCActor) state.actor(id);
	}

}
