package Token;

public enum Keywords {
    FUNCIO, LLAVORS, FISI, SI, RETORNAR, REPETIR, FINS, MENTRE, FER;

    public static boolean isKeyword(String word){
        for(Keywords k : Keywords.values()){
            if(k.name().equalsIgnoreCase(word)){
                return true;
            }
        }
        return false;
    }
}
