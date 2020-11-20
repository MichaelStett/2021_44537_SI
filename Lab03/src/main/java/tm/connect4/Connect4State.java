package tm.connect4;

import sac.State;
import sac.StateFunction;
import sac.game.GameState;
import sac.game.GameStateImpl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Connect4State extends GameStateImpl {
    public static int rowCount;
    public static int colCount;

    public byte[][] board;

    public Connect4State() {
        board = new byte[rowCount][colCount];

        for	(int i = 0; i < rowCount; i++)
            for	(int j = 0; j < colCount; j++)
                board[i][j] = 0;

        this.setMaximizingTurnNow(true);
    }

    public Connect4State(Connect4State other) {
        this.board = new byte[rowCount][colCount];

        for	(int i = 0; i < rowCount; i++)
            for	(int j = 0; j < colCount; j++)
                board[i][j] = other.board[i][j];

        this.setMaximizingTurnNow(other.maximizingTurnNow);
    }

    @Override
    public List<GameState> generateChildren() {
        List<GameState> children = new LinkedList<>();

        for (int j = 0; j < colCount; j++) {
            Connect4State child = new Connect4State(this);

            child.MakeMove(j);
            child.setMoveName(Integer.toString(j));
            children.add(child);
        }

        return children;
    }

    @Override
    public int hashCode() {
        byte[] arr = new byte[rowCount * colCount];

        int k = 0;
        for	(int i = 0; i < rowCount; i++)
            for	(int j = 0; j < colCount; j++)
                arr[k++] = board[i][j];

        return Arrays.hashCode(arr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for	(int i = 0; i < rowCount; i++) {
            for	(int j = 0; j < colCount; j++) {

                String tile = switch (board[i][j]) {
                    case 1 -> "o";
                    case 2 -> "x";
                    default -> ".";
                };

                sb.append(tile);
                sb.append(" ");
            }
            sb.append("\n");
        }

        for	(int j = 0; j < colCount; j++) {
            sb.append(j);
            sb.append(" ");
        }

        return sb.toString();
    }

    private boolean isLegal(int row, int col) {
        if (row >= rowCount || row < 0)
            return false;

        if (col >= colCount || col < 0)
            return false;

        return board[0][col] == 0;
    }

    public void MakeMove(int column) {
        var player = isMaximizingTurnNow() ? 1 : 2;

        for(int i = rowCount - 1; i >= 0; --i) {
            if(board[i][column] == 0) {
                board[i][column] = (byte)player;
                this.setMaximizingTurnNow(!isMaximizingTurnNow());
                return;
            }
        }
    }

    private boolean isVerticalSolution(int row, int col) {
        var countInRow = 0;

        for (int i = row; i < rowCount; i++) {
            if (board[i][col] == board[row][col]){
                countInRow++;
            } else {
                break;
            }
        }

        return countInRow >= 4;
    }

    private boolean isHorizontalSolution(int row, int col) {
        var countInRow = 0;

        for (int j = col; j < colCount; j++) {
            if (board[row][j] == board[row][col]){
                countInRow++;
            } else {
                break;
            }
        }

        return countInRow >= 4;
    }

    private boolean isDiagonalPositiveSolution(int row, int col) {
        var countInRow = 0;

        for (int rowIndex = row, colIndex = col;
             rowIndex >= row - 3 && colIndex >= col - 3;
             rowIndex--, colIndex--) {
            if(colIndex > colCount) {
                break;
            }

            if (this.isLegal(rowIndex, colIndex)) {
                if(board[rowIndex][colIndex] == board[row][col]) {
                    countInRow++;
                } else {
                    break;
                }
            }
        }

        return countInRow >= 4;
    }

    private boolean isDiagonalNegativeSolution(int row, int col) {
        var countInRow = 0;

        for (int rowIndex = row, colIndex = col;
                 rowIndex >= row - 3 && colIndex <= col + 3;
                 rowIndex--, colIndex++) {
            if(colIndex > colCount) {
                break;
            }

            if (this.isLegal(rowIndex, colIndex)) {
                if(board[rowIndex][colIndex] == board[row][col]) {
                    countInRow++;
                } else {
                    break;
                }
            }
            else {
                break;
            }
        }

        return countInRow >= 4;
    }

    private boolean isTopRowSolution(int col) {
        return board[0][col] != 0;
    }

    public boolean isSolution() {
        var solutionFlag = false;

        for	(int i = 0; i < rowCount; i++) {
            for	(int j = 0; j < colCount; j++) {
                if (board[i][j] != 0) {
                    if (isVerticalSolution(i, j)) {
                        solutionFlag = true;
                    }

                    if (isHorizontalSolution(i, j)) {
                        solutionFlag = true;
                    }

                    if (isDiagonalPositiveSolution(i, j)) {
                        solutionFlag = true;
                    }

                    if (isDiagonalNegativeSolution(i, j)) {
                        solutionFlag = true;
                    }

                    if(isTopRowSolution(j)) {
                        solutionFlag = true;
                    }
                }
            }
        }

        return solutionFlag;
    }

    static {
        setHFunction(new StateFunction() {
            @Override
            public double calculate(State state) {
                Connect4State connect4State = (Connect4State) state;

                var isMaximizing = connect4State.isMaximizingTurnNow();

                // If child is solution return sure win/lose
                if (connect4State.isSolution()) {
                    return (isMaximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
                }

                double h = 0.0;

                for	(int i = 0; i < rowCount; i++) {
                    for (int j = 0; j < colCount; j++) {

                        var currentTile = connect4State.board[i][j];

                        if (currentTile != 0) {
                            var sum = 0;

                            // Left-diag
                            for (int k = 1; k <= 4; k++) {
                                if (!connect4State.isLegal(i - k, j - k)) {
                                    break;
                                }

                                if (currentTile == connect4State.board[i - k][j - k]) {
                                    sum++;
                                }
                            }

                            // Right-diag
                            for (int k = 1; k <= 4; k++) {
                                if (!connect4State.isLegal(i - k, j + k)) {
                                    break;
                                }

                                if (currentTile == connect4State.board[i - k][j + k]) {
                                    sum++;
                                }
                            }

                            // Up
                            for (int k = 1; k <= 4; k++) {
                                if (!connect4State.isLegal(i - k, j)) {
                                    break;
                                }

                                if (currentTile == connect4State.board[i - k][j]) {
                                    sum++;
                                }
                            }

                            // Right
                            for (int k = 1; k <= 4; k++) {
                                if (!connect4State.isLegal(i, j + k)) {
                                    break;
                                }

                                if (currentTile == connect4State.board[i][j + k]) {
                                    sum++;
                                }
                            }

                            h += (isMaximizing ? -sum : +sum);
                        }
                    }
                }

                return h;
            }
        });
    }
}
