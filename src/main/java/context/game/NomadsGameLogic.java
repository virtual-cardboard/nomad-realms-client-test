package context.game;

import static app.NomadRealmsClient.SKIP_NETWORKING;
import static java.util.stream.Collectors.toList;
import static model.card.GameCard.CUT_TREE;
import static model.card.GameCard.EXTRA_PREPARATION;
import static model.card.GameCard.GATHER;
import static model.card.GameCard.REGENESIS;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import context.game.logic.QueueProcessor;
import context.game.logic.asynchandler.SpawnPlayerAsyncEventHandler;
import context.game.logic.asynchandler.SpawnSelfAsyncEventHandler;
import context.game.logic.handler.CardPlayedEventFailTest;
import context.game.logic.handler.CardPlayedEventHandler;
import context.game.logic.handler.CardPlayedNetworkEventHandler;
import context.game.logic.handler.CardResolvedAsyncEventHandler;
import context.game.logic.handler.CardResolvedEventHandler;
import context.game.logic.handler.ChainEventHandler;
import context.game.logic.handler.DoNothingConsumer;
import context.game.logic.handler.InGamePeerConnectRequestEventHandler;
import context.game.logic.handler.JoiningPlayerNetworkEventHandler;
import context.game.logic.handler.StreamChunkDataEventHandler;
import context.game.logic.handler.StreamChunksToJoiningPlayerHandler;
import context.logic.GameLogic;
import engine.common.event.GameEvent;
import engine.common.math.Vector2i;
import engine.common.networking.packet.PacketModel;
import engine.common.networking.packet.address.PacketAddress;
import event.NomadRealmsAsyncEvent;
import event.NomadRealmsGameEvent;
import event.logicprocessing.CardPlayedEvent;
import event.logicprocessing.CardResolvedAsyncEvent;
import event.logicprocessing.CardResolvedEvent;
import event.logicprocessing.SpawnPlayerAsyncEvent;
import event.logicprocessing.SpawnSelfAsyncEvent;
import event.network.NomadRealmsP2PNetworkEvent;
import event.network.c2s.JoinClusterResponseEvent;
import event.network.p2p.game.CardPlayedNetworkEvent;
import event.network.p2p.game.StreamChunkDataEvent;
import event.network.p2p.peerconnect.PeerConnectConfirmationEvent;
import event.network.p2p.peerconnect.PeerConnectRequestEvent;
import event.network.p2p.s2c.JoiningPlayerNetworkEvent;
import graphics.displayer.VillageFarmerDisplayer;
import math.WorldPos;
import model.actor.Actor;
import model.actor.ItemActor;
import model.actor.npc.village.farmer.VillageFarmer;
import model.card.CardDashboard;
import model.card.WorldCard;
import model.chain.EffectChain;
import model.chain.event.ChainEvent;
import model.chain.event.DrawCardEvent;
import model.hidden.village.Village;
import model.item.Item;
import model.item.ItemCollection;
import model.state.GameState;
import networking.NetworkEventDispatcher;

public class NomadsGameLogic extends GameLogic {

	private final WorldPos spawnPos;
	private NomadsGameData data;

	private QueueProcessor queueProcessor;

	/**
	 * Used at the end of the update() function to push all events to the queue group.
	 */
	private final Queue<GameEvent> eventBuffer = new ArrayDeque<>();
	private final Queue<CardPlayedEvent> cardPlayedEventQueue = new ArrayDeque<>();

	private NetworkEventDispatcher dispatcher;
	private final Queue<NomadRealmsP2PNetworkEvent> outgoingNetworkEvents = new ArrayDeque<>();

	public NomadsGameLogic(long startingTick, JoinClusterResponseEvent joinResponse) {
		setGameTick((int) startingTick);

		long spawnTick = SKIP_NETWORKING ? startingTick + 1 : joinResponse.spawnTick();
		spawnPos = SKIP_NETWORKING ? new WorldPos(0, 0, 0, 0) : joinResponse.spawnPos();
		asyncEventQueue().add(new SpawnSelfAsyncEvent(spawnTick, spawnPos));
	}

	@Override
	protected void init() {
		data = (NomadsGameData) context().data();

		final Queue<PacketModel> networkSend = context().networkSend();

		for (PacketAddress address : data.network().peers()) {
			PeerConnectConfirmationEvent event = new PeerConnectConfirmationEvent(spawnPos);
			networkSend.add(event.toPacketModel(address));
		}

		dispatcher = new NetworkEventDispatcher(data.network(), networkSend);

//		addFarmer();

		// Spawn player
		addHandler(SpawnSelfAsyncEvent.class, new SpawnSelfAsyncEventHandler(data));
		addHandler(SpawnPlayerAsyncEvent.class, new SpawnPlayerAsyncEventHandler(data));

		// Card played and card resolved
		addHandler(CardPlayedEvent.class, new CardPlayedEventFailTest(data), new DoNothingConsumer<>(), true);
		addHandler(CardPlayedEvent.class, new CardPlayedEventHandler(data, this, asyncEventQueue(), outgoingNetworkEvents));
		CardResolvedEventHandler cardResolvedEventHandler = new CardResolvedEventHandler(data);
		addHandler(CardResolvedEvent.class, cardResolvedEventHandler);
		addHandler(CardResolvedAsyncEvent.class, new CardResolvedAsyncEventHandler(this));
		addHandler(CardPlayedNetworkEvent.class, new CardPlayedNetworkEventHandler(data, cardPlayedEventQueue));

		queueProcessor = new QueueProcessor(cardResolvedEventHandler);

		addHandler(JoiningPlayerNetworkEvent.class, new JoiningPlayerNetworkEventHandler(data, asyncEventQueue(), networkSend));

		addHandler(PeerConnectRequestEvent.class, new InGamePeerConnectRequestEventHandler(data, data.username(), networkSend));
		addHandler(PeerConnectConfirmationEvent.class, new StreamChunksToJoiningPlayerHandler(data, networkSend));

		addHandler(StreamChunkDataEvent.class, new StreamChunkDataEventHandler(data));
//		addHandler(PlayerHoveredCardEvent.class, new CardHoveredEventHandler(sync));
//		addHandler(CardHoveredNetworkEvent.class, (event) -> System.out.println("Opponent hovered"));
		addHandler(ChainEvent.class, new ChainEventHandler(this, data));

		addHandler(NomadRealmsGameEvent.class, this::addEventToEventBuffer);
		addHandler(NomadRealmsAsyncEvent.class, this::addEventToEventBuffer);
		addHandler(NomadRealmsP2PNetworkEvent.class, e -> data.tools().logMessage("Received p2p network event: " + e.getClass().getSimpleName()));

	}

	private void addFarmer() {
		VillageFarmer farmer = new VillageFarmer(new Village(), new VillageFarmerDisplayer(), data.tools());
		farmer.setId(data.generators().genId());
		farmer.worldPos().set(new WorldPos(new Vector2i(0, 0), new Vector2i(4, 3)));

		WorldCard c4 = new WorldCard(data.generators().genId(), GATHER, 0);
		WorldCard c1 = new WorldCard(data.generators().genId(), CUT_TREE, 0);
		WorldCard c3 = new WorldCard(data.generators().genId(), REGENESIS, 0);
		WorldCard c2 = new WorldCard(data.generators().genId(), EXTRA_PREPARATION, 0);
		farmer.cardDashboard().hand().add(c1);
		farmer.cardDashboard().hand().add(c3);
		farmer.cardDashboard().hand().add(c2);
		farmer.cardDashboard().deck().add(c4);
		data.nextState().add(c1);
		data.nextState().add(c2);
		data.nextState().add(c3);
		data.nextState().add(c4);

		CardDashboard cardDashboard = farmer.cardDashboard();
		data.deck().addTo(cardDashboard.deck(), data.nextState(), data.generators().personalIdGenerator());
		for (int i = 0; i < 6; i++) {
			cardDashboard.deck().add(new WorldCard(data.generators().genId(), EXTRA_PREPARATION, 0));
		}
		data.setPlayerID(farmer.id());
		data.nextState().add(farmer);
	}

	@Override
	public void update() {
		GameState nextState = data.nextState();
		while (!cardPlayedEventQueue.isEmpty()) {
			handleEvent(cardPlayedEventQueue.poll());
		}
		queueProcessor.processAll(nextState, contextQueues());
		dispatcher.dispatch(outgoingNetworkEvents);

		nextState.worldMap().generateTerrainAround(data.camera().chunkPos(), nextState, data.generators().npcIdGenerator());

		List<ChainEvent> resolvedChainEvents = nextState.chainHeap().processAll(gameTick(), data, contextQueues());
		resolvedChainEvents.forEach(this::handleEvent);

		if (gameTick() % 100 == 0) {
			allPlayersDrawACard();
		}
		updateActors();

		removeDeadActors();

		data.advanceState();

		while (!eventBuffer.isEmpty()) {
			contextQueues().pushEventFromLogic(eventBuffer.poll());
		}
	}

	private void allPlayersDrawACard() {
		GameState nextState = data.nextState();
		nextState.cardPlayers().forEach(cardPlayer -> {
			EffectChain chain = new EffectChain();
			DrawCardEvent event = new DrawCardEvent(cardPlayer.id(), cardPlayer.id(), 1);
			chain.addWheneverEvent(event);
			nextState.chainHeap().add(chain);
		});
	}

	private void updateActors() {
		GameState nextState = data.nextState();
		nextState.actors().values().forEach(actor -> actor.update(gameTick(), nextState));
		nextState.npcs().forEach(npc -> npc.update(gameTick(), nextState, cardPlayedEventQueue));
	}

	private void removeDeadActors() {
		GameState nextState = data.nextState();
		List<Actor> toRemove = nextState.actors().values().stream().filter(Actor::shouldRemove).collect(toList());

		for (Actor a : toRemove) {
			ItemCollection items = a.dropItems();
			if (items != null) {
				for (Item item : items.keySet()) {
					for (int i = 0; i < items.get(item); i++) {
						ItemActor itemActor = new ItemActor(item);
						itemActor.setId(data.generators().genId());
						itemActor.worldPos().set(a.worldPos());
						nextState.add(itemActor);
					}
				}
			}
			nextState.remove(a);
		}
	}

	private void addEventToEventBuffer(GameEvent event) {
		eventBuffer.add(event);
	}

	/**
	 * Increased visibility (public)
	 *
	 * @param event the event to handle
	 */
	@Override
	public void handleEvent(GameEvent event) {
		super.handleEvent(event);
	}

}
