package tm.puzzle.helpers;

import java.util.ArrayList;

public class ListHelper {
    public static Integer sum(ArrayList<Integer> list) {
        return list.stream().mapToInt(x -> x).sum();
    }

    public static double avg(ArrayList<Integer> list) {
        return list.stream().mapToLong(x -> x).average().orElse(0.0);
    }
}
