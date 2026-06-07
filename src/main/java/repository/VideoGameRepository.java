package repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import entity.DigitalVideoGame;
import entity.PhysicalVideoGame;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class VideoGameRepository {

    private static final String PATH = "data/data.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    private void addToJson(Object object, String key) {
        JsonObject root = readJson();
        JsonArray array = root.getAsJsonArray(key);
        array.add(gson.toJsonTree(object));
        root.add(key, array);
        writeJson(root);
    }

    private boolean updateJson(String title, Object updatedObject, String key) {
        JsonObject root = readJson();
        JsonArray array = root.getAsJsonArray(key);
        for (int i = 0; i < array.size(); i++) {
            JsonObject item = array.get(i).getAsJsonObject();
            if (item.get("title").getAsString().equalsIgnoreCase(title)) {
                array.set(i, gson.toJsonTree(updatedObject));
                root.add(key, array);
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
    //  LOAD ARRAYLISTS FROM JSON
    // ================================================================

    private ArrayList<DigitalVideoGame> loadDigital() {
        JsonObject root = readJson();
        Type type = new TypeToken<ArrayList<DigitalVideoGame>>(){}.getType();
        ArrayList<DigitalVideoGame> list = gson.fromJson(root.get("digitalVideoGames"), type);
        return list != null ? list : new ArrayList<>();
    }

    private ArrayList<PhysicalVideoGame> loadPhysical() {
        JsonObject root = readJson();
        Type type = new TypeToken<ArrayList<PhysicalVideoGame>>(){}.getType();
        ArrayList<PhysicalVideoGame> list = gson.fromJson(root.get("physicalVideoGames"), type);
        return list != null ? list : new ArrayList<>();
    }

    // ================================================================
    //  DIGITAL OPERATIONS
    // ================================================================

    public ArrayList<DigitalVideoGame> getAllDigital() {
        return loadDigital();
    }

    public void saveDigital(DigitalVideoGame game) {
        addToJson(game, "digitalVideoGames");
    }

    public boolean updateDigital(String title, DigitalVideoGame updated) {
        return updateJson(title, updated, "digitalVideoGames");
    }

    public boolean deleteDigital(String title) {
        ArrayList<DigitalVideoGame> list = loadDigital();
        boolean removed = list.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
        if (removed) {
            JsonObject root = readJson();
            root.add("digitalVideoGames", gson.toJsonTree(list));
            writeJson(root);
        }
        return removed;
    }

    public DigitalVideoGame findDigitalByTitle(String title) {
        ArrayList<DigitalVideoGame> list = loadDigital();
        for (DigitalVideoGame g : list) {
            if (g.getTitle().equalsIgnoreCase(title)) return g;
        }
        return null;
    }

    // ================================================================
    //  PHYSICAL OPERATIONS
    // ================================================================

    public ArrayList<PhysicalVideoGame> getAllPhysical() {
        return loadPhysical();
    }

    public void savePhysical(PhysicalVideoGame game) {
        addToJson(game, "physicalVideoGames");
    }

    public boolean updatePhysical(String title, PhysicalVideoGame updated) {
        return updateJson(title, updated, "physicalVideoGames");
    }

    public boolean deletePhysical(String title) {
        ArrayList<PhysicalVideoGame> list = loadPhysical();
        boolean removed = list.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
        if (removed) {
            JsonObject root = readJson();
            root.add("physicalVideoGames", gson.toJsonTree(list));
            writeJson(root);
        }
        return removed;
    }

    public PhysicalVideoGame findPhysicalByTitle(String title) {
        ArrayList<PhysicalVideoGame> list = loadPhysical();
        for (PhysicalVideoGame g : list) {
            if (g.getTitle().equalsIgnoreCase(title)) return g;
        }
        return null;
    }
}