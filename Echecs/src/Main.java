
public class Main {
    public static void main(String[] args) {
        Plateau plateau=new Plateau ();
        MF mf=new MF(plateau);
        mf.setVisible(true);
        plateau.addObserver(mf);
        plateau.maj();
    }

}