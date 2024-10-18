package routing;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class DHT {
    // The DHT structure storing key-value pairs
    private ConcurrentHashMap<String, String> dhtMap;

    public DHT() {
        this.dhtMap = new ConcurrentHashMap<>();
    }

    // Add a key-value pair to the DHT
    public void put(String key, String value) {
        dhtMap.put(key, value);
        System.out.println("DHT Updated: Added " + key + " -> " + value);
    }

    // Retrieve a value based on the key
    public String get(String key) {
        return dhtMap.getOrDefault(key, null);
    }

    // Get the entire DHT map (for peer exchange purposes)
    public Map<String, String> getAll() {
        return new ConcurrentHashMap<>(dhtMap);
    }

    // Merge another DHT's entries into this DHT
    public void merge(Map<String, String> otherDHT) {
        dhtMap.putAll(otherDHT);
        System.out.println("DHT merged with entries from another peer.");
    }
}
