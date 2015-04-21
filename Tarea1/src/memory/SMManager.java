package memory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import nodes.INode;
import utils.Utils;

public class SMManager implements IMemoryManager{
	private RandomAccessFile file;
	private int visitados;
	private Buffer nodeBuffer;
	private long currentPos;
	private int blockSize;
	
	public SMManager(int nodeSize, int bufferSize) throws FileNotFoundException{
		this.file = new RandomAccessFile("tree.obj", "rw");
		this.visitados = 0;
		this.nodeBuffer = new Buffer(bufferSize);
		this.blockSize=nodeSize;
		this.currentPos=0;
	}
	
	public int getVisitados(){
		return visitados;
	}
	
	public INode loadNode(long pos) throws IOException{
		INode n = nodeBuffer.findNode(pos);
		if(n!=null){
			return n;
		}
		int toFree;
		byte[] data = new byte[4096];
		//si el nodo no esta en el buffer se busca el que tenga peor prioridad
		if(!nodeBuffer.isFull()){
			toFree = nodeBuffer.getBufferPos();
		}else{
			toFree = nodeBuffer.getLastPriority();
			//si fue modificado, se va a disco para dejar espacio
			nodeBuffer.getNode(toFree).writeBuffer(data);
			if(nodeBuffer.wasModified(toFree)){
				writeToFile(data, nodeBuffer.getFilePosition(toFree));
			}
		}
		//se lee del disco el nodo buscado
		readFromFile(data, nodeBuffer.getFilePosition(toFree));
		n = Utils.loadNode(data);
		//el nodo se agrega al buffer
		nodeBuffer.addNode(n,toFree,false);

		return n;
	}
	
	public void saveNode(INode n) throws IOException{
		//Si el nodo esta en el buffer, se actualiza
		if(nodeBuffer.updateNode(n)){
			return;
		}	
		//Si el buffer no esta lleno
		else if(!nodeBuffer.isFull()){
			nodeBuffer.addNode(n,nodeBuffer.getBufferPos(),true);
			return;
		}
		//Si no esta en el buffer, se busca el nodo con menor prioridad
		//en este para hacer espacio al nodo a grabar
		int toFree = nodeBuffer.getLastPriority();
		//Si el nodo fue modificado hay que mandarlo a disco
		if(nodeBuffer.wasModified(toFree)){
			byte[] data = new byte[4096];
			nodeBuffer.getNode(toFree).writeBuffer(data);
			writeToFile(data, nodeBuffer.getFilePosition(toFree));
		}
		//Se agrega el nuevo nodo al buffer 
		nodeBuffer.addNode(n,toFree,true);
	}
	
	public void writeToFile(byte[] archivo , long posicion) throws IOException {
		file.seek(posicion);
		file.write(archivo);
		visitados++;
	}
	
	public void readFromFile(byte[] archivo , long posicion) throws IOException{
		file.seek(posicion);
		file.read(archivo);
		visitados++;
	}


	@Override
	public long getNewPosition() {
		long c = this.currentPos;
		this.currentPos=this.currentPos+this.blockSize;
		return c;
	}
	
}
