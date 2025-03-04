import java.util.ArrayList;
import java.util.List;
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
        reines = 0x0800000000000008L;
        rois = 0x1000000000000010L;
        speciaux = rois | tours;
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

    public int getCasePos() {
        return casePos;
    }

    public int getCaseDep() {
        return getCaseDep();
    }

    public List<int[]> getAllDepPossibles() {
        List<int[]> deps = new ArrayList<>();

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
                    long piecesPromotion;
                    switch (nb) {
                        case 64: piecesPromotion = cavaliers;break;
                        case 65: piecesPromotion = fous;break;
                        case 66: piecesPromotion = tours;break;
                        case 67: piecesPromotion = reines;break;
                        default: return false;
                    }
                    BitBoardEchecs.promotion(caseDep, pions, piecesPromotion);
                    nouveauTour();
                }break;
                default: return false;
            }
            maj();
            return true;
        }
        return false;
    }
    
    public void deplacerPieces(int pos, int dep) {
        long[] afterMove = BitBoardEchecs.move(pos,dep,blancs,noirs,speciaux,pions,cavaliers,fous,tours,reines,rois,tour);
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
        if (getEchecEtMat())
            victoire = tour ? 2 : 1;
    }

    private boolean getEchecEtMat() {
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

    public int evaluation() {
        if (victoire == 0)
            return Evaluation.evaluation(blancs, noirs, pions, cavaliers, fous, tours, reines);
        if (victoire == 1)
            return 1000;
        if (victoire == 2)
            return -1000;
        return 0;
    }
}
