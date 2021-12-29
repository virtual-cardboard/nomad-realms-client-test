package context.game.visuals.renderer;

import static context.game.visuals.GameCamera.RENDER_RADIUS;
import static model.tile.Tile.THREE_QUARTERS_WIDTH;
import static model.tile.Tile.TILE_HEIGHT;
import static model.tile.Tile.TILE_WIDTH;
import static model.tile.TileChunk.CHUNK_HEIGHT;
import static model.tile.TileChunk.CHUNK_SIDE_LENGTH;
import static model.tile.TileChunk.CHUNK_WIDTH;

import app.NomadsSettings;
import common.math.Vector2i;
import context.GLContext;
import context.game.visuals.GameCamera;
import context.game.visuals.renderer.hexagon.HexagonRenderer;
import context.visuals.gui.RootGui;
import context.visuals.renderer.GameRenderer;
import model.tile.Tile;
import model.tile.TileChunk;
import model.tile.WorldMap;

public class WorldMapRenderer extends GameRenderer {

	private HexagonRenderer hexagonRenderer;

	public WorldMapRenderer(HexagonRenderer hexagonRenderer) {
		this.hexagonRenderer = hexagonRenderer;
	}

	public void renderMap(GLContext glContext, RootGui rootGui, NomadsSettings s, WorldMap map, GameCamera camera) {
		Vector2i cameraChunkPos = camera.chunkPos();
		for (int cy = -RENDER_RADIUS; cy <= RENDER_RADIUS; cy++) {
			for (int cx = -RENDER_RADIUS; cx <= RENDER_RADIUS; cx++) {
				TileChunk chunk = map.chunk(cameraChunkPos.add(cx, cy));
				if (chunk != null) {
					renderChunk(glContext, rootGui, s, camera, chunk);
				}
			}
		}
	}

	private void renderChunk(GLContext glContext, RootGui rootGui, NomadsSettings s, GameCamera camera, TileChunk chunk) {
		for (int i = 0; i < CHUNK_SIDE_LENGTH; i++) {
			for (int j = 0; j < CHUNK_SIDE_LENGTH; j++) {
				float x = (j * THREE_QUARTERS_WIDTH + (chunk.pos().x - camera.chunkPos().x) * CHUNK_WIDTH - camera.pos().x) * s.worldScale;
				float y = (i * TILE_HEIGHT + (j % 2) * TILE_HEIGHT * 0.5f + (chunk.pos().y - camera.chunkPos().y) * CHUNK_HEIGHT
						- camera.pos().y) * s.worldScale;
				Tile tile = chunk.tile(j, i);
				int outlineColour = tile.type().outlineColour();
				int colour = tile.type().colour();
				hexagonRenderer.render(glContext, rootGui, x, y,
						TILE_WIDTH * s.worldScale, TILE_HEIGHT * s.worldScale, outlineColour);
				hexagonRenderer.render(glContext, rootGui, x + 3, y + 3,
						TILE_WIDTH * s.worldScale - 6, TILE_HEIGHT * s.worldScale - 6, colour);
			}
		}
	}

}
