package pl.szinton.gk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Application extends JFrame implements KeyListener {

    protected final static int DEFAULT_WIDTH = 800;
    protected final static int DEFAULT_HEIGHT = 600;

    protected final Camera3D camera;
    protected final Scene scene;
    protected boolean debug;

    public Application(Camera3D camera, Scene scene) {
        this.camera = camera;
        this.scene = scene;
        this.debug = false;
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setClip(0, 0, this.getWidth(), this.getHeight());
        g2d.setColor(new Color(200, 230, 255));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        scene.render(g2d, camera);

        if (debug) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(new Font("Dialog", Font.PLAIN, 22));
            g2d.drawString(camera.toString(), 10, 90);
        }
    }

    public static void main(String[] args) {
        Vector3f initialPosition = new Vector3f(6f, 1f, 0f);
        Vector2i frameSize = new Vector2i(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        Camera3D camera = new Camera3D(frameSize, initialPosition);

        Scene scene = new Scene();
        scene.addObject(Utils.createCuboidModel(new Vector3f(4f, 0f, -4f), new Vector3f(1f, 2f, 1f)));
        scene.addObject(Utils.createCuboidModel(new Vector3f(4f, 0f, -8f), new Vector3f(1f, 4f, 1f)));
        scene.addObject(Utils.createCuboidModel(new Vector3f(8f, 0f, -4f), new Vector3f(1f, 2f, 1f)));
        scene.addObject(Utils.createCuboidModel(new Vector3f(8f, 0f, -8f), new Vector3f(1f, 4f, 1f)));

        Application app = new Application(camera, scene);
        app.run();
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
