package Parser;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class LoadFirstFollow {
    private Map<String, List<String>> first;
    private Map<String,List<String>>follow;

    public LoadFirstFollow() throws FileNotFoundException {
        jsonLoader("firstFollow.json");
    }

    private void jsonLoader(String filepath) throws FileNotFoundException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, List<String>>>>() {}.getType();
        Map<String, Map<String, List<String>>> firstFollow = gson.fromJson(new FileReader(filepath),type);
        this.first = firstFollow.get("FIRST");
        this.follow = firstFollow.get("FOLLOW");
    }

    public List<String> getFirst(String nonTerminal){
        return first.getOrDefault(nonTerminal,List.of());
    }

    public List<String> getFollow(String nonTerminal){
        return follow.getOrDefault(nonTerminal,List.of());
    }
}
