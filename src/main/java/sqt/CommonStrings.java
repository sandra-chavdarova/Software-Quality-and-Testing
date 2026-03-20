package sqt;

import java.util.*;

public class CommonStrings {
    public static List<String> findCommonIgnoreCase(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>();

        Set<String> set2 = new HashSet<>();
        for (String s : list2) {
            set2.add(s.toLowerCase());
        }

        Set<String> seen = new HashSet<>();

        for (String s : list1) {
            String lower = s.toLowerCase();

            if (set2.contains(lower) && !seen.contains(lower)) {
                result.add(s);
                seen.add(lower);
            }
        }
        return result;
    }
}