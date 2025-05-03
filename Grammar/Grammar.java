package Grammar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grammar {
    public Map<String, List<List<String>>> getGrammar(){
        Map<String, List<List<String>>> grammar = new HashMap<>();

        // Rule 1: Programa
        grammar.put("Program", Arrays.asList(
                Arrays.asList("Declarations", "Inici", "Llista_inst", "FI")
        ));

        // Rule 2: Declarations
        grammar.put("Declarations", Arrays.asList(
                Arrays.asList("Declaration", "Declarations"),
                Arrays.asList("ε")
        ));

        grammar.put("Declaration", Arrays.asList(
                Arrays.asList("Dec_Cte_Var"),
                Arrays.asList("Dec_Fun")
        ));

        // Rule 3: Declaraciones de constantes/variables
        grammar.put("Dec_Cte_Var", Arrays.asList(
                Arrays.asList("Const", "ID", "=", "Exp", ";"),
                Arrays.asList("Tipus", "ID", ";")
        ));

        // Rule 4: Declaración de funciones
        grammar.put("Dec_Fun", Arrays.asList(
                Arrays.asList("FUNCIO", "ID", "(", "Llista_Param", ")", ":", "tipus_simple", "{", "Dec_Cte_Var", "Llista_inst", "}", ";")
        ));

        // Parámetros de función
        grammar.put("Llista_Param", Arrays.asList(
                Arrays.asList("Parameter", "ParamListTail"),
                Arrays.asList("ε") // sin parámetros
        ));
        grammar.put("Parameter", Arrays.asList(
                Arrays.asList("Tipus", "ID")
        ));
        grammar.put("ParamListTail", Arrays.asList(
                Arrays.asList(",", "Parameter", "ParamListTail"),
                Arrays.asList("ε")
        ));

        // Tipos
        grammar.put("Tipus", Arrays.asList(
                Arrays.asList("tipus_simple"),
                Arrays.asList("vector", "[", "cte_entera", "]", "de", "tipus_simple")
        ));


        grammar.put("Exp", Arrays.asList(
                Arrays.asList("ExpSimple", "ExpRelTail")
        ));

        grammar.put("ExpRelTail", Arrays.asList(
                Arrays.asList("oper_rel", "ExpSimple"),
                Arrays.asList("ε")
        ));


        grammar.put("ExpSimple", Arrays.asList(
                Arrays.asList("Term", "ExpSimpleTail")
        ));
        grammar.put("ExpSimpleTail", Arrays.asList(
                Arrays.asList("+", "Term", "ExpSimpleTail"),
                Arrays.asList("-", "Term", "ExpSimpleTail"),
                Arrays.asList("ε")
        ));

        grammar.put("Term", Arrays.asList(
                Arrays.asList("Factor", "TermTail")
        ));
        grammar.put("TermTail", Arrays.asList(
                Arrays.asList("*", "Factor", "TermTail"),
                Arrays.asList("/", "Factor", "TermTail"),
                Arrays.asList("ε")
        ));

        grammar.put("Factor", Arrays.asList(
                Arrays.asList("(", "Exp", ")"),
                Arrays.asList("ID","FactorTail"),
                Arrays.asList("cte_entera"),
                Arrays.asList("cte_cadena"),
                Arrays.asList("CERT"),
                Arrays.asList("FALS")
        ));

        grammar.put("FactorTail", Arrays.asList(
                Arrays.asList("(", "Llista_expressio", ")"), // Function call
                Arrays.asList("ε") // Just an ID (variable)
        ));

        grammar.put("Inici", Arrays.asList(
                Arrays.asList("INICI")
        ));
        grammar.put("FI", Arrays.asList(
                Arrays.asList("FI")
        ));


        grammar.put("Llista_inst", Arrays.asList(
                Arrays.asList("Inst", "Llista_inst"),
                Arrays.asList("ε")
        ));

        grammar.put("Inst", Arrays.asList(
                Arrays.asList("ID", "InstTail"),
                Arrays.asList("LLEGIR", "(", "Llista_variables", ")", ";"),
                Arrays.asList("ESCRIURE", "(", "Llista_expressio", ")", ";"),
                Arrays.asList("SI", "Exp", "LLAVORS", "Llista_inst", "SINO", "Llista_inst", "FISI",";"),
                Arrays.asList("SI", "Exp", "LLAVORS", "Llista_inst", "FISI",";"),
                Arrays.asList("REPETIR", "Llista_inst", "FINS", "Exp", ";"),
                Arrays.asList("MENTRE", "Exp", "FER", "Llista_inst", "FIMENTRE",";"),
                Arrays.asList("RETORNAR", "Exp", ";")
        ));

        grammar.put("InstTail", Arrays.asList(
                Arrays.asList("=", "Exp", ";"),
                Arrays.asList("(", "Llista_expressio", ")", ";"),
                Arrays.asList(";","ε"),
                Arrays.asList("ε")
        ));

        grammar.put("Llista_expressio", Arrays.asList(
                Arrays.asList("Exp", "Llista_expressio_tail"),
                Arrays.asList("ε")
        ));

        grammar.put("Llista_expressio_tail", Arrays.asList(
                Arrays.asList(",", "Exp", "Llista_expressio_tail"),
                Arrays.asList("ε")
        ));

        return grammar;

    }
}
