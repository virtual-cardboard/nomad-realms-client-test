package nomadrealms.game.world.map.area.coordinate;

import static common.math.MathUtil.posMod;
import static java.lang.Math.floor;
import static nomadrealms.game.world.map.area.Tile.TILE_HORIZONTAL_SPACING;
import static nomadrealms.game.world.map.area.Tile.TILE_VERTICAL_SPACING;
import static nomadrealms.game.world.map.area.coordinate.ChunkCoordinate.CHUNK_SIZE;
import static nomadrealms.game.world.map.area.coordinate.RegionCoordinate.REGION_SIZE;
import static nomadrealms.game.world.map.area.coordinate.RegionCoordinate.regionCoordinateOf;

import common.math.Vector2f;
import nomadrealms.game.world.map.area.Region;

public class ZoneCoordinate extends Coordinate {

	/**
	 * The size of a zone in chunks.
	 */
	public static final int ZONE_SIZE = 16;

	private final RegionCoordinate region;

	public ZoneCoordinate(RegionCoordinate region, int x, int y) {
		super(x, y);
		this.region = region;
	}

	private ZoneCoordinate(Region region, int x, int y) {
		this(region.coord(), x, y);
	}

	public ZoneCoordinate up() {
		return new ZoneCoordinate(y() == 0 ? region.up() : region, x(), posMod(y() - 1, REGION_SIZE));
	}

	public ZoneCoordinate down() {
		return new ZoneCoordinate(y() == REGION_SIZE - 1 ? region.down() : region, x(), posMod(y() + 1, REGION_SIZE));
	}

	public ZoneCoordinate left() {
		return new ZoneCoordinate(x() == 0 ? region.left() : region, posMod(x() - 1, REGION_SIZE), y());
	}

	public ZoneCoordinate right() {
		return new ZoneCoordinate(x() == REGION_SIZE - 1 ? region.right() : region, posMod(x() + 1, REGION_SIZE), y());
	}

	@Override
	public RegionCoordinate region() {
		return region;
	}

	public static ZoneCoordinate zoneCoordinateOf(Vector2f position) {
		RegionCoordinate regionCoord = regionCoordinateOf(position);
		Vector2f tileToZone = new Vector2f(TILE_HORIZONTAL_SPACING, TILE_VERTICAL_SPACING)
				.scale(CHUNK_SIZE, CHUNK_SIZE)
				.scale(ZONE_SIZE);
		Vector2f tileToRegion = tileToZone.scale(REGION_SIZE);
		Vector2f offset = position
				.sub(tileToRegion.scale(regionCoord.x(), regionCoord.y()));
		return new ZoneCoordinate(regionCoord,
				(int) floor(offset.x() / tileToZone.x()),
				(int) floor(offset.y() / tileToZone.y()));
	}

	public boolean equals(Object o) {
		if (o instanceof ZoneCoordinate) {
			ZoneCoordinate other = (ZoneCoordinate) o;
			return x() == other.x() && y() == other.y() && region.equals(other.region);
		}
		return false;
	}

	@Override
	public String toString() {
		return "ZoneCoordinate(" + x() + ", " + y() + ")";
	}

}
