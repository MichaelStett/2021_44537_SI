package tm.puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

import static java.lang.System.*;
import static java.util.stream.IntStream.*;
import static tm.puzzle.helpers.EnumHelper.*;

public class Puzzle extends GraphStateImpl {
    public static int N;
    public byte[][] board;

    private static byte[][] solutionBoard = new byte[N][N];
    private int zeroColumnIndex;
    private int zeroRowIndex;

    public Puzzle() {
        byte[] digits = new byte[N*N];

        for (byte b = 0; b < N*N; b++) {
            digits[b] = b;
        }

        board = new byte[N][N];

        for	(int i = 0; i < N; i++)
            for	(int j = 0; j < N; j++)
                board[i][j] = 0;

        for	(int i = 0, k = 0; i < N; i++) {
            for	(int j = 0; j < N; j++, k++) {
                board[i][j] = digits[k];

                if (board[i][j] == 0) {
                    zeroColumnIndex = i;
                    zeroRowIndex = j;
                }
            }
        }

        if(solutionBoard.length < 1 || solutionBoard[0].length < 1) {
            Puzzle.initializeSolutionBoard(board);
        }
    }

    public Puzzle(Puzzle other) {
        this.board = new byte[N][N];
        this.zeroColumnIndex = other.zeroColumnIndex;
        this.zeroRowIndex = other.zeroRowIndex;

        range(0, N).forEach(i ->
            arraycopy(other.board[i], 0, this.board[i], 0, N)
        );
    }

    private int[] MoveIndexes(State newState) {
        int nextColumnIndex = zeroColumnIndex;
        int nextRowIndex = zeroRowIndex;

        switch (newState) {
            case U:
                nextColumnIndex -= 1;
                break;
            case D:
                nextColumnIndex += 1;
                break;
            case L:
                nextRowIndex -= 1;
                break;
            case R:
                nextRowIndex += 1;
                break;
        }

        if (nextColumnIndex < 0 || nextColumnIndex > N - 1)
            return new int[] { -1, -1};

        if (nextRowIndex < 0 || nextRowIndex > N - 1)
            return new int[] { -1, -1};

        return new int[] { nextColumnIndex, nextRowIndex};
    }

    public boolean MakeMove(State newState) {
        int[] nextIndex = MoveIndexes(newState);

        if (nextIndex[0] != -1 && nextIndex[1] != -1) {
            board[zeroColumnIndex][zeroRowIndex] = board[nextIndex[0]][nextIndex[1]];
            board[nextIndex[0]][nextIndex[1]] = 0;

            zeroColumnIndex = nextIndex[0];
            zeroRowIndex = nextIndex[1];

            return true;
        }

        return false;
    }

    public void Shuffle(int times) {
        for	(int i = 0; i < times; i++) {
            i -= MakeMove(randomEnum(State.class)) ? 0 : 1;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for	(int i = 0; i < N; i++) {
            for	(int j = 0; j < N; j++) {
                sb.append(board[i][j]);

                if (j == N - 1)
                    sb.append("\n");
                else
                    sb.append(" ");
            }
        }

        return sb.toString();
    }

    @Override
    public int hashCode() {
        byte[] arr = new byte[N * N];

        int k = 0;
        for	(int i = 0; i < N; i++)
            for	(int j = 0; j < N; j++)
                arr[k++] = board[i][j];

        return Arrays.hashCode(arr);
    }

    @Override
    public List<GraphState> generateChildren() {
        List<GraphState> children = new ArrayList<>();

        State[] states = { State.U, State.D, State.L, State.R};

        for (State state : states) {
            int[] nextIndex = MoveIndexes(state);

            if (nextIndex[0] != -1 && nextIndex[1] != -1) {

                Puzzle child = new Puzzle(this);

                child.MakeMove(state);

                child.setMoveName(state.toString());

                children.add(child);
            }
        }

        return children;
    }

    private boolean equals(byte[][] arr1, byte[][] arr2) {
        if (arr1 == null || arr2 == null)
            return false;

        if (arr1.length != arr2.length)
            return false;

        return range(0, N).allMatch(i -> Arrays.equals(arr1[i], arr2[i]));
    }

    @Override
    public boolean isSolution() {
        return equals(this.board, Puzzle.solutionBoard);
    }

    public static void initializeSolutionBoard(byte[][] board) {
        // https://stackoverflow.com/a/53397359/13037458
        Puzzle.solutionBoard = Arrays.stream(board).map(byte[]::clone).toArray(byte[][]::new);
    }
}
