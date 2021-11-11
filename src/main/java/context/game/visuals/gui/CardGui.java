package context.game.visuals.gui;

import static math.Quaternion.interpolate;

import common.math.Matrix4f;
import common.math.Vector2f;
import common.math.Vector3f;
import context.GLContext;
import context.ResourcePack;
import context.visuals.gui.Gui;
import context.visuals.gui.constraint.dimension.PixelDimensionConstraint;
import context.visuals.gui.constraint.position.PixelPositionConstraint;
import context.visuals.lwjgl.Texture;
import context.visuals.renderer.TextRenderer;
import context.visuals.renderer.TextureRenderer;
import context.visuals.text.GameFont;
import math.UnitQuaternion;
import model.card.GameCard;

public class CardGui extends Gui {

	public static final float WIDTH = 256;
	public static final float HEIGHT = 512;
	public static final float SIZE_FACTOR = 1.2f;
	private static final UnitQuaternion DEFAULT_ORIENTATION = new UnitQuaternion(new Vector3f(0, 0, 1), 0);

	private GameCard card;
	private TextureRenderer textureRenderer;
	private TextRenderer textRenderer;
	private Texture base;
	private Texture decoration;
	private Texture front;
	private Texture banner;
	private GameFont font;
	private boolean hovered;
	private boolean lockPos;
	private boolean lockTargetPos;
//	private int val;

	private UnitQuaternion currentOrientation = DEFAULT_ORIENTATION;

	private Vector2f targetPos = new Vector2f();

	public CardGui(GameCard card, ResourcePack resourcePack) {
		this.card = card;
		this.textureRenderer = resourcePack.getRenderer("texture", TextureRenderer.class);
		this.textRenderer = resourcePack.getRenderer("text", TextRenderer.class);
		base = resourcePack.getTexture("card_base");
		decoration = resourcePack.getTexture("card_decoration_" + card.type().name);
		front = resourcePack.getTexture("card_front");
		banner = resourcePack.getTexture("card_banner");
		font = resourcePack.getFont("baloo2");
		setPosX(new PixelPositionConstraint(0));
		setPosY(new PixelPositionConstraint(0));
		setWidth(new PixelDimensionConstraint(WIDTH));
		setHeight(new PixelDimensionConstraint(HEIGHT));
	}

	@Override
	public void render(GLContext glContext, Matrix4f matrix4f, float x, float y, float width, float height) {
		currentOrientation = new UnitQuaternion(interpolate(currentOrientation, DEFAULT_ORIENTATION, 0.1f));
		Matrix4f rotation = currentOrientation.toRotationMatrix();
		Matrix4f copy = matrix4f.copy().translate(x + width * 0.5f, y + height * 0.5f).scale(new Vector3f(1, 1, 0f)).multiply(rotation)
				.translate(-width * 0.5f, -height * 0.5f).scale(width, height);
//		Matrix4f cardImageTransformation = matrix4f.copy().translate(x + width * 0.5f, y + height * 0.5f).scale(new Vector3f(1, 1, 0.01f))
//				.multiply(rotation)
//				.translate(new Vector3f(-width * 0.5f, -height * 0.5f, 0)).scale(width, height).translate(new Vector3f(0, 0, 20));
//		Matrix4f cardImageTransformation = matrix4f.copy().translate(x + width * 0.5f, y + height * 0.5f).scale(new Vector3f(1, 1, 0.01f))
//				.multiply(rotation)
//				.translate(new Vector3f(-width * 0.5f, -height * 0.5f, 20)).scale(width, height);
		textureRenderer.render(glContext, base, copy);
		textureRenderer.render(glContext, decoration, copy.copy().translate(0, 0, 8));
		textureRenderer.render(glContext, front, copy.copy().translate(0, 0, 12));
		textureRenderer.render(glContext, banner, copy.copy().translate(0, 0, 16));
		textureRenderer.render(glContext, card.texture(), copy.copy().translate(0, 0, 20));
//		textRenderer.render(glContext, matrix4f, card.name(), x + width * 0.3f, y + height * 0.45f, width, font, width * 0.07f, rgb(28, 68, 124));
//		textRenderer.render(glContext, matrix4f, card.text(), x + width * 0.21f, y + height * 0.52f, width * 0.58f, font, width * 0.06f, rgb(28, 68, 124));
	}

	public void updatePos() {
		if (lockPos) {
			return;
		}
		PixelPositionConstraint posX = (PixelPositionConstraint) getPosX();
		PixelPositionConstraint posY = (PixelPositionConstraint) getPosY();
		float width = ((PixelDimensionConstraint) getWidth()).getPixels();
		float height = ((PixelDimensionConstraint) getHeight()).getPixels();
		float centerX = posX.getPixels() + width * 0.5f;
		float centerY = posY.getPixels() + height * 0.5f;
		if (Math.abs(targetPos.x - centerX) <= 1 && Math.abs(targetPos.y - centerY) <= 1) {
			posX.setPixels(targetPos.x - width * 0.5f);
			posY.setPixels(targetPos.y - height * 0.5f);
			return;
		}
		posX.setPixels(centerX - width * 0.5f + (targetPos.x - centerX) * 0.2f);
		posY.setPixels(centerY - height * 0.5f + (targetPos.y - centerY) * 0.2f);
	}

	public void hover() {
		if (!hovered) {
			((PixelPositionConstraint) getPosX()).setPixels(((PixelPositionConstraint) getPosX()).getPixels() - WIDTH * (SIZE_FACTOR - 1) * 0.5f);
			((PixelPositionConstraint) getPosY()).setPixels(((PixelPositionConstraint) getPosY()).getPixels() - HEIGHT * (SIZE_FACTOR - 1) * 0.5f);
			((PixelDimensionConstraint) getWidth()).setPixels(WIDTH * SIZE_FACTOR);
			((PixelDimensionConstraint) getHeight()).setPixels(HEIGHT * SIZE_FACTOR);
			hovered = true;
		}
	}

	public void unhover() {
		if (hovered) {
			((PixelPositionConstraint) getPosX()).setPixels(((PixelPositionConstraint) getPosX()).getPixels() + WIDTH * (SIZE_FACTOR - 1) * 0.5f);
			((PixelPositionConstraint) getPosY()).setPixels(((PixelPositionConstraint) getPosY()).getPixels() + HEIGHT * (SIZE_FACTOR - 1) * 0.5f);
			((PixelDimensionConstraint) getWidth()).setPixels(WIDTH);
			((PixelDimensionConstraint) getHeight()).setPixels(HEIGHT);
			hovered = false;
		}
	}

	public boolean hovered() {
		return hovered;
	}

	public boolean lockedPos() {
		return lockPos;
	}

	public void setLockPos(boolean lockPos) {
		this.lockPos = lockPos;
	}

	public boolean lockedTargetPos() {
		return lockTargetPos;
	}

	public void setLockTargetPos(boolean lockTargetPos) {
		this.lockTargetPos = lockTargetPos;
	}

	public GameCard card() {
		return card;
	}

	public void setPos(Vector2f pos) {
		((PixelPositionConstraint) getPosX()).setPixels(pos.x);
		((PixelPositionConstraint) getPosY()).setPixels(pos.y);
	}

	public void setTargetPos(float x, float y) {
		if (lockTargetPos) {
			System.out.println("locked target pos.");
			return;
		}
		targetPos.set(x, y);
	}

	public UnitQuaternion currentOrientation() {
		return currentOrientation;
	}

	public void setCurrentOrientation(UnitQuaternion currentOrientation) {
		this.currentOrientation = currentOrientation;
	}

}
