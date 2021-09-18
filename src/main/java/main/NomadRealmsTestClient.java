package main;

import context.GameContext;
import context.data.GameData;
import context.input.GameInput;
import context.logic.GameLogic;
import context.misc.BootstrapData;
import context.misc.BootstrapInput;
import context.misc.BootstrapLogic;
import context.visuals.DefaultGameVisuals;
import context.visuals.GameVisuals;
import engine.GameEngine;

public class NomadRealmsTestClient {

	public static void main(String[] args) {
		GameData data = new BootstrapData();
		GameInput input = new BootstrapInput();
		GameLogic logic = new BootstrapLogic();
		GameVisuals visuals = new DefaultGameVisuals();
		GameContext context = new GameContext(data, input, logic, visuals);
		new GameEngine("Nomad Realms Test Client", context)
				.disableLoading()
				.enableNetworking()
				.enableRendering()
				.enablePrintProgress()
				.run();
	}

}
