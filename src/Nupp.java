import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Nupp extends StackPane {

    int x;
    int y;
    Värv värv;

    public Nupp(int x, int y, Värv värv) {

        this.x = x;
        this.y = y;
        this.värv = värv;

        Circle circle = new Circle();
        circle.setRadius(0.3 * Main.TILE_SIZE);
        circle.setFill(värv == Värv.VALGE ? Color.WHITE : Color.BLACK);
        relocate((x + 0.2) * Main.TILE_SIZE, (y + 0.2) * Main.TILE_SIZE);
        getChildren().add(circle);

        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);
    }

    public void onMouseReleased(MouseEvent e) {

        if (värv != Main.kelleKäik) {
            relocate((this.x + 0.2) * Main.TILE_SIZE, (this.y + 0.2) * Main.TILE_SIZE);
            return;
        }

        int newx = (int) (e.getSceneX() / Main.TILE_SIZE);
        int newy = (int) (e.getSceneY() / Main.TILE_SIZE);

        ArrayList<Samm> sammud = võimalikud_sammud(Main.nupud);
        System.out.println(sammud);
        int dx = newx - x;
        int dy = newy - y;
        boolean oliLegaalneSamm = false;
        Nupp söödud = null;
        for (Samm samm : sammud) {
            if (samm.dx == dx && samm.dy == dy) {
                oliLegaalneSamm = true;
                if (samm.söömine) söödud = samm.söödud;
                break;
            }
        }

        if (oliLegaalneSamm) {
            this.x = newx;
            this.y = newy;
            if (!sammud.get(0).söömine) {  // Ei olnud söömine, vahetame käiku
                Main.kelleKäik = Main.kelleKäik == Värv.VALGE ? Värv.MUST : Värv.VALGE;
            } else {  // Oli söömine, kustutame nupu ära
                Main.nupud.remove(söödud);
                Main.pieceGroup.getChildren().remove(söödud);
            }

            if ((this.y == 0 || this.y == 7) && !(this instanceof Tamm)) {
                Main.nupud.remove(this);
                Main.pieceGroup.getChildren().remove(this);
                Tamm tammena = new Tamm(this);
                Main.nupud.add(tammena);
                Main.pieceGroup.getChildren().add(tammena);
            }
        }

        relocate((this.x + 0.2) * Main.TILE_SIZE, (this.y + 0.2) * Main.TILE_SIZE);
    }


    public void onMouseDragged(MouseEvent e) {
        relocate(e.getSceneX() - 0.3 * Main.TILE_SIZE, e.getSceneY() - 0.3 * Main.TILE_SIZE);
    }

    public static Nupp[][] nupud_ruudustikku(ArrayList<Nupp> nupud) {
        Nupp[][] ruudustik = new Nupp[8][8];
        for (Nupp n : nupud) {
            ruudustik[n.y][n.x] = n;
        }
        return ruudustik;
    }

    public ArrayList<Samm> võimalikud_sammud(ArrayList<Nupp> nupud) {
        ArrayList<Samm> söömised = new ArrayList<>();
        ArrayList<Samm> astumised = new ArrayList<>();
        int suund = värv == Värv.VALGE ? -1 : 1;
        if (x > 0) {  // Juhul kui nupp ei asu vasakpoolses reas, proovime vasakule astda
            boolean onEesVasakul = false;
            boolean onKaugelEesVasakul = false;
            boolean onTagaVasakul = false;
            boolean onKaugelTagaVasakul = false;
            Värv eesOlevaNupuVärv = Värv.VALGE;
            Värv tagaOlevaNupuVärv = Värv.VALGE;
            Nupp eesVasakul = null;
            Nupp tagaVasakul = null;
            for (Nupp nupp : nupud) {  // Teeme selgeks, kas nupu vasakul naabruses asuvad söödavad nupud
                if (nupp.x == x - 1 && nupp.y == y + suund) {
                    onEesVasakul = true;
                    eesVasakul = nupp;
                    eesOlevaNupuVärv = nupp.värv;
                } else if (nupp.x == x - 2 && nupp.y == y + 2 * suund) {
                    onKaugelEesVasakul = true;
                } else if (nupp.x == x - 1 && nupp.y == y - suund) {
                    onTagaVasakul = true;
                    tagaVasakul = nupp;
                    tagaOlevaNupuVärv = nupp.värv;
                } else if (nupp.x == x - 2 && nupp.y == y - 2 * suund) {
                    onKaugelTagaVasakul = true;
                }
            }
            if (x > 1 && onEesVasakul && eesOlevaNupuVärv != värv && !onKaugelEesVasakul) {   // Juhul kui saame süüa kas ette v taha, siis lisame söömised
                söömised.add(new Samm(-2, 2 * suund, true, eesVasakul));
            } if (x > 1 && onTagaVasakul && tagaOlevaNupuVärv != värv && !onKaugelTagaVasakul) {
                söömised.add(new Samm(-2, -2 * suund, true, tagaVasakul));
            } if (!onEesVasakul) {
                astumised.add(new Samm(-1, suund));  // kui nuppu ei ole ees, siis lisame astumise
            }
        }
        if (x < 7) {  // kui nupp pole paremas ääres, käitume sarnaselt
            boolean onEesParemal = false;
            boolean onKaugelEesParemal = false;
            boolean onTagaParemal = false;
            boolean onKaugelTagaParemal = false;
            Värv eesOlevaNupuVärv = Värv.VALGE;
            Värv tagaOlevaNupuVärv = Värv.VALGE;
            Nupp eesParemal = null;
            Nupp tagaParemal = null;
            for (Nupp nupp : nupud) {
                if (nupp.x == x + 1 && nupp.y == y + suund) {
                    onEesParemal = true;
                    eesOlevaNupuVärv = nupp.värv;
                    eesParemal = nupp;
                } else if (nupp.x == x + 2 && nupp.y == y + 2 * suund) {
                    onKaugelEesParemal = true;
                } else if (nupp.x == x + 1 && nupp.y == y - suund) {
                    onTagaParemal = true;
                    tagaOlevaNupuVärv = nupp.värv;
                    tagaParemal = nupp;
                } else if (nupp.x == x + 2 && nupp.y == y - 2 * suund) {
                    onKaugelTagaParemal = true;
                }
            }
            if (x < 6 && onEesParemal && eesOlevaNupuVärv != värv && !onKaugelEesParemal) {
                söömised.add(new Samm(2, 2 * suund, true, eesParemal));
            } if (x < 6 && onTagaParemal && tagaOlevaNupuVärv != värv && !onKaugelTagaParemal) {
                söömised.add(new Samm(2, -2 * suund, true, tagaParemal));
            } if (!onEesParemal) {
                astumised.add(new Samm(1, suund));
            }
        }
        if (söömised.size() > 0) return söömised;  // kui mõni samm oli söömine, siis tagastame ainult söömised
        return astumised;  // vastaseljuhul tagastame astumised
    }

    public Värv getVärv() {
        return värv;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
