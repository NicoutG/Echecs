import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

public class Echecs extends Observable {
    private boolean tour;
    private int victoire;
    private int ordre;
    private int casePos;
    private int caseDep;
    private long deps;

    private long blancs;
    private long noirs;
    private long speciaux;
    private long pions;
    private long cavaliers;
    private long fous;
    private long tours;
    private long reines;
    private long rois;

    private HashMap<Long, Integer> coupsPrecedents;
    private int nbCoupsNuls;

    public Echecs(Echecs echecs) {
        tour = echecs.tour;
        victoire = echecs.victoire;
        ordre = echecs.ordre;
        casePos = echecs.casePos;
        caseDep = echecs.caseDep;
        deps = echecs.deps;
        blancs = echecs.blancs;
        noirs = echecs.noirs;
        speciaux = echecs.speciaux;
        pions = echecs.pions;
        cavaliers = echecs.cavaliers;
        fous = echecs.fous;
        tours = echecs.tours;
        reines = echecs.reines;
        rois = echecs.rois;
        coupsPrecedents = (HashMap<Long,Integer>)(echecs.coupsPrecedents).clone();
        nbCoupsNuls = echecs.nbCoupsNuls;
    }

    public Echecs() {
        init();
    }

    public void init() {
        tour = true;
        victoire = 0;
        ordre = 0;
        casePos = -1;
        caseDep = -1;
        deps = 0;
        blancs = 0x000000000000FFFFL;
        noirs = 0xFFFF000000000000L;
        pions = 0x00FF00000000FF00L;
        cavaliers = 0x4200000000000042L;
        fous = 0x2400000000000024L;
        tours = 0x8100000000000081L;
        reines = 0x1000000000000010L;
        rois = 0x0800000000000008L;
        speciaux = rois | tours;
        coupsPrecedents = new HashMap<>();
        nbCoupsNuls = 0;
    }

    public Echecs clone() {
        return new Echecs(this);
    }

    public int getVictoire() {
        return victoire;
    }

    public boolean getTour() {
        return tour;
    }

    public int getOrdre() {
        return ordre;
    }

    public int getCasePos() {
        return casePos;
    }

    public int getCaseDep() {
        return caseDep;
    }

    public ArrayList<int[]> getAllDepPossibles() {
        ArrayList<int[]> deps = new ArrayList<>();

        int[] positions;
        if (tour)
            positions = BitBoardEchecs.getPositions(blancs);
        else
            positions = BitBoardEchecs.getPositions(noirs);
        
        for (int position : positions) {
            int[] depsPosition = getDepPossibles(position);
            for (int dep : depsPosition)
                deps.add(new int[] {position, dep});
        }
        return deps;
    }

    public int[] getDepPossibles(int pos) {
        return BitBoardEchecs.getPositions(BitBoardEchecs.getCorrectDep(pos, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, tour));
    }

    public int[] getDepPossibles() {
        return BitBoardEchecs.getPositions(deps);
    }

    public boolean action (int nb) {
        if (victoire==0) {
            switch (ordre) {
                case 0: {
                    long bitboard = BitBoardEchecs.toBitBoard(nb);
                    if ((tour && ((bitboard & blancs) == 0)) || (!tour && ((bitboard & noirs) == 0)))
                        return false;
                    casePos = nb;
                    deps = BitBoardEchecs.getCorrectDep(casePos, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, tour);
                    ordre++;
                }break;
                case 1: {
                    long bitboard = BitBoardEchecs.toBitBoard(nb);
                    if ((bitboard & deps) == 0) {
                        if ((tour && (bitboard & blancs) != 0) || (!tour && (bitboard & noirs) != 0)) {
                            ordre--;
                            action(nb);
                        }
                        return false;
                    }
                    caseDep = nb;
                    deplacerPieces(casePos, caseDep);
                    if(BitBoardEchecs.possiblePromotion(pions))
                        ordre++;
                    else
                        nouveauTour();
                }break;
                case 2: {
                    promotion(nb);
                    nouveauTour();
                }break;
                default: return false;
            }
            maj();
            return true;
        }
        return false;
    }

    public void promotion(int nb) {
        switch (nb) {
            case 64: {
                long[] afterPromotion = BitBoardEchecs.promotion(caseDep, pions, cavaliers);
                pions = afterPromotion[0];
                cavaliers = afterPromotion[1];
            };break;
            case 65: {
                long[] afterPromotion = BitBoardEchecs.promotion(caseDep, pions, fous);
                pions = afterPromotion[0];
                fous = afterPromotion[1];
            };break;
            case 66: {
                long[] afterPromotion = BitBoardEchecs.promotion(caseDep, pions, tours);
                pions = afterPromotion[0];
                tours = afterPromotion[1];
            };break;
            case 67: {
                long[] afterPromotion = BitBoardEchecs.promotion(caseDep, pions, reines);
                pions = afterPromotion[0];
                reines = afterPromotion[1];
            };break;
        }
    }
    
    public void deplacerPieces(int pos, int dep) {
        long[] afterMove = BitBoardEchecs.move(pos,dep,blancs,noirs,speciaux,pions,cavaliers,fous,tours,reines,rois,tour);

        if (pions != afterMove[3] || BitBoardEchecs.countPieces(blancs) != BitBoardEchecs.countPieces(afterMove[0]) || BitBoardEchecs.countPieces(noirs) != BitBoardEchecs.countPieces(afterMove[1]))
            nbCoupsNuls = -1;
            
        blancs = afterMove[0];
        noirs = afterMove[1];
        speciaux = afterMove[2];
        pions = afterMove[3];
        cavaliers = afterMove[4];
        fous = afterMove[5];
        tours = afterMove[6];
        reines = afterMove[7];
        rois = afterMove[8];
    }

    private void nouveauTour () {   
        ordre = 0;
        tour = !tour;

        nbCoupsNuls++;
        if (nbCoupsNuls >= 50)
            victoire = 3;

        long hash = genererHash();
        int nbRepet = 0;
        if (coupsPrecedents.containsKey(hash)) {
            nbRepet = coupsPrecedents.get(hash);
            if (nbRepet >= 2)
                victoire = 3;
        }

        coupsPrecedents.put(hash, nbRepet + 1);

        if (getDeplacementPossible()) {
            if (BitBoardEchecs.IsEchec(blancs, noirs, pions, cavaliers, fous, tours, reines, rois, tour))
                victoire = tour ? 2 : 1;
            else
                victoire = 3;
        }
            
    }

    private boolean getDeplacementPossible() {
        long piecesJoueur;
        if (tour)
            piecesJoueur = blancs;
        else
            piecesJoueur = noirs;
        int[] positions = BitBoardEchecs.getPositions(piecesJoueur);
        for (int pos : positions) {
            long depsPos = BitBoardEchecs.getCorrectDep(pos, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, tour);
            if (depsPos != 0)
                return false;
        }
        return true;
    }

    public void maj () {
        setChanged();
        notifyObservers();
    }

    public double evaluation() {
        if (victoire == 0)
            return Evaluation.evaluation(blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois);
        if (victoire == 1)
            return 1000;
        if (victoire == 2)
            return -1000;
        return 0;
    }

    public int[][] getPlateau() {
        int[][] plateau = new int[8][8];
        long pionsBlancs = pions & blancs;
        for (int pos : BitBoardEchecs.getPositions(pionsBlancs))
            plateau[pos%8][pos/8] = 1;
        long cavaliersBlancs = cavaliers & blancs;
        for (int pos : BitBoardEchecs.getPositions(cavaliersBlancs))
            plateau[pos%8][pos/8] = 2;
        long fousBlancs = fous & blancs;
        for (int pos : BitBoardEchecs.getPositions(fousBlancs))
            plateau[pos%8][pos/8] = 3;
        long toursBlancs = tours & blancs;
        for (int pos : BitBoardEchecs.getPositions(toursBlancs))
            plateau[pos%8][pos/8] = 4;
        long reinesBlancs = reines & blancs;
        for (int pos : BitBoardEchecs.getPositions(reinesBlancs))
            plateau[pos%8][pos/8] = 5;
        long roisBlancs = rois & blancs;
        for (int pos : BitBoardEchecs.getPositions(roisBlancs))
            plateau[pos%8][pos/8] = 6;
        long pionsNoirs = pions & noirs;
        for (int pos : BitBoardEchecs.getPositions(pionsNoirs))
            plateau[pos%8][pos/8] = 7;
        long cavaliersNoirs = cavaliers & noirs;
        for (int pos : BitBoardEchecs.getPositions(cavaliersNoirs))
            plateau[pos%8][pos/8] = 8;
        long fousNoirs = fous & noirs;
        for (int pos : BitBoardEchecs.getPositions(fousNoirs))
            plateau[pos%8][pos/8] = 9;
        long toursNoirs = tours & noirs;
        for (int pos : BitBoardEchecs.getPositions(toursNoirs))
            plateau[pos%8][pos/8] = 10;
        long reinesNoirs = reines & noirs;
        for (int pos : BitBoardEchecs.getPositions(reinesNoirs))
            plateau[pos%8][pos/8] = 11;
        long roisNoirs = rois & noirs;
        for (int pos : BitBoardEchecs.getPositions(roisNoirs))
            plateau[pos%8][pos/8] = 12;
        return plateau;
    }

    public long genererHash() {
        long hash = blancs + noirs + pions + cavaliers + fous + tours + reines + rois + speciaux + (tour ? 1 : 0);
        return hash;
    }

    public double getAvancement() {
        return Evaluation.getAvancement(blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois);
    }
}
