package model.task;

import common.math.Vector2i;
import math.WorldPos;
import model.actor.CardPlayer;
import model.state.GameState;
import model.world.Tile;

public class MoveTask extends Task {

	private int timer = 10;
	private boolean done;

	@Override
	public void execute(long playerID, GameState state) {
		if (timer != 0) {
			timer--;
			return;
		}
		timer = 10;
		CardPlayer player = state.cardPlayer(playerID);
		WorldPos playerPos = player.worldPos();
		WorldPos targetPos = state.worldMap().finalLayerChunk(targetID()).tile(Tile.tileCoords(targetID())).worldPos();
		if (playerPos.equals(targetPos)) {
			done = true;
		}
		Vector2i tilePos = playerPos.tilePos();
		playerPos.setTilePos(tilePos.add(playerPos.directionTo(targetPos)));
	}

	@Override
	public void pause(long playerID, GameState state) {
		timer = 10;
	}

	@Override
	public void resume(long playerID, GameState state) {
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public MoveTask copy() {
		MoveTask copy = new MoveTask();
		copy.timer = timer;
		return super.copyTo(copy);
	}

	@Override
	public String name() {
		return "move";
	}

}
