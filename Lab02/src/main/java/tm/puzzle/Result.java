package tm.puzzle;

import sac.StateFunction;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphState;

public class Result {
    private final GraphState Sol;
    private final long Time;
    private final int ClosedStates;

    public Result(GraphState sol, long time, int closedStates) {
        Sol = sol;
        Time = time;
        ClosedStates = closedStates;
    }

    public GraphState getSol() {
        return Sol;
    }

    public long getTime() {
        return Time;
    }

    public int getClosedStates() {
        return ClosedStates;
    }

    public static Result execute(GraphSearchAlgorithm algo, StateFunction function) {
        Puzzle.setHFunction(function);

        algo.execute();

        GraphState sol = algo.getSolutions().get(0);
        long time = algo.getDurationTime();

        int closedStates = algo.getClosedStatesCount();

        return new Result(sol, time, closedStates);
    }
}
