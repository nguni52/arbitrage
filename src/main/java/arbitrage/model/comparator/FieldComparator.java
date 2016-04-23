package arbitrage.model.comparator;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * Created by nguni52 on 16/04/23.
 */
public class FieldComparator implements Comparator<Field> {
    @Override
    public int compare(Field o1, Field o2) {
        if(o1.getName().compareTo(o2.getName()) == 0) {
            return 0;
        }

        return o1.getName().compareTo(o2.getName()) > 0 ? 1 : -1;
    }
}
