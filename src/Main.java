
public class Main {
    public static void main(String[] args) {
        Echecs echecs = new Echecs();
        MF mf=new MF(echecs);
        mf.setVisible(true);
        echecs.addObserver(mf);
        echecs.maj();
        // long start = System.currentTimeMillis();
        // for (int i = 0; i < 100000000; i++)
        //     echecs.clone();
        // System.out.println(System.currentTimeMillis() - start);
        // start = System.currentTimeMillis();
        // for (int i = 0; i < 1000000; i++)
        //     echecs.evaluation();
        // System.out.println(System.currentTimeMillis() - start);
    }

}