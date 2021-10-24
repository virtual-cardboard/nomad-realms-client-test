package context.connect;

import static common.event.NetworkEvent.fromPacket;

import common.GameInputEventHandler;
import context.input.GameInput;

public class PeerConnectInput extends GameInput {

	@Override
	protected void init() {
		addPacketReceivedFunction(new GameInputEventHandler<>((event) -> {
			return fromPacket(event.getModel());
		}));
	}

}
