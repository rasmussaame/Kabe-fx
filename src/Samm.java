public class Samm {

    private int dx;  // Kui palju astuti x-teljel.
    private int dy;  // Kui palju astuti y-teljel.
    private boolean söömine = false;  // Kas astumisel söödi ka mõni nupp ära
    private Nupp söödud= null;  // Astumisel söödud nupp

    Samm(int dx, int dy) {
        this.dx=dx;
        this.dy=dy;
    }

    Samm(int dx, int dy, boolean söömine, Nupp söödud) {
        this.dx=dx;
        this.dy=dy;
        this.söömine = söömine;
        this.söödud = söödud;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }


    public boolean isSöömine() {
        return söömine;
    }


    public Nupp getSöödud() {
        return söödud;
    }

    @Override
    public String toString() {
        return "Samm{" +
                "dx=" + dx +
                ", dy=" + dy +
                ", söömine=" + söömine +
                '}';
    }
}
