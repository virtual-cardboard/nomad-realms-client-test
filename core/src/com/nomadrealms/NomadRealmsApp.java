package com.nomadrealms;

import static com.nomadrealms.loading.LocalAssets.disposeLocalAssets;
import static com.nomadrealms.loading.LocalAssets.lavaGolemTexture;
import static com.nomadrealms.loading.LocalAssets.loadLocalAssets;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nomadrealms.logic.game.world.World;
import com.nomadrealms.rendering.basic.shape.HexagonShapeRenderer;

public class NomadRealmsApp extends ApplicationAdapter {

	private SpriteBatch batch;
	private World world;
	private Vector2 camera;

	@Override
	public void create() {
		batch = new SpriteBatch();
		loadLocalAssets();
		HexagonShapeRenderer.init();
		world = new World();
		world.addRegion(new Vector2(0, 0));
	}

	@Override
	public void render() {
		ScreenUtils.clear(1, 1, 1, 1);
		batch.begin();
		batch.draw(lavaGolemTexture, 10, 20);
		batch.end();

		world.render(camera);
	}

	@Override
	public void dispose() {
		batch.dispose();
		disposeLocalAssets();
	}

}
