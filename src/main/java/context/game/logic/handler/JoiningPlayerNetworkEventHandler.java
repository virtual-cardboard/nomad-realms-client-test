package context.game.logic.handler;

import java.util.Queue;
import java.util.function.Consumer;

import context.game.NomadsGameData;
import engine.common.event.async.AsyncEventPriorityQueue;
import engine.common.networking.packet.PacketModel;
import event.logicprocessing.SpawnPlayerAsyncEvent;
import event.network.p2p.peerconnect.PeerConnectRequestEvent;
import event.network.p2p.s2c.JoiningPlayerNetworkEvent;

public class JoiningPlayerNetworkEventHandler implements Consumer<JoiningPlayerNetworkEvent> {

	private final NomadsGameData data;
	private final AsyncEventPriorityQueue asyncEventPriorityQueue;
	private final Queue<PacketModel> networkSend;

	public JoiningPlayerNetworkEventHandler(NomadsGameData data, AsyncEventPriorityQueue asyncEventPriorityQueue, Queue<PacketModel> networkSend) {
		this.data = data;
		this.asyncEventPriorityQueue = asyncEventPriorityQueue;
		this.networkSend = networkSend;
	}

	@Override
	public void accept(JoiningPlayerNetworkEvent e) {
		data.tools().logMessage("Received JoiningPlayerNetworkEvent: " + e.nonce() + " " + e.lanAddress() + " " + e.wanAddress());
		PeerConnectRequestEvent connectRequest = new PeerConnectRequestEvent(e.nonce(), data.username());
		data.tools().logMessage("Sending PeerConnectRequestEvent to the joining player");
		networkSend.add(connectRequest.toPacketModel(e.lanAddress()));
		networkSend.add(connectRequest.toPacketModel(e.wanAddress()));
		data.tools().logMessage("Scheduled to spawn player on tick " + (e.spawnTick()));
		asyncEventPriorityQueue.add(new SpawnPlayerAsyncEvent(e.spawnTick(), e.spawnPos()));
	}

}
