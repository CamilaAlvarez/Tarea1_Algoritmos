package memory;

import java.io.IOException;

import nodes.INode;

public interface IMemoryManager {
	/**
	 * Retorna el nodo ubicado en la posicion de disco entregada
	 * @param filePosition
	 * @return
	 * @throws IOException
	 */
	public INode loadNode(long filePosition) throws IOException;
	
	/**
	 * Graba el nodo entregado en memoria
	 * @param n Nodo a grabar en memoria
	 * @throws IOException
	 */
	public void saveNode(INode n) throws IOException;
	
	/**
	 * Entrega la siguiente posición disponible en memoria
	 * @return
	 */
	public long getNewPosition();
}
