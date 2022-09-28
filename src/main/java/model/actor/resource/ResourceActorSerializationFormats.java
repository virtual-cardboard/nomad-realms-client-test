package model.actor.resource;

import static derealizer.SerializationClassGenerator.generate;
import static derealizer.format.SerializationFormat.types;

import derealizer.format.Derealizable;
import derealizer.format.FieldNames;
import derealizer.format.HasId;
import derealizer.format.SerializationFormat;
import derealizer.format.SerializationFormatEnum;

public enum ResourceActorSerializationFormats implements SerializationFormatEnum, HasId {

	@FieldNames({})
	TREE_ACTOR(types(), TreeActor.class),
	;

	static {
		TREE_ACTOR.id = 100;
	}

	private short id = -1;
	private final SerializationFormat format;
	private final Class<? extends Derealizable> pojoClass;

	private ResourceActorSerializationFormats(SerializationFormat format, Class<? extends Derealizable> pojoClass) {
		this.format = format;
		this.pojoClass = pojoClass;
	}

	@Override
	public short id() {
		return id;
	}

	@Override
	public SerializationFormat format() {
		return format;
	}

	@Override
	public Class<? extends Derealizable> objClass() {
		return pojoClass;
	}

	public static void main(String[] args) {
		generate(ResourceActorSerializationFormats.class, ResourceActor.class);
	}

}
