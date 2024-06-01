package nomadrealms.game.actor.cardplayer;

import common.math.Vector2i;
import nomadrealms.game.GameState;
import nomadrealms.game.card.WorldCard;
import nomadrealms.game.event.CardPlayedEvent;
import nomadrealms.game.world.map.tile.Tile;
import nomadrealms.render.RenderingEnvironment;
import visuals.lwjgl.render.framebuffer.DefaultFrameBuffer;

import java.util.stream.Stream;

import static common.colour.Colour.rgb;
import static nomadrealms.game.card.GameCard.*;
import static nomadrealms.game.world.map.tile.Tile.SCALE;

public class Farmer extends CardPlayer {

    private final String name;

    public Farmer(String name, Tile tile) {
        this.name = name;
        this.tile(tile);
        this.health(10);
        this.deckCollection().deck1().addCards(Stream.of(MOVE, HEAL, TILL_SOIL).map(WorldCard::new));
    }

    public void render(RenderingEnvironment re) {
        float scale = 0.6f * SCALE;
        DefaultFrameBuffer.instance().render(
                () -> {
                    re.textureRenderer.render(
                            re.imageMap.get("farmer"),
                            tile().getScreenPosition().x() - 0.5f * scale,
                            tile().getScreenPosition().y() - 0.7f * scale,
                            scale, scale
                    );
                    re.textRenderer.render(
                            tile().getScreenPosition().x(),
                            tile().getScreenPosition().y() + 0.1f * scale,
                            name + " FARMER",
                            0,
                            re.font,
                            0.5f * scale,
                            rgb(255, 255, 255)
                    );
                    re.textRenderer.render(
                            tile().getScreenPosition().x(),
                            tile().getScreenPosition().y() + 0.5f * scale,
                            health() + " HP",
                            0,
                            re.font,
                            0.5f * scale,
                            rgb(255, 255, 255)
                    );
                }
        );
    }

    private int thinkingTime = 10;

    @Override
    public void update(GameState state) {
        if (thinkingTime > 0) {
            thinkingTime--;
            return;
        }
        thinkingTime = (int) (Math.random() * 20) + 4;
        WorldCard cardToPlay = deckCollection().deck1().peek();
        switch (cardToPlay.card().targetingInfo().targetType()) {
            case HEXAGON:
                // TODO figure out which chunk the next tile is on
                addNextPlay(new CardPlayedEvent(cardToPlay, this,
                        state.world.getTile(new Vector2i(0, 0), tile().y() + 1, tile().x() + 1)));
                break;
            case NONE:
                addNextPlay(new CardPlayedEvent(cardToPlay, this, null));
                break;
            case CARD_PLAYER:
                addNextPlay(new CardPlayedEvent(cardToPlay, this, this));
                break;
        }
    }

}
