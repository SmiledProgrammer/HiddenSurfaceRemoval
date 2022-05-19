package pl.szinton.gk.view;

import pl.szinton.gk.hsr.HiddenSurfaceRemoval;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlaneScene implements Scene {

    private final List<Model3D> objects;

    public PlaneScene() {
        this.objects = new ArrayList<>();
    }

    @Override
    public void addObject(Model3D obj) {
        objects.add(obj);
    }

    @Override
    public void render(Graphics2D g, Camera3D camera) {
        HiddenSurfaceRemoval.render(g, camera, objects);
    }
}
