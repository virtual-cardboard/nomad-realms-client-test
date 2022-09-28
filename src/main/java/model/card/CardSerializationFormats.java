package model.card;

import derealizer.Derealizable;
import derealizer.DerealizerEnum;

public enum CardSerializationFormats implements DerealizerEnum {

	WORLD_CARD(WorldCard.class),
	;

	private final Class<? extends Derealizable> objClass;
	private final Class<? extends DerealizerEnum> derealizerEnum;

	CardSerializationFormats(Class<? extends Derealizable> objClass) {
		this(objClass, null);
	}

	CardSerializationFormats(Class<? extends Derealizable> objClass, Class<? extends DerealizerEnum> derealizerEnum) {
		this.objClass = objClass;
		this.derealizerEnum = derealizerEnum;
	}

	@Override
	public Class<? extends Derealizable> objClass() {
		return objClass;
	}

	@Override
	public Class<? extends DerealizerEnum> derealizerEnum() {
		return derealizerEnum;
	}

}
