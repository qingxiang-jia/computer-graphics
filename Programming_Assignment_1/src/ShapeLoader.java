import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.util.vector.Vector3f;

/**
 * I learned from the LWJGL official tutorial and wrote this class. There may be similarities,
 * but the code is written by me 100%.
 * This class is used to create the data structure for the shape by loading an .obj file.
 */
public class ShapeLoader 
{
	public static Shape loadShapeFromFile(File f) throws FileNotFoundException, IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Shape shape = new Shape();
		String line; // Represents each line in the .obj file
		while((line = reader.readLine()) != null)
		{
			if(line.startsWith("v ")) // Then it must represent a vertex
			{
				float x = Float.valueOf(line.split(" ")[1]); // Parsing
				float y = Float.valueOf(line.split(" ")[2]); // Parsing
				float z = Float.valueOf(line.split(" ")[3]); // Parsing
				shape.vertices.add(new Vector3f(x,y,z)); // Add it to the shape object
			}
			else if(line.startsWith("vn ")) // Then it must represent a normal vector
			{
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				shape.normals.add(new Vector3f(x,y,z));
			}
			else if(line.startsWith("f "))	// Then it must represent the face
			{
				float vIdx1 = Float.valueOf(line.split(" ")[1].split("/")[0]); // Parsing
				float vIdx2 = Float.valueOf(line.split(" ")[2].split("/")[0]); // Parsing
				float vIdx3 = Float.valueOf(line.split(" ")[3].split("/")[0]); // Parsing
				Vector3f vertexIndices = new Vector3f(vIdx1, vIdx2, vIdx3); // Index for vertexes
				
				float nIdx1 = Float.valueOf(line.split(" ")[1].split("/")[2]);
				float nIdx2 = Float.valueOf(line.split(" ")[2].split("/")[2]);
				float nIdx3 = Float.valueOf(line.split(" ")[3].split("/")[2]);
				Vector3f normalIndices = new Vector3f(nIdx1, nIdx2, nIdx3); // Index for normal vectors
				
				shape.faces.add(new Face(vertexIndices, normalIndices)); // Combine, we get a face to add		
			}
		}
		reader.close(); // Close the file
		return shape;
	}
}
