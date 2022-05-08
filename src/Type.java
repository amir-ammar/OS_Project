
public class Type {

    int intData;
    String stringData;

    public Type(int intData) {
        this.intData = intData;
    }

    public Type(String stringData) {
        this.stringData = stringData;
    }


    public int getIntData() {
        return intData;
    }

    public String getStringData() {
        return stringData;
    }


    public String toString(){
        if(stringData != null){
            return getStringData();
        }
        else{
            return Integer.toString(getIntData());
        }
    }

}
