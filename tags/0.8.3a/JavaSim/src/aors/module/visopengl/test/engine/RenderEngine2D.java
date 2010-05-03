package aors.module.visopengl.test.engine;

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

import aors.module.visopengl.test.gui.StatusPanel;
import aors.module.visopengl.test.shapes.Circle;
import aors.module.visopengl.test.shapes.Ellipse;
import aors.module.visopengl.test.shapes.Rectangle;
import aors.module.visopengl.test.shapes.RegularPolygon;
import aors.module.visopengl.test.shapes.Triangle;
import aors.module.visopengl.test.utility.Camera2D;
import aors.module.visopengl.test.utility.Color;

import com.sun.opengl.util.BufferUtil;

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

  // Test polygons
  Rectangle rect = new Rectangle(50, 50, 4);
  Circle circ = new Circle(20, 7);
  Triangle tri = new Triangle(50, 5);
  Ellipse ell = new Ellipse(50, 25, 3);
  RegularPolygon reg = new RegularPolygon(30, 6, 2);

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
    gl.glTranslated(0, 0, 95);
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
    gl.glPopName();

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

    rect.setFillColor(new Color(Color.RED));
    circ.setFillColor(new Color(Color.TEAL));
    tri.setFillColor(new Color(Color.GREEN));
    ell.setFillColor(new Color(Color.NAVY));
    reg.setFillColor(new Color(Color.SILVER));

    rect.generateDisplayList(gl, glu);
    circ.generateDisplayList(gl, glu);
    tri.generateDisplayList(gl, glu);
    ell.generateDisplayList(gl, glu);
    reg.generateDisplayList(gl, glu);

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
    gl.glOrtho(-width / 2, width / 2, -height / 2, height / 2, 1, -100);

    // Reset model view matrix stack
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
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
