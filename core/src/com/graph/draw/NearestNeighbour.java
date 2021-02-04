package com.graph.draw;

import java.util.ArrayList;

abstract class NearestNeighbour {
    static void run(ArrayList<Node> nodes, Node startNode) {
        Node crntNode = startNode;
        ArrayList<Node> visited = new ArrayList<>();
        ArrayList<Connection> path = new ArrayList<>();
        float totalWeight = 0;
        while (visited.size() < nodes.size()-1) {
            Connection shortestCon = null;
            for (Connection con : crntNode.getConnections().values()) {
                if (shortestCon == null && !visited.contains(con.getEnd())) {
                    shortestCon = con;
                } else if (con.getLength() < shortestCon.getLength() && (!visited.contains(con.getEnd()) || visited.size() == nodes.size() -1)) {
                    shortestCon = con;
                }
            }
            path.add(shortestCon);
            visited.add(shortestCon.getEnd());
            crntNode = shortestCon.getEnd();
            System.out.println("visit " + String.valueOf(visited.size()) + "    node:" + String.valueOf(nodes.size()));
        }

    }
}
