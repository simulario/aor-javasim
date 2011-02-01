package aors.module.visopengl3d.test.engine;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.shape.Cone;
import aors.module.visopengl3d.shape.Cube;
import aors.module.visopengl3d.shape.Cuboid;
import aors.module.visopengl3d.shape.Cylinder;
import aors.module.visopengl3d.shape.Pyramid;
import aors.module.visopengl3d.shape.RegularTriangularPrism;
import aors.module.visopengl3d.shape.Sphere;
import aors.module.visopengl3d.shape.Tetrahedra;
import aors.module.visopengl3d.test.gui.StatusPanel;
import aors.module.visopengl3d.test.utility.Camera2D;
//import aors.module.visopengl3d.test.utility.Color;
import aors.module.visopengl3d.utility.Color;
import aors.module.visopengl3d.test.utility.TextureLoader;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;

/**
 * The RenderEngine2D class provides all methods that are required for drawing
 * to an OpenGL canvas. This includes method to initialize rendering, handle
 * resizes of the canvas and to render a single frame.
 * 
 * @author Sebastian Mucha
 * @since February 16th, 2010
 */
public class RenderEngine2D implements GLEventListener, MouseMotionListener,
    MouseListener, KeyListener {

  // OpenGL Utility Library object
  GLU glu = new GLU();

  // Reference to the applications status panel
  private StatusPanel statusPanel;

  // Scene camera
  private Camera2D camera;

  // Dimensions of the OpenGL canvas
  //private int screenWidth;
  private int screenHeight;

  // Size of the selection buffer
  private final int SELECTION_BUFFER_SIZE = 512;

  // A point in screen coordinate system (used for object selection)
  private Point selectionPoint = new Point();

  // Flag indicating if the user wants to select something
  private boolean pickingMode;

  // Test Shapes
  Cube cube = new Cube();
  Cuboid cuboid = new Cuboid();
  Cylinder cylinder = new Cylinder();
  Cone cone = new Cone();
  Sphere sphere = new Sphere();
  RegularTriangularPrism prism = new RegularTriangularPrism();
  Pyramid pyramid = new Pyramid();
  Tetrahedra tetrahedra = new Tetrahedra();
  /*Rectangle rect = new Rectangle(50, 50, 4);
  Circle circ = new Circle(20, 7);
  Triangle tri = new Triangle(50, 5);
  Ellipse ell = new Ellipse(50, 25, 3);
  RegularPolygon reg = new RegularPolygon(30, 6, 2);*/

  /**
   * Process the hits that where returned from a selection operation.
   * 
   * @param hits
   *          the number of hits
   * @param selectionBuffer
   *          the content of the selection buffer
   */
  private void processHits(int hits, int selectionBuffer[]) {

    if (hits > 0) {

      /*
       * Have a look at the content of the selection buffer to understand this
       * formula
       */
      int pos = (hits * 4) + ((hits * (hits + 1)) / 2);

      /*
       * Update the object label of the status panel with the selected object's
       * ID. If objects overlap the one that is on top will be returned.
       */
      statusPanel.updateObjectLabel(selectionBuffer[pos - 1]);
    }
  }

  /**
   * Displays objects (shapes) inside of the scene. If objects need to be
   * selectable a unique name for each object has to be pushed onto the name
   * stack.
   * 
   * @param gl
   *          the OpenGL pipeline object
   */
  private void displayObjects(GL2 gl) {
	gl.glPushName(1);
	//gl.glRotated(-90, 1, 0, 0);
	//gl.glRotated(-45, 0, 1, 0);
	//gl.glRotated(-90, 1, 0, 0);
	cylinder.display(gl, glu);
	gl.glPopName();
	  
    /*gl.glTranslated(0, 0, 95);
    gl.glPushName(1);
    rect.display(gl);
    gl.glPopName();

    gl.glLoadIdentity();

    gl.glTranslated(0, 0, 98);
    gl.glPushName(2);
    circ.display(gl);
    gl.glPopName();

    gl.glLoadIdentity();

    gl.glTranslated(0, 0, 99);
    gl.glPushName(3);
    tri.display(gl);
    gl.glPopName();*/

    // gl.glTranslated(100, 0, 0);
    // gl.glPushName(4);
    // ell.display(gl);
    // gl.glPopName();
    //		
    // gl.glTranslated(100, 0, 0);
    // gl.glPushName(5);
    // reg.display(gl);
    // gl.glPopName();
  }

/**
   * Switches rendering into selection mode. A name stack and projection matrix
   * for object selection are established and all objects are displayed.
   * 
   * @param gl
   *          OpenGL pipeline object.
   */
  private void selectObjects(GL2 gl) {
    // Set up the selection buffer
    int selectionBufferArray[] = new int[SELECTION_BUFFER_SIZE];
    IntBuffer selectionBuffer = BufferUtil.newIntBuffer(SELECTION_BUFFER_SIZE);

    // Number of hits
    int hits = 0;

    // Get the viewport dimensions
    int viewport[] = new int[4];
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

    // Create the selection buffer and switch into selection mode
    gl.glSelectBuffer(SELECTION_BUFFER_SIZE, selectionBuffer);
    gl.glRenderMode(GL2.GL_SELECT);

    // Initialize the name stack
    gl.glInitNames();
    gl.glPushName(-1);

    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPushMatrix();
    gl.glLoadIdentity();

    // Create a small 1x1 pixel picking region around the cursor
    glu.gluPickMatrix(selectionPoint.x, selectionPoint.y, 1, 1, viewport, 0);

    // Set up the projection matrix
    gl.glOrtho(-viewport[2] / 2, viewport[2] / 2, -viewport[3] / 2,
        viewport[3] / 2, -1, 100);

    // Draw the objects
    displayObjects(gl);

    // Restore the old matrix settings
    gl.glPopMatrix();
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glFlush();

    // Get the number of hits
    hits = gl.glRenderMode(GL2.GL_RENDER);

    // Get the content of the selection buffer
    selectionBuffer.get(selectionBufferArray);

    // Process the hits
    processHits(hits, selectionBufferArray);
  }

  // GLEventListener -----------------------------------------------------------

  @Override
  public void display(GLAutoDrawable drawable) {

    // Get the GL pipeline object
    final GL2 gl = (GL2) drawable.getGL();

    // Clear the color and depth buffer
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

    // Reset model view matrix stack
    gl.glLoadIdentity();

    // Apply camera transformations
    camera.getElapsedTime();
    // camera.scroll(gl);
    // camera.rotate(gl);
    // camera.zoom(gl);

    if (pickingMode == true) {
      // Select objects
      selectObjects(gl);

      // Disable picking mode
      pickingMode = false;
    }

    // Display objects
    displayObjects(gl);
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
  }

  @Override
  public void init(GLAutoDrawable drawable) {

    // Get the GL pipeline object
    final GL2 gl = (GL2) drawable.getGL();

    // Set the clearing color for the color and depth buffer
    gl.glClearColor(0, 0, 0, 1);

    // Enable back face culling
    gl.glEnable(GL2.GL_CULL_FACE);
    gl.glCullFace(GL2.GL_BACK);

    // Enable alpha blending
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

    // Enable depth testing
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glDepthFunc(GL2.GL_LEQUAL);
    
    // Set the shading model to smooth shading
    gl.glShadeModel(GL2.GL_SMOOTH);
    
    // Color of global ambient light
    float[] globalAmbient = {0.4f, 0.4f, 0.4f, 1.0f};
    
    // Position and colors of light source 0
    float[] lightPosition = {1.0f, 1.0f, 1.0f, 0.0f};
    //float[] lightAmbient = {0.3f, 0.3f, 0.3f, 1.0f};
    float[] lightDiffuse = {0.8f, 0.8f, 0.8f, 1.0f};
    float[] lightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
    
    // Specular material properties
    float[] specularRef = {1.0f, 1.0f, 1.0f, 1.0f};
    
    // Set global ambient light
    gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globalAmbient, 0);
    
    // Set position and colors of light source 0
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
    //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightSpecular, 0);
    
    // Set specular material properties and shininess
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specularRef, 0);
    gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 20);
    
    // Enable color tracking
    gl.glEnable(GL2.GL_COLOR_MATERIAL);
    // Set ambient and diffuse material properties to follow glColor values  
    gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
    
    // Multiply texture color by primitive color, so that textured geometry appears lit
    gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
    // Apply specular highlights after texturing
    gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
    
    // Enable lighting
    gl.glEnable(GL2.GL_LIGHTING);
    // Enable light source 0
    gl.glEnable(GL2.GL_LIGHT0);

 /*   rect.setFillColor(new Color(Color.RED));
    circ.setFillColor(new Color(Color.TEAL));
    tri.setFillColor(new Color(Color.GREEN));
    ell.setFillColor(new Color(Color.NAVY));
    reg.setFillColor(new Color(Color.SILVER));

    rect.generateDisplayList(gl, glu);
    circ.generateDisplayList(gl, glu);
    tri.generateDisplayList(gl, glu);
    ell.generateDisplayList(gl, glu);
    reg.generateDisplayList(gl, glu);*/
    
    Texture texture = TextureLoader.load("C:\\Users\\Susi\\workspace\\AOR-JavaSIM\\media\\images\\backgrounds\\Sunflower.jpg");
    cylinder.setTexture(texture);
    
    cube.setWidth(100);
    cube.setFill(Color.RED);
    cube.generateDisplayList(gl, glu);
    
    cuboid.setWidth(50);
    cuboid.setHeight(100);
    cuboid.setDepth(80);
    cuboid.setFill(Color.RED);
    cuboid.generateDisplayList(gl, glu);
    
    cylinder.setWidth(50);
    cylinder.setHeight(100);
    cylinder.setFill(Color.RED);
    cylinder.generateDisplayList(gl, glu);
    
    cone.setWidth(50);
    cone.setHeight(100);
    cone.setFill(Color.RED);
    cone.generateDisplayList(gl, glu);
    
    sphere.setWidth(100);
    sphere.setFill(Color.BLUE);
    sphere.generateDisplayList(gl, glu);
    
    prism.setWidth(100);
    prism.setHeight(200);
    prism.setDepth(100);
    prism.setFill(Color.RED);
    prism.generateDisplayList(gl, glu);
    
    pyramid.setWidth(100);
    pyramid.setHeight(100);
    pyramid.setDepth(100);
    pyramid.setFill(Color.RED);
    pyramid.generateDisplayList(gl, glu);
    
    tetrahedra.setWidth(100);
    tetrahedra.setHeight(100);
    tetrahedra.setDepth(100);
    tetrahedra.setFill(Color.RED);
    tetrahedra.generateDisplayList(gl, glu);

    // Create the scene camera
    camera = new Camera2D();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width,
      int height) {

    // Get the GL pipeline object
    final GL2 gl = (GL2) drawable.getGL();

    // Store the screen dimensions
    //screenWidth = width;
    screenHeight = height;

    // Set the viewport to window dimensions
    gl.glViewport(x, y, width, height);

    // Reset projection matrix stack
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();

    // Establish an orthogonal clipping volume
    //gl.glOrtho(-width / 2, width / 2, -height / 2, height / 2, 1, -100);
    
    //gl.glTranslated(0, 0, -100);
    //gl.glFrustum(-width/2, width/2, -height/2, height/2, 1000, 2000);
    double aspect = ((double)width)/((double)height);
    glu.gluPerspective(45, aspect, 1, 1001);
    //System.out.println(width + "/" + height + " = " + aspect);
    glu.gluLookAt(0, 0, 501, 0, 0, 0, 0, 1, 0);

    // Reset model view matrix stack
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    //glu.gluLookAt(0, 0, 400, 0, 0, 0, 0, 1, 0);
  }

  // MouseMotionListener -------------------------------------------------------

  @Override
  public void mouseDragged(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {

    // Update the mouse label in the status panel
    statusPanel.updateMousePosition(e.getX(), screenHeight - e.getY() - 1);
  }

  // MouseListener -------------------------------------------------------------

  @Override
  public void mouseClicked(MouseEvent e) {

    // Convert the window coordinates into viewport coordinates and store them
    selectionPoint.x = e.getX();
    selectionPoint.y = screenHeight - e.getY() - 1;

    // Inform the engine that the user wants to select something
    pickingMode = true;
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  // KeyListener ---------------------------------------------------------------

  @Override
  public void keyPressed(KeyEvent e) {

    // Scroll left when the left arrow key was pressed
    if (e.getKeyCode() == KeyEvent.VK_LEFT)
      camera.scrollLeft(true);

    // Scroll right when the right arrow key was pressed
    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
      camera.scrollRight(true);

    // Scroll up when the up arrow key was pressed
    if (e.getKeyCode() == KeyEvent.VK_UP)
      camera.scrollUp(true);

    // Scroll down when the down arrow key was pressed
    if (e.getKeyCode() == KeyEvent.VK_DOWN)
      camera.scrollDown(true);

    // Rotate clockwise if the page down key is pressed
    if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
      camera.rotateClockwise(true);

    // Rotate counterclockwise if the page up key is pressed
    if (e.getKeyCode() == KeyEvent.VK_PAGE_UP)
      camera.rotateCounterclockwise(true);

    // Zoom in when the plus key is typed
    if (e.getKeyCode() == KeyEvent.VK_PLUS)
      camera.zoomIn();

    // Zoom out when the minus key was typed
    if (e.getKeyCode() == KeyEvent.VK_MINUS)
      camera.zoomOut();

    // Reset the camera if the space key is pressed
    if (e.getKeyCode() == KeyEvent.VK_SPACE)
      camera.reset();
  }

  @Override
  public void keyReleased(KeyEvent e) {

    // Stop scrolling left when the left arrow key was released
    if (e.getKeyCode() == KeyEvent.VK_LEFT)
      camera.scrollLeft(false);

    // Stop scrolling right when the right arrow key was released
    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
      camera.scrollRight(false);

    // Stop scrolling up when the up arrow key was released
    if (e.getKeyCode() == KeyEvent.VK_UP)
      camera.scrollUp(false);

    // Stop scrolling down when the down arrow key was released
    if (e.getKeyCode() == KeyEvent.VK_DOWN)
      camera.scrollDown(false);

    // Stop rotating clockwise if the page down key is released
    if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
      camera.rotateClockwise(false);

    // Stop rotating counterclockwise if the page up key is released
    if (e.getKeyCode() == KeyEvent.VK_PAGE_UP)
      camera.rotateCounterclockwise(false);
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  // Setter & Getter -----------------------------------------------------------

  public void setStatusPanel(StatusPanel statusPanel) {
    this.statusPanel = statusPanel;
  }

}
