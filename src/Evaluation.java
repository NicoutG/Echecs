public class Evaluation {
    private static int valPion=10;
    private static int valCavalier=30;
    private static int valFou=30;
    private static int valTour=50;
    private static int valReine=90;

    public static int evaluation (long blancs, long noirs, long pions, long cavaliers, long fous, long tours, long reines) {
        int res=0;
        int nbPionsBlans = BitBoardEchecs.countPieces(pions & blancs);
        int nbPionsNoirs = BitBoardEchecs.countPieces(pions & noirs);
        res += (nbPionsBlans - nbPionsNoirs) * valPion;
        int nbCavaliersBlans = BitBoardEchecs.countPieces(cavaliers & blancs);
        int nbCavaliersNoirs = BitBoardEchecs.countPieces(cavaliers & noirs);
        res += (nbCavaliersBlans - nbCavaliersNoirs) * valCavalier;
        int nbFousBlans = BitBoardEchecs.countPieces(fous & blancs);
        int nbFousNoirs = BitBoardEchecs.countPieces(fous & noirs);
        res += (nbFousBlans - nbFousNoirs) * valFou;
        int nbToursBlans = BitBoardEchecs.countPieces(tours & blancs);
        int nbToursNoirs = BitBoardEchecs.countPieces(tours & noirs);
        res += (nbToursBlans - nbToursNoirs) * valTour;
        int nbReinesBlans = BitBoardEchecs.countPieces(reines & blancs);
        int nbReinesNoirs = BitBoardEchecs.countPieces(reines & noirs);
        res += (nbReinesBlans - nbReinesNoirs) * valReine;
        return res;
    }
}
