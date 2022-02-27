package model.id;

import model.hidden.HiddenGameObject;
import model.state.GameState;

public class HiddenGameObjectID extends ID {

	public HiddenGameObjectID(long id) {
		super(id);
	}

	@Override
	public HiddenGameObject getFrom(GameState state) {
		return state.hiddens().get(id);
	}

}
