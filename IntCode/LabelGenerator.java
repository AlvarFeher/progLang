package IntCode;

import java.util.ArrayList;
import java.util.List;

public class LabelGenerator {
    private int tempCount = 0, labelCount = 0;
    private final List<String[]> instructions = new ArrayList<>();
    String newTemp() { return "t" + tempCount++; }
    String newLabel() { return "L" + labelCount++; }

    public void emit(String op, String arg1, String arg2, String result) {
        instructions.add(new String[]{op, arg1, arg2, result});
    }

    public void emitLabel(String label) {
        instructions.add(new String[]{"LABEL", label, null, null});
    }

}
