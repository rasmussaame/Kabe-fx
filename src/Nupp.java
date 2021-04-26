import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.*;
import java.util.ArrayList;

public class Nupp extends StackPane {

    private int x;
    private int y;
    private Värv värv;

    public Nupp(int x, int y, Värv värv) {

        this.x = x;
        this.y = y;
        this.värv = värv;

        Circle circle = new Circle();  // Lisame nupu värvi ringi StackPane
        circle.setRadius(0.3 * Main.RUUDU_SUURUS);
        circle.setFill(värv == Värv.VALGE ? Color.WHITE : Color.BLACK);
        relocate((x + 0.2) * Main.RUUDU_SUURUS, (y + 0.2) * Main.RUUDU_SUURUS);
        getChildren().add(circle);

        setOnMouseDragged(this::onMouseDragged);  // Lisame sündmused
        setOnMouseReleased(this::onMouseReleased);
    }

    public void onMouseReleased(MouseEvent e) {

        if (värv != Main.kelleKäik) {  // Juhul kui kasutaja üritas liigutada vastase nuppu
            relocate((this.x + 0.2) * Main.RUUDU_SUURUS, (this.y + 0.2) * Main.RUUDU_SUURUS);
            return;
        }

        ArrayList<Samm> sammud = new ArrayList<>();
        boolean saabSüüaMõnega = false;
        for (Node node : Main.pieceGroup.getChildren()) {  // Leiame kõikide mängija nuppude jaoks sammud
            Nupp nupp = (Nupp) node;
            if (nupp.värv != this.värv) continue;
            ArrayList<Samm> v_sammud = nupp.võimalikud_sammud(Nupp.nupu_list(Main.pieceGroup.getChildren()));
            if (v_sammud.size() > 0 && v_sammud.get(0).isSöömine())  // Märgime ära kas mõnega neist saab süüa.
                saabSüüaMõnega = true;
            if (nupp == this)  // Salvestame ka kasutaja liigutatud nupu sammud eraldi
                sammud = v_sammud;
        }

        if (saabSüüaMõnega && (sammud.size() == 0 || !sammud.get(0).isSöömine())) {  // Veendume et ühegi teise nupuga ei saaks süüa
            relocate((this.x + 0.2) * Main.RUUDU_SUURUS, (this.y + 0.2) * Main.RUUDU_SUURUS);
            return;
        }

        int newx = (int) (e.getSceneX() / Main.RUUDU_SUURUS);  // Leiame kuhu kasutaja astus
        int newy = (int) (e.getSceneY() / Main.RUUDU_SUURUS);

        int dx = newx - x;
        int dy = newy - y;
        boolean oliLegaalneSamm = false;
        Nupp söödud = null;
        for (Samm samm : sammud) {  // Käime sammud läbi, et selgitada, kas oli sobiv samm
            if (samm.getDx() == dx && samm.getDy() == dy) {
                oliLegaalneSamm = true;
                if (samm.isSöömine()) söödud = samm.getSöödud();  // Salvestame nupu, mille ta ära sõi, kui sõi
                break;
            }
        }

        if (oliLegaalneSamm) {  // Kui kasutaja tehtud samm on kooskõlas reeglitega
            try (FileWriter fw = new FileWriter(Main.failinimi, true);  // Kirjutame faili, et ta tegi sellise sammu
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(värv + " astus: (" + this.x + ", " + this.y + ") -> (" + newx + ", " + newy + ")\n");
            } catch (IOException fileNotFoundException) {
                throw new RuntimeException();  // Juhul kui faili ei ole olemas
            }

            this.x = newx;  // Uuendame nupu asukohta
            this.y = newy;
            if (sammud.get(0).isSöömine()) {  // Kui oli söömine, kustutame nupu
                Main.pieceGroup.getChildren().remove(söödud);

                sammud = võimalikud_sammud(nupu_list(Main.pieceGroup.getChildren()));
                if (sammud.size() == 0 || !sammud.get(0).isSöömine())  // Juhul kui kasutaja saab veel süüa selle nupuga, jätame käigu talle
                    Main.kelleKäik = Main.kelleKäik == Värv.VALGE ? Värv.MUST : Värv.VALGE;
            } else {
                Main.kelleKäik = Main.kelleKäik == Värv.VALGE ? Värv.MUST : Värv.VALGE;  // Muidu vahetame käiku
            }

            if ((this.y == 0 || this.y == 7) && !(this instanceof Tamm)) {  // Kui kasutaja astus viimasele reale ja nupp ei olnud juba Tamm
                Main.pieceGroup.getChildren().remove(this);  // Muudame nupu tammeks
                Tamm tammena = new Tamm(this);
                Main.pieceGroup.getChildren().add(tammena);
            }
        }

        relocate((this.x + 0.2) * Main.RUUDU_SUURUS, (this.y + 0.2) * Main.RUUDU_SUURUS);  // Paneme nupu ruudu keskele
    }

    public void onMouseDragged(MouseEvent e) {  // Kui kasutaja lohistab nuppu, siis liigutame nuppu hiirega kaasa
        relocate(e.getSceneX() - 0.3 * Main.RUUDU_SUURUS, e.getSceneY() - 0.3 * Main.RUUDU_SUURUS);
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

    public static Nupp[][] nupud_ruudustikku(ArrayList<Nupp> nupud) {  // Meetod, mis muudab nuppude listi kahemõõtmeliseks massiiviks
        Nupp[][] ruudustik = new Nupp[8][8];
        for (Nupp n : nupud) {
            ruudustik[n.y][n.x] = n;
        }
        return ruudustik;
    }

    public static ArrayList<Nupp> nupu_list(ObservableList<Node> lauaNupud) {  // Meetod, mis teisendab Groupis olevad Nupud ArrayListi
        ArrayList<Nupp> nupud = new ArrayList<>();
        for (Node node : lauaNupud)
            nupud.add((Nupp) node);
        return nupud;
    }
}
