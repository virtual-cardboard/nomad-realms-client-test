package model.card;

import static derealizer.SerializationClassGenerator.generate;

import derealizer.format.Serializable;
import derealizer.format.SerializationFormat;
import derealizer.format.SerializationFormatEnum;

public enum CardSerializationFormats implements SerializationFormatEnum {

	;

	private final SerializationFormat format;
	private final Class<? extends Serializable> pojoClass;

	private CardSerializationFormats(SerializationFormat format, Class<? extends Serializable> pojoClass) {
		this.format = format;
		this.pojoClass = pojoClass;
	}

	@Override
	public SerializationFormat format() {
		return format;
	}

	@Override
	public Class<? extends Serializable> pojoClass() {
		return pojoClass;
	}

	public static void main(String[] args) {
		generate(CardSerializationFormats.class, Serializable.class);
	}

}
