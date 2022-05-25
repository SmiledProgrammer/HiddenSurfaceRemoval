package pl.szinton.gk.view;

import javax.swing.*;
import java.awt.*;

public class ScenePanel extends JPanel {

    private final static Color DEFAULT_BACKGROUND_COLOR = new Color(150, 150, 150);

    protected final Camera3D camera;
    private Scene scene;

    public ScenePanel(Camera3D camera, Scene scene) {
        this.camera = camera;
        this.scene = scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setClip(0, 0, this.getWidth(), this.getHeight());
        g2d.setColor(DEFAULT_BACKGROUND_COLOR);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        scene.render(g2d, camera);
    }
}
