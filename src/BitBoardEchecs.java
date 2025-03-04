import java.util.ArrayList;

public class BitBoardEchecs {
    private static final long A_FILE = 0x0101010101010101L;
    private static final long H_FILE = 0x8080808080808080L;

    public static long toBitBoard(int pos) {
        return 1L << (63 - pos);
    }

    public static int toPosition(long bitboard) {
        return Long.numberOfLeadingZeros(bitboard);
    }

    public static int[] getPositions(long bitboard) {
        ArrayList<Integer> positions = new ArrayList<>();

        while (bitboard != 0) {
            int pos = Long.numberOfLeadingZeros(bitboard);
            positions.add(pos);
            bitboard &= ~(1L << (63 - pos));
        }

        return positions.stream().mapToInt(i -> i).toArray();
    }
    
    public static int countPieces(long bitboard) {
        return Long.bitCount(bitboard);
    }
    

    public static void afficherBitboard(long bitboard) {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                int pos = 63 - (rank * 8 + file);
                if (((bitboard >>> pos) & 1) == 1) {
                    System.out.print(" 1 ");
                } else {
                    System.out.print(" . ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    //#region Dep simple

    private static long getCavalierDep(int pos) {
        if (pos == 0)
            return 0x0020400000000000L;
        long cavalier = toBitBoard(pos);
        long moves = 0L;
    
        moves |= (cavalier << 17) & ~A_FILE; // 2H + 1D
        moves |= (cavalier << 15) & ~H_FILE; // 2H + 1G
        moves |= (cavalier << 10) & ~(A_FILE | (A_FILE << 1)); // 2D + 1H
        moves |= (cavalier << 6)  & ~(H_FILE | (H_FILE >> 1)); // 2G + 1H
        moves |= (cavalier >> 17) & ~H_FILE; // 2B + 1G
        moves |= (cavalier >> 15) & ~A_FILE; // 2B + 1D
        moves |= (cavalier >> 10) & ~(H_FILE | (H_FILE >> 1)); // 2G + 1B
        moves |= (cavalier >> 6)  & ~(A_FILE | (A_FILE << 1)); // 2D + 1B

        return moves;
    }

    private static long getRoiDep(int pos) {
        if (pos == 0)
            return 0x40C0000000000000L;
        long roi = toBitBoard(pos);
        long moves = 0L;
    
        moves |= (roi << 8);
        moves |= (roi >> 8);
        moves |= (roi << 1) & ~A_FILE;
        moves |= (roi >> 1) & ~H_FILE;
        moves |= (roi << 9) & ~A_FILE;
        moves |= (roi << 7) & ~H_FILE;
        moves |= (roi >> 9) & ~H_FILE;
        moves |= (roi >> 7) & ~A_FILE;
    
        return moves;
    }

    //#endregion

    //#region Dep Complexe

    private static long getDepHaut(int pos, long piecesJoueur, long piecesAdversaire) {
        long bitboard = toBitBoard(pos);
        long moves = 0L;
        long mask = bitboard;
    
        while ((mask & 0xFF00000000000000L) == 0) {
            mask <<= 8;
            if ((mask & piecesJoueur) != 0) break;
            moves |= mask;
            if ((mask & piecesAdversaire) != 0) break;
        }
    
        return moves;
    }

    private static long getDepBas(int pos, long piecesJoueur, long piecesAdversaire) {
        long bitboard = toBitBoard(pos);
        long moves = 0L;
        long mask = bitboard;
    
        while ((mask & 0x00000000000000FFL) == 0) {
            mask >>>= 8;
            if ((mask & piecesJoueur) != 0) break;
            moves |= mask;
            if ((mask & piecesAdversaire) != 0) break;
        }
    
        return moves;
    }

    private static long getDepGauche(int pos, long piecesJoueur, long piecesAdversaire) {
        long bitboard = toBitBoard(pos);
        long moves = 0L;
        long mask = bitboard;
    
        while ((mask & H_FILE) == 0) {
            mask <<= 1;
            if ((mask & piecesJoueur) != 0) break;
            moves |= mask;
            if ((mask & piecesAdversaire) != 0) break;
        }
    
        return moves;
    }

    private static long getDepDroite(int pos, long piecesJoueur, long piecesAdversaire) {
        long bitboard = toBitBoard(pos);
        long moves = 0L;
        long mask = bitboard;
    
        while ((mask & A_FILE) == 0) {
            mask >>>= 1;
            if ((mask & piecesJoueur) != 0) break;
            moves |= mask;
            if ((mask & piecesAdversaire) != 0) break;
        }
    
        return moves;
    }

    private static long getDepHautGauche(int pos, long piecesJoueur, long piecesAdversaire) {
        long bitboard = toBitBoard(pos);
        long moves = 0L;
        long mask = bitboard;
    
        while ((mask & H_FILE) == 0 && (mask & 0xFF00000000000000L) == 0) {
            mask <<= 9;
            if ((mask & piecesJoueur) != 0) break;
            moves |= mask;
            if ((mask & piecesAdversaire) != 0) break;
        }
    
        return moves;
    }
    
    private static long getDepHautDroite(int pos, long piecesJoueur, long piecesAdversaire) {
        long bitboard = toBitBoard(pos);
        long moves = 0L;
        long mask = bitboard;
    
        while ((mask & A_FILE) == 0 && (mask & 0xFF00000000000000L) == 0) {
            mask <<= 7;
            if ((mask & piecesJoueur) != 0) break;
            moves |= mask;
            if ((mask & piecesAdversaire) != 0) break;
        }
    
        return moves;
    }

    private static long getDepBasGauche(int pos, long piecesJoueur, long piecesAdversaire) {
        long bitboard = toBitBoard(pos);
        long moves = 0L;
        long mask = bitboard;
    
        while ((mask & H_FILE) == 0 && (mask & 0x00000000000000FFL) == 0) {
            mask >>>= 7;
            if ((mask & piecesJoueur) != 0) break;
            moves |= mask;
            if ((mask & piecesAdversaire) != 0) break;
        }
    
        return moves;
    }

    private static long getDepBasDroite(int pos, long piecesJoueur, long piecesAdversaire) {
        long bitboard = toBitBoard(pos);
        long moves = 0L;
        long mask = bitboard;
    
        while ((mask & 0x0101010101010101L) == 0 && (mask & 0x00000000000000FFL) == 0) {
            mask >>>= 9;
            if ((mask & piecesJoueur) != 0) break;
            moves |= mask;
            if ((mask & piecesAdversaire) != 0) break;
        }
    
        return moves;
    }

    private static long getPrisesPions(int pos, long piecesAdversaire, boolean couleurJoueur) {
        long bitboard = toBitBoard(pos);
        long moves = 0;
        if (couleurJoueur) {
            if ((bitboard & A_FILE) == 0)
                moves |= (bitboard << 7) & piecesAdversaire;
            if ((bitboard & H_FILE) == 0) 
                moves |= (bitboard << 9) & piecesAdversaire;
        } else {
            if ((bitboard & A_FILE) == 0) 
                moves = (bitboard >>> 9) & piecesAdversaire;
            if ((bitboard & H_FILE) == 0) 
                moves |= (bitboard >>> 7) & piecesAdversaire;
        }
        return moves;
    }

    private static long getPionDep(int pos, long pieces, boolean couleurJoueur) {
        long bitboard = toBitBoard(pos);
        long moves;
        if (couleurJoueur) {
            moves = (bitboard << 8) & ~pieces;
            if (moves != 0 && (bitboard & 0x000000000000FF00L) != 0)
                moves |= (bitboard << 16) & ~pieces;
        }
        else {
            moves = (bitboard >> 8) & ~pieces;
            if (moves != 0 && (bitboard & 0x00FF000000000000L) != 0)
                moves |= (bitboard >> 16) & ~pieces;
        }
        return moves;
    }

    private static long getPriseEnPassant(int pos, long pionsAdversaire, long speciaux, boolean couleurJoueur) {
        if ((couleurJoueur && (pos < 24 || 32 <= pos)) || (!couleurJoueur && (pos < 32 || 40 <= pos)))
                return 0;
        
        long pionsSpeciaux = pionsAdversaire & speciaux;
        if (pionsSpeciaux == 0)
            return 0;
        long bitboard = toBitBoard(pos);
        long pionsAdversesAGauche = (bitboard << 1) & pionsSpeciaux & ~A_FILE;
        long pionsAdversesADroite = (bitboard >> 1) & pionsSpeciaux & ~H_FILE;
        long moves = 0;
        if (couleurJoueur) {
            if (pionsAdversesAGauche != 0)
                moves |= (bitboard << 9);
            if (pionsAdversesADroite != 0)
                moves |= (bitboard << 7);
        }
        else {
            if (pionsAdversesAGauche != 0)
                moves |= (bitboard >> 7);
            if (pionsAdversesADroite != 0)
                moves |= (bitboard >> 9);
        }
        return moves;
    }

    private static long getRoque(long toursJoueur, long roiJoueur, long pieces, long speciaux, boolean couleurJoueur) {
        long moves = 0;
        long roquePetitMask, roqueGrandMask;
        long roquePetitPath, roqueGrandPath;
        
        if (couleurJoueur) {
            roquePetitMask = 0x0000000000000009L;
            roqueGrandMask = 0x0000000000000088L;
            roquePetitPath = 0x0000000000000006L;
            roqueGrandPath = 0x0000000000000070L;
        } else {
            roquePetitMask = 0x0900000000000000L;
            roqueGrandMask = 0x8800000000000000L;
            roquePetitPath = 0x0600000000000000L;
            roqueGrandPath = 0x7000000000000000L;
        }
    
        long toursRoisStatics = (roiJoueur | toursJoueur) & speciaux;
        if (((toursRoisStatics & roqueGrandMask) == roqueGrandMask) && (pieces & roqueGrandPath) == 0)
            moves |= (roiJoueur << 2);
        if (((toursRoisStatics & roquePetitMask) == roquePetitMask) && (pieces & roquePetitPath) == 0)
            moves |= (roiJoueur >>> 2);
    
        return moves;
    }

    //#endregion

    //#region Dep Pieces

    private static long getPionDep(int pos, long pionsAdversaire, long piecesAdversaire, long pieces, long speciaux, boolean couleurJoueur) {
        return getPionDep(pos,pieces,couleurJoueur) 
                | getPrisesPions(pos,piecesAdversaire,couleurJoueur) 
                | getPriseEnPassant(pos, pionsAdversaire, speciaux, couleurJoueur);
    }

    private static long getCavalierDep(int pos, long piecesJoueur) {
        return getCavalierDep(pos) & ~piecesJoueur;
    }

    private static long getFouDep(int pos, long piecesJoueur, long piecesAdversaire) {
        return getDepHautGauche(pos, piecesJoueur, piecesAdversaire)
                | getDepHautDroite(pos, piecesJoueur, piecesAdversaire)
                | getDepBasGauche(pos, piecesJoueur, piecesAdversaire)
                | getDepBasDroite(pos, piecesJoueur, piecesAdversaire);
    }

    private static long getTourDep(int pos, long piecesJoueur, long piecesAdversaire) {
        return getDepHaut(pos, piecesJoueur, piecesAdversaire)
                | getDepBas(pos, piecesJoueur, piecesAdversaire)
                | getDepGauche(pos, piecesJoueur, piecesAdversaire)
                | getDepDroite(pos, piecesJoueur, piecesAdversaire);
    }

    private static long getReineDep(int pos, long piecesJoueur, long piecesAdversaire) {
        return getFouDep(pos, piecesJoueur, piecesAdversaire) | getTourDep(pos, piecesJoueur, piecesAdversaire);
    }

    private static long getRoiDep(int pos, long toursJoueur, long piecesJoueur, long pieces, long speciaux, boolean couleurJoueur) {
        long bitboard = toBitBoard(pos);
        return (getRoiDep(pos) & ~piecesJoueur) | getRoque(toursJoueur, bitboard, speciaux, pieces, couleurJoueur);
    }

    //#endregion

    //#region Dep Verif

    private static boolean isPriseEnPassant(int pos, int dep, long pionsAdversaire, long speciaux, boolean couleurJoueur) {
        long priseEnPassant = getPriseEnPassant(pos, pionsAdversaire, speciaux, couleurJoueur);
        long bitboardDep = toBitBoard(dep);
        return ((bitboardDep & priseEnPassant) != 0);
    }

    private static boolean isRoque(int dep, long toursJoueur, long roiJoueur, long pieces, long speciaux, boolean couleurJoueur) {
        long roque = getRoque(toursJoueur, roiJoueur, pieces, speciaux, couleurJoueur);
        long bitboardDep = toBitBoard(dep);
        return ((bitboardDep & roque) != 0);
    }

    public static boolean IsEchec(long blancs, long noirs, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long piecesJoueur;
        long piecesAdversaire;
        if (couleurJoueur) {
            piecesJoueur = blancs;
            piecesAdversaire = noirs;
        }
        else {
            piecesJoueur = noirs;
            piecesAdversaire = blancs;
        }
        long roiJoueur = rois & piecesJoueur;
        int posRoi = toPosition(roiJoueur);
        
        // pris par un pion
        long prisePion = getPrisesPions(posRoi, piecesAdversaire, couleurJoueur);
        if ((prisePion & (pions & piecesAdversaire)) != 0)
            return true;

        // pris par un cavalier
        long priseCavalier = getCavalierDep(posRoi);
        if ((priseCavalier & (cavaliers & piecesAdversaire)) != 0)
            return true;

        // pris par un fou ou la reine
        long fousReineAdversaire = (fous | reines) & piecesAdversaire;
        long priseHautGauche = getDepHautGauche(posRoi,piecesJoueur,piecesAdversaire);
        if ((priseHautGauche & fousReineAdversaire) != 0)
            return true;
        long priseHautDroite = getDepHautDroite(posRoi,piecesJoueur,piecesAdversaire);
        if ((priseHautDroite & fousReineAdversaire) != 0)
            return true;
        long priseBasGauche = getDepBasGauche(posRoi,piecesJoueur,piecesAdversaire);
        if ((priseBasGauche & fousReineAdversaire) != 0)
            return true;
        long priseBasDroite = getDepBasDroite(posRoi,piecesJoueur,piecesAdversaire);
        if ((priseBasDroite & fousReineAdversaire) != 0)
            return true;
        
        // pris par une tour ou la reine
        long toursReineAdversaire = (tours | reines) & piecesAdversaire;
        long priseHaut = getDepHaut(posRoi,piecesJoueur,piecesAdversaire);
        if ((priseHaut & toursReineAdversaire) != 0)
            return true;
        long priseBas = getDepBas(posRoi,piecesJoueur,piecesAdversaire);
        if ((priseBas & toursReineAdversaire) != 0)
            return true;
        long priseGauche = getDepGauche(posRoi,piecesJoueur,piecesAdversaire);
        if ((priseGauche & toursReineAdversaire) != 0)
            return true;
        long priseDroite = getDepDroite(posRoi,piecesJoueur,piecesAdversaire);
        if ((priseDroite & toursReineAdversaire) != 0)
            return true;

        // pris par le roi
        long priseRoi = getRoiDep(posRoi);
        if ((priseRoi & (rois & piecesAdversaire)) != 0)
            return true;
        
        return false;
    }

    //#endregion

    //#region Move

    private static long[] movePiece(long bitboardPos, long bitboardDep, long blancs, long noirs, long speciaux, long piece, boolean couleurJoueur) {
        long pieceCopy = piece;
        pieceCopy = (pieceCopy & ~bitboardPos) | bitboardDep;
        long speciauxCopy = speciaux & ~bitboardDep & 0xFF000000000000FFL; // les prises en passant de ce tour ne sont plus disponibles après
        long blancsCopy = blancs;
        long noirsCopy = noirs;
        if (couleurJoueur) {
            blancsCopy = (blancsCopy & ~bitboardPos) | bitboardDep;
            noirsCopy &= ~bitboardDep;
        }
        else {
            blancsCopy &= ~bitboardDep;
            noirsCopy = (noirsCopy & ~bitboardPos) | bitboardDep;
        }
        return new long[] {blancsCopy, noirsCopy, speciauxCopy, pieceCopy};
    }

    public static long[] movePion(int pos, int dep, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long bitboardPos = toBitBoard(pos);
        long bitboardDep = toBitBoard(dep);
        long blancsCopy = blancs;
        long noirsCopy = noirs;
        long speciauxCopy = speciaux;
        long pionsCopy = pions;
        long cavaliersCopy = cavaliers;
        long fousCopy = fous;
        long toursCopy = tours;
        long reinesCopy = reines;
        long roisCopy = rois;

        long piecesAdversaire;
        if (couleurJoueur)
            piecesAdversaire = noirs;
        else
            piecesAdversaire = blancs;
        long pionsAdversaire = pions & piecesAdversaire;

        // deplacement du pion
        long[] copies = movePiece(bitboardPos, bitboardDep, blancsCopy, noirsCopy, speciauxCopy, pionsCopy, couleurJoueur);
        blancsCopy = copies[0];
        noirsCopy = copies[1];
        speciauxCopy = copies[2];
        pionsCopy = copies[3];

        // prise de pion
        if (isPriseEnPassant(pos, dep, pionsAdversaire, speciaux, couleurJoueur)) {
            long pionAdversaire;
            if (couleurJoueur) {
                pionAdversaire = toBitBoard(dep + 8);
                noirsCopy &= ~pionAdversaire;
            }
            else {
                pionAdversaire = toBitBoard(dep - 8);
                blancsCopy &= ~pionAdversaire;
            }
            pionsCopy &= ~pionAdversaire;
            speciauxCopy &= ~pionAdversaire;
        }
        else {
            cavaliersCopy &= ~bitboardDep;
            fousCopy &= ~bitboardDep;
            toursCopy &= ~bitboardDep;
            reinesCopy &= ~bitboardDep;
            roisCopy &= ~bitboardDep;

            if (Math.abs(pos - dep) == 16)
                speciauxCopy |= bitboardDep;
        }
        return new long[] {blancsCopy, noirsCopy, speciauxCopy, pionsCopy, cavaliersCopy, fousCopy, toursCopy, reinesCopy, roisCopy};
    }

    public static long[] moveCavalier(int pos, int dep, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long bitboardPos = toBitBoard(pos);
        long bitboardDep = toBitBoard(dep);
        long blancsCopy = blancs;
        long noirsCopy = noirs;
        long speciauxCopy = speciaux;
        long pionsCopy = pions;
        long cavaliersCopy = cavaliers;
        long fousCopy = fous;
        long toursCopy = tours;
        long reinesCopy = reines;
        long roisCopy = rois;

        long[] copies = movePiece(bitboardPos, bitboardDep, blancsCopy, noirsCopy, speciauxCopy, cavaliersCopy, couleurJoueur);
        blancsCopy = copies[0];
        noirsCopy = copies[1];
        speciauxCopy = copies[2];
        cavaliersCopy = copies[3];

        pionsCopy &= ~bitboardDep;
        fousCopy &= ~bitboardDep;
        toursCopy &= ~bitboardDep;
        reinesCopy &= ~bitboardDep;
        roisCopy &= ~bitboardDep;

        return new long[] {blancsCopy, noirsCopy, speciauxCopy, pionsCopy, cavaliersCopy, fousCopy, toursCopy, reinesCopy, roisCopy};
    }

    public static long[] moveFou(int pos, int dep, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long bitboardPos = toBitBoard(pos);
        long bitboardDep = toBitBoard(dep);
        long blancsCopy = blancs;
        long noirsCopy = noirs;
        long speciauxCopy = speciaux;
        long pionsCopy = pions;
        long cavaliersCopy = cavaliers;
        long fousCopy = fous;
        long toursCopy = tours;
        long reinesCopy = reines;
        long roisCopy = rois;

        long[] copies = movePiece(bitboardPos, bitboardDep, blancsCopy, noirsCopy, speciauxCopy, fousCopy, couleurJoueur);
        blancsCopy = copies[0];
        noirsCopy = copies[1];
        speciauxCopy = copies[2];
        fousCopy = copies[3];

        pionsCopy &= ~bitboardDep;
        cavaliersCopy &= ~bitboardDep;
        toursCopy &= ~bitboardDep;
        reinesCopy &= ~bitboardDep;
        roisCopy &= ~bitboardDep;

        return new long[] {blancsCopy, noirsCopy, speciauxCopy, pionsCopy, cavaliersCopy, fousCopy, toursCopy, reinesCopy, roisCopy};
    }

    public static long[] moveTour(int pos, int dep, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long bitboardPos = toBitBoard(pos);
        long bitboardDep = toBitBoard(dep);
        long blancsCopy = blancs;
        long noirsCopy = noirs;
        long speciauxCopy = speciaux;
        long pionsCopy = pions;
        long cavaliersCopy = cavaliers;
        long fousCopy = fous;
        long toursCopy = tours;
        long reinesCopy = reines;
        long roisCopy = rois;

        long[] copies = movePiece(bitboardPos, bitboardDep, blancsCopy, noirsCopy, speciauxCopy, toursCopy, couleurJoueur);
        blancsCopy = copies[0];
        noirsCopy = copies[1];
        speciauxCopy = copies[2];
        toursCopy = copies[3];

        pionsCopy &= ~bitboardDep;
        cavaliersCopy &= ~bitboardDep;
        fousCopy &= ~bitboardDep;
        reinesCopy &= ~bitboardDep;
        roisCopy &= ~bitboardDep;

        return new long[] {blancsCopy, noirsCopy, speciauxCopy, pionsCopy, cavaliersCopy, fousCopy, toursCopy, reinesCopy, roisCopy};
    }

    public static long[] moveReine(int pos, int dep, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long bitboardPos = toBitBoard(pos);
        long bitboardDep = toBitBoard(dep);
        long blancsCopy = blancs;
        long noirsCopy = noirs;
        long speciauxCopy = speciaux;
        long pionsCopy = pions;
        long cavaliersCopy = cavaliers;
        long fousCopy = fous;
        long toursCopy = tours;
        long reinesCopy = reines;
        long roisCopy = rois;

        long[] copies = movePiece(bitboardPos, bitboardDep, blancsCopy, noirsCopy, speciauxCopy, reinesCopy, couleurJoueur);
        blancsCopy = copies[0];
        noirsCopy = copies[1];
        speciauxCopy = copies[2];
        reinesCopy = copies[3];

        pionsCopy &= ~bitboardDep;
        cavaliersCopy &= ~bitboardDep;
        fousCopy &= ~bitboardDep;
        toursCopy &= ~bitboardDep;
        roisCopy &= ~bitboardDep;

        return new long[] {blancsCopy, noirsCopy, speciauxCopy, pionsCopy, cavaliersCopy, fousCopy, toursCopy, reinesCopy, roisCopy};
    }

    public static long[] moveRoi(int pos, int dep, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long bitboardPos = toBitBoard(pos);
        long bitboardDep = toBitBoard(dep);
        long blancsCopy = blancs;
        long noirsCopy = noirs;
        long speciauxCopy = speciaux;
        long pionsCopy = pions;
        long cavaliersCopy = cavaliers;
        long fousCopy = fous;
        long toursCopy = tours;
        long reinesCopy = reines;
        long roisCopy = rois;

        long[] copies = movePiece(bitboardPos, bitboardDep, blancsCopy, noirsCopy, speciauxCopy, roisCopy, couleurJoueur);
        blancsCopy = copies[0];
        noirsCopy = copies[1];
        speciauxCopy = copies[2];
        roisCopy = copies[3];

        long pieces = blancs | noirs;
        long piecesJoueur;
        if (couleurJoueur)
            piecesJoueur = blancs;
        else
            piecesJoueur = noirs;

        if (isRoque(dep, (tours & piecesJoueur), (rois & piecesJoueur), pieces, speciaux, couleurJoueur)) {
            switch(dep) {
                case 2: {
                    toursCopy = (toursCopy | 0x1000000000000000L) & ~0x8000000000000000L;
                    if (couleurJoueur)
                        blancsCopy = (blancsCopy | 0x1000000000000000L) & ~0x8000000000000000L;
                    else
                        noirsCopy = (noirsCopy | 0x1000000000000000L) & ~0x8000000000000000L;
                    speciauxCopy &= ~0x8000000000000000L;
                }break;
                case 6: {
                    toursCopy = (toursCopy | 0x0400000000000000L) & ~0x0100000000000000L;
                    if (couleurJoueur)
                        blancsCopy = (blancsCopy | 0x0400000000000000L) & ~0x0100000000000000L;
                    else
                        noirsCopy = (noirsCopy | 0x0400000000000000L) & ~0x0100000000000000L;
                    speciauxCopy &= ~0x0100000000000000L;
                }break;
                case 46: {
                    toursCopy = (toursCopy | 0x0000000000000010L) & ~0x0000000000000080L;
                    if (couleurJoueur)
                        blancsCopy = (blancsCopy | 0x0000000000000010L) & ~0x0000000000000080L;
                    else
                        noirsCopy = (noirsCopy | 0x0000000000000010L) & ~0x0000000000000080L;
                    speciauxCopy &= ~0x0000000000000080L;
                }break;
                case 63: {
                    toursCopy = (toursCopy | 0x0000000000000004L) & ~0x0000000000000001L;
                    if (couleurJoueur)
                        blancsCopy = (blancsCopy | 0x0000000000000004L) & ~0x0000000000000001L;
                    else
                        noirsCopy = (noirsCopy | 0x0000000000000004L) & ~0x0000000000000001L;
                    speciauxCopy &= ~0x0000000000000001L;
                }break;
            }
            if (dep == 2) {

            }
        }
        else {
            pionsCopy &= ~bitboardDep;
            cavaliersCopy &= ~bitboardDep;
            fousCopy &= ~bitboardDep;
            toursCopy &= ~bitboardDep;
            reinesCopy &= ~bitboardDep;
        }
        

        return new long[] {blancsCopy, noirsCopy, speciauxCopy, pionsCopy, cavaliersCopy, fousCopy, toursCopy, reinesCopy, roisCopy};
    }

    public static long[] move(int pos, int dep, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long bitboard = toBitBoard(pos);
        if ((bitboard & pions) != 0)
            return movePion(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        if ((bitboard & cavaliers) != 0)
            return moveCavalier(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        if ((bitboard & fous) != 0)
            return moveFou(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        if ((bitboard & tours) != 0)
            return moveTour(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        if ((bitboard & reines) != 0)
            return moveReine(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        if ((bitboard & rois) != 0)
            return moveRoi(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        return null;
    }

    //#endregion

    //#region Dep Corrects

    public static long getCorrectPionDep(int pos, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long pieces = blancs | noirs;
        long piecesAdversaire;
        if (couleurJoueur)
            piecesAdversaire = noirs;
        else
            piecesAdversaire = blancs;
        long pionsAdversaire = pions & piecesAdversaire;
        long moves = getPionDep(pos, pionsAdversaire, piecesAdversaire, pieces, speciaux, couleurJoueur);
        int[] deps = getPositions(moves);
        long correctsMoves = 0;
        for (int dep : deps) {
            long[] afterMove = movePion(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
            if (!IsEchec(afterMove[0], afterMove[1], afterMove[3], afterMove[4], afterMove[5], afterMove[6], afterMove[7], afterMove[8], couleurJoueur))
                correctsMoves |= toBitBoard(dep);
        }
        return correctsMoves;
    }

    public static long getCorrectCavalierDep(int pos, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long piecesJoueur;
        if (couleurJoueur)
            piecesJoueur = blancs;
        else
            piecesJoueur = noirs;
        
        long moves = getCavalierDep(pos, piecesJoueur);
        int[] deps = getPositions(moves);
        long correctsMoves = 0;
        for (int dep : deps) {
            long[] afterMove = moveCavalier(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
            if (!IsEchec(afterMove[0], afterMove[1], afterMove[3], afterMove[4], afterMove[5], afterMove[6], afterMove[7], afterMove[8], couleurJoueur))
                correctsMoves |= toBitBoard(dep);
        }
        return correctsMoves;
    }

    public static long getCorrectFouDep(int pos, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long piecesJoueur;
        long piecesAdversaire;
        if (couleurJoueur) {
            piecesJoueur = blancs;
            piecesAdversaire = noirs;
        } 
        else {
            piecesJoueur = noirs;
            piecesAdversaire = blancs;
        }

        long moves = getFouDep(pos,piecesJoueur,piecesAdversaire);
        int[] deps = getPositions(moves);
        long correctsMoves = 0;
        for (int dep : deps) {
            long[] afterMove = moveFou(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
            if (!IsEchec(afterMove[0], afterMove[1], afterMove[3], afterMove[4], afterMove[5], afterMove[6], afterMove[7], afterMove[8], couleurJoueur))
                correctsMoves |= toBitBoard(dep);
        }
        return correctsMoves;
    }

    public static long getCorrectTourDep(int pos, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long piecesJoueur;
        long piecesAdversaire;
        if (couleurJoueur) {
            piecesJoueur = blancs;
            piecesAdversaire = noirs;
        } 
        else {
            piecesJoueur = noirs;
            piecesAdversaire = blancs;
        }
        
        long moves = getTourDep(pos,piecesJoueur,piecesAdversaire);
        int[] deps = getPositions(moves);
        long correctsMoves = 0;
        for (int dep : deps) {
            long[] afterMove = moveTour(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
            if (!IsEchec(afterMove[0], afterMove[1], afterMove[3], afterMove[4], afterMove[5], afterMove[6], afterMove[7], afterMove[8], couleurJoueur))
                correctsMoves |= toBitBoard(dep);
        }
        return correctsMoves;
    }

    public static long getCorrectReineDep(int pos, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long piecesJoueur;
        long piecesAdversaire;
        if (couleurJoueur) {
            piecesJoueur = blancs;
            piecesAdversaire = noirs;
        } 
        else {
            piecesJoueur = noirs;
            piecesAdversaire = blancs;
        }
        
        long moves = getReineDep(pos,piecesJoueur,piecesAdversaire);
        int[] deps = getPositions(moves);
        long correctsMoves = 0;
        for (int dep : deps) {
            long[] afterMove = moveReine(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
            if (!IsEchec(afterMove[0], afterMove[1], afterMove[3], afterMove[4], afterMove[5], afterMove[6], afterMove[7], afterMove[8], couleurJoueur))
                correctsMoves |= toBitBoard(dep);
        }
        return correctsMoves;
    }

    public static long getCorrectRoiDep(int pos, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long pieces = blancs | noirs;
        long piecesJoueur;
        if (couleurJoueur)
            piecesJoueur = blancs;
        else
            piecesJoueur = noirs;
        
        long moves = getRoiDep(pos,(tours & piecesJoueur), piecesJoueur, pieces, speciaux, couleurJoueur);
        int[] deps = getPositions(moves);
        long correctsMoves = 0;
        for (int dep : deps) {
            long[] afterMove = moveRoi(pos, dep, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
            if (!IsEchec(afterMove[0], afterMove[1], afterMove[3], afterMove[4], afterMove[5], afterMove[6], afterMove[7], afterMove[8], couleurJoueur))
                correctsMoves |= toBitBoard(dep);
        }
        return correctsMoves;
    }

    public static long getCorrectDep(int pos, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois, boolean couleurJoueur) {
        long bitboard = BitBoardEchecs.toBitBoard(pos);
        if ((bitboard & pions) != 0)
            return getCorrectPionDep(pos, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        if ((bitboard & cavaliers) != 0)
            return getCorrectCavalierDep(pos, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        if ((bitboard & fous) != 0)
            return getCorrectFouDep(pos, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        if ((bitboard & tours) != 0)
            return getCorrectTourDep(pos, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        if ((bitboard & reines) != 0)
            return getCorrectReineDep(pos, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        if ((bitboard & rois) != 0)
            return getCorrectRoiDep(pos, blancs, noirs, speciaux, pions, cavaliers, fous, tours, reines, rois, couleurJoueur);
        return 0;
    }

    //#endregion

    //#region Promotion

    public static long[] promotion(int pos, long pions, long piecesPromotion) {
        long bitboard = toBitBoard(pos);
        long pionsCopy = pions & ~bitboard;
        long piecesPromotionCopy = piecesPromotion | bitboard;
        return new long[] {pionsCopy, piecesPromotionCopy};
    }

    public static boolean possiblePromotion(long pions) {
        return (pions & 0xFF000000000000FFL) != 0;
    }

    //#endregion

}
