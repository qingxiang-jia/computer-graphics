import org.lwjgl.util.vector.Vector3f;

/**
 * This is the class used to hold the face of a shape. I wrote this file by learning from
 * the LWJGL official tutorial. But I did not copy the code directly from the tutorial,
 * I wrote them from scratch.
 */
public class Face 
{
	// Three indices for the vertices
	public Vector3f vertex = new Vector3f();
	public Vector3f normal = new Vector3f();
	public Face(Vector3f vertex, Vector3f normal)
	{
		this.vertex = vertex;
		this.normal = normal;
	}
}
