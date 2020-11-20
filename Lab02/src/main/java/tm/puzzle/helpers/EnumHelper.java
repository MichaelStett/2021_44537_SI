package tm.puzzle.helpers;

import java.security.SecureRandom;

public class EnumHelper {
    // https://stackoverflow.com/a/14257525/13037458
    private static final SecureRandom random = new SecureRandom();

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}
