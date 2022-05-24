package pl.szinton.gk.view;

import java.awt.*;

public interface Scene {

    void addObject(Model3D obj);

    void render(Graphics2D g, Camera3D camera);
}
