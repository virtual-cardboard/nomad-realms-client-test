package event.network.c2s;

import derealizer.SerializationReader;
import derealizer.SerializationWriter;
import engine.common.networking.packet.address.PacketAddress;
import event.network.NomadRealmsC2SNetworkEvent;

public class JoinClusterRequestEvent extends NomadRealmsC2SNetworkEvent {

	private PacketAddress lanAddress;
	private long worldID;
	private long playerId;

	public JoinClusterRequestEvent() {
	}

	public JoinClusterRequestEvent(PacketAddress lanAddress, long worldID, long playerId) {
		this.lanAddress = lanAddress;
		this.worldID = worldID;
		this.playerId = playerId;
	}

	public JoinClusterRequestEvent(byte[] bytes) {
		read(new SerializationReader(bytes));
	}

	@Override
	public void read(SerializationReader reader) {
		this.lanAddress = new PacketAddress();
		this.lanAddress.read(reader);
		this.worldID = reader.readLong();
		this.playerId = reader.readLong();
	}

	@Override
	public void write(SerializationWriter writer) {
		lanAddress.write(writer);
		writer.consume(worldID);
		writer.consume(playerId);
	}

	public PacketAddress lanAddress() {
		return lanAddress;
	}

	public long worldID() {
		return worldID;
	}

	public long playerId() {
		return playerId;
	}

}
