import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int RUUDU_SUURUS = 100;  // Mitu pikslit on üks ruut

    public static Värv kelleKäik = Värv.VALGE;  // Mängija, kelle käik on hetkel

    public static Group tileGroup = new Group();  // Ruutude jaoks konteiner
    public static Group pieceGroup = new Group();  // Nuppude jaoks konteiner

    public static String failinimi = null;   // Fail, kuhu kirjutame mängu käigud

    private Parent createContent() {

        Pane root = new Pane();  // Loome uue stseeni
        root.setPrefSize(8 * RUUDU_SUURUS,8 * RUUDU_SUURUS);
        root.getChildren().addAll(tileGroup, pieceGroup);

        for (int y = 0; y < 8; y++) {  // Lisame kõik ruuudud
            for(int x = 0; x < 8; x++) {
                Ruut ruut = new Ruut((x + y) % 2 == 0, x, y);
                tileGroup.getChildren().add(ruut);

                if (y < 3 && (x + y) % 2 == 1) {  // Kui tegemist on musta ruuduga ning ta asub ekraani üleval, siis lisame sinna musta nupu
                    Nupp nupp = new Nupp(x, y, Värv.MUST);
                    pieceGroup.getChildren().add(nupp);
                }

                if (y > 4 && (x + y) % 2 == 1) {  // Kui tegemist on musta ruuduga ning ta asub ekraani all, siis lisame sinna valge nupu
                    Nupp nupp = new Nupp(x, y, Värv.VALGE);
                    pieceGroup.getChildren().add(nupp);
                }
            }
        }
        return root;
    }

    @Override
    public void start(Stage primaryStage) {
        failinimi = "mängud/" + System.currentTimeMillis() + ".txt";  // Loome failinime, kuhu hakkame käike salvestama
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Kabe");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);  // Ei lase kasutajal ekraani suurust muuta
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
