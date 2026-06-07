package repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import entity.Sale;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SaleRepository {

    private static final String PATH = "data/data.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(java.time.LocalDateTime.class,
                    (JsonSerializer<java.time.LocalDateTime>) (src, t, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(java.time.LocalDateTime.class, (JsonDeserializer<java.time.LocalDateTime>) (json, t, ctx) -> java.time.LocalDateTime.parse(json.getAsString())).create();

    // ================================================================
    //  BASE JSON FUNCTIONS
    // ================================================================

    private JsonObject readJson() {
        File file = new File(PATH);
        if (!file.exists()) return emptyJson();
        try (Reader r = new FileReader(file)) {
            JsonElement el = JsonParser.parseReader(r);
            return el.isJsonNull() ? emptyJson() : el.getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return emptyJson();
        }
    }

    private void addToJson(Sale sale) {
        JsonObject root = readJson();
        JsonArray array = root.getAsJsonArray("sales");
        array.add(gson.toJsonTree(sale));
        root.add("sales", array);
        writeJson(root);
    }

    private boolean updateJson(String id, Sale updatedSale) {
        JsonObject root = readJson();
        JsonArray array = root.getAsJsonArray("sales");
        for (int i = 0; i < array.size(); i++) {
            JsonObject item = array.get(i).getAsJsonObject();
            if (item.get("id").getAsString().equals(id)) {
                array.set(i, gson.toJsonTree(updatedSale));
                root.add("sales", array);
                writeJson(root);
                return true;
            }
        }
        return false;
    }

    // ================================================================
    //  INTERNAL WRITE
    // ================================================================

    private void writeJson(JsonObject root) {
        File file = new File(PATH);
        file.getParentFile().mkdirs();
        try (Writer w = new FileWriter(file)) {
            gson.toJson(root, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonObject emptyJson() {
        JsonObject obj = new JsonObject();
        obj.add("digitalVideoGames", new JsonArray());
        obj.add("physicalVideoGames", new JsonArray());
        obj.add("sales", new JsonArray());
        return obj;
    }

    // ================================================================
    //  LOAD ARRAYLIST FROM JSON
    // ================================================================

    private ArrayList<Sale> loadSales() {
        JsonObject root = readJson();
        Type type = new TypeToken<ArrayList<Sale>>(){}.getType();
        ArrayList<Sale> list = gson.fromJson(root.get("sales"), type);
        return list != null ? list : new ArrayList<>();
    }

    // ================================================================
    //  SALE OPERATIONS
    // ================================================================

    public ArrayList<Sale> getAll() {
        return loadSales();
    }

    public void save(Sale sale) {
        addToJson(sale);
    }

    public boolean update(String id, Sale updatedSale) {
        return updateJson(id, updatedSale);
    }

    public boolean delete(String id) {
        ArrayList<Sale> list = loadSales();
        boolean removed = list.removeIf(s -> s.getId().equalsIgnoreCase(id));
        if (removed) {
            JsonObject root = readJson();
            root.add("sales", gson.toJsonTree(list));
            writeJson(root);
        }
        return removed;
    }

    public Sale findById(String id) {
        ArrayList<Sale> list = loadSales();
        for (Sale s : list) {
            if (s.getId().equalsIgnoreCase(id)) return s;
        }
        return null;
    }
}