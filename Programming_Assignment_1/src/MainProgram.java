/**
 * COMS 4160 Computer Graphics, Spring 2014, Programming Assignment 1, Qingxiang Jia
 * Qingxiang Jia's Magic Piano
 */

// File handling
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;

// Music handling
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// OpenGL APIs
import static org.lwjgl.util.glu.GLU.gluPerspective;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;

/**
 * This is Qingxiang Jia's Magic Piano. When you open the program, use W, A, S, D, I, and O to
 * navigate (I = zoom in, O = zoom out). Press key 1 to 7 will cause the piano's keys been
 * pressed, and by doing this you can make nice music! The idea is totally original. The music
 * sound is create by GarageBand, legally obtained by me, and then processed by Audacity.
 * All the objects except the spiral, are created by me using blender. The spiral is obtained
 * from the Turbo Squid, by Will_LaPuerta, free of charge.
 * 
 * The Face.java, Shape.java, and ShapeLoader.java are created by me learning the LWJGL official
 * tutorial. However, I did not copy the exact code from the tutorial, instead, I built my own code
 * from scratch.
 */
public class MainProgram 
{
    private boolean done = false;  // Decides whether the program should stop    
    private float rAngle;           // Rotation angle
    
    // Array for displayList of the music notes
    public int[] notesDisplayList;
    
    // Hold the display list of the objects, [100] for 100 objs, [4] for list#, dX, dY, dZ
    public float[][] onTrackNotesHolder = new float[100][4]; 
    public int listCounter = 0;
    
    // Hold the display list of the spiral
    public int spiralDisplayList;
    
    // Hold the display list of the ring of the notes
    public float[][] onLineNotesHolder = new float[7][4];
    public int lineIter = 0;
    
    // display lists
    int triangleList;
    int quadList;
    
    // basic camera parameters
    public boolean key_up;
    public boolean key_down;
    public float dY = 0;	// The change in y coordinate
    
    public boolean key_left;
    public boolean key_right;
    public float dX = 0;	// The change in x coordinate
    
    public boolean key_in;
    public boolean key_out;
    public float dZ = 0;	// The change in z coordinate
    
    public boolean key_fire;
    public int canPress = 0; // The flag used to separate two adjacent key press.

    // Main method
    public static void main(String args[]) 
    {
        MainProgram mainGraphics = new MainProgram();
        mainGraphics.launch();
    }
    
    // Start setting up the graphics
    public void launch() 
    {
        try // Initialization procedures
        {
        	// Setting up the graphics environment
            initialization();
            // Identify the .obj files to load
            String[] filenames = {"1.obj","2.obj","3.obj","4.obj","5.obj","6.obj","7.obj"};
            // Configure the light
            configureLight();
            // As it's named
            loadAndDrawNotes(filenames);
            // As it's named
            loadSpiral("spiral.obj");
            // Main loop to display animations
            while (!done) 
            {
                inputDetection(); // Detect keyboard input (camera, pressing key, etc)
                render(); 		  // Rendering animation frame by frame
                Display.update();
            }
            Display.destroy(); // Release resources
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0); }
    }
    
    // Handles all user interactions: camera and music
    private void inputDetection() 
    {
    	if(canPress > 0) // Used to create minimal time interval between key presses
    		canPress--;
    	// Press ESC or close the window to exit
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || Display.isCloseRequested()) 
            done = true;
        /* Use W, S, A, D, I, O to navigate.
           W = up, S = down, A = left, D = right, I = zoom in, O = zoom out */
        if((key_up = Keyboard.isKeyDown(Keyboard.KEY_W)))
        	dY = dY - 0.01f;
        if((key_down = Keyboard.isKeyDown(Keyboard.KEY_S))) 
        	dY = dY + 0.01f;
        if((key_down = Keyboard.isKeyDown(Keyboard.KEY_A))) 
        	dX = dX - 0.01f;
        if((key_down = Keyboard.isKeyDown(Keyboard.KEY_D))) 
        	dX = dX + 0.01f;
        if((key_down = Keyboard.isKeyDown(Keyboard.KEY_I))) 
        	dZ = dZ + 0.01f;
        if((key_down = Keyboard.isKeyDown(Keyboard.KEY_O))) 
        	dZ = dZ - 0.01f;
        // "Do" - press key 1 to trigger the following operations
        if((key_fire = Keyboard.isKeyDown(Keyboard.KEY_1)) && canPress == 0) {
        	if(listCounter == onTrackNotesHolder.length-1)
        		listCounter = 0; // Keep a finite number of shapes to draw, save resources
        	playSound("1.wav");
        	onTrackNotesHolder[listCounter][0] = notesDisplayList[0]; // add to the track
        	onTrackNotesHolder[listCounter][1] = 0; // x position
        	onTrackNotesHolder[listCounter][2] = 0; // y position
        	onTrackNotesHolder[listCounter][3] = 0; // z position
        	listCounter++; // Move to the next slot for new shapes to be added on to the list
        	canPress = 120; // Create an inhibiting time interval to prevent key press.
        }
        // "Re" - press key 2 to trigger the following operations
        if((key_fire = Keyboard.isKeyDown(Keyboard.KEY_2)) && canPress == 0) {
        	if(listCounter == onTrackNotesHolder.length-1)
        		listCounter = 0;
        	playSound("2.wav");
        	onTrackNotesHolder[listCounter][0] = notesDisplayList[1];
        	onTrackNotesHolder[listCounter][1] = 0.8f;
        	onTrackNotesHolder[listCounter][2] = 0;
        	onTrackNotesHolder[listCounter][3] = 0;
        	listCounter++;
        	canPress = 120; // Create an inhibiting time interval to prevent key press.
        }
        // Mi - press key 3 to trigger the following operations
        if((key_fire = Keyboard.isKeyDown(Keyboard.KEY_3)) && canPress == 0) {
        	if(listCounter == onTrackNotesHolder.length-1)
        		listCounter = 0;
        	playSound("3.wav");
        	onTrackNotesHolder[listCounter][0] = notesDisplayList[2];
        	onTrackNotesHolder[listCounter][1] = 0.8f*2;
        	onTrackNotesHolder[listCounter][2] = 0;
        	onTrackNotesHolder[listCounter][3] = 0;
        	listCounter++;
        	canPress = 120; // Create an inhibiting time interval to prevent key press.
        }
        // Fa - press key 4 to trigger the following operations
        if((key_fire = Keyboard.isKeyDown(Keyboard.KEY_4)) && canPress == 0) {
        	if(listCounter == onTrackNotesHolder.length-1)
        		listCounter = 0;
        	playSound("4.wav");
        	onTrackNotesHolder[listCounter][0] = notesDisplayList[3];
        	onTrackNotesHolder[listCounter][1] = 0.8f*3;
        	onTrackNotesHolder[listCounter][2] = 0;
        	onTrackNotesHolder[listCounter][3] = 0;
        	listCounter++;
        	canPress = 120; // Create an inhibiting time interval to prevent key press.
        }
        // So - press key 5 to trigger the following operations
        if((key_fire = Keyboard.isKeyDown(Keyboard.KEY_5)) && canPress == 0) {
        	if(listCounter == onTrackNotesHolder.length-1)
        		listCounter = 0;
        	playSound("5.wav");
        	onTrackNotesHolder[listCounter][0] = notesDisplayList[4];
        	onTrackNotesHolder[listCounter][1] = 0.8f*4;
        	onTrackNotesHolder[listCounter][2] = 0;
        	onTrackNotesHolder[listCounter][3] = 0;
        	listCounter++;
        	canPress = 120; // Create an inhibiting time interval to prevent key press.
        }
        // La - press key 6 to trigger the following operations
        if((key_fire = Keyboard.isKeyDown(Keyboard.KEY_6)) && canPress == 0) {
        	if(listCounter == onTrackNotesHolder.length-1)
        		listCounter = 0;
        	playSound("6.wav");
        	onTrackNotesHolder[listCounter][0] = notesDisplayList[5];
        	onTrackNotesHolder[listCounter][1] = 0.8f*5;
        	onTrackNotesHolder[listCounter][2] = 0;
        	onTrackNotesHolder[listCounter][3] = 0;
        	listCounter++;
        	canPress = 120; // Create an inhibiting time interval to prevent key press.
        }
        // Ti - press key 7 to trigger the following operations
        if((key_fire = Keyboard.isKeyDown(Keyboard.KEY_7)) && canPress == 0) {
        	if(listCounter == onTrackNotesHolder.length-1)
        		listCounter = 0;
        	playSound("7.wav");
        	onTrackNotesHolder[listCounter][0] = notesDisplayList[6];
        	onTrackNotesHolder[listCounter][1] = 0.8f*6;
        	onTrackNotesHolder[listCounter][2] = 0;
        	onTrackNotesHolder[listCounter][3] = 0;
        	listCounter++;
        	canPress = 120; // Create an inhibiting time interval to prevent key press.
        }
    }
    
    // Play sound when key is down. Use a new thread to make sure the rendering will not
    // be blocked out.
    public static void playSound(String filename)
    {
    	final String fn = filename; // Java requires this to be final
    	Runnable r = new Runnable() 
    	{
            public void run() 
            {
            	try{ Clip clip = AudioSystem.getClip();
    	        	 clip.open(AudioSystem.getAudioInputStream(new File(fn)));
    	        	 clip.start(); } 
            	catch(Exception e) {e.printStackTrace(); }
            }
    	};
        new Thread(r).start(); // Start new thread
    }
    
    // Load spiral and put it into a displayList
    private void loadSpiral(String filename)
    {
    	Shape shape = null;
		try { shape = ShapeLoader.loadShapeFromFile(new File(filename)); }
		catch(FileNotFoundException e) {
			System.out.println(".obj file not found.");
			Display.destroy(); // Release resources
			System.exit(0); }
		catch(IOException e) {
			System.out.println("I/O exception!");
			Display.destroy();
			System.exit(0); }
		// Create displayList
		spiralDisplayList = GL11.glGenLists(1);
		GL11.glNewList(spiralDisplayList, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_TRIANGLES); // Start Drawing
		for(Face face: shape.faces)
		{
			Vector3f n1 = shape.normals.get((int) face.normal.x-1); // Normal of the 1st vertex
    		Vector3f v1 = shape.vertices.get((int) face.vertex.x-1); // Coordinates of it
    		Vector3f n2 = shape.normals.get((int) face.normal.y-1); // Normal of the 2nd vertex
    		Vector3f v2 = shape.vertices.get((int) face.vertex.y-1); // Coordinates of it
    		Vector3f n3 = shape.normals.get((int) face.normal.z-1); // For the 3rd vertex
    		Vector3f v3 = shape.vertices.get((int) face.vertex.z-1); // For the 3rd vertex
    		GL11.glNormal3f(n1.x, n1.y, n1.z); // Draw the 1st vertex
    		GL11.glVertex3f(v1.x, v1.y, v1.z); 
    		GL11.glNormal3f(n2.x, n2.y, n2.z); // ........ 2nd ......
    		GL11.glVertex3f(v2.x, v2.y, v2.z); 
    		GL11.glNormal3f(n3.x, n3.y, n3.z); // ........ 3rd ......
    		GL11.glVertex3f(v3.x, v3.y, v3.z);
		}
		GL11.glEnd(); // Done drawing
		GL11.glEndList(); // Added into the displayList
    }
    
    // Load all music notes from file
    private void loadAndDrawNotes(String[] filenames)
    {
    	notesDisplayList = new int[filenames.length]; // Initialize the list for displayList of notes
    	int i = 0;
    	for(String filename: filenames)
    	{
    		Shape shape = null;
    		try { shape = ShapeLoader.loadShapeFromFile(new File(filename)); }
    		catch(FileNotFoundException e) {
    			System.out.println(".obj file not found.");
    			Display.destroy(); // Release resources
    			System.exit(0); }
    		catch(IOException e) {
    			System.out.println("I/O exception!");
    			Display.destroy();
    			System.exit(0); }
    		// Create displayList
    		notesDisplayList[i] = GL11.glGenLists(1);
    		GL11.glNewList(notesDisplayList[i], GL11.GL_COMPILE);
    		GL11.glBegin(GL11.GL_TRIANGLES); // Start Drawing
    		for(Face face: shape.faces)
    		{
    			Vector3f n1 = shape.normals.get((int) face.normal.x-1); // Normal of the 1st vertex
        		Vector3f v1 = shape.vertices.get((int) face.vertex.x-1); // Coordinates of it
        		Vector3f n2 = shape.normals.get((int) face.normal.y-1); // Normal of the 2nd vertex
        		Vector3f v2 = shape.vertices.get((int) face.vertex.y-1); // Coordinates of it
        		Vector3f n3 = shape.normals.get((int) face.normal.z-1); // For the 3rd vertex
        		Vector3f v3 = shape.vertices.get((int) face.vertex.z-1); // For the 3rd vertex
        		GL11.glNormal3f(n1.x, n1.y, n1.z); // Draw the 1st vertex
        		GL11.glVertex3f(v1.x, v1.y, v1.z); // +0.8f*i to make notes on different tracks
        		GL11.glNormal3f(n2.x, n2.y, n2.z); // ........ 2nd ......
        		GL11.glVertex3f(v2.x, v2.y, v2.z); // ..
        		GL11.glNormal3f(n3.x, n3.y, n3.z); // ........ 3rd ......
        		GL11.glVertex3f(v3.x, v3.y, v3.z); // .
    		}
    		GL11.glEnd(); // Done drawing
    		GL11.glEndList(); // Added into the displayList
    		i++; // Adding the each music note into the array, "1" for 0 ("Do"), "2" for 1 ("Re"), etc.
    	}
    }
    
    // Configure the light
    private static void configureLight() 
    {
    	GL11.glEnable(GL11.GL_LIGHTING);
    	GL11.glEnable(GL11.GL_LIGHT0);
    	GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, 
    			floatArrayToFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
    	GL11.glEnable(GL11.GL_COLOR_MATERIAL); // So that we don't need to set material for each shape
    }
    
    // Because the need for FloatBuffer from GL11.glLightModel, we need this conversion method
    // This method is created by following the LWJGL official tutorial
    public static FloatBuffer floatArrayToFloatBuffer(float[] toBeConverted) 
    {
        FloatBuffer result = BufferUtils.createFloatBuffer(toBeConverted.length);
        result.put(toBeConverted);
        result.flip();
        return result;
    }
     
    // Basically, I don't want the user to press key and create a "continuous" series of shapes.
    // So I create this loadingDelay to create the minimal distance between shapes on the track.
    int loadingDelay = 420;
    
    // The repeatedly called method used to create each frame
    private void render() 
    {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clean up buffers
        int listPicker = 0; // Used to identify the music notes to be rendered on track one by one
        
        // Rendering the music notes on track
        while(onTrackNotesHolder[listPicker][0] != 0 && listPicker < onTrackNotesHolder.length)
        {
        	GL11.glPushMatrix();
        		float selfDX = onTrackNotesHolder[listPicker][1]; // Load the x position
        		float selfDY = onTrackNotesHolder[listPicker][2]; // Load the y position
        		float selfDZ = onTrackNotesHolder[listPicker][3]; // Load the z position
        		selfDZ = selfDZ - 0.01f; // To make the music note moving away from us
        		onTrackNotesHolder[listPicker][3] = selfDZ; // See above
        		GL11.glLoadIdentity();	// Reset the matrix
        		// Also consider the influence from the moving camera
        		GL11.glTranslatef(-1.8f+dX+selfDX,-3.0f+dY+selfDY,-6.0f+dZ+selfDZ);
        		GL11.glCallList((int)onTrackNotesHolder[listPicker][0]);
        		listPicker++; // Move to the next music notes on track to render
            GL11.glPopMatrix();
        }
        
    	GL11.glDisable(GL11.GL_LIGHTING); // I don't like the lighting for spiral, disable it.
    	GL11.glColor3f(0.933f, 0.071f, 0.537f); // Set a color for the spiral
        GL11.glPushMatrix(); // Start rendering the spiral
			GL11.glLoadIdentity();
			GL11.glTranslatef(dX, dY, dZ-120f); // Finally move it away by 120
			GL11.glRotatef(rAngle, 0.0f, 0.0f, 1.0f); // Then rotate around z-axis
			GL11.glRotatef(90, 1f, 0f, 0f); // First rotate the spiral by 90 degrees around x-axis
			GL11.glCallList(spiralDisplayList); // Get the spiral
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING); // Re-enable the lightning
        
        // Rendering tracks
        for(int i = 0; i < 7; i++)
        {
        	GL11.glPushMatrix();
        		GL11.glLoadIdentity();                         
        		GL11.glTranslatef(-1.8f+dX,-3.0f+dY,-6.0f+dZ);
        		GL11.glBegin(GL11.GL_QUADS); // Drawing tracks for notes 
        		// I want the track to get the light, so I deliberately set the normal this way,
        		GL11.glNormal3f(0f, 0f, 1f); // so this is not a mistake!
        			if(Keyboard.isKeyDown(Keyboard.KEY_1) && i==0)	// Light up the chosen track...
        				GL11.glColor3f(1.000f, 0.078f, 0.576f);
            		else if(Keyboard.isKeyDown(Keyboard.KEY_2) && i==1)
            			GL11.glColor3f(1.000f, 0.078f, 0.576f);
            		else if(Keyboard.isKeyDown(Keyboard.KEY_3) && i==2)
            			GL11.glColor3f(1.000f, 0.078f, 0.576f);
            		else if(Keyboard.isKeyDown(Keyboard.KEY_4) && i==3)
            			GL11.glColor3f(1.000f, 0.078f, 0.576f);
            		else if(Keyboard.isKeyDown(Keyboard.KEY_5) && i==4)
            			GL11.glColor3f(1.000f, 0.078f, 0.576f);
            		else if(Keyboard.isKeyDown(Keyboard.KEY_6) && i==5)
            			GL11.glColor3f(1.000f, 0.078f, 0.576f);
            		else if(Keyboard.isKeyDown(Keyboard.KEY_7) && i==6)
            			GL11.glColor3f(1.000f, 0.078f, 0.576f);
            		else
            			GL11.glColor3f(1.0f,0.0f,0.0f);
        			GL11.glVertex3f(0f+0.8f*i, 0f, -0f);  // Left front
                	GL11.glColor3f(0.0f,1.0f,0.0f);		  // Pick a different color for each vertex
                	GL11.glVertex3f(1f+0.8f*i, 0f, -0f);  // Right front
                	GL11.glColor3f(0.0f,0.0f,1.0f);
                	GL11.glVertex3f(1f+0.8f*i, 0f, -100f); // Right rear
                	GL11.glColor3f(0.0f,0.0f,1.0f*i);
                	GL11.glVertex3f(0f+0.8f*i, 0f, -100f); // Left rear
                GL11.glEnd(); // Done drawing the tracks
                
        		if(Keyboard.isKeyDown(Keyboard.KEY_2) && i==2) // Light up the chosen track...
        			GL11.glColor3f(1.0f,0.0f,0.0f);
        		if(Keyboard.isKeyDown(Keyboard.KEY_3) && i==3)
        			GL11.glColor3f(1.0f,0.0f,0.0f);
        		if(Keyboard.isKeyDown(Keyboard.KEY_4) && i==4)
        			GL11.glColor3f(1.0f,0.0f,0.0f);
        		if(Keyboard.isKeyDown(Keyboard.KEY_5) && i==5)
        			GL11.glColor3f(1.0f,0.0f,0.0f);
        		if(Keyboard.isKeyDown(Keyboard.KEY_6) && i==6)
        			GL11.glColor3f(1.0f,0.0f,0.0f);
        		if(Keyboard.isKeyDown(Keyboard.KEY_7) && i==7)
        			GL11.glColor3f(1.0f,0.0f,0.0f);
            GL11.glPopMatrix();
        }
        
        // Drawing the floating music notes from 1 - 7 one by one...
        GL11.glPushMatrix();
        	GL11.glLoadIdentity();
        	if(Keyboard.isKeyDown(Keyboard.KEY_1)) // Light up the chosen music note...
    			GL11.glColor3f(1.0f,0.0f,0.0f);
        	else
        		GL11.glColor3f(1.0f, 0.98f, 0.804f);
        	GL11.glTranslatef(-1.5f+dX,0.0f+dY,-6.0f+dZ);
        	GL11.glRotatef(rAngle,0.0f,1.0f,0.0f); // Make it rotate
        	GL11.glCallList(notesDisplayList[0]); // Get the shape
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
    		GL11.glLoadIdentity();
    		if(Keyboard.isKeyDown(Keyboard.KEY_2)) // Light up the chosen music note...
    			GL11.glColor3f(0.000f, 0.749f, 1.000f);
        	else
        		GL11.glColor3f(1.0f, 0.98f, 0.804f);
    		GL11.glTranslatef(-1.1f+dX,0.0f+dY,-6.0f+dZ);
    		GL11.glRotatef(rAngle,0.0f,1.0f,0.0f); // Make it rotate
    		GL11.glCallList(notesDisplayList[1]); // Get the shape
    	GL11.glPopMatrix();
    	
    	GL11.glPushMatrix();
    		GL11.glLoadIdentity();
    		if(Keyboard.isKeyDown(Keyboard.KEY_3)) // Light up the chosen music note...
    			GL11.glColor3f(0.000f, 1.000f, 1.000f);
        	else
        		GL11.glColor3f(1.0f, 0.98f, 0.804f);
    		GL11.glTranslatef(-0.7f+dX,0.0f+dY,-6.0f+dZ);
    		GL11.glRotatef(rAngle,0.0f,1.0f,0.0f); // Make it rotate
    		GL11.glCallList(notesDisplayList[2]); // Get the shape
    	GL11.glPopMatrix();   
        
        GL11.glPushMatrix();
        	GL11.glLoadIdentity();
        	if(Keyboard.isKeyDown(Keyboard.KEY_4)) // Light up the chosen music note...
    			GL11.glColor3f(0.678f, 1.000f, 0.184f);
        	else
        		GL11.glColor3f(1.0f, 0.98f, 0.804f);
        	GL11.glTranslatef(-0.3f+dX,0.0f+dY,-6.0f+dZ);
        	GL11.glRotatef(rAngle,0.0f,1.0f,0.0f); // Make it rotate
        	GL11.glCallList(notesDisplayList[3]); // Get the shape
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        	GL11.glLoadIdentity();
        	if(Keyboard.isKeyDown(Keyboard.KEY_5)) // Light up the chosen music note...
    			GL11.glColor3f(1.0f,0.0f,0.0f);
        	else
        		GL11.glColor3f(1.0f, 0.98f, 0.804f);
        	GL11.glTranslatef(0.1f+dX,0.0f+dY,-6.0f+dZ);
        	GL11.glRotatef(rAngle,0.0f,1.0f,0.0f); // Make it rotate
        	GL11.glCallList(notesDisplayList[4]); // Get the shape
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        	GL11.glLoadIdentity();
        	if(Keyboard.isKeyDown(Keyboard.KEY_6)) // Light up the chosen music note...
    			GL11.glColor3f(1.000f, 0.078f, 0.576f);
        	else
        		GL11.glColor3f(1.0f, 0.98f, 0.804f);
        	GL11.glTranslatef(0.5f+dX,0.0f+dY,-6.0f+dZ);
        	GL11.glRotatef(rAngle,0.0f,1.0f,0.0f); // Make it rotate
        	GL11.glCallList(notesDisplayList[5]); // Get the shape
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        	GL11.glLoadIdentity();
        	if(Keyboard.isKeyDown(Keyboard.KEY_7)) // Light up the chosen music note...
    			GL11.glColor3f(0.000f, 1.000f, 0.000f);
        	else
        		GL11.glColor3f(1.0f, 0.98f, 0.804f);
        	GL11.glTranslatef(0.9f+dX,0.0f+dY,-6.0f+dZ);
        	GL11.glRotatef(rAngle,0.0f,1.0f,0.0f); // Make it rotate
        	GL11.glCallList(notesDisplayList[6]); // Get the shape
        GL11.glPopMatrix();
        gluPerspective(68, (float)4/3, 0.3f, 10f);
        
        rAngle -= 0.15f; // Make sure the shapes actually rotate frame by frame
    }
    
    private void setUpWindow() throws Exception 
    {
        Display.setDisplayMode(new DisplayMode(960, 540));
        Display.setTitle("Qingxiang Jia's Magic Piano");
        Display.create();
    }
    
    private void initialization() throws Exception 
    {
        setUpWindow();
        initGL();
    }
    
    private void initGL() 
    {
        GL11.glShadeModel(GL11.GL_SMOOTH); 			// As recommended, smooth out the shapes
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);  // Set background color as black
        GL11.glClearDepth(1.0);
        GL11.glEnable(GL11.GL_DEPTH_TEST); 			// Make sure shapes will cover each other
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glMatrixMode(GL11.GL_PROJECTION);	    // Choose PROJECTION matrix
        GL11.glLoadIdentity(); 						// Reset PROJECTION matrix
        GLU.gluPerspective(45.0f, (float) 960 / (float) 540, 0.1f, 100.0f); // Set aspect ratio
        GL11.glMatrixMode(GL11.GL_MODELVIEW); 		// Select The MODELVIEW Matrix
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, 
        		GL11.GL_NICEST); 					// As recommended in class, use NICEST
    }
}