package Token;

public enum Keywords {
    FUNCIO, LLAVORS, FISI, SI, RETORNAR, REPETIR, FINS, MENTRE, FER, SENCER, LOGIC, INICI, FI, ESCRIURE;

    public static boolean isKeyword(String word){
        for(Keywords k : Keywords.values()){
            if(k.name().equalsIgnoreCase(word)){
                return true;
            }
        }
        return false;
    }
}
