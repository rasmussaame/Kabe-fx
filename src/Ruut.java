import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Ruut extends Rectangle {
    public Ruut(boolean light, int x, int y){
        setWidth(Main.RUUDU_SUURUS);  // Määrame ruudu suurused
        setHeight(Main.RUUDU_SUURUS);
        relocate(x * Main.RUUDU_SUURUS, y * Main.RUUDU_SUURUS);  // Paigutame ruudu õigesse kohta
        setFill(light ? Color.valueOf("#c96553"): Color.valueOf("#693016"));  // Määra ruudu värvi õigeks
    }
}
