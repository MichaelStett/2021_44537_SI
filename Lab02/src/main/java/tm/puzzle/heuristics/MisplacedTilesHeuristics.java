package tm.puzzle.heuristics;

import sac.State;
import sac.StateFunction;

import tm.puzzle.Puzzle;

public class MisplacedTilesHeuristics extends StateFunction {
    @Override
    public double calculate(State state) {
        Puzzle puzzle = (Puzzle) state;

        double h = 0.0;

        for (int i = 0, k = 0; i < Puzzle.N; i++)
            for (int j = 0; j < Puzzle.N; j++, k++)
                if (puzzle.board[i][j] != 0 && puzzle.board[i][j] != k)
                    h += 1.0;

        return h;
    }
}