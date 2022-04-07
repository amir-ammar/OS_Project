import java.util.HashMap;

public class Memory {
    private HashMap<String, Type> memory;

    public Memory() {
        this.memory = new HashMap<>();
    }

    public void add(String name, Type type) {
        this.memory.put(name, type);
    }

    public Type get(String name) {
        return this.memory.get(name);
    }

    public void remove(String name) {
        this.memory.remove(name);
    }

    public boolean contains(String name) {
        return this.memory.containsKey(name);
    }

    public int size() {
        return this.memory.size();
    }

    public void clear() {
        this.memory.clear();
    }

}
