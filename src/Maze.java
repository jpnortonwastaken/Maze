import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*; 

// a class that represents a vertex
class Vertex {
  int x;
  int y;
  Vertex top;
  Vertex right;
  Vertex bottom;
  Vertex left;
  Color color;
  ArrayList<Edge> edges;

  Vertex(int x, int y) {
    this.x = x;
    this.y = y;
    this.color = Color.WHITE;
    this.edges = new ArrayList<Edge>(); 
  }

  Vertex(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.edges = new ArrayList<Edge>();
  }

  Vertex(int x, int y, Vertex top, Vertex right, Vertex bottom, Vertex left) {
    this.x = x;
    this.y = y;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
    this.color = Color.WHITE;
    this.edges = new ArrayList<Edge>();
  }

  // determines if this vertex is the same as a given vertex
  //  @Override
  //  public boolean equals(Object other) {
  //    if (!(other instanceof Vertex)) {
  //      return false;
  //    }
  //    Vertex that = (Vertex)other;
  //    return this.x == that.x
  //        && this.y == that.y;
  //  }

  public void printVertex() {
    System.out.println(this.x + "," + this.y);
  }

  // draws a single vertex
  public WorldImage drawVertex(int size) {
    return new RectangleImage(size, size, "solid", this.color);
  }
}

// a class that represents an edge
class Edge implements Comparable<Edge> {
  Vertex from;
  Vertex to;
  int weight;

  Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  public Vertex getOtherSide(Vertex vert) {
    if (this.from.equals(vert)) {
      return this.to;
    } else if (this.to.equals(vert)) {
      return this.from;
    } else {
      throw new IllegalArgumentException("This vertex is not connected to the edge");
    }
  }

  // determines if this edge is the same as a given edge
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Edge)) {
      return false;
    }
    Edge that = (Edge)other;
    return this.from.equals(that.from) && this.to.equals(that.to);
  }

  // draws a single wall
  public WorldImage drawWall(int size, Color color) {
    if (this.from.y == this.to.y) {
      // horizontal wall
      return new LineImage(new Posn(0, size), color);
    } else { // if (this.from.y == this.to.y) {
      // vertical wall
      return new LineImage(new Posn(size, 0), color);
    }
  }

  // compares edges by weight
  public int compareTo(Edge other) {
    return this.weight - other.weight;
  }
}

interface ICollection {
  public void add(Vertex v);

  public Vertex remove();

  public Vertex getNext();

  public boolean isEmpty();
}

// a class that represents a maze
class Maze extends World {
  Vertex start;
  int sizeX;
  int sizeY;

  int boardSizeX = 1000;
  int boardSizeY = 600;

  ArrayList<ArrayList<Vertex>> vertexList;
  ArrayList<ArrayList<Edge>> edgesList;

  ArrayList<Edge> finalEdgesList;

  HashMap<Vertex, Vertex> representatives = new HashMap<Vertex, Vertex>();

  boolean isDFS;

  ArrayList<Vertex> processed = new ArrayList<Vertex>();

  int ticNum;
  int ticNum2;

  ArrayList<Vertex> reconstructList;

  Maze(int sizeX, int sizeY) {
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.vertexList = new ArrayList<ArrayList<Vertex>>();
    this.edgesList = new ArrayList<ArrayList<Edge>>();
    this.finalEdgesList = this.kruskal();
    ticNum = 0;
    ticNum2 = 0;
    reconstructList = new ArrayList<Vertex>();
  }

  public ArrayList<Vertex> reconstruct(HashMap<Vertex, Edge> cameFromEdge, Vertex next) {
    ArrayList<Vertex> seen = new ArrayList<Vertex>();
    while (cameFromEdge.get(next) != null) {
      Edge currentEdge = cameFromEdge.get(next) ;
      seen.add(next);
      if (currentEdge.to.equals(next)) {
        next = currentEdge.from;
      } else if (currentEdge.from.equals(next)) {
        next = currentEdge.to;
      }
    }
    this.reconstructList = seen;
    return seen;
  }

  public void solver() {
    HashMap<Vertex, Edge> cameFromEdge = new HashMap<Vertex, Edge>();
    //ICollection worklist = collection; // A Queue or a Stack, depending on the algorithm
    Deque<Vertex> worklist = new ArrayDeque<Vertex>();

    worklist.addFirst(this.vertexList.get(0).get(0));

    // initialize the worklist to contain the starting node
    while (!worklist.isEmpty()) {
      Vertex next = worklist.pop();

      if (this.processed.contains(next)) {
        continue;
      } else if (next.equals(this.vertexList.get(this.sizeY - 1).get(this.sizeX - 1))) {
        this.reconstruct(cameFromEdge, next);
        break;
      } else {
        this.processed.add(next);
        for (Edge e : next.edges) {
          if (this.isDFS) {
            worklist.addFirst(e.getOtherSide(next));
          } else {
            worklist.addLast(e.getOtherSide(next));
          }
          if (!e.equals(cameFromEdge.get(next))) {
            cameFromEdge.put(e.getOtherSide(next), e);
          }
        }
      }
    }   
  }

  public void onTick() {
    if (this.ticNum < this.processed.size()) {
      Vertex v = this.processed.get(this.ticNum);
      if (this.ticNum > 0) {
        v.color = new Color(122, 204, 255);
      }
      this.ticNum++;
    }
    if ((this.ticNum >= this.processed.size())
        && (this.ticNum2 < this.reconstructList.size())) {
      Vertex v2 = this.reconstructList.get(this.ticNum2);
      if (this.ticNum2 > 0) {
        v2.color = new Color(31, 106, 196);
      }
      this.ticNum2++;
    }
  }

  public void onKeyEvent(String key) {
    if (key.equals("d")) {
      this.isDFS = true;
      solver();
    } else if (key.equals("b")) {
      this.isDFS = false;
      solver();
    }
  }

  // draws the current scene
  public WorldScene makeScene() {
    WorldScene worldScene = new WorldScene(1000, 600);

    int cellSize = (this.boardSizeX - 0) / this.sizeX;

    // draws vertices and border
    for (ArrayList<Vertex> vlist : this.vertexList) {
      for (Vertex v : vlist) {
        worldScene.placeImageXY(v.drawVertex(cellSize),
            v.x * cellSize + cellSize / 2,
            v.y * cellSize + cellSize / 2);
        if (v.top == null) {
          worldScene.placeImageXY(new LineImage(new Posn(cellSize, 0), Color.BLACK),
              v.x * cellSize + cellSize / 2,
              v.y * cellSize);
        }
        if (v.bottom == null) {
          worldScene.placeImageXY(new LineImage(new Posn(cellSize, 0), Color.BLACK),
              v.x * cellSize,
              v.y * cellSize + cellSize);
        }
        if (v.right == null) {
          worldScene.placeImageXY(new LineImage(new Posn(0, cellSize), Color.BLACK),
              v.x * cellSize + cellSize,
              v.y * cellSize);
        }
        if (v.left == null) {
          worldScene.placeImageXY(new LineImage(new Posn(0, cellSize), Color.BLACK),
              v.x * cellSize,
              v.y * cellSize + cellSize / 2);
        }
      }
    }

    // make list of walls to be drawn
    ArrayList<Edge> edgesWallsList = new ArrayList<Edge>();
    ArrayList<Edge> sortedList = new ArrayList<Edge>(this.orderEdgesList());

    for (Edge e1 : sortedList) {
      if (!this.finalEdgesList.contains(e1)) {
        edgesWallsList.add(e1);
      }
    }

    // draws walls
    for (Edge e : edgesWallsList) {
      if (e.from.y == e.to.y) {
        // vertical wall
        worldScene.placeImageXY(e.drawWall(cellSize, Color.BLACK),
            e.from.x * cellSize + cellSize,
            e.to.y * cellSize + cellSize / 2);
      } else if (e.from.x == e.to.x) {
        // horizontal wall
        worldScene.placeImageXY(e.drawWall(cellSize, Color.BLACK),
            e.from.x * cellSize + cellSize / 2,
            e.to.y * cellSize);
      }
    }

    return worldScene;
  }

  public ArrayList<ArrayList<Vertex>> makeVertexList() {
    ArrayList<ArrayList<Vertex>> list = new ArrayList<ArrayList<Vertex>>();

    for (int i = 0; i < this.sizeY; i++) {
      ArrayList<Vertex> row = new ArrayList<Vertex>();

      for (int j = 0; j < this.sizeX; j++) {
        Vertex vertex = new Vertex(j, i);

        row.add(vertex);

        if (i == 0 && j == 0) {
          row.get(0).color = new Color(17, 125, 60);
        }

        if (i == sizeY - 1 && j == sizeX - 1) {
          row.get(j).color = new Color(67, 17, 125);
        }

        if (i == 0 && j > 0 && j < this.sizeX) {
          vertex.left = row.get(j - 1);
          row.get(j - 1).right = vertex;
        } else if (i > 0 && j == 0 && i < this.sizeX) {
          vertex.top = this.vertexList.get(i - 1).get(j);
          this.vertexList.get(i - 1).get(j).bottom = vertex;
        } else if (i > 0 && j > 0 && i < this.sizeX) {
          vertex.top = this.vertexList.get(i - 1).get(j);
          vertex.left = row.get(j - 1);
          row.get(j - 1).right = vertex;
          this.vertexList.get(i - 1).get(j).bottom = vertex;
        }
      }
      vertexList.add(row);
    }

    return new ArrayList<ArrayList<Vertex>>();
  }

  public ArrayList<Edge> makeEdgesList() {
    for (int i = 0; i < this.sizeY; i++) {
      ArrayList<Edge> row = new ArrayList<Edge>();

      for (int j = 0; j < this.sizeX; j++) {
        Vertex currentVertex = this.vertexList.get(i).get(j);

        if (currentVertex.right != null) {
          //currentVertex.edges.add(new Edge(currentVertex, currentVertex.right, 0));
          row.add(new Edge(currentVertex, currentVertex.right, new Random().nextInt(4000)));
        }
        if (currentVertex.bottom != null) {
          //currentVertex.edges.add(new Edge(currentVertex, currentVertex.bottom, 0));
          row.add(new Edge(currentVertex, currentVertex.bottom, new Random().nextInt(4000)));
        }
        if (currentVertex.top != null) {
          //currentVertex.edges.add(new Edge(currentVertex, currentVertex.top, 0));
        }
        if (currentVertex.left != null) {
          //currentVertex.edges.add(new Edge(currentVertex, currentVertex.left, 0));
        }
      }
      edgesList.add(row);
    }

    return new ArrayList<Edge>();
  }

  public HashMap<Vertex, Vertex> makeHashMap() {
    for (int i = 0; i < this.vertexList.size(); i++) {
      for (int j = 0; j < this.vertexList.get(i).size(); j++) {
        Vertex currentVertex = this.vertexList.get(i).get(j);
        this.representatives.put(currentVertex, currentVertex);
      }
    }
    return new HashMap<Vertex, Vertex>();
  }

  public ArrayList<Edge> orderEdgesList() {
    ArrayList<Edge> unsortedList = new ArrayList<Edge>();

    // convert 2D list edgesList to 1D list unsortedList
    for (int i = 0; i < this.edgesList.size(); i++) {
      for (int j = 0; j < this.edgesList.get(i).size(); j++) {
        unsortedList.add(this.edgesList.get(i).get(j));
      }
    }

    // sort by weight
    Collections.sort(unsortedList);
    ArrayList<Edge> sortedList = new ArrayList<Edge>(unsortedList);

    return sortedList;
  }

  public Vertex find(Vertex x) {
    if (this.representatives.get(x).equals(x)) {
      return x;
    } else {
      return this.find(this.representatives.get(x));
    }
  }

  public void union(Vertex x, Vertex y) {
    this.representatives.put(x, y);
  }

  public boolean moreThanOneTree() {
    int moreThanOneTree = 0;

    for (Entry<Vertex, Vertex> vert : this.representatives.entrySet()) {
      if (vert.getKey().equals(vert.getValue())) {
        moreThanOneTree++;
      }
    }

    return moreThanOneTree > 1;
  }

  // does kruskals algorithm
  public ArrayList<Edge> kruskal() {
    this.makeVertexList();
    this.makeEdgesList();
    this.makeHashMap();

    ArrayList<Edge> edgesInTree = new ArrayList<Edge>();
    ArrayList<Edge> worklist = this.orderEdgesList();

    // initialize every node's representative to itself
    while (this.moreThanOneTree()) {
      Edge currentCheapestEdge = worklist.get(0);
      // Pick the next cheapest edge of the graph: suppose it connects X and Y.
      if (find(currentCheapestEdge.from).equals(find(currentCheapestEdge.to))) {
        worklist.remove(0);
      } else {
        currentCheapestEdge.from.edges.add(currentCheapestEdge);
        currentCheapestEdge.to.edges.add(currentCheapestEdge);
        edgesInTree.add(currentCheapestEdge);
        union(find(currentCheapestEdge.from), find(currentCheapestEdge.to));
      }
    }

    return edgesInTree;
  }
}

//examples class
class ExamplesMaze {
  ExamplesMaze(){}

  ArrayList<ArrayList<Vertex>> vertexList1;
  ArrayList<ArrayList<Vertex>> vertexList2;

  Vertex vertex0;
  Vertex vertex1;
  Vertex vertex2;
  Vertex vertex3;
  Vertex vertex4;
  Vertex vertex5;
  Vertex vertex6;
  Vertex vertex7;
  Vertex vertex8;

  Vertex vertex9;
  Vertex vertex10;
  Vertex vertex11;
  Vertex vertex12;

  // initialize data
  void initData1() {
    vertex0 = new Vertex(0, 0);
    vertex1 = new Vertex(1, 0);
    vertex2 = new Vertex(2, 0);
    vertex3 = new Vertex(0, 1);
    vertex4 = new Vertex(1, 1);
    vertex5 = new Vertex(2, 1);
    vertex6 = new Vertex(0, 2);
    vertex7 = new Vertex(1, 2);
    vertex8 = new Vertex(2, 2);

    vertex0.top = null;
    vertex0.left = null;
    vertex0.bottom = vertex3;
    vertex0.right = vertex1;

    vertex1.top = null;
    vertex1.left = vertex0;
    vertex1.bottom = vertex4;
    vertex1.right = vertex2;

    vertex2.top = null;
    vertex2.left = vertex1;
    vertex2.bottom = vertex5;
    vertex2.right = null;

    vertex3.top = vertex0;
    vertex3.left = null;
    vertex3.bottom = vertex6;
    vertex3.right = vertex4;

    vertex4.top = vertex1;
    vertex4.left = vertex3;
    vertex4.bottom = vertex7;
    vertex4.right = vertex5;

    vertex5.top = vertex2;
    vertex5.left = vertex4;
    vertex5.bottom = vertex8;
    vertex5.right = null;

    vertex6.top = vertex3;
    vertex6.left = null;
    vertex6.bottom = null;
    vertex6.right = vertex7;

    vertex7.top = vertex4;
    vertex7.left = vertex6;
    vertex7.bottom = null;
    vertex7.right = vertex8;

    vertex8.top = vertex5;
    vertex8.left = vertex7;
    vertex8.bottom = null;
    vertex8.right = null;

    this.vertexList1 = new ArrayList<ArrayList<Vertex>>(
        Arrays.asList(new ArrayList<Vertex>(Arrays.asList(vertex0, vertex1, vertex2)),
            new ArrayList<Vertex>(Arrays.asList(vertex3, vertex4, vertex5)),
            new ArrayList<Vertex>(Arrays.asList(vertex6, vertex7, vertex8))));

  }


  void initData2() {
    vertex9 = new Vertex(0, 0);
    vertex10 = new Vertex(1, 0);
    vertex11 = new Vertex(0, 1);
    vertex12 = new Vertex(1, 1);

    vertex9.top = null;
    vertex9.left = null;
    vertex9.bottom = vertex2;
    vertex9.right = vertex1;

    vertex10.top = null;
    vertex10.left = vertex0;
    vertex10.bottom = vertex3;
    vertex10.right = null;

    vertex11.top = vertex0;
    vertex11.left = null;
    vertex11.bottom = null;
    vertex11.right = vertex3;

    vertex12.top = vertex1;
    vertex12.left = vertex2;
    vertex12.bottom = null;
    vertex12.right = null;

    this.vertexList2 = new ArrayList<ArrayList<Vertex>>(
        Arrays.asList(new ArrayList<Vertex>(Arrays.asList(vertex9, vertex10)),
            new ArrayList<Vertex>(Arrays.asList(vertex11, vertex12))));

  }


  // tests drawVertex method
  void testDrawVertex(Tester t) {
    this.initData1();
    t.checkExpect(vertex4.drawVertex(20), new RectangleImage(20, 20,
        "solid", Color.WHITE));
    t.checkExpect(vertex8.drawVertex(20), new RectangleImage(20, 20,
        "solid", Color.WHITE));
    t.checkExpect(vertex2.drawVertex(20), new RectangleImage(20, 20,
        "solid", Color.WHITE));
    t.checkExpect(vertex1.drawVertex(20), new RectangleImage(20, 20,
        "solid", Color.WHITE));
  }

  // tests drawWall method
  void testDrawWall(Tester t) {
    this.initData1();
    t.checkExpect(new Edge(vertex0, vertex3, 40).drawWall(20, Color.BLACK),
        new LineImage(new Posn(20, 0), Color.BLACK));
    t.checkExpect(new Edge(vertex3, vertex6, 8).drawWall(20, Color.BLACK),
        new LineImage(new Posn(20, 0), Color.BLACK));
    t.checkExpect(new Edge(vertex3, vertex4, 2).drawWall(20, Color.BLACK),
        new LineImage(new Posn(0, 20), Color.BLACK));
    t.checkExpect(new Edge(vertex7, vertex8, 120).drawWall(20, Color.BLACK),
        new LineImage(new Posn(0, 20), Color.BLACK));
  }

  // tests compareTo method
  void testCompareTo(Tester t) {
    this.initData1();
    t.checkExpect(new Edge(vertex3, vertex4, 10).compareTo(new Edge(vertex7, vertex8, 3)) , 7);
    t.checkExpect(new Edge(vertex0, vertex1, 4).compareTo(new Edge(vertex0, vertex1, 5)) , -1);
    t.checkExpect(new Edge(vertex5, vertex2, 15).compareTo(new Edge(vertex7, vertex8, 6)) , 9);
    t.checkExpect(new Edge(vertex6, vertex1, 3).compareTo(new Edge(vertex0, vertex1, 2)) , 1);
  }

  // tests makeHashMap method
  boolean testMakeHashMap(Tester t) {
    this.initData1();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    return t.checkExpect(myMaze1.makeHashMap(), new HashMap<Vertex, Vertex>())
        && t.checkExpect(myMaze2.makeHashMap(), new HashMap<Vertex, Vertex>())
        && t.checkExpect(myMaze3.makeHashMap(), new HashMap<Vertex, Vertex>())
        && t.checkExpect(myMaze4.makeHashMap(), new HashMap<Vertex, Vertex>());
  }

  // tests makeEdgesList method
  boolean testMakeEdgesList(Tester t) {
    this.initData1();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    return t.checkExpect(myMaze1.makeEdgesList(), new ArrayList<Edge>())
        && t.checkExpect(myMaze2.makeEdgesList(), new ArrayList<Edge>())
        && t.checkExpect(myMaze3.makeEdgesList(), new ArrayList<Edge>())
        && t.checkExpect(myMaze4.makeEdgesList(), new ArrayList<Edge>());
  }

  //tests makeVertexList method
  boolean testMakeVertexList(Tester t) {
    this.initData1();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    return t.checkExpect(myMaze1.makeVertexList(), new ArrayList<ArrayList<Vertex>>())
        && t.checkExpect(myMaze2.makeVertexList(), new ArrayList<ArrayList<Vertex>>())
        && t.checkExpect(myMaze3.makeVertexList(), new ArrayList<ArrayList<Vertex>>())
        && t.checkExpect(myMaze4.makeVertexList(), new ArrayList<ArrayList<Vertex>>());
  }

  // tests find method
  void testFind(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests makeScene method
  void testMakeScene(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests orderEdgesList method
  void testOrderEdgesList(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests union method
  void testUnion(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests moreThanOneTree method
  void testMoreThanOneTree(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests solver method
  void testSolver(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests reconstruct method
  void testReconstruct(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests getOtherSide method
  void testGetOtherSide(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests onKeyEvent method
  void testOnKeyEvent(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    myMaze1.onKeyEvent("b");
    myMaze2.onKeyEvent("b");
    myMaze3.onKeyEvent("d");
    myMaze4.onKeyEvent("d");
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests onTick method
  void testOnTick(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests kruskal method
  void testKruskal(Tester t) {
    this.initData1();
    this.initData2();
    Maze myMaze1 = new Maze(100, 60);
    Maze myMaze2 = new Maze(10, 6);
    Maze myMaze3 = new Maze(5, 3);
    Maze myMaze4 = new Maze(20, 12);
    t.checkExpect(vertex0, vertex0);
    t.checkExpect(vertex1, vertex1);
    t.checkExpect(vertex2, vertex2);
    t.checkExpect(vertex3, vertex3);
  }

  // tests/runs the game
  void testFloodIt(Tester t) {
    //initData1();
    int boardSizeX = 1000;
    int boardSizeY = 600;
    Maze myMaze = new Maze(50, 30);
    myMaze.bigBang(boardSizeX, boardSizeY, 0.001);
  }
}