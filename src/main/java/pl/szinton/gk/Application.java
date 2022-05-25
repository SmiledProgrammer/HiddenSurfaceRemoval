package pl.szinton.gk;

import pl.szinton.gk.math.Vector2i;
import pl.szinton.gk.math.Vector3f;
import pl.szinton.gk.utils.ModelUtils;
import pl.szinton.gk.view.*;

import java.util.List;

import static pl.szinton.gk.view.ApplicationWindow.DEFAULT_HEIGHT;
import static pl.szinton.gk.view.ApplicationWindow.DEFAULT_WIDTH;

public class Application {

    public static void main(String[] args) {
        Vector3f initialPosition = new Vector3f(6.5f, 1f, 3f);
        Vector2i frameSize = new Vector2i(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        Camera3D camera = new Camera3D(frameSize, initialPosition);

        Scene scene = new PlaneScene();
        List<Model3D> objects = List.of(
                ModelUtils.createCuboidModel(new Vector3f(4f, 0f, -4f), new Vector3f(1f, 2f, 1f)),
                ModelUtils.createCuboidModel(new Vector3f(4f, 0f, -8f), new Vector3f(1f, 4f, 1f))
//                ModelUtils.createCuboidModel(new Vector3f(8f, 0f, -4f), new Vector3f(1f, 2f, 1f)),
//                ModelUtils.createCuboidModel(new Vector3f(8f, 0f, -8f), new Vector3f(1f, 4f, 1f))
        );

        ApplicationWindow app = new ApplicationWindow(camera, objects);
        app.run();
    }
}
