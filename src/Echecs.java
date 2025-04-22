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

    public long blancs;
    public long noirs;
    public long speciaux;
    public long pions;
    public long cavaliers;
    public long fous;
    public long tours;
    public long reines;
    public long rois;

    private HashMap<Long, Integer> coupsPrecedents;
    private int nbCoupsNuls;
    private boolean enableRollBack;
    private ArrayList<RollBack> rollBacks;

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
        coupsPrecedents = new HashMap<>(echecs.coupsPrecedents);
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
        enableRollBack = false;
        rollBacks = null;
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

    public int[] getPrisesPossibles(int pos) {
        return BitBoardEchecs.getPositions(BitBoardEchecs.getPrises(pos, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, tour));
    }

    public boolean action (int nb) {
        if (victoire==0) {
            if (enableRollBack) {
                rollBacks.add(new RollBack(casePos, deps, ordre, nbCoupsNuls, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois));
            }
                
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

        if (pions != afterMove[3] || BitBoardEchecs.countPieces(blancs) != BitBoardEchecs.countPieces(afterMove[0]) || BitBoardEchecs.countPieces(noirs) != BitBoardEchecs.countPieces(afterMove[1])) {
            nbCoupsNuls = -1;
            if (!enableRollBack)
                coupsPrecedents.clear();
        }
            
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

    public void loadFEN(String fen) {
        blancs = noirs = pions = cavaliers = fous = tours = reines = rois = speciaux = 0;
    
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");
    
        for (int i = 0; i < 8; i++) {
            String row = rows[i];
            int col = 0;
            for (char c : row.toCharArray()) {
                if (Character.isDigit(c)) {
                    col += c - '0';
                } else {
                    int pos = (7 - i) * 8 + (7 - col);
                    long bit = 1L << pos;
                    switch (c) {
                        case 'P': blancs |= bit; pions |= bit; break;
                        case 'N': blancs |= bit; cavaliers |= bit; break;
                        case 'B': blancs |= bit; fous |= bit; break;
                        case 'R': blancs |= bit; tours |= bit; break;
                        case 'Q': blancs |= bit; reines |= bit; break;
                        case 'K': blancs |= bit; rois |= bit; break;
                        case 'p': noirs |= bit; pions |= bit; break;
                        case 'n': noirs |= bit; cavaliers |= bit; break;
                        case 'b': noirs |= bit; fous |= bit; break;
                        case 'r': noirs |= bit; tours |= bit; break;
                        case 'q': noirs |= bit; reines |= bit; break;
                        case 'k': noirs |= bit; rois |= bit; break;
                    }
                    col++;
                }
            }
        }
    
        tour = parts[1].equals("w");
    
        if(parts[2].contains("K"))
            speciaux |= 0x0000000000000009L;
        if(parts[2].contains("Q"))
            speciaux |= 0x0000000000000088L;
        if(parts[2].contains("k"))
            speciaux |= 0x0900000000000000L;
        if(parts[2].contains("q"))
            speciaux |= 0x8800000000000000L;
    
        if (!parts[3].equals("-")) {
            int casePassant =  8 * (parts[3].charAt(1) - '1') + 7 - (parts[3].charAt(0) - 'a');
            int pionPassant = tour ? casePassant - 8 : casePassant + 8;
            speciaux |= 1 << pionPassant;
        }
    
        nbCoupsNuls = Integer.parseInt(parts[4]);
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

    public long generatePlateau() {
        long hash = 0;
        hash ^= Long.rotateLeft(blancs, 3);
        hash ^= Long.rotateLeft(noirs, 7);
        hash ^= Long.rotateLeft(pions, 11);
        hash ^= Long.rotateLeft(cavaliers, 13);
        hash ^= Long.rotateLeft(fous, 17);
        hash ^= Long.rotateLeft(tours, 19);
        hash ^= Long.rotateLeft(reines, 23);
        hash ^= Long.rotateLeft(rois, 29);
        hash ^= Long.rotateLeft(speciaux, 31);
        return hash;
    }

    private long genererHash() {
        return generatePlateau() ^ (tour ? 0xABCD1234EF56789L : 0);
    }

    public double getAvancement() {
        return Evaluation.getAvancement(blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois);
    }

    public int[] getActions() {
        switch(ordre) {
            case 0: {
                if (tour)
                    return BitBoardEchecs.getPositions(blancs);
                return BitBoardEchecs.getPositions(noirs);
            }
            case 1: return BitBoardEchecs.getPositions(deps);
            case 2: return new int[] {64,65,66,67};
        }
        return null;
    }

    public int getPiece(int pos) {
        long bitboard = BitBoardEchecs.toBitBoard(pos);
        if ((bitboard & pions) != 0)
            return 1;
        if ((bitboard & cavaliers) != 0)
            return 2;
        if ((bitboard & fous) != 0)
            return 3;
        if ((bitboard & tours) != 0)
            return 4;
        if ((bitboard & reines) != 0)
            return 5;
        if ((bitboard & rois) != 0)
            return 6;
        return 0;
    }

    public boolean enPrise(int pos) {
        return BitBoardEchecs.IsEchec(pos, blancs, noirs, pions, cavaliers, fous, tours, reines, rois, tour);
    }

    public void activateRollBack() {
        enableRollBack = true;
        rollBacks = new ArrayList<>(20);
    }

    public void desactivateRollBack() {
        enableRollBack = false;
        rollBacks = null;
    }

    public boolean rollBack() {
        if (enableRollBack && rollBacks.size() > 0) {
            victoire = 0;
            
            if (ordre == 0) {
                long hash = genererHash();
                int nb = coupsPrecedents.get(hash);
                if (nb == 1)
                    coupsPrecedents.remove(hash);
                else
                    coupsPrecedents.replace(hash, nb - 1);
                tour = !tour;
            }
            
            RollBack rollBack = rollBacks.remove(rollBacks.size() - 1);
            ordre = rollBack.ordre;
            casePos = rollBack.casePos;
            deps = rollBack.deps;
            nbCoupsNuls = rollBack.nbCoupsNuls;
            blancs = rollBack.blancs;
            noirs = rollBack.noirs;
            speciaux = rollBack.speciaux;
            pions = rollBack.pions;
            cavaliers = rollBack.cavaliers;
            fous = rollBack.fous;
            tours = rollBack.tours;
            reines = rollBack.reines;
            rois = rollBack.rois;
            // if (ordre == 0 || ordre == 2) {
            //     if (ordre == 0) {
            //         casePos = rollBack.casePos;
            //         nbCoupsNuls = rollBack.nbCoupsNuls;
            //     }
            //     blancs = rollBack.blancs;
            //     noirs = rollBack.noirs;
            //     speciaux = rollBack.speciaux;
            //     pions = rollBack.pions;
            //     cavaliers = rollBack.cavaliers;
            //     fous = rollBack.fous;
            //     tours = rollBack.tours;
            //     reines = rollBack.reines;
            //     rois = rollBack.rois;
            // }

            return true;
        }
        return false;
    }

    public void compare(Echecs echecs) {
        if (tour != echecs.tour)
            System.out.println("tour: " + tour + " contre " + echecs.tour);
        if (victoire != echecs.victoire)
            System.out.println("victoire: " + victoire + " contre " + echecs.victoire);
        if (ordre != echecs.ordre)
            System.out.println("ordre: " + ordre + " contre " + echecs.ordre);
        if (casePos != echecs.casePos)
            System.out.println("casePos: " + casePos + " contre " + echecs.casePos);
        if (blancs != echecs.blancs)
            System.out.println("blancs: " + blancs + " contre " + echecs.blancs);
        if (noirs != echecs.noirs)
            System.out.println("noirs: " + noirs + " contre " + echecs.noirs);
        if (speciaux != echecs.speciaux)
            System.out.println("speciaux: " + speciaux + " contre " + echecs.speciaux);
        if (pions != echecs.pions)
            System.out.println("pions: " + pions + " contre " + echecs.pions);
        if (cavaliers != echecs.cavaliers)
            System.out.println("cavaliers: " + cavaliers + " contre " + echecs.cavaliers);
        if (fous != echecs.fous)
            System.out.println("fous: " + fous + " contre " + echecs.fous);
        if (tours != echecs.tours)
            System.out.println("tours: " + tours + " contre " + echecs.tours);
        if (reines != echecs.reines)
            System.out.println("reines: " + reines + " contre " + echecs.reines);
        if (rois != echecs.rois)
            System.out.println("rois: " + rois + " contre " + echecs.rois);
        if (nbCoupsNuls != echecs.nbCoupsNuls)
            System.out.println("nbCoupsNuls: " + nbCoupsNuls + " contre " + echecs.nbCoupsNuls);
    }

}
