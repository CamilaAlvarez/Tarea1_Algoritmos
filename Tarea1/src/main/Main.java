package main;

import generator.RectangleGenerator;
import gui.GUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import point.Point;

import memory.IMemoryManager;
import memory.PMManager;
import memory.SMManager;
import nodes.INode;
import nodes.InternalNode;
import rectangles.DistanceComparator;
import rectangles.IRectangle;
import rectangles.MBR;
import rectangles.MyRectangle;
import trees.RTree;
import utils.Pair;

public class Main {

	
	public static void main(String[] args) throws IOException {
		finalMainSplit();
	}
		
	public static void testDistancia(){
		Point c = new Point(0, 0);
		LinkedList<MyRectangle> r = new LinkedList<MyRectangle>();
		DistanceComparator comp = new DistanceComparator(c);
		for(int i = 9; i>0; i--){
			double[] x = {i, i+1};
			double[] y = {i, i+1};
			r.add(new MyRectangle(x,y));
		}
		
		Collections.sort(r,comp);
		for(MyRectangle mr : r){
			System.out.println(mr.toString());
		}
	}
	
	public static void testInsertar() throws IOException{
		int t = 15;
		RTree tree = new RTree(t, new PMManager());
		int i=1;
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(1000,700);
		for(MyRectangle r : rects){
			System.out.println("insertando n�: "+i);
			tree.insertar(r);
			i++;
		}
		System.out.println("Termin�!");
	}
	
	public static void testGUI1() throws IOException{
		RTree tree = new RTree(3,new PMManager());
		int n = 30;
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(n,700);
		for(int i=0; i<n; i++){
			MyRectangle r = rects.get(i);
			tree.insertar(r);
		}
		//System.out.println(tree.root.isLeaf());
		GUI.draw(tree,"despues");
	}
	
	public static void testGUIReinsert() throws IOException{
		RTree tree = new RTree(3,new PMManager());
		int n = 15;
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(n,700);
		for(int i=0; i<n; i++){
			MyRectangle r = rects.get(i);
			//System.out.println("Se inserta: "+r);
			tree.insertWithReinsert(r);
			//System.out.println("En arbol: "+tree.root);
			if(i == n-2){
				//GUI.draw(tree);
			}
		}
		//System.out.println(tree.root.isLeaf());
		GUI.draw(tree,"reinsert");
	}
	
	public static void testGUIReinsert2() throws IOException{
		RTree tree = new RTree(2,new PMManager());
		int n = 10;
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(n,700);
		for(int i=0; i<n; i++){
			MyRectangle r = rects.get(i);		
			tree.insertWithReinsert(r);			
		}
		GUI.draw(tree, "Reinsert");
		
		RTree tree2 = new RTree(2,new PMManager());
		for(int i=0; i<n; i++){
			MyRectangle r = rects.get(i);		
			tree2.insertar(r);			
		}
		GUI.draw(tree2, "split");
	}
	
	public static void testSecondaryMem() throws IOException{
		int cant = 6;
		int buffSize = 2;
		IMemoryManager memManager = new SMManager(4096, buffSize);
		
		LinkedList<INode> nodes = new LinkedList<INode>();
		for(int j=1; j<=cant; j++){
			long pos = memManager.getNewPosition();
			LinkedList<Pair> pairs = new LinkedList<Pair>();
			for(int i = 0; i<10;i++){
				double[] x1 = {(1+i)*j ,(1+i)*j};
				double[] y1 = {(1+i)*j ,(1+i)*j};
				pairs.add(new Pair(new MBR(x1, y1), i));			
			}
			INode l = new InternalNode(pairs.size(), false, null, pos, pairs, false);
			l.setParentMBR(l.getNewMBR().r);
			nodes.add(l);
			memManager.saveNode(l);
		}
		for(int j=0; j<cant; j++){
			INode n = nodes.get(j);
			INode l2 = memManager.loadNode(n.getPosition());
			System.out.println(n.equals(l2));
		}
		
		System.out.println("Visitados: "+memManager.getVisitados());
	}
	
	public static void testSecondaryMem2() throws IOException{
		int buffSize = 5;
		IMemoryManager memManager = new SMManager(4096, buffSize);
		
		RTree treePM = new RTree(2, memManager);
		int n = 10;
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(n,700);
		for(int i=0; i<n; i++){
			MyRectangle r = rects.get(i);		
			treePM.insertWithReinsert(r);			
		}
		GUI.draw(treePM, "Secondary");
		
		RTree treeSM = new RTree(2, new PMManager());
		for(int i=0; i<n; i++){
			MyRectangle r = rects.get(i);		
			treeSM.insertWithReinsert(r);
		}
		GUI.draw(treeSM, "Principal");
		
	}	
	
	public static void testMasiveInsert() throws IOException{
		int buffSize = 10;
		IMemoryManager memManager = new SMManager(4096, buffSize);
		System.out.println("se empieza el test");
		RTree treePM = new RTree(20, memManager);
		int n = 100;
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(n,100000);
		long t1 = System.currentTimeMillis();
		for(int i=0; i<n; i++){
			System.out.println(i+"");
			MyRectangle r = rects.get(i);		
			treePM.insertar(r);			
		}
		long t2 = System.currentTimeMillis();
		System.out.println(t2-t1);
		
	}
	
	public static void testBuscar() throws IOException{
		LinkedList<MyRectangle> rects = new LinkedList<MyRectangle>();
		int n = 9;
		for(int i = 0; i<n;i++){
			double[] x1 = {i*50 ,i*50+30};
			double[] y1 = {i*50 ,i*50+30};
			rects.add(new MyRectangle(x1, y1));			
		}
		
		int buffSize = 5;
		IMemoryManager memManager = new SMManager(4096, buffSize);		
		RTree tree = new RTree(2, memManager);
		
		for(int i=0; i<n-1; i++){
			MyRectangle r = rects.get(i);		
			tree.insertar(r);
		}
		MyRectangle r = rects.get(n-1);
		GUI.draw(tree, "Tree");
		tree.insertar(r);
		GUI.draw(tree, "Tree");
		
		double[] x = {25, 75};
		double[] y = {25, 75};
		MyRectangle search = new MyRectangle(x, y);
		
		LinkedList<IRectangle> ans = tree.buscar(search);
		for(IRectangle a : ans){
			System.out.println(a);
		}
	}
	
	public static void testRects2(){
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom2(512, 500000);
		for(MyRectangle r : rects){
			System.out.println(r.getArea());
		}
	}
	
	public static void finalMainSplit() throws IOException{
		int buffSize = 7;
		int t = 50;
		double n = Math.pow(2, 12);
		
		IMemoryManager memManager = new SMManager(4096, buffSize);
		System.out.println("Se empieza el test");
		RTree tree = new RTree(t, memManager);
		
		long t0 = System.currentTimeMillis();
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom2(n,500000);
		long t1 = System.currentTimeMillis();
		System.out.println("Tiempo creado de rectángulos: "+(t1-t0));
		
		t1 = System.currentTimeMillis();
		for(int i=0; i<n; i++){
			MyRectangle r = rects.get(i);		
			tree.insertar(r);			
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Tiempo inserción: "+(t2-t1));
		System.out.println("Cantidad de accesos a disco: "+tree.getAccess());
		System.out.println("Empezando búsqueda");
		ArrayList<MyRectangle> search = RectangleGenerator.generateRandom2((n/10),500000);
		long t3 = System.currentTimeMillis();
		for(MyRectangle mr : search){
			tree.buscar(mr);
		}
		long t4 = System.currentTimeMillis();
		System.out.println("Tiempo búsqueda: "+(t4-t3));
	}
	
}
