import java.awt.Color;
import java.util.Arrays;

import javalib.worldimages.RectangleImage;
import tester.Tester;

// examples class
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

  Edge edge1;
  Edge edge2;
  Edge edge3;
  Edge edge4;
  Edge edge5;
  Edge edge6;
  Edge edge7;
  Edge edge8;
  Edge edge9;
  Edge edge10;
  Edge edge11;
  Edge edge12;

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
    t.checkExpect(vertex0.drawVertex() , new RectangleImage(20,20, "solid", Color.WHITE));
    t.checkExpect(vertex1.drawVertex() , new RectangleImage(20,20, "solid", Color.WHITE));
  }

  // tests compareTo method
  void testCompareTo(Tester t) {
    this.initData1();
    t.checkExpect(new Edge(vertex0, vertex1, 4).compareTo(new Edge(vertex0, vertex1, 3)) , 1);
    t.checkExpect(new Edge(vertex3, vertex4, 20).compareTo(new Edge(vertex7, vertex8, 3)) , 17);
  }
  void testDrawEdge(Tester t) {
    this.initData1();
    t.checkExpect(new Edge(vertex0, vertex1, 4).compareTo(new Edge(vertex0, vertex1, 3)) , 1);
    t.checkExpect(new Edge(vertex3, vertex4, 20).compareTo(new Edge(vertex7, vertex8, 3)) , 17);
  }


  // tests makeLov method
  void testMakeLov(Tester t) {
    this.initData1();
    Maze starterWorld = new Maze(3, 3);
    starterWorld.makeVertexList();
    t.checkExpect(this.lov ,this.lov);
  }
  void testMakeLov1(Tester t) {
    this.initData2();
    Maze starterWorld = new Maze(2, 2);
    starterWorld.makeVertexList();
    t.checkExpect(this.lov2 ,this.lov2);
  }

  // tests makeLoe method
  void testMakeLoe(Tester t) {
    this.initData2();
    Maze starterWorld = new Maze(2, 2);
    starterWorld.makeEdgesList();
    t.checkExpect(this.loe2 ,this.loe2);
  }
  void testMakeLoe1(Tester t) {
    this.initData2();
    Maze starterWorld = new Maze(2, 2);
    starterWorld.makeEdgesList();
    t.checkExpect(this.loe2 ,this.loe2);
  }

  // tests/runs the game
  void testFloodIt(Tester t) {
    initData1();
    int boardSizeX = 1000;
    int boardSizeY = 600;
    Maze myMaze = new Maze(100/1, 60/1);
    myMaze.bigBang(boardSizeX, boardSizeY, 0.02);
  }
}



