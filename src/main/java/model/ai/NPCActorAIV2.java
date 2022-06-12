package model.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import event.logicprocessing.CardPlayedEvent;
import model.actor.NPCActor;
import model.hidden.objective.Objective;
import model.hidden.objective.ObjectiveTree;
import model.hidden.objective.ObjectiveType;
import model.hidden.objective.decomposition.ObjectiveDecompositionRule;
import model.state.GameState;

public abstract class NPCActorAIV2 {

	protected ObjectiveTree objectiveTree;
	protected Map<ObjectiveType, List<ObjectiveDecompositionRule>> decompositionRulesMap;

	public NPCActorAIV2(ObjectiveType root, ObjectiveDecompositionRule... rules) {
		objectiveTree = new ObjectiveTree(new Objective(root, null, null));
		decompositionRulesMap = convertRulesArrayToMap(rules);
	}

	public CardPlayedEvent update(NPCActor npc, long tick, GameState state) {
		return playCard(npc, state);
	}

	public abstract CardPlayedEvent playCard(NPCActor npc, GameState state);

	public abstract NPCActorAIV2 copy();

	public <A extends NPCActorAIV2> A copyTo(A ai) {
		ai.objectiveTree = objectiveTree;
		ai.decompositionRulesMap = decompositionRulesMap;
		return ai;
	}

	private static Map<ObjectiveType, List<ObjectiveDecompositionRule>> convertRulesArrayToMap(ObjectiveDecompositionRule[] rules) {
		Map<ObjectiveType, List<ObjectiveDecompositionRule>> map = new HashMap<>();
		for (ObjectiveDecompositionRule rule : rules) {
			map.computeIfAbsent(rule.objective(), key -> new ArrayList<>()).add(rule);
		}
		return map;
	}

}
