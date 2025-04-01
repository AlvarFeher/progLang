package Parser;

import java.util.Arrays;
import java.util.List;

public class Production {
    String left;
    List<String> right;

    public Production(String left, String... right) {
        this.left = left;
        this.right = Arrays.asList(right);
    }

    public boolean isEpsilon() {
        return right.isEmpty();
    }

    @Override
    public String toString() {
        return left + " → " + String.join(" ", right);
    }

}
