
public class Evaluation {
    private static double valPion=10;
    private static double valCavalier=30;
    private static double valFou=32;
    private static double valTour=50;
    private static double valReine=90;
    private static double valRoi = 100;

    private static double[] pionsTableDeb =
        {  0,   0,   0,   0,   0,   0,   0,   0 ,
          50,  50,  50,  50,  50,  50,  50,  50 ,
          10,  10,  20,  30,  30,  20,  10,  10 ,
           5,   5,  10,  25,  25,  10,   5,   5 ,
           0,   0,   0,  20,  20,   0,   0,   0 ,
           5,  -5, -10,   0,   0, -10,  -5,   5 ,
           5,  10,  10, -20, -20,  10,  10,   5 ,
           0,   0,   0,   0,   0,   0,   0,   0 };

    private static double[] pionsTableFin =
        {  0,   0,   0,   0,   0,   0,   0,   0 ,
          50,  50,  50,  50,  50,  50,  50,  50 ,
          30,  30,  30,  30,  30,  30,  30,  30 ,
          10,  10,  20,  30,  30,  20,  10,  10 ,
           5,   5,  10,  25,  25,  10,   5,   5 ,
           0,   0,   0,  20,  20,   0,   0,   0 ,
           0,   0,   0,   0,   0,   0,   0,   0 ,
           0,   0,   0,   0,   0,   0,   0,   0 };

    private static double[] cavaliersTableDeb =
        { -50, -40, -30, -30, -30, -30, -40, -50 ,
          -40, -20,   0,   0,   0,   0, -20, -40 ,
          -30,   0,  10,  15,  15,  10,   0, -30 ,
          -30,   5,  15,  20,  20,  15,   5, -30 ,
          -30,   0,  15,  20,  20,  15,   0, -30 ,
          -30,   5,  10,  15,  15,  10,   5, -30 ,
          -40, -20,   0,   5,   5,   0, -20, -40 ,
          -50, -40, -30, -30, -30, -30, -40, -50 };

    private static double[] cavaliersTableFin =
        { -50, -30, -20, -20, -20, -20, -30, -50 ,
          -30, -10,   0,   5,   5,   0, -10, -30 ,
          -20,   0,  10,  15,  15,  10,   0, -20 ,
          -20,   5,  15,  20,  20,  15,   5, -20 ,
          -20,   0,  15,  20,  20,  15,   0, -20 ,
          -20,   5,  10,  15,  15,  10,   5, -20 ,
          -30, -10,   0,   5,   5,   0, -10, -30 ,
          -50, -30, -20, -20, -20, -20, -30, -50 };

    private static double[] fousTableDeb =
        { -20, -10, -10, -10, -10, -10, -10, -20 ,
          -10,   5,   0,   0,   0,   0,   5, -10 ,
          -10,  10,  10,  10,  10,  10,  10, -10 ,
          -10,   0,  10,  10,  10,  10,   0, -10 ,
          -10,   5,   5,  10,  10,   5,   5, -10 ,
          -10,   0,   5,  10,  10,   5,   0, -10 ,
          -10,   0,   0,   0,   0,   0,   0, -10 ,
          -20, -10, -10, -10, -10, -10, -10, -20 };

    private static double[] fousTableFin =
        { -10, -10, -10, -10, -10, -10, -10, -10 ,
          -10,  10,   0,   0,   0,   0,  10, -10 ,
          -10,   0,  10,  10,  10,  10,   0, -10 ,
          -10,   5,  10,  20,  20,  10,   5, -10 ,
          -10,   5,  10,  20,  20,  10,   5, -10 ,
          -10,   0,  10,  10,  10,  10,   0, -10 ,
          -10,  10,   0,   0,   0,   0,  10, -10 ,
          -10, -10, -10, -10, -10, -10, -10, -10 };

    private static double[] toursTableDeb =
        {  0,   0,   0,   5,   5,   0,   0,   0 ,
          -5,   0,   0,   0,   0,   0,   0,  -5 ,
          -5,   0,   0,   0,   0,   0,   0,  -5 ,
          -5,   0,   0,   0,   0,   0,   0,  -5 ,
          -5,   0,   0,   0,   0,   0,   0,  -5 ,
          -5,   0,   0,   0,   0,   0,   0,  -5 ,
           5,  10,  10,  10,  10,  10,  10,   5 ,
           0,   0,   0,   0,   0,   0,   0,   0 };

    private static double[] toursTableFin =
        {  5,  10,  10,  15,  15,  10,  10,   5 ,
           5,  10,  10,  15,  15,  10,  10,   5 ,
           5,  10,  10,  15,  15,  10,  10,   5 ,
           5,  10,  10,  15,  15,  10,  10,   5 ,
           5,  10,  10,  15,  15,  10,  10,   5 ,
           5,  10,  10,  15,  15,  10,  10,   5 ,
          10,  10,  15,  20,  20,  15,  10,  10 ,
           5,   5,   5,  10,  10,   5,   5,   5 };
    
    private static double[] reinesTableDeb =
        { -20, -10, -10,  -5,  -5, -10, -10, -20 ,
          -10,   0,   0,   0,   0,   0,   0, -10 ,
          -10,   0,   5,   5,   5,   5,   0, -10 ,
           -5,   0,   5,   5,   5,   5,   0,  -5 ,
            0,   0,   5,   5,   5,   5,   0,  -5 ,
          -10,   5,   5,   5,   5,   5,   0, -10 ,
          -10,   0,   5,   0,   0,   0,   0, -10 ,
          -20, -10, -10,  -5,  -5, -10, -10, -20 };

    private static double[] reinesTableFin =
        { -10,  -5,  -5,   0,   0,  -5,  -5, -10 ,
           -5,   0,   0,   5,   5,   0,   0,  -5 ,
           -5,   0,   5,   5,   5,   5,   0,  -5 ,
            0,   0,   5,   5,   5,   5,   0,   0 ,
            0,   0,   5,   5,   5,   5,   0,   0 ,
           -5,   0,   5,   5,   5,   5,   0,  -5 ,
           -5,   0,   0,   5,   5,   0,   0,  -5 ,
          -10,  -5,  -5,   0,   0,  -5,  -5, -10 };

    private static double[] roisTableDeb =
        { -30, -40, -40, -50, -50, -40, -40, -30 ,
          -30, -40, -40, -50, -50, -40, -40, -30 ,
          -30, -40, -40, -50, -50, -40, -40, -30 ,
          -30, -40, -40, -50, -50, -40, -40, -30 ,
          -20, -30, -30, -40, -40, -30, -30, -20 ,
          -10, -20, -20, -20, -20, -20, -20, -10 ,
           20,  20,   0,   0,   0,   0,  20,  20 ,
           20,  30,  10,   0,   0,  10,  30,  20 };

    private static double[] roisTableFin =
        { -50, -40, -30, -20, -20, -30, -40, -50 ,
          -30, -20, -10,   0,   0, -10, -20, -30 ,
          -30, -10,  20,  30,  30,  20, -10, -30 ,
          -30, -10,  30,  40,  40,  30, -10, -30 ,
          -30, -10,  30,  40,  40,  30, -10, -30 ,
          -30, -10,  20,  30,  30,  20, -10, -30 ,
          -30, -30,   0,   0,   0,   0, -30, -30 ,
          -50, -30, -30, -30, -30, -30, -30, -50 };

    public static double evaluation (long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois) {
        long pionsBlancs = pions & blancs;
        long cavaliersBlancs = cavaliers & blancs;
        long fousBlancs = fous & blancs;
        long toursBlancs = tours & blancs;
        long reinesBlancs = reines & blancs;
        long roisBlancs = rois & blancs;

        double materielBlancs = evaluationPiecesCouleur(pionsBlancs, cavaliersBlancs, fousBlancs, toursBlancs, reinesBlancs);

        long pionsNoirs = pions & noirs;
        long cavaliersNoirs = cavaliers & noirs;
        long fousNoirs = fous & noirs;
        long toursNoirs = tours & noirs;
        long reinesNoirs = reines & noirs;
        long roisNoirs = rois & noirs;
        
        double materielNoirs = evaluationPiecesCouleur(pionsNoirs, cavaliersNoirs, fousNoirs, toursNoirs, reinesNoirs);
        
        double avance = (materielBlancs + materielNoirs) / evaluationMaterielDepart();
        double positionsBlancs = evaluationPositions(pionsBlancs, cavaliersBlancs, fousBlancs, toursBlancs, reinesBlancs, roisBlancs, true, avance);
        double positionsNoirs = evaluationPositions(pionsNoirs, cavaliersNoirs, fousNoirs, toursNoirs, reinesNoirs, roisNoirs, false, avance);
    
        double val = materielBlancs - materielNoirs + 0.1 * (positionsBlancs - positionsNoirs);

        return val;
    }

    public static double evaluationMaterielDepart() {
        return 16 * valPion + 4 * valCavalier + 4 * valFou + 4 * valTour + 2 * valReine;
    }

    public static double evaluationPiecesCouleur(long pions, long cavaliers, long fous, long tours, long reines) {
        double materiel = 0;
        int nbPionsCouleur = BitBoardEchecs.countPieces(pions);
        materiel += nbPionsCouleur * valPion;
        int nbCavaliersCouleur = BitBoardEchecs.countPieces(cavaliers);
        materiel += nbCavaliersCouleur * valCavalier;
        int nbFousCouleur = BitBoardEchecs.countPieces(fous);
        materiel += nbFousCouleur* valFou;
        int nbToursCouleur = BitBoardEchecs.countPieces(tours);
        materiel += nbToursCouleur * valTour;
        int nbReinesCouleur = BitBoardEchecs.countPieces(reines);
        materiel += nbReinesCouleur * valReine;
        return materiel;
    }

    public static double evaluationPositions(long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleur, double avance) {
        long valPositions = 0;
        int[] positions = BitBoardEchecs.getPositions(pions);
        for (int pos : positions)
            valPositions += evaluationPosition(pos, couleur ? 1 : 7, avance);
        positions = BitBoardEchecs.getPositions(cavaliers);
        for (int pos : positions)
            valPositions += evaluationPosition(pos, couleur ? 2 : 8, avance);
        positions = BitBoardEchecs.getPositions(fous);
        for (int pos : positions)
            valPositions += evaluationPosition(pos, couleur ? 3 : 9, avance);
        positions = BitBoardEchecs.getPositions(tours);
        for (int pos : positions)
            valPositions += evaluationPosition(pos, couleur ? 4 : 10, avance);
        positions = BitBoardEchecs.getPositions(reines);
        for (int pos : positions)
            valPositions += evaluationPosition(pos, couleur ? 5 : 11, avance);
        positions = BitBoardEchecs.getPositions(rois);
        for (int pos : positions)
            valPositions += evaluationPosition(pos, couleur ? 6 : 12, avance);
        return valPositions;
    }

    public static double evaluationPosition(int pos, int type, double avance) {
        switch (type) {
            case 1: return avance * pionsTableDeb[pos] + (1 - avance) * pionsTableFin[pos];
            case 2: return avance * cavaliersTableDeb[pos] + (1 - avance) * cavaliersTableFin[pos];
            case 3: return avance * fousTableDeb[pos] + (1 - avance) * fousTableFin[pos];
            case 4: return avance * toursTableDeb[pos] + (1 - avance) * toursTableFin[pos];
            case 5: return avance * reinesTableDeb[pos] + (1 - avance) * reinesTableFin[pos];
            case 6: return avance * roisTableDeb[pos] + (1 - avance) * roisTableFin[pos];
            case 7: return avance * pionsTableDeb[63 - pos] + (1 - avance) * pionsTableFin[63 - pos];
            case 8: return avance * cavaliersTableDeb[63 - pos] + (1 - avance) * cavaliersTableFin[63 - pos];
            case 9: return avance * fousTableDeb[63 - pos] + (1 - avance) * fousTableFin[63 - pos];
            case 10: return avance * toursTableDeb[63 - pos] + (1 - avance) * toursTableFin[63 - pos];
            case 11: return avance * reinesTableDeb[63 - pos] + (1 - avance) * reinesTableFin[63 - pos];
            case 12: return avance * roisTableDeb[63 - pos] + (1 - avance) * roisTableFin[63 - pos];
        }
        return 0;
    }

    public static double evaluationCoup(int pos, int dep, int[][] plateau) {
        if (plateau[dep%8][dep/8] == 0)
            return 0;
        
        double valPos = 0;
        switch ((plateau[pos%8][pos/8]-1)%6) {
            case 0: valPos = valPion;break;
            case 1: valPos = valCavalier;break;
            case 2: valPos = valFou;break;
            case 3: valPos = valTour;break;
            case 4: valPos = valReine;break;
            case 5: valPos = valRoi;break;
        }

        double valDep = 0;
        switch ((plateau[dep%8][dep/8]-1)%6) {
            case 0: valDep = 10 * valPion;break;
            case 1: valDep = 10 * valCavalier;break;
            case 2: valDep = 10 * valFou;break;
            case 3: valDep = 10 * valTour;break;
            case 4: valDep = 10 * valReine;break;
            case 5: valDep = 10 * valRoi;break;
        }

        return valDep-valPos;
    }

    public static double getAvancement(long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois) {
        long pionsBlancs = pions & blancs;
        long cavaliersBlancs = cavaliers & blancs;
        long fousBlancs = fous & blancs;
        long toursBlancs = tours & blancs;
        long reinesBlancs = reines & blancs;

        double materielBlancs = evaluationPiecesCouleur(pionsBlancs, cavaliersBlancs, fousBlancs, toursBlancs, reinesBlancs);

        long pionsNoirs = pions & noirs;
        long cavaliersNoirs = cavaliers & noirs;
        long fousNoirs = fous & noirs;
        long toursNoirs = tours & noirs;
        long reinesNoirs = reines & noirs;
        
        double materielNoirs = evaluationPiecesCouleur(pionsNoirs, cavaliersNoirs, fousNoirs, toursNoirs, reinesNoirs);

        double materielDepart = evaluationMaterielDepart();

        return 1 - (materielBlancs + materielNoirs) / materielDepart;
    }
}
