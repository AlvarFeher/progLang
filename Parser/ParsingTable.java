package Parser;

import Grammar.Grammar;

import java.util.*;

import java.util.Map;

public class ParsingTable {
    private Map<String, Map<String, Production>> table;
    private Map<String, List<List<String>>> grammar;
    private LoadFirstFollow firstFollow;
    public ParsingTable(Map<String, List<List<String>>> grammar, LoadFirstFollow firstFollow) {
        this.table = new HashMap<>();
    }

    public Map<String, Map<String, Production>> buildTable() {
        for(String rule : grammar.keySet()){
            List<List<String>> productions = grammar.get(rule);
            for(List<String> production : productions){

            }

        }
        return null;
    }






}
