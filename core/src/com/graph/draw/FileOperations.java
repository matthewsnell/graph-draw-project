package com.graph.draw;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

abstract class FileOperations {
    static JSONObject wrapperObj;
    static JSONObject nodeObj;
    static JSONObject connectionObj;
    static JSONObject graphParamsObj;
    static JSONArray nodesArray;
    static FileWriter writeFile;

    static void write(Graph graph) {
        wrapperObj = new JSONObject();
        graphParamsObj = new JSONObject();
        nodesArray = new JSONArray();
        JSONArray connectionsArray = new JSONArray();

        if (graph.getClass() == Graph.class) {
            graphParamsObj.put("weighted", false);
        } else {
            graphParamsObj.put("weighted", true);
        }

        for (Node node : graph.getNodes()) {
            nodeObj = new JSONObject();
            nodeObj.put("id", node.getId());
            nodeObj.put("x", node.getX());
            nodeObj.put("y", node.getY());
            nodesArray.add(nodeObj);

            for (Connection con : node.getConnections().values()) {
                connectionObj = new JSONObject();
                connectionObj.put("start", con.getStart().getId());
                connectionObj.put("end", con.getEnd().getId());
                connectionObj.put("length", con.getLength());
                connectionsArray.add(connectionObj);
            }
        }

        wrapperObj.put("params", graphParamsObj);
        wrapperObj.put("nodes", nodesArray);
        wrapperObj.put("connections", connectionsArray);

        try {
            writeFile = new FileWriter(Gdx.files.internal("savedgraph.graph").toString(), false);
            writeFile.write(wrapperObj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writeFile.flush();
                writeFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void read(Graph graph, ShapeRenderer sr, Stage stage, Skin skin) {
        boolean weighted = false;

        graph.clear();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(Gdx.files.internal("savedgraph.graph").toString())) {

            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            JSONObject params = (JSONObject) jsonObject.get("params");
            if ((boolean) params.get("weighted")) {
                weighted = true;
            }


            JSONArray nodesArray = (JSONArray) jsonObject.get("nodes");
            Iterator<JSONObject> nodeIterator = nodesArray.iterator();
            while (nodeIterator.hasNext()) {
                JSONObject node = nodeIterator.next();
                int nodeID = Integer.parseInt(node.get("id").toString());
                float nodeX = Float.parseFloat(node.get("x").toString());
                float nodeY = Float.parseFloat(node.get("y").toString());
                graph.addManualNode(nodeID, nodeX, nodeY);

            }

            JSONArray connectionsArray = (JSONArray) jsonObject.get("connections");
            Iterator<JSONObject> conIterator = connectionsArray.iterator();
            while (conIterator.hasNext()) {
                JSONObject con = conIterator.next();
                int startNodeID = Integer.parseInt(con.get("start").toString());
                int endNodeID = Integer.parseInt(con.get("end").toString());
                int length = Integer.parseInt(con.get("length").toString());
                Node startNode = graph.getNodes().get(startNodeID);
                Node endNode = graph.getNodes().get(endNodeID);
                startNode.addConnection(endNode, graph.getNodes());
                if (weighted) {
                    startNode.getConnection(endNode).setLength(length);
                    startNode.getConnection(endNode).addLabel(length);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

}
