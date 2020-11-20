package tm.puzzle.heuristics;

import sac.State;
import sac.StateFunction;

import tm.puzzle.Puzzle;

public class ManhattanHeuristics extends StateFunction
{
    @Override
    public double calculate(State state)
    {
        Puzzle puzzle = (Puzzle) state;

        double h = 0.0;

        for (int i = 0, k = 0; i < Puzzle.N; i++) {
            for (int j = 0; j < Puzzle.N; j++) {
                if (puzzle.board[i][j] != 0) {
                    int new_i = (int) (puzzle.board[i][j] / Puzzle.N);
                    int new_j = (int) (puzzle.board[i][j] % Puzzle.N);

                    double distance = Math.abs(i - new_i) + Math.abs(j - new_j);

                    h += distance;
                }

                k++;
            }
        }

        return h;
    }
}