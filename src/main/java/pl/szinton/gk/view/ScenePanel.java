package pl.szinton.gk.view;

import javax.swing.*;
import java.awt.*;

public class ScenePanel extends JPanel {

    protected final Camera3D camera;
    private final Scene scene;

    public ScenePanel(Camera3D camera, Scene scene) {
        this.camera = camera;
        this.scene = scene;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setClip(0, 0, this.getWidth(), this.getHeight());
        g2d.setColor(new Color(200, 230, 255));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        scene.render(g2d, camera);
    }
}
