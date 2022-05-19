package pl.szinton.gk.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EdgeScene implements Scene {

    private final List<Model3D> objects;

    public EdgeScene() {
        this.objects = new ArrayList<>();
    }

    @Override
    public void addObject(Model3D obj) {
        objects.add(obj);
    }

    @Override
    public void render(Graphics2D g, Camera3D camera) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        for (Model3D obj : objects) {
            obj.render(g, camera);
        }
    }
}
