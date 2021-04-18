public class Samm {
    int dx;
    int dy;
    boolean söömine = false;
    Nupp söödud= null;

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

    @Override
    public String toString() {
        return "Samm{" +
                "dx=" + dx +
                ", dy=" + dy +
                ", söömine=" + söömine +
                '}';
    }
}
