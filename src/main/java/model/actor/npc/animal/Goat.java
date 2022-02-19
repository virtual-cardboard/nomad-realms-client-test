package model.actor.npc.animal;

import static model.item.Item.MEAT;

import graphics.displayer.GoatDisplayer;
import model.actor.NPCActor;
import model.item.ItemCollection;

/**
 * A goat
 * 
 * @author Jay
 */
public class Goat extends NPCActor {

	private transient GoatDisplayer displayer;

	public Goat() {
		super(10);
		this.displayer = new GoatDisplayer(id);
		this.ai = new GoatAI();
	}

	public Goat(long id, GoatDisplayer displayer) {
		super(10, id);
		this.displayer = displayer;
	}

	@Override
	public GoatDisplayer displayer() {
		return displayer;
	}

	@Override
	public Goat copy() {
		return super.copyTo(new Goat(id, displayer));
	}

	@Override
	public ItemCollection dropItems() {
		return new ItemCollection(MEAT, 1);
	}

}
