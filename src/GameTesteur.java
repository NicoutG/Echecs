import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GameTesteur {

    
    public static void Test (Joueur joueur1, Joueur joueur2, int nbParties, boolean ... aff) {
        boolean print = aff != null && aff.length > 0 && aff[0];
        ExecutorService executor = Executors.newFixedThreadPool(4);
        AtomicInteger nbVictoires1 = new AtomicInteger(0);
        AtomicInteger nbVictoires2 = new AtomicInteger(0);
        AtomicInteger nbEgalite = new AtomicInteger(0);
        AtomicInteger nbCoupsParPartie = new AtomicInteger(0);
        AtomicInteger duration1 = new AtomicInteger(0);
        AtomicInteger duration2 = new AtomicInteger(0);
        for (int i = 0; i < nbParties; i++) {
            final int ite = i;
            executor.execute(() -> {
                if (print)
                    System.out.println("Partie " + (ite + 1) + "/" + nbParties + " en cours");
                Joueur blanc = joueur1;
                Joueur noir = joueur2;
                boolean joueur1Blanc = ite % 2 == 0;
                if (!joueur1Blanc) {
                    blanc = joueur2;
                    noir = joueur1;
                }
                Echecs echecs = new Echecs();
                long start = System.currentTimeMillis();
                int nbCoups = 0;
                int durationBlanc = 0;
                int durationNoir = 0;
                do {
                    long end;
                    if (nbCoups % 2 == 0) {
                        blanc.jouer(echecs);
                        end = System.currentTimeMillis();
                        durationBlanc += (int)(end - start);
                    }
                    else {
                        noir.jouer(echecs);
                        end = System.currentTimeMillis();
                        durationNoir += (int)(end - start);
                    }
                    start = end;
                    nbCoups++;
                }while(echecs.getVictoire() == 0);
                nbCoupsParPartie.addAndGet(nbCoups);

                int victoire = echecs.getVictoire();
                boolean victoire1 = (joueur1Blanc && victoire == 1) || (!joueur1Blanc && victoire == 2);
                boolean victoire2 = (!joueur1Blanc && victoire == 1) || (joueur1Blanc && victoire == 2);

                if (joueur1Blanc) {
                    duration1.addAndGet(durationBlanc);
                    duration2.addAndGet(durationNoir);
                }
                else {
                    duration2.addAndGet(durationBlanc);
                    duration1.addAndGet(durationNoir);
                }

                if (victoire1)
                    nbVictoires1.addAndGet(1);
                else {
                    if (victoire2)
                        nbVictoires2.addAndGet(1);
                    else
                        nbEgalite.addAndGet(1);
                }

                if (print)
                    System.out.println("Partie " + (ite + 1) + "/" + nbParties + " terminée " + (victoire1 ? 1 : 0) + "-" + (victoire2 ? 1 : 0));
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(24, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Nombre de parties : " + nbParties);
        System.out.println("Joueur1 : " + joueur1.getClass().getSimpleName());
        System.out.println("Joueur2 : " + joueur2.getClass().getSimpleName());
        System.out.println("Nombre de parties : " + nbParties);
        System.out.println("Victoires Joueur1 : " + (100.0 * nbVictoires1.get() / nbParties) + "%");
        System.out.println("Victoires Joueur2 : " + (100.0 * nbVictoires2.get() / nbParties) + "%");
        System.out.println("Egalite : " + (100.0 * nbEgalite.get() / nbParties) + "%");
        System.out.println("Nombre de coups par partie : " + (1.0 * nbCoupsParPartie.get() / nbParties));
        System.out.println("Durée d'un coup Joueur1 : " + (2.0 * duration1.get() / nbCoupsParPartie.get()) + " ms");
        System.out.println("Durée d'un coup Joueur2 : " + (2.0 * duration2.get() / nbCoupsParPartie.get()) + " ms");
    }

}
