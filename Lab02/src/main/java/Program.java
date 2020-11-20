import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import sac.graph.AStar;
import sac.graph.GraphSearchAlgorithm;

import tm.puzzle.Puzzle;
import tm.puzzle.Result;
import tm.puzzle.heuristics.ManhattanHeuristics;
import tm.puzzle.heuristics.MisplacedTilesHeuristics;

import static java.text.MessageFormat.*;

import static tm.puzzle.Result.*;
import static tm.puzzle.helpers.ListHelper.*;
import static tm.puzzle.helpers.SystemHelper.print;

public class Program {
    private static final int MEGABYTE = (1024*1024);

    public static void main(String[] args) {
        Puzzle.N = 3;

        int shuffles = 1000;
        int count = 1;

        var puzzle = new Puzzle();

        var misplacedTilesClosedList = new ArrayList<Integer>(count);
        var manhattanTimeClosedList = new ArrayList<Integer>(count);

        var misplacedTilesHeuristic = new MisplacedTilesHeuristics();
        var manhattanHeuristic = new ManhattanHeuristics();

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        for (int i = 0; i < count; i++) {
            puzzle.Shuffle(shuffles);

            print(puzzle);

            var algo = new AStar(puzzle);

            try {
                Result misplacedTilesResult = execute(algo, misplacedTilesHeuristic);

                print("Misplaced");
                print(solutionProperties(algo));

                Result manhattanResult = execute(algo, manhattanHeuristic);

                print("Manhattan");
                print(solutionProperties(algo));

                //print(format("{0}:", i+1));
                //print(format("Misplaced: {0}[ms]\nManhattan: {1}[ms]\n", misplacedTilesResult.getTime(), manhattanResult.getTime()));

                misplacedTilesClosedList.add(misplacedTilesResult.getClosedStates());
                manhattanTimeClosedList.add(manhattanResult.getClosedStates());

            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
                long maxMemory = heapUsage.getMax() / MEGABYTE;
                long usedMemory = heapUsage.getUsed() / MEGABYTE;
                System.out.println(i+ " : Memory Use :" + usedMemory + "M/" +maxMemory+"M");
            }
        }

        print(format("N: {0}\nPuzzles solved: {1}\nShuffles on each: {2}\n", Puzzle.N, count, shuffles));
        print(format("Misplaced avg: {0, number, .##}", avg(misplacedTilesClosedList)));
        print(format("Manhattan avg: {0, number, .##}", avg(manhattanTimeClosedList)));
    }

    private static String solutionProperties(GraphSearchAlgorithm algo) {
        return format("Time [ms]: {0}\nClosed: {1}\nOpen: {2}\nSolutions: {3}\nMoves: {4}\n",
                algo.getDurationTime(), algo.getClosedStatesCount(), algo.getOpenSet().size(), algo.getSolutions().size(), algo.getSolutions().get(0).getMovesAlongPath());
    }
}
