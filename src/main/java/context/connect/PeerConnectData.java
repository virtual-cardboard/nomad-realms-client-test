package context.connect;

import java.util.ArrayList;
import java.util.List;

import context.data.GameData;
import engine.common.networking.packet.address.PacketAddress;
import event.network.c2s.JoinClusterResponseEvent;

public class PeerConnectData extends GameData {

	public static final int TIMEOUT_MILLISECONDS = 1000;
	public static final int MAX_RETRIES = 1000;

	private final String username;
	private final long nonce;

	public final List<PacketAddress> unconnectedLanAddresses;
	public final List<PacketAddress> unconnectedWanAddresses;

	public final List<PacketAddress> connectedPeers = new ArrayList<>();

	private long lastTriedTime = -1;
	private int timesTried = 0;

	public PeerConnectData(JoinClusterResponseEvent response, String username) {
		this.unconnectedLanAddresses = response.lanAddresses();
		this.unconnectedWanAddresses = response.wanAddresses();
		this.nonce = response.nonce();
		this.username = username;
	}

	public void confirmConnectedPeer(PacketAddress address) {
		if (unconnectedLanAddresses.contains(address)) {
			connectedPeers.add(address);
			unconnectedLanAddresses.remove(address);
		} else if (unconnectedWanAddresses.contains(address)) {
			connectedPeers.add(address);
			unconnectedWanAddresses.remove(address);
		} else {
			throw new IllegalArgumentException("Cannot confirm connected peer: " + address);
		}
	}

	public long lastTriedTime() {
		return lastTriedTime;
	}

	public void setLastTriedTime(long lastTriedTime) {
		this.lastTriedTime = lastTriedTime;
	}

	public int timesTried() {
		return timesTried;
	}

	public void incrementTimesTried() {
		timesTried++;
	}

	public boolean isConnected() {
		return unconnectedLanAddresses.isEmpty() && unconnectedWanAddresses.isEmpty();
	}

	public String username() {
		return username;
	}

	public long nonce() {
		return nonce;
	}

}
