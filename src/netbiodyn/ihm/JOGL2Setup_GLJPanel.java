/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.ihm;

import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LINEAR;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_NICEST;
import static com.jogamp.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import com.jogamp.opengl.GL2;
import static com.jogamp.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static com.jogamp.opengl.GL2ES1.GL_POINT_SMOOTH_HINT;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.awt.GLJPanel;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.*;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import netbiodyn.AllInstances;
import netbiodyn.util.Serialized;
import netbiodyn.InstanceReaxel;
import netbiodyn.Behavior;
import netbiodyn.Entity;

/**
 * JOGL 2.0 Program Template (GLJPanel) This is a "Component" which can be added
 * into a top-level "Container". It also handles the OpenGL events to render
 * graphics.
 */
@SuppressWarnings("serial")
public class JOGL2Setup_GLJPanel extends GLJPanel implements IhmListener, GLEventListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, ActionListener {

    private GLU glu;  // for the GL Utility
    // Texture applied over the shape
    private Texture texture;
    private final String textureFileName = "Images/light2.png";
    private final String textureFileType = ".png";

    private int tailleX;
    private int tailleY;
    private int tailleZ;

    private ArrayList<InstanceReaxel> reaxels;

    /**
     * constructor to set up the GUI for this Component
     *
     * @param X
     * @param Y
     * @param Z
     * @param reaxels
     */
    public JOGL2Setup_GLJPanel(int X, int Y, int Z, ArrayList<InstanceReaxel> reaxels) {
        this.addGLEventListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.tailleX = X;
        this.tailleY = Y;
        this.tailleZ = Z;

        this.reaxels = reaxels;
    }

    // ------ Implement methods declared in GLEventListener ------
    /**
     * Called back immediately after the OpenGL context is initialized. Can be
     * used to perform one-time initialization. Run only once.
     *
     * @param drawable
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
        glu = new GLU();                         // get GL Utilities
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f); // set background (clear) color
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting

        gl.glEnable(GL_BLEND); // Enable Blending
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); //GL_ONE /*GL_SRC_ALPHA,*/ , GL_ONE); // Type Of Blending To Perform
        gl.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST); // Really Nice Point Smoothing
        initLight(gl);

        // Load the texture image
        try {
            // Create a OpenGL Texture object from (URL, mipmap, file suffix)
            URL url = getClass().getClassLoader().getResource(textureFileName);
            texture = TextureIO.newTexture(url, false, textureFileType);
        } catch (GLException | IOException e) {
            e.printStackTrace();
        }

        // Use linear filter for texture if image is larger than the original texture
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Use linear filter for texture if image is smaller than the original texture
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

    }

    public void initLight(GL2 gl) {

        if (true) {
            return;
        }

        float values_ambient[] = {0.5f, 0.5f, 0.5f, 1.0f};
        float values_diffuse[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float values_position[] = {0.0f, 0.0f, 2.0f, 1.0f};
        FloatBuffer ambient = FloatBuffer.allocate(4);
        FloatBuffer diffuse = FloatBuffer.allocate(4);
        FloatBuffer position = FloatBuffer.allocate(4);
        ambient.put(values_ambient);
        diffuse.put(values_diffuse);
        position.put(values_position);
        // First Switch the lights on.
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_LIGHT1);
        gl.glEnable(GL_LIGHT2);
        //
        // Light 0.
        //	
        // Default from the red book.
        //
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, ambient);
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse);
        gl.glLightfv(GL_LIGHT0, GL_POSITION, position);

        //
        // Light 1.
        //
        // Position and direction (spotlight)
        float values_posLight1[] = {1.0f, 1.0f, 1.0f, 0.0f};
        float values_spotDirection[] = {-1.0f, -1.0f, 0.f};
        FloatBuffer posLight1 = FloatBuffer.allocate(4);
        FloatBuffer spotDirection = FloatBuffer.allocate(4);
        posLight1.put(values_posLight1);
        spotDirection.put(values_spotDirection);
        gl.glLightfv(GL_LIGHT1, GL_POSITION, posLight1);
        gl.glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 1000.0F);
        gl.glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, spotDirection);
        //
        gl.glLightfv(GL_LIGHT1, GL_AMBIENT, diffuse);
        gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, diffuse);
        gl.glLightfv(GL_LIGHT1, GL_SPECULAR, diffuse);
        gl.glLightfv(GL_LIGHT1, GL_SPECULAR, diffuse);
        //
        gl.glLightf(GL_LIGHT1, GL_CONSTANT_ATTENUATION, 1.0f);

        //
        // Light 2.
        //
        // Position and direction
        float values_posLight2[] = {.5f, 1.f, 3.f, 0.0f};
        FloatBuffer posLight2 = FloatBuffer.allocate(4);
        posLight2.put(values_posLight2);
        gl.glLightfv(GL_LIGHT2, GL_POSITION, posLight2);
        gl.glLightf(GL_LIGHT2, GL_SPOT_CUTOFF, 1000.0F);
        //
        gl.glLightfv(GL_LIGHT2, GL_AMBIENT, diffuse);
        gl.glLightfv(GL_LIGHT2, GL_DIFFUSE, diffuse);
        gl.glLightfv(GL_LIGHT2, GL_SPECULAR, diffuse);
        //
        gl.glLightf(GL_LIGHT2, GL_CONSTANT_ATTENUATION, 1.0f);
    }

    /**
     * Call-back handler for window re-size event. Also called when the drawable
     * is first set to visible.
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

        if (height == 0) {
            height = 1;   // prevent divide by zero
        }
        float aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        glu.gluPerspective(45.0, aspect, 0.1, 1000.0); // fovy, aspect, zNear, zFar

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity(); // reset
    }

    int oldX, oldY;
    float eyeX = 0, eyeY = 0, eyeZ = 0;
    float radius = 155.0f, phi = -(float) Math.PI / 2.0f, theta = -2.3f;
    float pickObjX = 0, pickObjY = 0, pickObjZ = 0.0f;

    /**
     * Called back by the animator to perform rendering.
     *
     * @param drawable
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers

        gl.glLoadIdentity();  // reset the model-view matrix

        // Render the environment
        gl.glLineWidth(1.0f);
        gl.glColor3f(0.7f, 0.7f, 0.7f);
        gl.glBegin(GL_LINES);
        // *************** BOTTOM PLAN
        gl.glVertex3f(-getTailleX() / 2.0f, -getTailleY() / 2.0f, -getTailleZ() / 2.0f);
        gl.glVertex3f(getTailleX() / 2.0f, -getTailleY() / 2.0f, -getTailleZ() / 2.0f);

        gl.glVertex3f(getTailleX() / 2.0f, -getTailleY() / 2.0f, -getTailleZ() / 2.0f);
        gl.glVertex3f(getTailleX() / 2.0f, getTailleY() / 2.0f, -getTailleZ() / 2.0f);

        gl.glVertex3f(getTailleX() / 2.0f, getTailleY() / 2.0f, -getTailleZ() / 2.0f);
        gl.glVertex3f(-getTailleX() / 2.0f, getTailleY() / 2.0f, -getTailleZ() / 2.0f);

        gl.glVertex3f(-getTailleX() / 2.0f, getTailleY() / 2.0f, -getTailleZ() / 2.0f);
        gl.glVertex3f(-getTailleX() / 2.0f, -getTailleY() / 2.0f, -getTailleZ() / 2.0f);
        // *************** UPPER PLAN
        gl.glVertex3f(-getTailleX() / 2.0f, -getTailleY() / 2.0f, getTailleZ() / 2.0f);
        gl.glVertex3f(getTailleX() / 2.0f, -getTailleY() / 2.0f, getTailleZ() / 2.0f);

        gl.glVertex3f(getTailleX() / 2.0f, -getTailleY() / 2.0f, getTailleZ() / 2.0f);
        gl.glVertex3f(getTailleX() / 2.0f, getTailleY() / 2.0f, getTailleZ() / 2.0f);

        gl.glVertex3f(getTailleX() / 2.0f, getTailleY() / 2.0f, getTailleZ() / 2.0f);
        gl.glVertex3f(-getTailleX() / 2.0f, getTailleY() / 2.0f, getTailleZ() / 2.0f);

        gl.glVertex3f(-getTailleX() / 2.0f, getTailleY() / 2.0f, getTailleZ() / 2.0f);
        gl.glVertex3f(-getTailleX() / 2.0f, -getTailleY() / 2.0f, getTailleZ() / 2.0f);
        // *************** LATERAL EDGES
        gl.glVertex3f(-getTailleX() / 2.0f, -getTailleY() / 2.0f, -getTailleZ() / 2.0f);
        gl.glVertex3f(-getTailleX() / 2.0f, -getTailleY() / 2.0f, getTailleZ() / 2.0f);

        gl.glVertex3f(getTailleX() / 2.0f, -getTailleY() / 2.0f, -getTailleZ() / 2.0f);
        gl.glVertex3f(getTailleX() / 2.0f, -getTailleY() / 2.0f, getTailleZ() / 2.0f);

        gl.glVertex3f(getTailleX() / 2.0f, getTailleY() / 2.0f, -getTailleZ() / 2.0f);
        gl.glVertex3f(getTailleX() / 2.0f, getTailleY() / 2.0f, getTailleZ() / 2.0f);

        gl.glVertex3f(-getTailleX() / 2.0f, getTailleY() / 2.0f, -getTailleZ() / 2.0f);
        gl.glVertex3f(-getTailleX() / 2.0f, getTailleY() / 2.0f, getTailleZ() / 2.0f);

        gl.glEnd();

        // Render the particles
        for (int i = 0; i < getReaxels().size(); i++) {
            //if (particles[i].active) {
            InstanceReaxel rea = getReaxels().get(i);

            // Draw the particle using our RGB values, fade the particle based on it's life
            //gl.glColor4f(particles[i].r, particles[i].g, particles[i].b, particles[i].life);
            float r, g, b, a;
            r = rea.getCouleur().getRed() / 255.0f;
            g = rea.getCouleur().getGreen() / 255.0f;
            b = rea.getCouleur().getBlue() / 255.0f;
            a = rea.getCouleur().getAlpha() / 255.0f;
            gl.glColor4f(r, g, b, a);

            //texture.enable(gl);
            //texture.bind(gl);
            //gl.glBegin(GL_TRIANGLE_STRIP); // build quad from a triangle strip
            gl.glBegin(GL2.GL_QUADS);

            //String str_nr = new Integer(r).toString();
            float px = rea.getX() - getTailleX() / 2.0f;
            float py = rea.getY() - getTailleY() / 2.0f;
            float pz = -(rea.getZ() - getTailleZ() / 2.0f);

            // front : blue
            //gl.glColor4f(r, g, b, a);
            gl.glVertex3f(px - 0.5f, py + 0.5f, pz + 0.5f);
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz + 0.5f);
            gl.glVertex3f(px + 0.5f, py - 0.5f, pz + 0.5f);
            gl.glVertex3f(px + 0.5f, py + 0.5f, pz + 0.5f);

            // back : green
            //gl.glColor4f(0.5f*r, 0.5f*g, 0.5f*b, a);
            gl.glVertex3f(px + 0.5f, py - 0.5f, pz - 0.5f);
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz - 0.5f);
            gl.glVertex3f(px - 0.5f, py + 0.5f, pz - 0.5f);
            gl.glVertex3f(px + 0.5f, py + 0.5f, pz - 0.5f);

            // left : red
            //gl.glColor4f(0.75f*r, 0.75f*g, 0.75f*b, a);
            gl.glVertex3f(px - 0.5f, py + 0.5f, pz + 0.5f);
            gl.glVertex3f(px - 0.5f, py + 0.5f, pz - 0.5f);
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz - 0.5f);
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz + 0.5f);

            // right : orange
            //gl.glColor4f(0.3f*r, 0.3f*g, 0.3f*b, a);
            gl.glVertex3f(px + 0.5f, py + 0.5f, pz - 0.5f);
            gl.glVertex3f(px + 0.5f, py + 0.5f, pz + 0.5f);
            gl.glVertex3f(px + 0.5f, py - 0.5f, pz + 0.5f);
            gl.glVertex3f(px + 0.5f, py - 0.5f, pz - 0.5f);

            // top : white
            //gl.glColor4f(0.6f*r, 0.6f*g, 0.6f*b, a);
            gl.glVertex3f(px + 0.5f, py + 0.5f, pz + 0.5f);
            gl.glVertex3f(px - 0.5f, py + 0.5f, pz + 0.5f);
            gl.glVertex3f(px - 0.5f, py + 0.5f, pz - 0.5f);
            gl.glVertex3f(px + 0.5f, py + 0.5f, pz - 0.5f);

            // bottom : yellow
            //gl.glColor4f(0.25f*r, 0.25f*g, 0.25f*b, a);
            gl.glVertex3f(px + 0.5f, py - 0.5f, pz + 0.5f);
            gl.glVertex3f(px + 0.5f, py - 0.5f, pz - 0.5f);
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz - 0.5f);
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz + 0.5f);

            /*
             gl.glTexCoord2d(textureRight, textureTop);
             gl.glVertex3f(px + 0.5f, py + 0.5f, pz); // Top Right
             gl.glTexCoord2d(textureLeft, textureTop);
             gl.glVertex3f(px - 0.5f, py + 0.5f, pz); // Top Left
             gl.glTexCoord2d(textureRight, textureBottom);
             gl.glVertex3f(px + 0.5f, py - 0.5f, pz); // Bottom Right
             gl.glTexCoord2d(textureLeft, textureBottom);
             gl.glVertex3f(px - 0.5f, py - 0.5f, pz); // Bottom Left
             */
            gl.glEnd();

            // Outlines of the cube
            gl.glLineWidth(2.0f);
            gl.glColor3f(0.1f, 0.1f, 0.1f);
            gl.glBegin(GL_LINES);
            // *************** BOTTOM PLAN
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz - 0.5f);
            gl.glVertex3f(px + 0.5f, py - 0.5f, pz - 0.5f);

            gl.glVertex3f(px + 0.5f, py - 0.5f, pz - 0.5f);
            gl.glVertex3f(px + 0.5f, py + 0.5f, pz - 0.5f);

            gl.glVertex3f(px + 0.5f, py + 0.5f, pz - 0.5f);
            gl.glVertex3f(px - 0.5f, py + 0.5f, pz - 0.5f);

            gl.glVertex3f(px - 0.5f, py + 0.5f, pz - 0.5f);
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz - 0.5f);
            // *************** UPPER PLAN
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz + 0.5f);
            gl.glVertex3f(px + 0.5f, py - 0.5f, pz + 0.5f);

            gl.glVertex3f(px + 0.5f, py - 0.5f, pz + 0.5f);
            gl.glVertex3f(px + 0.5f, py + 0.5f, pz + 0.5f);

            gl.glVertex3f(px + 0.5f, py + 0.5f, pz + 0.5f);
            gl.glVertex3f(px - 0.5f, py + 0.5f, pz + 0.5f);

            gl.glVertex3f(px - 0.5f, py + 0.5f, pz + 0.5f);
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz + 0.5f);
            // *************** LATERAL EDGES
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz - 0.5f);
            gl.glVertex3f(px - 0.5f, py - 0.5f, pz + 0.5f);

            gl.glVertex3f(px + 0.5f, py - 0.5f, pz - 0.5f);
            gl.glVertex3f(px + 0.5f, py - 0.5f, pz + 0.5f);

            gl.glVertex3f(px + 0.5f, py + 0.5f, pz - 0.5f);
            gl.glVertex3f(px + 0.5f, py + 0.5f, pz + 0.5f);

            gl.glVertex3f(px - 0.5f, py + 0.5f, pz - 0.5f);
            gl.glVertex3f(px - 0.5f, py + 0.5f, pz + 0.5f);

            gl.glEnd();
        }

        // Camera position
        eyeX = pickObjX + radius * (float) Math.cos(phi) * (float) Math.sin(theta);
        eyeY = pickObjY + radius * (float) Math.sin(phi) * (float) Math.sin(theta);
        eyeZ = pickObjZ + radius * (float) Math.cos(theta);

        //eyeY = (float)Math.sin(phi) * radius;
        //float R = (float)Math.cos(phi) * radius;
        //eyeX = (float)Math.cos(theta) * R;
        //eyeZ = (float)Math.sin(theta) * R;
        // Change to projection matrix.
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);

        glu.gluLookAt(eyeX, eyeY, eyeZ, pickObjX, pickObjY, pickObjZ, 0, 0, -1);

        // Change back to model view matrix.
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();

        //theta += 0.01f;
        //phi += 0.01f;
        //radius += 0.0f;
    }

    /**
     * Called back before the OpenGL context is destroyed. Release resource such
     * as buffers.
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    boolean rotate = false;

    /**
     * Called when the user presses a mouse button on the display.
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        if (rotate) {
            return;  // don't start a new drag while one is already in progress
        }
        oldX = evt.getX();
        oldY = evt.getY();
        rotate = true;  // might not always be correct!
        //prevX = startX = x;
        //prevY = startY = y;
        //display.repaint();    //  only needed if display should change

    }

    /**
     * Called when the user releases a mouse button after pressing it on the
     * display.
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        rotate = false;
    }

    /**
     * Called during a drag operation when the user drags the mouse on the
     * display/
     */
    @Override
    public void mouseDragged(MouseEvent evt) {
        if (rotate == true) {

            int x = evt.getX();
            int y = evt.getY();
            phi += (x - oldX) * 0.01f;
            theta -= (y - oldY) * 0.01f;

            // new origin
            oldX = x;
            oldY = y;

            if (theta > -1.7f) {
                theta = -1.7f;
            }
            if (theta < -3.14f) {
                theta = -3.14f;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
    }

    @Override
    public void mouseExited(MouseEvent evt) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int val = e.getScrollAmount();
        if (e.getWheelRotation() > 0) {
            radius += val;
        } else {
            radius -= val;
        }

        if (radius < 1) {
            radius = 1;
        }
        if (radius > getTailleX() + getTailleY() + getTailleZ()) {
            radius = getTailleX() + getTailleY() + getTailleZ();
        }
    }

    public int getTailleX() {
        return tailleX;
    }

    public int getTailleY() {
        return tailleY;
    }

    public int getTailleZ() {
        return tailleZ;
    }

    public ArrayList<InstanceReaxel> getReaxels() {
        return reaxels;
    }

    public void setReaxels(ArrayList<InstanceReaxel> reaxels) {
        this.reaxels = reaxels;
    }

    @Override
    public void newEnvParameters(Env_Parameters parameters) {
        this.tailleX = parameters.getX();
        this.tailleY = parameters.getY();
        this.tailleZ = parameters.getZ();
        reaxels = new ArrayList<>();
    }

    @Override
    public void moteurReactionUpdate(ArrayList<Behavior> behaviours) {
    }

    @Override
    public void newEnvLoaded(Serialized saved, HashMap<String, Integer> entitesBook) {
        this.tailleX = saved.getParameters().getX();
        this.tailleY = saved.getParameters().getY();
        this.tailleZ = saved.getParameters().getZ();
        reaxels = saved.getInstances().getList();
    }

    @Override
    public void protoEntityUpdate(ArrayList<Entity> entities, HashMap<String, Integer> entitesBook) {
    }

    @Override
    public void matrixUpdate(AllInstances instances, HashMap<String, Integer> entitesBook, int time) {
        this.setReaxels(instances.getList());
    }

    @Override
    public void ready() {
    }
}
