import sac.game.AlphaBetaPruning;
import sac.game.GameSearchAlgorithm;
import sac.game.GameSearchConfigurator;
import sac.game.MinMax;
import tm.connect4.Connect4State;

import java.util.*;

import static java.text.MessageFormat.*;
import static tm.connect4.helpers.SystemHelper.print;

public class Program {
    private static int Round = 1;

    private static void playerTurn(Connect4State connect4State) {
        var scanner = new Scanner(System.in);

        print("Player turn: ");
        print(connect4State);
        print("Choose column: ");
        var input = scanner.nextInt();

        connect4State.MakeMove(input);
    }

    private static void computerTurn(Connect4State connect4State) {
        print("Computer turn: ");
        print(connect4State);

        GameSearchAlgorithm algo = new AlphaBetaPruning(connect4State);

        //GameSearchAlgorithm algo = new MinMax(connect4State);

        algo.execute();

        print("MOVES: " + algo.getMovesScores());
       /* print("VISITED: " + algo.getClosedStatesCount());
        print("DURATION [ms]: " + algo.getDurationTime());
        print("BEST: " + algo.getBestMoves());*/

        var move = algo.getFirstBestMove();

        var opc = Integer.parseInt(move);

        print("Computer chosen: " + opc);
        connect4State.MakeMove(opc);
    }

    private static void play(Connect4State connect4State) {
        var scanner = new Scanner(System.in);

        print("Who plays first? 1 - Human | 2 - Computer");
        var input = scanner.nextInt();

        if (input != 1 && input != 2) {
            print("Invalid input.");
            System.exit(-1);
        }

        if (input == 2) {
            print(format("\nRound: {0}", Round++));
            computerTurn(connect4State);
        }

        do {
            print(format("\nRound: {0}", Round++));
            playerTurn(connect4State);
            if (connect4State.isSolution()) { print("Human won!"); break; }
            computerTurn(connect4State);
            if (connect4State.isSolution()) { print("Computer won!"); break; }
        } while(true);

        print("Final board: ");
        print(connect4State);
    }


    public static void main(String[] args) {
        Connect4State.rowCount = 6;
        Connect4State.colCount = 7;

        Connect4State connect4State = new Connect4State();

        play(connect4State);
    }
}
