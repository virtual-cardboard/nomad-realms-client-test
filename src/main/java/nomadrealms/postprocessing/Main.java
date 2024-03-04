package nomadrealms.postprocessing;

import nengen.Nengen;
import nomadrealms.postprocessing.context.MainContext;

public class Main {

	public static void main(String[] args) {
		MainContext context = new MainContext();

		Nengen nengen = new Nengen();
		nengen.configure()
				.setWindowDim(800, 600)
				.setWindowName("Nomad Realms")
				.setFrameRate(60)
				.setTickRate(10);
		nengen.startNengen(context);
	}

}
