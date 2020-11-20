import java.util.List;

import sac.graph.BestFirstSearch;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphSearchConfigurator;
import sac.graph.GraphState;

import tm.sudoku.Sudoku;

public class Program
{

    public static void main(String[] args)
    {
        // String boardAsString = "003020600900305001001806400008102900700000008006708200002609500800203009005010300";

        String boardAsString = "000000000000305001001806400008102900700000008006708200002609500800203009005010300";

        Sudoku sudoku = new Sudoku(boardAsString);

        System.out.println(sudoku.toString());

        GraphSearchConfigurator conf  = new GraphSearchConfigurator();
        conf.setWantedNumberOfSolutions(Integer.MAX_VALUE);

        System.out.println("children: " + sudoku.generateChildren());

        GraphSearchAlgorithm algo = new BestFirstSearch(sudoku, conf);

        algo.execute();
        List<GraphState> solutions = algo.getSolutions();

        for (GraphState sol : solutions)
        {
            System.out.println("...");
            System.out.println(sol);
        }

        System.out.println(solutionProperties(algo));

        System.out.println("Done.");
    }

    private static String solutionProperties(GraphSearchAlgorithm algo)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("Time [ms]: " + algo.getDurationTime() + "\n");
        sb.append("Closed: " + algo.getClosedStatesCount()+ "\n");
        sb.append("Open: " + algo.getOpenSet().size() + "\n");
        sb.append("Solutions: " + algo.getSolutions().size() + "\n");

        return sb.toString();
    }
}
