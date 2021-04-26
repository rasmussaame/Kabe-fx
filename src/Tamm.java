import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Tamm extends Nupp {

    Tamm(Nupp nupp) {
        super(nupp.getX(), nupp.getY(), nupp.getVärv());

        Circle circle = new Circle();  // Joonistame nupu peale 2 ringi, et märkida, et tegemist on tammega.
        circle.setRadius(0.2 * Main.RUUDU_SUURUS);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.RED);
        circle.setStrokeWidth(2.0);

        Circle circle2 = new Circle();
        circle2.setRadius(0.1 * Main.RUUDU_SUURUS);
        circle2.setFill(Color.TRANSPARENT);
        circle2.setStroke(Color.RED);
        circle2.setStrokeWidth(2.0);

        getChildren().addAll(circle, circle2);
    }

    @Override
    public ArrayList<Samm> võimalikud_sammud(ArrayList<Nupp> nupud) {
        ArrayList<Samm> söömised = new ArrayList<>();
        ArrayList<Samm> astumised = new ArrayList<>();

        Nupp[][] ruudustik = Nupp.nupud_ruudustikku(nupud);
        // Vaatame läbi kõik neli diagonaali kuhu suunas tamm saaks liikuda.
        for (int uusx = getX() + 1, uusy = getY() + 1; uusx < 8 && uusy < 8; uusx++, uusy++) {
            if (ruudustik[uusy][uusx] == null) {  // Lisame astumisi seni kui mõni nupp jääb ette
                astumised.add(new Samm(uusx - getX(), uusy - getY()));
            } else {
                if (ruudustik[uusy][uusx].getVärv() != getVärv()) {  // Juhul kui ta on vastane, siis lisame kõik sammud, mis teda söövad
                    Nupp söödav = null;
                    for (int i = 0; i < nupud.size(); i++) {
                        if (nupud.get(i).getX() == uusx && nupud.get(i).getY() == uusy) {
                            söödav = nupud.get(i);
                            break;
                        }
                    }
                    for (int vabax = uusx + 1, vabay = uusy + 1; vabax < 8 && vabay < 8; vabay++, vabax++) {
                        if (ruudustik[vabay][vabax] == null) {
                            söömised.add(new Samm(vabax - getX(), vabay - getY(), true, söödav));
                        }
                        else break;
                    }
                }
                break;
            }
        }  // Sama teeme kõikidel teistel diagonaalidel ka
        for (int uusx = getX() + 1, uusy = getY() - 1; uusx < 8 && uusy >= 0; uusx++, uusy--) {
            if (ruudustik[uusy][uusx] == null) {  // Saame astuda
                astumised.add(new Samm(uusx - getX(), uusy - getY()));
            } else {
                if (ruudustik[uusy][uusx].getVärv() != getVärv()) {
                    Nupp söödav = null;
                    for (int i = 0; i < nupud.size(); i++) {
                        if (nupud.get(i).getX() == uusx && nupud.get(i).getY() == uusy) {
                            söödav = nupud.get(i);
                            break;
                        }
                    }
                    for (int vabax = uusx + 1, vabay = uusy - 1; vabax < 8 && vabay >= 0; vabay--, vabax++) {
                        if (ruudustik[vabay][vabax] == null) {
                            söömised.add(new Samm(vabax - getX(), vabay - getY(), true, söödav));
                        }
                        else break;
                    }
                }
                break;
            }
        }
        for (int uusx = getX() - 1, uusy = getY() - 1; uusx >= 0 && uusy >= 0; uusx--, uusy--) {
            if (ruudustik[uusy][uusx] == null) {  // Saame astuda
                astumised.add(new Samm(uusx - getX(), uusy - getY()));
            } else {
                if (ruudustik[uusy][uusx].getVärv() != getVärv()) {
                    Nupp söödav = null;
                    for (int i = 0; i < nupud.size(); i++) {
                        if (nupud.get(i).getX() == uusx && nupud.get(i).getY() == uusy) {
                            söödav = nupud.get(i);
                            break;
                        }
                    }
                    for (int vabax = uusx - 1, vabay = uusy - 1; vabax >= 0 && vabay >= 0; vabay--, vabax--) {
                        if (ruudustik[vabay][vabax] == null) {
                            söömised.add(new Samm(vabax - getX(), vabay - getY(), true, söödav));
                        }
                        else break;
                    }
                }
                break;
            }
        }

        for (int uusx = getX() - 1, uusy = getY() + 1; uusx >= 0 && uusy < 8; uusx--, uusy++) {
            if (ruudustik[uusy][uusx] == null) {  // Saame astuda
                astumised.add(new Samm(uusx - getX(), uusy - getY()));
            } else {
                if (ruudustik[uusy][uusx].getVärv() != getVärv()) {
                    Nupp söödav = null;
                    for (int i = 0; i < nupud.size(); i++) {
                        if (nupud.get(i).getX() == uusx && nupud.get(i).getY() == uusy) {
                            söödav = nupud.get(i);
                            break;
                        }
                    }
                    for (int vabax = uusx - 1, vabay = uusy + 1; vabax >= 0 && vabay < 8; vabay++, vabax--) {
                        if (ruudustik[vabay][vabax] == null) {
                            söömised.add(new Samm(vabax - getX(), vabay - getY(), true, söödav));
                        } else break;
                    }
                }
                break;
            }
        }
        return söömised.size() > 0 ? söömised : astumised;
    }
}
