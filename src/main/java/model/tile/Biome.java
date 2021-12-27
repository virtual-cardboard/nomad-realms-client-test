package model.tile;

import java.util.function.BiFunction;

public enum Biome {
	PLAINS((Double elevation, Double moisture) -> {
		return TileType.GRASS;
	}), OCEAN((Double elevation, Double moisture) -> {
		return TileType.WATER;
	}), DESERT((Double elevation, Double moisture) -> {
		return TileType.SAND;
	});

	public final BiFunction<Double, Double, TileType> tileTypeFunction;

	private Biome(BiFunction<Double, Double, TileType> tileTypeFunction) {
		this.tileTypeFunction = tileTypeFunction;

	}
}
