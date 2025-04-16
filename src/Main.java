
public class Main {
    // public static void main(String[] args) {
    //     Echecs echecs = new Echecs();
    //     MF mf=new MF(echecs);
    //     mf.setVisible(true);
    //     echecs.addObserver(mf);
    //     echecs.maj();
    // }

    public static void main(String[] args) {
        Joueur joueur1 = new AlphaBetaTime5(2000);
        Joueur joueur2 = new AlphaBetaTime5(10000);
        int nbParties = 12;
        GameTesteur.Test(joueur1,joueur2,nbParties,true);
        // Echecs echecs = new Echecs();
        // int n = 200362;
        // long start = System.currentTimeMillis();
        // for (int i = 0; i < n; i++) {
        //     echecs.evaluation();
        // }
        // long end = System.currentTimeMillis();
        // System.out.println(end - start);
    }

}