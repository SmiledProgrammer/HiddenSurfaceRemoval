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
        Vector3f initialPosition = new Vector3f(6.86f, 2.5f, 0f);
        Vector2i frameSize = new Vector2i(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        Camera3D camera = new Camera3D(frameSize, initialPosition);
        camera.rotate(new Vector3f(0f, -6.13f, 0f));

        Scene scene = new PlaneScene();
        scene.addObject(ModelUtils.createCuboidModel(new Vector3f(4f, 0f, -4f), new Vector3f(1f, 2f, 1f)));
        scene.addObject(ModelUtils.createCuboidModel(new Vector3f(4f, 0f, -8f), new Vector3f(1f, 4f, 1f)));
        scene.addObject(ModelUtils.createCuboidModel(new Vector3f(8f, 0f, -4f), new Vector3f(1f, 2f, 1f)));
        scene.addObject(ModelUtils.createCuboidModel(new Vector3f(8f, 0f, -8f), new Vector3f(1f, 4f, 1f)));
        List<Vector3f> vertices1 = List.of(
                new Vector3f(0f, 0f, 0f),
                new Vector3f(2f, 0f, -1f),
                new Vector3f(0f, 2f, 0f),
                new Vector3f(2f, 2f, -1f)
        );
        List<Vector2i> edges1 = List.of(
                new Vector2i(0, 1),
                new Vector2i(1, 3),
                new Vector2i(3, 2),
                new Vector2i(2, 0)
        );
        List<List<Integer>> planes1 = List.of(
                List.of(0, 1, 3, 2)
        );
        Model3D model1 = new Model3D(vertices1, edges1, planes1);

        List<Vector3f> vertices2 = List.of(
                new Vector3f(1f, 0f, 0f),
                new Vector3f(3f, 0f, 0f),
                new Vector3f(1f, 3f, 0f),
                new Vector3f(3f, 3f, 0f)
        );
        List<Vector2i> edges2 = List.of(
                new Vector2i(0, 1),
                new Vector2i(1, 3),
                new Vector2i(3, 2),
                new Vector2i(2, 0)
        );
        List<List<Integer>> planes2 = List.of(
                List.of(2, 3, 1, 0)
        );
        Model3D model2 = new Model3D(vertices2, edges2, planes2);

//        scene.addObject(model1);
//        scene.addObject(model2);

        ApplicationWindow app = new ApplicationWindow(camera, scene);
        app.run();
    }
}
