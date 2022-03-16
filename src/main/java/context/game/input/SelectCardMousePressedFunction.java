package context.game.input;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.function.Function;

import common.event.GameEvent;
import context.game.visuals.gui.dashboard.WorldCardGui;
import context.input.event.MousePressedInputEvent;

public class SelectCardMousePressedFunction implements Function<MousePressedInputEvent, GameEvent> {

	private NomadsInputInfo inputInfo;

	public SelectCardMousePressedFunction(NomadsInputInfo inputInfo) {
		this.inputInfo = inputInfo;
	}

	@Override
	public GameEvent apply(MousePressedInputEvent event) {
		if (inputInfo.cardWaitingForTarget != null || event.button() != GLFW_MOUSE_BUTTON_LEFT) {
			return null;
		}
		WorldCardGui hovered = inputInfo.hoveredCardGui();
		inputInfo.unhoverAllCardGuis();
		if (hovered != null && inputInfo.cardWaitingForTarget == null) {
			hovered.hover(inputInfo.settings);
			inputInfo.selectedCardGui = hovered;
			inputInfo.cardMouseOffset = hovered.centerPos().negate().add(inputInfo.cursor.pos().toVec2f());
			hovered.setLockPos(true);
		}
		return null;
	}

}
