package repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import entity.Sale;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SaleRepository {

    private static final String PATH = "data/data.json";
    private final Gson gson = new GsonBuilder()
                    .setPrettyPrinting().registerTypeAdapter(java.time.LocalDateTime.class, (JsonSerializer<java.time.LocalDateTime>) (src, t, ctx) ->
                    new JsonPrimitive(src.toString())).registerTypeAdapter(java.time.LocalDateTime.class,
                    (JsonDeserializer<java.time.LocalDateTime>) (json, t, ctx) -> java.time.LocalDateTime.parse(json.getAsString())).create();

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

    private ArrayList<Sale> loadSales() {
        JsonObject root = readJson();
        Type type = new TypeToken<ArrayList<Sale>>(){}.getType();
        ArrayList<Sale> list = gson.fromJson(root.get("sales"), type);
        return list != null ? list : new ArrayList<>();
    }

    public ArrayList<Sale> getAll() {
        return loadSales();
    }

    public void save(Sale sale) {
        addToJson(sale);
    }

}