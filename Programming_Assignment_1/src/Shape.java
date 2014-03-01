import java.util.ArrayList;
import org.lwjgl.util.vector.Vector3f;

/**
 * I learned from the LWJGL official tutorial and wrote this file.
 * This class is used to hold the three-valued vertices and normal vectors, and a series of faces.
 */
public class Shape 
{
	public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	public ArrayList<Face> faces = new ArrayList<Face>();
}
