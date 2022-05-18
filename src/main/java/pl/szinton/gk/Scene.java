package pl.szinton.gk;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scene {

    private final List<Model3D> objects;

    public Scene() {
        this.objects = new ArrayList<>();
    }

    public void addObject(Model3D obj) {
        objects.add(obj);
    }

    public void render(Graphics2D g, Camera3D camera) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        for (Model3D obj : objects) {
            obj.render(g, camera);
        }
    }
}
