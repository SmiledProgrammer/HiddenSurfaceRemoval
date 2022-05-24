package pl.szinton.gk.view;

import pl.szinton.gk.enums.Direction;
import pl.szinton.gk.enums.RotationAxis;
import pl.szinton.gk.enums.Zoom;
import pl.szinton.gk.math.Vector2i;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ApplicationWindow extends JFrame implements KeyListener {

    public final static int DEFAULT_WIDTH = 800;
    public final static int DEFAULT_HEIGHT = 600;

    protected final Camera3D camera;
    protected boolean debug;

    private final ScenePanel scenePanel;

    public ApplicationWindow(Camera3D camera, Scene scene) {
        this.camera = camera;
        this.debug = false;
        this.scenePanel = new ScenePanel(camera, scene);
        this.add(scenePanel);
        this.setTitle("Virtual Camera");
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addKeyListener(this);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                Component component = (Component) e.getSource();
                camera.setFrameSize(new Vector2i(component.getWidth(), component.getHeight()));
            }
        });
    }

    public void run() {
        this.setVisible(true);
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (debug) {
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Dialog", Font.PLAIN, 22));
            g.drawString(camera.toString(), 10, 90);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> camera.move(Direction.LEFT);
            case KeyEvent.VK_D -> camera.move(Direction.RIGHT);
            case KeyEvent.VK_W -> camera.move(Direction.FORWARD);
            case KeyEvent.VK_S -> camera.move(Direction.BACKWARD);
            case KeyEvent.VK_R -> camera.move(Direction.UP);
            case KeyEvent.VK_F -> camera.move(Direction.DOWN);
            case KeyEvent.VK_J -> camera.rotate(RotationAxis.NEGATIVE_X);
            case KeyEvent.VK_K -> camera.rotate(RotationAxis.NEGATIVE_Y);
            case KeyEvent.VK_L -> camera.rotate(RotationAxis.NEGATIVE_Z);
            case KeyEvent.VK_U -> camera.rotate(RotationAxis.POSITIVE_X);
            case KeyEvent.VK_I -> camera.rotate(RotationAxis.POSITIVE_Y);
            case KeyEvent.VK_O -> camera.rotate(RotationAxis.POSITIVE_Z);
            case KeyEvent.VK_C -> camera.zoom(Zoom.IN);
            case KeyEvent.VK_V -> camera.zoom(Zoom.OUT);
            case KeyEvent.VK_P -> debug = !debug;
        }
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
