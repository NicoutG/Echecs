
public class Main {
    public static void main(String[] args) {
        Echecs echecs = new Echecs();
        MF mf=new MF(echecs);
        mf.setVisible(true);
        echecs.addObserver(mf);
        echecs.maj();
    }

}