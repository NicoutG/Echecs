import java.util.Observer;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.GridLayout;


/**
 * Classe MF permet la gestion de l'interface graphique
 */

public class MF extends JFrame implements Observer{
    
    private Echecs echecs;

    private JButton tab [][] = null;

    private JButton changePion [] = null;

    private HashMap<String, ImageIcon> images = null;

    private Joueur joueur1 = new AlphaBetaTime2(2000);

    private Joueur joueur2 = new Humain();

    /**
     * Constructeur de MF
     */
    public MF (Echecs eche) {
        // Construit l'interface graphique
        build();
        echecs = eche;
    }

    /**
     * Construit l'interface graphique
     */
    private void build () {

        // Création de la fenêtre
        setTitle("Echecs"); 
        setLayout(new BorderLayout());

        int tailleCase=90;

        // Initialisation de la matrice de JLabels
        tab = new JButton [8][8];

        JPanel jp=new JPanel(); 
        //jp.setLayout(new GridLayout(1, 2));

        JPanel jpE=new JPanel(); 
        jpE.setLayout(new GridLayout(8, 8));
        jpE.setPreferredSize(new Dimension(8*tailleCase, 8*tailleCase));

        for (int j=0;j<8;j++)
            for (int i=0;i<8;i++) {
                tab[i][j]=new JButton();
                final int nb=i+8*j;
                tab[i][j].addActionListener(e -> {
                    if ((echecs.getTour() && joueur1 instanceof Humain) || (!echecs.getTour() && joueur2 instanceof Humain)) {
                        echecs.action(nb);
                        requestFocusInWindow();
                    }
                });
                jpE.add(tab[i][j]);
            }
        
        jp.add(jpE, BorderLayout.CENTER);

        JPanel jpC=new JPanel(); 
        jpC.setLayout(new GridLayout(4, 1));
        jpC.setPreferredSize(new Dimension(tailleCase, 4*tailleCase));

        changePion=new JButton [4];
        for (int i=0;i<4;i++) {
            changePion[i]=new JButton();
                final int nb=64+i;
                changePion[i].addActionListener(e -> {
                    if ((echecs.getTour() && joueur1 instanceof Humain) || (!echecs.getTour() && joueur2 instanceof Humain)) {
                        echecs.action(nb);
                        requestFocusInWindow();
                    }
                });
                jpC.add(changePion[i]);
        }

        jp.add(jpC, BorderLayout.CENTER);
        this.add(jp, BorderLayout.CENTER);
        this.pack();
        
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        
        String folder="data/img/";

        // Récupère la liste des textures
        File fold=new File (folder);
        Vector <String> textures=new Vector <String> ();
        for (File file : fold.listFiles()) {
            if (!file.isDirectory())
                textures.add(file.getName());
        }
        images = new HashMap <String, ImageIcon>();
        // Charge les images et les stocke dans le HashMap
        for (int i=0;i<textures.size();i++) {
            images.put(textures.get(i),new ImageIcon(new ImageIcon( folder+textures.get(i)).getImage().getScaledInstance(tailleCase, tailleCase, java.awt.Image.SCALE_FAST))); // Load the images and store them in the HashMap
        }
    }

    /**
     * Met à jour l'interface graphique
     * @param o l'objet observable
     * @param arg l'objet passé en argument
     */
    @Override
    public void update(Observable o, Object arg) {

        // couleur du plateau
        for (int i=0;i<8;i++)
            for (int j=0;j<8;j++) {
                tab[i][j].setIcon(null);
                if ((i+j)%2==1)
                    tab[i][j].setBackground(Color.DARK_GRAY);
                else
                    tab[i][j].setBackground(Color.WHITE);
            }
        if (echecs.getOrdre()==1) {
            int caseSelec=echecs.getCasePos();
            int x=caseSelec%8;
            int y=caseSelec/8;
            tab[x][y].setBackground(Color.YELLOW);
            int[] dep=echecs.getDepPossibles();
            for (int i=0;i<dep.length;i++) {
                x=dep[i]%8;
                y=dep[i]/8;
                tab[x][y].setBackground(Color.RED);
            }
        }
        else {
            
            int caseDep=echecs.getCaseDep();
            if (caseDep>=0) {
                int x=caseDep%8;
                int y=caseDep/8;
                tab[x][y].setBackground(Color.YELLOW);
                int caseSelec=echecs.getCasePos();
                x=caseSelec%8;
                y=caseSelec/8;
                tab[x][y].setBackground(Color.YELLOW);
            }
        }

        // place les pions
        int[][] plateau = echecs.getPlateau();
        for (int x = 0; x < plateau.length; x++)
            for (int y = 0; y < plateau[x].length; y++) {
                switch(plateau[x][y]) {
                    case 1: tab[x][y].setIcon(images.get("bp.png"));break;
                    case 2: tab[x][y].setIcon(images.get("bc.png"));break;
                    case 3: tab[x][y].setIcon(images.get("bf.png"));break;
                    case 4: tab[x][y].setIcon(images.get("bt.png"));break;
                    case 5: tab[x][y].setIcon(images.get("bq.png"));break;
                    case 6: tab[x][y].setIcon(images.get("bk.png"));break;
                    case 7: tab[x][y].setIcon(images.get("np.png"));break;
                    case 8: tab[x][y].setIcon(images.get("nc.png"));break;
                    case 9: tab[x][y].setIcon(images.get("nf.png"));break;
                    case 10: tab[x][y].setIcon(images.get("nt.png"));break;
                    case 11: tab[x][y].setIcon(images.get("nq.png"));break;
                    case 12: tab[x][y].setIcon(images.get("nk.png"));break;
                }
            }

        if (echecs.getOrdre()==2) {
            for (int i=0;i<4;i++) {
                changePion[i].setVisible(true);
            }
            if (echecs.getTour()) {
                changePion[0].setIcon(images.get("bc.png"));
                changePion[1].setIcon(images.get("bf.png"));
                changePion[2].setIcon(images.get("bt.png"));
                changePion[3].setIcon(images.get("bq.png"));
            }
            else {
                changePion[0].setIcon(images.get("nc.png"));
                changePion[1].setIcon(images.get("nf.png"));
                changePion[2].setIcon(images.get("nt.png"));
                changePion[3].setIcon(images.get("nq.png"));
            }
        }
        else {
            for (int i=0;i<4;i++)
                changePion[i].setVisible(false);
        }

        switch (echecs.getVictoire()) {
            case 1: System.out.println("Victoire des blancs");break;
            case 2: System.out.println("Victoire des noirs");break;
            case 3: System.out.println("Egalité");break;
        }
        if (echecs.getOrdre()==0) {
            // évaluation
            double eval=echecs.evaluation();
            double seuil = 30;
            if (Math.abs(eval) <= seuil)
                System.out.println("Evaluation : "+eval+" (égalité)");
            else
                if (eval > seuil)
                    System.out.println("Evaluation : "+eval+" (avantage Blanc)");
                else
                    System.out.println("Evaluation : "+eval+" (avantage Noir)");
            
            if (echecs.getTour())
                joueur1.jouer(echecs);
            else
                joueur2.jouer(echecs);
        }
    }
}
