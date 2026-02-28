public class Tile {
    public final int q;
    public final int r;
    public boolean isSteppable;
    public boolean isConcealed;

    public Tile (int q, int r, boolean isSteppable){
        this.q = q;
        this.r = r;
        this.isSteppable = isSteppable;
        this.isConcealed = false;
    }
}