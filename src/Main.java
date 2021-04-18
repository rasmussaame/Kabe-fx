import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    public static Nupp clickedPiece = null;
    public static ArrayList<Nupp> nupud = null;
    public static Värv kelleKäik = Värv.VALGE;

    private Parent createContent() {

        Group tileGroup = new Group();
        Group pieceGroup = new Group();

        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE,HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup);

        nupud = new ArrayList<>();

        for (int y = 0; y < HEIGHT; y++) {
            for(int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                tileGroup.getChildren().add(tile);

                if (y < 3 && (x + y) % 2 == 1) {
                    Nupp nupp = new Nupp(x, y, Värv.MUST);
                    pieceGroup.getChildren().add(nupp);
                    nupud.add(nupp);
                }

                if (y > 4 && (x + y) % 2 == 1) {
                    Nupp nupp = new Nupp(x, y, Värv.VALGE);
                    pieceGroup.getChildren().add(nupp);
                    nupud.add(nupp);
                }
            }
        }
        return root;
    }



    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Kabe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
