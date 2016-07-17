/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.file;

import javafx.scene.text.Font;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import rgvm.data.DataManager;
import rgvm.data.RegionItem;
import rgvm.dialog.ProgressDialog;
import saf.components.AppDataComponent;
import saf.components.AppFileComponent;

/**
 *
 * @author McKillaGorilla
 */
public class FileManager implements AppFileComponent {

    public void loadRVME(AppDataComponent data, String filePath) throws IOException {
        DataManager dm = (DataManager) data;
        dm.reset();
        JsonObject json = loadJSONFile(filePath);
        dm.setName(json.getString("MAP_NAME"));
        System.out.println(json.getString("MAP_NAME"));
        dm.setParent(json.getString("PARENT_DIRECTORY"));
        System.out.println(json.getString("PARENT_DIRECTORY"));
        dm.setWidth(json.getJsonNumber("WIDTH").doubleValue());
        System.out.println(json.getJsonNumber("WIDTH"));
        dm.setHeight(json.getJsonNumber("HEIGHT").doubleValue());
        System.out.println(json.getJsonNumber("HEIGHT"));
        dm.setThickness(json.getJsonNumber("BORDER_THICKNESS").doubleValue());
        System.out.println(json.getJsonNumber("BORDER_THICKNESS"));
        dm.setZoom(json.getJsonNumber("ZOOM").doubleValue());
        System.out.println(json.getJsonNumber("ZOOM"));
        dm.setBackgroundColor(json.getJsonNumber("BACKGROUND_COLOR").intValue());
        System.out.println(json.getJsonNumber("BACKGROUND_COLOR"));
        dm.setBorderColor(json.getJsonNumber("BORDER_COLOR").intValue());
        System.out.println(json.getJsonNumber("BORDER_COLOR"));
        dm.setRawPath(json.getString("RAW_PATH"));
        System.out.println(json.getString("RAW_PATH"));
        dm.setBigFlagPath(json.getString("BIG_FLAG_PATH"));
        System.out.println(json.getString("BIG_FLAG_PATH"));
        dm.setSealPath(json.getString("SEAL_PATH"));
        System.out.println(json.getString("SEAL_PATH"));
        JsonArray jsonRegionArray = json.getJsonArray("REGION");
        for (int i = 0; i < jsonRegionArray.size(); i++) {
            JsonObject jsonRegion = jsonRegionArray.getJsonObject(i);
            RegionItem region = new RegionItem();
            region.setName(jsonRegion.getString("NAME"));
            region.setLeader(jsonRegion.getString("LEADER"));
            region.setCapital(jsonRegion.getString("CAPITAL"));
            region.setFlagPath(jsonRegion.getString("FLAG_PATH"));
            region.setLeaderPath(jsonRegion.getString("LEADER_PATH"));
            region.setRed(jsonRegion.getJsonNumber("RED").intValue());
            region.setBlue(jsonRegion.getJsonNumber("BLUE").intValue());
            region.setGreen(jsonRegion.getJsonNumber("GREEN").intValue());
            JsonArray jsonSecond = jsonRegion.getJsonArray("POINTS");
            for (int j = 0; j < jsonSecond.size(); j++) {
                JsonObject myItem = jsonSecond.getJsonObject(i);
                double x = myItem.getJsonNumber("X").doubleValue();
                double y = myItem.getJsonNumber("Y").doubleValue();
                region.add(x, y);
            }
            dm.addItem(region);
        }
    }

    @Override
    //filepath takes in raw map data json file
    public void loadData(AppDataComponent data, String filePath) throws IOException {

        DataManager dataManager = (DataManager) data;

        //load JSON
        JsonObject json = loadJSONFile(filePath);
        JsonArray jsonItemArray = json.getJsonArray("SUBREGIONS");
        ReentrantLock lock = new ReentrantLock();
        //  new Thread() {
        //  public void run() {
        //  lock.lock();
        dataManager.getPB().setProgress(0);
        for (int i = 0; i < jsonItemArray.size(); i++) {
            Double prog = (double) i / jsonItemArray.size();
            //returns array of subregion polygons
            JsonObject jsonItem = jsonItemArray.getJsonObject(i);
            JsonArray jsonSecond = jsonItem.getJsonArray("SUBREGION_POLYGONS");
            for (int j = 0; j < jsonSecond.size(); j++) {

                JsonArray jsonRegion = jsonSecond.getJsonArray(j);
                RegionItem item = loadItem(jsonRegion);
                dataManager.addItem(item);
            }
            dataManager.setProgress(prog);
        }

        dataManager.setProgress(1);
        //   lock.unlock();
        //    }
        //    }.start();

    }

    public RegionItem loadItem(JsonArray jsonItem) {
        RegionItem region = new RegionItem();
        for (int i = 0; i < jsonItem.size(); i++) {
            JsonObject myItem = jsonItem.getJsonObject(i);
            double x = myItem.getJsonNumber("X").doubleValue();
            double y = myItem.getJsonNumber("Y").doubleValue();
            region.add(x, y);
        }
        return region;
    }

    public double getDataAsDouble(JsonObject json, String dataName) {
        JsonValue value = json.get(dataName);
        JsonNumber number = (JsonNumber) value;
        return number.bigDecimalValue().doubleValue();
    }

    public int getDataAsInt(JsonObject json, String dataName) {
        JsonValue value = json.get(dataName);
        JsonNumber number = (JsonNumber) value;
        return number.bigIntegerValue().intValue();
    }

    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        return json;
    }

    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
        DataManager manager = (DataManager) data;
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        ObservableList<RegionItem> items = manager.getItems();
        for (RegionItem item : items) {
            JsonArrayBuilder regionBuilder = Json.createArrayBuilder();
            for (Double[] ar : item.getList()) {
                JsonObject coordinates = Json.createObjectBuilder()
                        .add("X", ar[0])
                        .add("Y", ar[1]).build();
                regionBuilder.add(coordinates);
            }

            JsonObject itemJson = Json.createObjectBuilder()
                    .add("NAME", item.getName())
                    .add("LEADER", item.getLeader())
                    .add("CAPITAL", item.getCapital())
                    .add("FLAG_PATH", item.getFlagPath())
                    .add("LEADER_PATH", item.getLeaderPath())
                    .add("RED", item.getRed())
                    .add("BLUE", item.getBlue())
                    .add("GREEN", item.getGreen())
                    .add("POINTS", regionBuilder).build();

            arrayBuilder.add(itemJson);
        }
        JsonArray itemsArray = arrayBuilder.build();
        JsonObject dataManagerJSO = Json.createObjectBuilder()
                .add("MAP_NAME", manager.getName())
                .add("PARENT_DIRECTORY", manager.getParent())
                .add("WIDTH", manager.getWidth())
                .add("HEIGHT", manager.getHeight())
                .add("BORDER_THICKNESS", manager.getThickness())
                .add("ZOOM", manager.getZoom())
                .add("BACKGROUND_COLOR", manager.getBackgroundColor())
                .add("BORDER_COLOR", manager.getBorderColor())
                .add("RAW_PATH", manager.getRawPath())
                .add("BIG_FLAG_PATH", manager.getBigFlagPath())
                .add("SEAL_PATH", manager.getSealPath())
                .add("REGION", itemsArray).build();

        //output the file
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        StringWriter sw = new StringWriter();
        JsonWriter jsonWriter = writerFactory.createWriter(sw);
        jsonWriter.writeObject(dataManagerJSO);
        jsonWriter.close();

        // INIT THE WRITER
        OutputStream os = new FileOutputStream(filePath);
        JsonWriter jsonFileWriter = Json.createWriter(os);
        jsonFileWriter.writeObject(dataManagerJSO);
        String prettyPrinted = sw.toString();
        PrintWriter pw = new PrintWriter(filePath);
        pw.write(prettyPrinted);
        pw.close();
    }

    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        DataManager manager = (DataManager) data;
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        ObservableList<RegionItem> items = manager.getItems();
        for (RegionItem item : items) {
            if (manager.hasCapitals() && manager.hasLeaders()) {
                JsonObject itemJson = Json.createObjectBuilder().add("name", item.getName())
                        .add("capital", item.getLeader())
                        .add("leader", item.getCapital())
                        .add("red", item.getRed())
                        .add("green", item.getBlue())
                        .add("blue", item.getGreen()).build();

                arrayBuilder.add(itemJson);
            } else if (!manager.hasCapitals() && !manager.hasLeaders()) {
                JsonObject itemJson = Json.createObjectBuilder().add("name", item.getName())
                        .add("red", item.getRed())
                        .add("green", item.getBlue())
                        .add("blue", item.getGreen()).build();

                arrayBuilder.add(itemJson);
            } else if (!manager.hasCapitals() && manager.hasLeaders()) {
                JsonObject itemJson = Json.createObjectBuilder().add("name", item.getName())
                        .add("leader", item.getCapital())
                        .add("red", item.getRed())
                        .add("green", item.getBlue())
                        .add("blue", item.getGreen()).build();

                arrayBuilder.add(itemJson);
            } else if (manager.hasCapitals() && !manager.hasLeaders()) {
                JsonObject itemJson = Json.createObjectBuilder().add("name", item.getName())
                        .add("capital", item.getLeader())
                        .add("red", item.getRed())
                        .add("green", item.getBlue())
                        .add("blue", item.getGreen()).build();

                arrayBuilder.add(itemJson);
            }

        }
        JsonArray subregionArray = arrayBuilder.build();
        JsonObject dataManagerJSO = Json.createObjectBuilder()
                .add("name", manager.getName())
                .add("subregions_have_capitals", manager.hasCapitals())
                .add("subregions_have_flags", manager.hasFlags())
                .add("subregions_have_leaders", manager.hasLeaders())
                .add("subregions", subregionArray).build();

        //output the file
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        StringWriter sw = new StringWriter();
        JsonWriter jsonWriter = writerFactory.createWriter(sw);
        jsonWriter.writeObject(dataManagerJSO);
        jsonWriter.close();

        // INIT THE WRITER
        OutputStream os = new FileOutputStream(filePath);
        JsonWriter jsonFileWriter = Json.createWriter(os);
        jsonFileWriter.writeObject(dataManagerJSO);
        String prettyPrinted = sw.toString();
        PrintWriter pw = new PrintWriter(filePath);
        pw.write(prettyPrinted);
        pw.close();
    }

    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
