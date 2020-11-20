package tm.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sac.State;
import sac.StateFunction;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

public class Sudoku extends GraphStateImpl
{
    private int N = 3;
    private int N2 = N * N;

    private byte[][] board;

    public int zeros = N2 * N2;

    public int getZeros()
    {
        return zeros;
    }

    public Sudoku(String s)
    {
        initializeEmpty();

        stringToBoardN3(s);

        refreshZeros();
    }

    public Sudoku(Sudoku other)
    {
        board = new byte[N2][N2];

        for	(int i =0; i < N2; i++)
            for	(int j =0; j < N2; j++)
                board[i][j] = other.board[i][j];

        zeros = other.zeros;
    }

    public boolean isLegal()
    {
        byte[] group = new byte[N2];

        // rows
        for (int i = 0; i < N2; i++)
        {
            for (int j = 0; j < N2; j++)
            {
                group[j] = board[i][j];
            }

            if (!isGroupLegal(group))
                return false;
        }

        // cols
        for (int i = 0; i < N2; i++)
        {
            for (int j = 0; j < N2; j++)
            {
                group[j] = board[j][i];
            }

            if (!isGroupLegal(group))
                return false;
        }

        // squares
        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
            {
                int q = 0;
                for (int k = 0; k < N; k++)
                {
                    for (int l = 0; l < N; l++)
                    {
                        group[q++] = board[i * N + k][j * N + l];
                    }
                }

                if (!isGroupLegal(group))
                    return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        byte[] arr = new byte[N2 * N2];

        int k = 0;
        for	(int i = 0; i < N2; i++)
            for	(int j = 0; j < N2; j++)
                arr[k++] = board[i][j];

        return Arrays.hashCode(arr);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for	(int i = 0; i < N2; i++)
        {
            for	(int j = 0; j < N2; j++)
            {
                sb.append(board[i][j]);

                if (j == N2 - 1)
                    sb.append("\n");
                else
                    sb.append(", ");
            }
        }

        return sb.toString();
    }

    private void initializeEmpty()
    {
        board = new byte[N2][N2];

        for	(int i =0; i < N2; i++)
            for	(int j =0; j < N2; j++)
                board[i][j] = 0;
    }

    private void stringToBoardN3(String s)
    {
        int k = 0;

        for	(int i = 0; i < N2; i++)
        {
            for	(int j =0; j < N2; j++, k++)
            {
                board[i][j] = Byte.valueOf(s.substring(k, k + 1));
            }
        }
    }

    private void refreshZeros()
    {
        int count = 0;

        for (int i = 0; i < N2; i++)
        {
            for (int j = 0; j < N2; j++)
            {
                if (board[i][j] == 0)
                    count++;
            }
        }

        zeros = count;
    }

    private boolean isGroupLegal(byte[] group)
    {
        boolean[] visited = new boolean[N2];

        for (int i = 0; i < N2; i++)
        {
            visited[i] = false;
        }

        for (int i = 0; i < N2; i++)
        {
            // discard empty spaces
            if (group[i] == 0) continue;

            if (visited[group[i] - 1] == true) return false;

            visited[group[i] - 1] = true;
        }

        return true;
    }

    @Override
    public List<GraphState> generateChildren()
    {
        List<GraphState> children = new ArrayList<GraphState>();

        int i = 0, j = 0;

        zeroSeeker:
        for (i = 0; i < N2; i++)
        {
            for (j = 0; j < N2; j++)
            {
                if (board[i][j] == 0)
                    break zeroSeeker;
            }
        }

        if (i == N2) return children;

        for (int k = 0; k < N2; k++)
        {
            Sudoku child = new Sudoku(this);

            child.board[i][j] = (byte)(k + 1);

            if (child.isLegal())
            {
                children.add(child);
                child.zeros--;
            }
        }

        return children;
    }

    @Override
    public boolean isSolution()
    {
        return ((zeros == 0) && (isLegal()));
    }

    static {
        setHFunction( new StateFunction() {
            @Override
            public double calculate(State state) {
                Sudoku sudoku = (Sudoku) state;

                return sudoku.getZeros();
            }
        });
    }
}
