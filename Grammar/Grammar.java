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
                Arrays.asList("Const", "Id", "=", "Exp", ";"),
                Arrays.asList("Tipus", "Id", ";")
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

        // Expresiones (parte declarativa en Dec_Cte_Var)
        // Se puede extender la gramática para Exp de la siguiente forma:
        grammar.put("Exp", Arrays.asList(
                Arrays.asList("ExpSimple", "ExpRelTail")
        ));
        // Cola de operaciones relacionales
        grammar.put("ExpRelTail", Arrays.asList(
                Arrays.asList("oper_rel", "ExpSimple"),
                Arrays.asList("ε")
        ));

        // Expresiones aritméticas
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
                Arrays.asList("ID"),
                Arrays.asList("cte_entera"),
                Arrays.asList("cte_cadena"),
                Arrays.asList("CERT"),
                Arrays.asList("FALS")
        ));

        // Regla para el inicio y final del programa
        grammar.put("Inici", Arrays.asList(
                Arrays.asList("INICI")
        ));
        grammar.put("FI", Arrays.asList(
                Arrays.asList("FI")
        ));

        // Lista de instrucciones
        grammar.put("Llista_inst", Arrays.asList(
                Arrays.asList("Inst", "Llista_inst"),
                Arrays.asList("ε")
        ));

        // Instrucciones (Inst)
        grammar.put("Inst", Arrays.asList(
                // Asignación: variable = expresión;
                Arrays.asList("ID", "=", "Exp", ";"),
                // Instrucción de lectura
                Arrays.asList("llegir", "(", "Llista_variables", ")", ";"),
                // Instrucción de escritura
                Arrays.asList("escriure", "(", "Llista_expressio", ")", ";"),
                // Condicional con sino
                Arrays.asList("si", "Exp", "llavors", "Llista_inst", "sino", "Llista_inst", "fisi"),
                // Condicional sin sino
                Arrays.asList("si", "Exp", "llavors", "Llista_inst", "fisi"),
                // Bucle repetir
                Arrays.asList("repetir", "Llista_inst", "fins", "Exp", ";"),
                // Bucle mentre
                Arrays.asList("mentre", "Exp", "fer", "Llista_inst", "fimentre"),
                // Retorno de función
                Arrays.asList("retornar", "Exp", ";")
        ));

        // Lista de variables para la instrucción de lectura
        grammar.put("Llista_variables", Arrays.asList(
                Arrays.asList("ID", "Llista_variables_tail")
        ));
        grammar.put("Llista_variables_tail", Arrays.asList(
                Arrays.asList(",", "ID", "Llista_variables_tail"),
                Arrays.asList("ε")
        ));

        // Lista de expresiones para la instrucción de escritura
        grammar.put("Llista_expressio", Arrays.asList(
                Arrays.asList("Exp", "Llista_expressio_tail")
        ));
        grammar.put("Llista_expressio_tail", Arrays.asList(
                Arrays.asList(",", "Exp", "Llista_expressio_tail"),
                Arrays.asList("ε")
        ));

        return grammar;

    }
}
