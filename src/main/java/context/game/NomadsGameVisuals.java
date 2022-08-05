package context.game;

import static context.visuals.colour.Colour.rgb;

import app.NomadsSettings;
import context.ResourcePack;
import context.game.visuals.GameCamera;
import context.game.visuals.gui.dashboard.CardDashboardGui;
import context.game.visuals.gui.deckbuilding.DeckBuildingGui;
import context.game.visuals.handler.CardDrawnSyncEventHandler;
import context.game.visuals.handler.CardPlayedEventParticleVisualHandler;
import context.game.visuals.handler.CardPlayedEventVisualHandler;
import context.game.visuals.handler.CardResolvedEventVisualHandler;
import context.game.visuals.handler.CardShuffledSyncEventHandler;
import context.game.visuals.handler.ShowDeckBuildingGuiHandler;
import context.game.visuals.handler.SpawnSelfAsyncEventVisualHandler;
import context.game.visuals.renderer.ActorRenderer;
import context.game.visuals.renderer.ChainHeapRenderer;
import context.game.visuals.renderer.ParticleRenderer;
import context.game.visuals.renderer.WorldMapRenderer;
import context.game.visuals.renderer.hexagon.HexagonRenderer;
import context.visuals.GameVisuals;
import context.visuals.gui.renderer.RootGuiRenderer;
import event.logicprocessing.CardPlayedEvent;
import event.logicprocessing.CardResolvedEvent;
import event.logicprocessing.SpawnSelfAsyncEvent;
import event.sync.CardDrawnSyncEvent;
import event.sync.CardShuffledSyncEvent;
import model.actor.CardPlayer;
import model.chain.event.BuildDeckEvent;
import model.state.GameState;

public class NomadsGameVisuals extends GameVisuals {

	private NomadsGameData data;
	private GameCamera camera;

	private CardDashboardGui dashboardGui;
	private DeckBuildingGui deckBuildingGui;

	private WorldMapRenderer worldMapRenderer;
	private ActorRenderer actorRenderer;
	private ParticleRenderer particleRenderer;
	private ChainHeapRenderer chainHeapRenderer;
	private RootGuiRenderer rootGuiRenderer;

	private NomadsSettings settings;

	@Override
	public void init() {
		data = (NomadsGameData) context().data();
		camera = data.camera();

		this.settings = data.settings();
		initRenderers(resourcePack());
		initGuis();
		initCardPlayerDisplayers(resourcePack());

		addHandler(SpawnSelfAsyncEvent.class, new SpawnSelfAsyncEventVisualHandler(dashboardGui, rootGui(), data));

		addHandler(CardPlayedEvent.class, new CardPlayedEventVisualHandler(data, dashboardGui, rootGui()));
		addHandler(CardPlayedEvent.class, new CardPlayedEventParticleVisualHandler(data, particleRenderer.particles(), resourcePack(), camera, settings));
		addHandler(CardResolvedEvent.class, new CardResolvedEventVisualHandler(data, dashboardGui, rootGui(), particleRenderer.particles()));

		addHandler(CardDrawnSyncEvent.class, new CardDrawnSyncEventHandler(data, dashboardGui, resourcePack(), rootGui()));
		addHandler(CardShuffledSyncEvent.class, new CardShuffledSyncEventHandler(data, dashboardGui, rootGui()));
		addHandler(BuildDeckEvent.class, new ShowDeckBuildingGuiHandler(deckBuildingGui));
	}

	@Override
	public void render() {
		background(rgb(3, 51, 97));
		GameState state = data.previousState();
		worldMapRenderer.renderMap(settings, state.worldMap(), camera);
		actorRenderer.renderActors(settings, state, camera, alpha());
		chainHeapRenderer.render(state.chainHeap(), state, camera, settings);
		rootGuiRenderer.render(data, rootGui());
		updateCamera(state);
		particleRenderer.renderParticles();
	}

	private void updateCamera(GameState state) {
		CardPlayer player = data.playerID() != null ? data.playerID().getFrom(state) : null;
		if (player != null) {
			camera.update(settings, player.worldPos(), rootGui());
		}
	}

	private void initRenderers(ResourcePack rp) {
		particleRenderer = rp.getRenderer("particle", ParticleRenderer.class);
		worldMapRenderer = new WorldMapRenderer(glContext(), rp.getRenderer("hexagon", HexagonRenderer.class));
		actorRenderer = new ActorRenderer(glContext(), rp);
		chainHeapRenderer = rp.getRenderer("chainHeap", ChainHeapRenderer.class);
		rootGuiRenderer = rp.getRenderer("rootGui", RootGuiRenderer.class);
	}

	private void initGuis() {
		ResourcePack rp = resourcePack();
		dashboardGui = new CardDashboardGui(rp, settings);
		dashboardGui.setEnabled(false);
		rootGui().addChild(dashboardGui);

		deckBuildingGui = new DeckBuildingGui(rp, settings, data);
		rootGui().addChild(deckBuildingGui);
		deckBuildingGui.setEnabled(false);
		deckBuildingGui.createCardGuis(rp, settings);
		deckBuildingGui.resetTargetPositions(settings);

		rootGui().addChild(data.tools().consoleGui);
		data.tools().consoleGui.setHidden(true);
	}

	private void initCardPlayerDisplayers(ResourcePack rp) {
		GameState previousState = data.previousState();
		previousState.cardPlayers().forEach(cp -> cp.displayer().doInit(rp, previousState));
	}

	public CardDashboardGui dashboardGui() {
		return dashboardGui;
	}

	public DeckBuildingGui deckBuildingGui() {
		return deckBuildingGui;
	}

	public GameCamera camera() {
		return camera;
	}

}
