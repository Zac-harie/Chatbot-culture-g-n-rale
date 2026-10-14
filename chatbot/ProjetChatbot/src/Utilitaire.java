import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Scanner;

public class Utilitaire {

    private static final int NBMOTS_FORME = 5; // nombre maximal de mots-outils pris en compte pour les formes dans l'étape 2

    static public ArrayList<String> lireMotsOutils(String nomFichier) {
        //{}=>{résultat = le vecteur des mots outils construit à partir du fichier nomFichier}
        ArrayList<String> motsOutils = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                motsOutils.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return motsOutils;
    }

    static public ArrayList<String> lireReponses(String nomFichier) {
        //{}=>{résultat = le vecteur des réponses construit à partir du fichier nomFichier}
        ArrayList<String> reponses = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                reponses.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reponses;
    }

    static public ArrayList<String> lireQuestionsReponses(String nomFichier) {
        //{}=>{résultat = le vecteur des questions/réponses construit à partir du fichier nomFichier}
        ArrayList<String> questionsReponses = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                questionsReponses.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionsReponses;


    }

    public static void ecrireFichier(String nomFichier, String chaineAEcrire) {
        //{}=>{la chaîne  chaineAEcrire est écrite après saut de ligne à la suite du fichier nomFichier}
        // true = mode append ? écrit à la suite sans effacer ce qui existe
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier, true))) {
            writer.newLine();
            writer.write(chaineAEcrire);// ajoute un retour à la ligne
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static private ArrayList<String> decoupeEnMots(String contenu) {
        //{}=>{résultat = le vecteur des mots de la chaîne contenu après pré-traitements divers}
        String chaine = contenu.toLowerCase();
        chaine = Normalizer.normalize(chaine, Normalizer.Form.NFD);
        chaine = chaine.replaceAll("\\p{M}", "");

        chaine = chaine.replace('\n', ' ');
        chaine = chaine.replace('?', ' ');
        chaine = chaine.replace('-', ' ');
        chaine = chaine.replace('\'', ' ');
        chaine = chaine.replace('.', ' ');
        chaine = chaine.replace(',', ' ');
        chaine = chaine.replace(':', ' ');
        chaine = chaine.replace(';', ' ');
        chaine = chaine.replace('\'', ' ');
        chaine = chaine.replace('"', ' ');
        chaine = chaine.replace('', ' ');
        chaine = chaine.replace('', ' ');
        chaine = chaine.replace("'", " ");
        chaine = chaine.replace('(', ' ');
        chaine = chaine.replace(')', ' ');
        chaine = chaine.replace('«', ' ');
        chaine = chaine.replace('-', ' ');

        chaine = chaine.replace('‑', ' ');
        chaine = chaine.replace("’", " ");


        String[] tabchaine = chaine.split(" ");
        ArrayList<String> resultat = new ArrayList<>();

        for (int i = 0; i < tabchaine.length; ++i) {
            if (!tabchaine[i].equals("")) {
                resultat.add(tabchaine[i]);
            }
        }

        return resultat;
    }


    static private boolean existeChaine(ArrayList<String> mots, String mot) {
        //{}=>  {recherche séquentielle de mot dans mots
        // résultat =  true si trouvé et false sinon }

        int i = 0;
        while (i < mots.size() && !mots.get(i).equals(mot)) {
            i++;
        }
        if (i >= mots.size()) {
            return false;
        } else {
            return true;
        }
    }


    static private boolean existeChaineDicho(ArrayList<String> lesChaines, String chaine) {
        //{lesChaines (triée dans l'ordre lexicographique)}=>  {recherche dichotomique de chaine dans lesChaines
        // résultat =  true si trouvé et false sinon }
        int inf = 0;
        int sup = lesChaines.size() - 1;
        int m;
        while (inf < sup) {
            m = (sup + inf) / 2;
            if (lesChaines.get(m).compareTo(chaine) >= 0) {
                sup = m;
            } else {
                inf = m + 1;
            }
        }
        return lesChaines.get(sup).compareTo(chaine) == 0;
    }

    static public boolean entierementInclus(ArrayList<String> mots, String question) {
        //{mots est trié dans l'ordre lexicographique}=>
        // résultat = true si tous les mots de questions sont dans mots, false sinon
        // remarque : utilise decoupeEnMots et existeChaineDicho}
        ArrayList<String> motsQuestion = decoupeEnMots(question);
        int i = 0;
        while (i<motsQuestion.size() && existeChaineDicho(mots, motsQuestion.get(i))) {
            i++;
        }
        return i>=motsQuestion.size();
    }


    static public int rechercherChaine(ArrayList<String> lesChaines, String chaine) {
        // {}=>{résultat = l'indice de chaine dans lesChaines si trouvé et -1 sinon }
        int i=0;
        while (i<lesChaines.size() && !lesChaines.get(i).equals(chaine)) {
            i++;
        }
        if(i>=lesChaines.size()){
            return -1;
        } else {
            return i;
        }
    }

    static public void integrerNouvelleQuestionReponse(String question, String reponse, ArrayList<String> formes, Index indexFormes, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{la forme de reponse n'existe pas ou n'est pas associée à question dans indexFormes}=>{la forme de reponse est ajoutée à la fin de formes si elle n'y est pas déjà
        // et indexFormes est mis à jour pour tenir compte de cette nouvelle question-réponse
        // remarque 1 : utilise calculForme, rechercherChaine, decoupeEnMots, existeChaineDicho, ajouterSortieAEntree, rechercherSortiePourEntree
        // remarque 2 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}
        String ligneAEcrire;
        if (question.contains("?")) {
            ligneAEcrire = question + " " + reponse;
        } else {
            ligneAEcrire = question + " ? " + reponse;
        }
        ecrireFichier("questions-reponses.txt", ligneAEcrire);

        String formeReponse = calculForme(reponse, motsOutils, thesaurus);
        int indiceForme = rechercherChaine(formes, formeReponse);

        if (indiceForme == -1) {
            formes.add(formeReponse);
            indiceForme = formes.size() - 1;
        }

        String formeQuestion = calculForme(question, motsOutils, thesaurus);
        ArrayList<String> motsOutilsQuestion = decoupeEnMots(formeQuestion);

        int i = 0;
        while (i < motsOutilsQuestion.size() && i < NBMOTS_FORME) {
            String motCle = motsOutilsQuestion.get(i) + "_" + i;
            indexFormes.ajouterSortieAEntree(motCle, indiceForme);

            i++;
        }

    }


    static public void IntegrerNouvelleReponse(String reponse, ArrayList<String> reponses, Index indexContenu, ArrayList<String> motsOutils,Thesaurus thesaurus) {
        //{reponse n'est pas présent dans reponses}=>{reponse est ajoutée à la fin de reponses et indexContenu est mis à jour pour tenir compte de cette nouvelle réponse
        // remarque : utilise decoupeEnMots, existeChaineDicho, ajouterSortieAEntree, rechercherSortiePourEntree
        ecrireFichier("reponses.txt",reponse);
        ArrayList<String> motsReponse = decoupeEnMots(reponse);
        for (int i = 0; i < motsReponse.size(); i++) {
            String motTraite = thesaurus.rechercherSortiePourEntree(motsReponse.get(i));

            if(!existeChaineDicho(motsOutils,motTraite)){
                indexContenu.ajouterSortieAEntree(motTraite,reponses.size());
            }
        }
        reponses.add(reponse);
    }


    static public Index constructionIndexReponses(ArrayList<String> reponses, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{}=>{résultat = un index dont les entrées sont les mots des réponses (reponses) absents de motsOutils.
        // et les sorties sont les indices (dans reponses) des réponses les contenant.
        // remarque : utilise existeChaineDicho, decoupeEnMots et ajouterSortieAEntree }
        Index index = new Index();
        int i = 0;
        while (i < reponses.size()) {
            String reponse = reponses.get(i);
            ArrayList<String> mots = decoupeEnMots(reponse);

            for (int j = 0; j < mots.size(); j++) {

                String motTraite = thesaurus.rechercherSortiePourEntree(mots.get(j));

                if (!existeChaineDicho(motsOutils, motTraite)) {
                    index.ajouterSortieAEntree(motTraite, i);
                }
            }
            i++;

        }
        return index;
    }


    static void trierChaines(ArrayList<String> v) {
        //{}=>{v est trié dans l'ordre lexicographique }
        triFusionWorker(v, 0, v.size() - 1);
    }

    private static void triFusionWorker(ArrayList<String> v, int borneInf, int borneSup) {
        if (borneInf < borneSup) {
            int m = (borneInf + borneSup) / 2;

            triFusionWorker(v, borneInf, m);
            triFusionWorker(v, m + 1, borneSup);

            fusionner(v, borneInf, m, borneSup); //
        }
    }

    private static void fusionner(ArrayList<String> v, int borneInf, int m, int borneSup) {
        ArrayList<String> temp = new ArrayList<>();
        int iG = borneInf;
        int iD = m + 1;

        while (iG <= m && iD <= borneSup) {
            if (v.get(iG).compareTo(v.get(iD)) <= 0) {
                temp.add(v.get(iG));
                iG = iG + 1;
            } else {
                temp.add(v.get(iD));
                iD = iD + 1;
            }
        }

        while (iG <= m) {
            temp.add(v.get(iG));
            iG = iG + 1;
        }

        while (iD <= borneSup) {
            temp.add(v.get(iD));
            iD = iD + 1;
        }

        int k = 0;
        while (k < temp.size()) {
            v.set(borneInf + k, temp.get(k));
            k = k + 1;
        }

    }

    static ArrayList<Integer> maxOccurences(ArrayList<Integer> v, int seuil) {
        //{v trié} => {résultat = vecteur des entiers dont le nombre d'occurences
        // est maximal et au moins égal au seuil. Si le nombre d'occurences maximal est inférieur au seuil , un vecteur vide est retourné.
        // Par exemple, si V est [3,4,5,5,5,6,6,8,8,8,12,16,16,20]
        // si seuil<=3 alors le résultat est [5,8].
        // si le seuil>3 alors le résultat est []}
        ArrayList<Integer> maxoccu = new ArrayList<>();
        int i = 0;
        while (i < v.size()) {
            int compteur = 0;
            int j = i;
            while (j < v.size() && v.get(i).equals(v.get(j))) {
                compteur++;
                j++;
            }
            if (compteur >= seuil) {
                maxoccu.add(v.get(i));
            }
            i = j;
        }
        return maxoccu;
    }

    static ArrayList<Integer> fusion(ArrayList<Integer> v1, ArrayList<Integer> v2) {
        //{v1 et v2 triés}=>{résultat = vecteur trié fusionnant v1 et v2 sans supprimer les répétitions
        // par exemple si v1 est [4,8,8,10,25] et v2 est [5,8,9,25]
        // le résultat est [4,5,8,8,8,9,10,25,25]}
        ArrayList<Integer> resultat = new ArrayList<>();
        int i = 0;
        int j = 0;

        while (i < v1.size() && j < v2.size()) {
            if (v1.get(i) <= v2.get(j)) {
                resultat.add(v1.get(i));
                i++;
            } else {
                resultat.add(v2.get(j));
                j++;
            }
        }

        while (i < v1.size()) {
            resultat.add(v1.get(i));
            i++;
        }

        while (j < v2.size()) {
            resultat.add(v2.get(j));
            j++;
        }

        return resultat;
    }


    static String calculForme(String chaine, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{}=>{résultat = la concaténation des NBMOTS_FORME premiers mots-outils de chaine séparés par des blancs
        // remarque 1 : utilise decoupeMots et existeChaineDicho
        // remarque 2 : la limitation de la taille des formes permet d'accepter des réponses terminant par des précisions }
        String res = "";
        ArrayList<String> mots = decoupeEnMots(chaine);

        for (int i = 0; i < mots.size(); i++) {

            String motTraite = thesaurus.rechercherSortiePourEntree(mots.get(i));

            if (estUnNombre(motTraite)) {
                motTraite="NUM";
            }

            if (existeChaineDicho(motsOutils, motTraite)) {
                res = res + motTraite + " ";
            }
        }
        return res.stripTrailing();
    }

    static public ArrayList<String> constructionTableFormes(ArrayList<String> reponses, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{}=>{résultat = le vecteur de toutes les formes de réponses dans reponses.
        // remarque : utilise calculForme et existeChaine }
        ArrayList<String> formes = new ArrayList<>();
        String forme;
        for (int i = 0; i < reponses.size(); i++) {
            forme = calculForme(reponses.get(i), motsOutils, thesaurus);
            if (!existeChaine(formes, forme)) {
                formes.add(forme);
            }
        }
        return formes;
    }

    static public Index constructionIndexFormes(ArrayList<String> questionsReponses, ArrayList<String> formes, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{}=>{résultat = un index dont les entrées sont les "mots-outils positionnés" des questions (par exemple l'entrée pour un "Qui" en première position sera "qui_0")
        // et les sorties sont les indices (dans formes) des formes de réponses répondant aux questions contenant le mot-outil à cette position.
        // remarque 1 : utilise calculForme, rechercherChaine, decoupeEnMots, rechercherEntree, existeChaineDicho et ajouterSortieAEntree
        // remarque 2 : utilisez les méthodes indexOf et substring de String pour décomposer la question-réponse en question et réponse
        // remarque 3 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}
        Index index = new Index();
        int indiceReponse;
        String reponse;
        String formeReponse;
        int indiceFormeReponse;
        String formeQuestion;

        ArrayList<String> motsOutilsQuest = new ArrayList<>();
        int i = 0;
        while (i < questionsReponses.size()) {
            indiceReponse = questionsReponses.get(i).indexOf("?");
            reponse = questionsReponses.get(i).substring(indiceReponse + 1);
            formeReponse = calculForme(reponse, motsOutils, thesaurus);

            indiceFormeReponse = rechercherChaine(formes, formeReponse);
            if (indiceFormeReponse >= 0) {
                formeQuestion = calculForme(questionsReponses.get(i).substring(0, indiceReponse), motsOutils, thesaurus);

                motsOutilsQuest = decoupeEnMots(formeQuestion);
                for (int j = 0; j < motsOutilsQuest.size(); j++) {
                    motsOutilsQuest.set(j, motsOutilsQuest.get(j) + "_" + j);
                }

                for (int k = 0; k < motsOutilsQuest.size(); k++) {
                    index.ajouterSortieAEntree(motsOutilsQuest.get(k), indiceFormeReponse);
                }
            }
            i++;
        }
        return index;
    }

    static public ArrayList<Integer> constructionReponsesCandidates(String question, Index IndexReponses, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{}=>{résultat = vecteur des identifiants de réponses contenant l'ensemble des mots non outils de la question.
        // remarque 1 : utilise decoupeEnMots, existeChaineDicho, rechercherSorties, fusion et maxOccurences
        // remarque 2 : maxOccurences est appelé en passant le nombre de mots non outils de la question comme valeur de seuil.
        // remarque 3 : on aurait pu calculer directement une intersection au lieu d'une fusion et se passer de maxOccurences mais on
        // souhaite pouvoir garder la possibilité d'assouplir par la suite la contrainte sur la présence de l'intégralité
        // des mots de la question dans la réponse }

        ArrayList<String> motsQuestion = decoupeEnMots(question);
        ArrayList<String> motsNonOutilsQuestion = new ArrayList<>();

        for (int i = 0; i < motsQuestion.size(); i++) {
            String motTraite = thesaurus.rechercherSortiePourEntree(motsQuestion.get(i));

            if (!existeChaineDicho(motsOutils, motTraite)) {
                motsNonOutilsQuestion.add(motTraite);
            }
        }

        ArrayList<Integer> sortie = IndexReponses.rechercherSorties(motsNonOutilsQuestion.get(0));
        for (int j = 1; j < motsNonOutilsQuestion.size(); j++) {
            sortie = fusion(sortie, IndexReponses.rechercherSorties(motsNonOutilsQuestion.get(j)));
            }

        ArrayList<Integer> resultat = maxOccurences(sortie, motsNonOutilsQuestion.size());

        return resultat;
    }


    static public boolean estUnNombre(String s) {
        //{s est non vide}=>{résultat = true si s ne contient que des caractères représentant des chiffres (>='0'&<='9') et false sinon}
        int i = 0;
        while (i < s.length() && Character.isDigit(s.charAt(i))) {
            i++;
        }
        return i>= s.length();
    }


    static public ArrayList<Integer> selectionReponsesCandidates(String question,
                                                                 ArrayList<Integer> candidates,
                                                                 Index IndexFormes,
                                                                 ArrayList<String> reponses,
                                                                 ArrayList<String> formesReponses,
                                                                 ArrayList<String> motsOutils,
                                                                 Thesaurus thesaurus) {
        //{}=>{résultat = vecteur des identifiants de réponses (parmi les candidates) dont la forme est cohérente
        // avec la question.
        // remarque 1 : utilise decoupeEnMots, existeChaineDicho, rechercherSorties, fusion, maxOccurences, calculForme
        // remarque 2 : l'algorithme procède en 2 temps. D'abord il trouve les formes de réponses qui répondent à la question.
        // puis ajoute au résultat l'identifiant des réponses candidates qui respectent au moins une de ces formes.
        // remarque 3 : pour trouver les formes de réponses qui répondent à la question, on utilise l'index des formes, et on sélectionne
        // en appelant maxOccurences (avec seuil = nombre des mots-outils de la question) celles associées dans l'index à tous les mots-outils de la question.
        // remarque 4 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}

        String formeQuestion = calculForme(question, motsOutils, thesaurus);
        ArrayList<String> motsOutilsQuest = decoupeEnMots(formeQuestion);

        while (motsOutilsQuest.size() > NBMOTS_FORME) {
            motsOutilsQuest.remove(motsOutilsQuest.size() - 1);
        }

        if (motsOutilsQuest.isEmpty()) {
            return new ArrayList<>();
        }

        String premierMotPositionne = motsOutilsQuest.get(0) + "_0";
        ArrayList<Integer> sortiesFusionnees = IndexFormes.rechercherSorties(premierMotPositionne);

        for (int i = 1; i < motsOutilsQuest.size(); i++) {
            String motPositionne = motsOutilsQuest.get(i) + "_" + i;
            ArrayList<Integer> sortieMot = IndexFormes.rechercherSorties(motPositionne);
            sortiesFusionnees = fusion(sortiesFusionnees, sortieMot);
        }

        ArrayList<Integer> indicesFormesCompatibles = maxOccurences(sortiesFusionnees, motsOutilsQuest.size());

        ArrayList<Integer> resultats = new ArrayList<>();

        for (Integer idReponse : candidates) {
            String reponse = reponses.get(idReponse);

            String formeDeLaReponse = calculForme(reponse, motsOutils, thesaurus);

            int idForme = rechercherChaine(formesReponses, formeDeLaReponse);

            if (idForme != -1 && indicesFormesCompatibles.contains(idForme)) {
                resultats.add(idReponse);
            }
        }

        return resultats;
    }

    static public boolean reponseExiste(String reponse,
                                        Index indexReponses,
                                        ArrayList<String> reponses,
                                        ArrayList<String> motsOutils,
                                        Thesaurus thesaurus) {
        //{}=>{résultat = true si la reponse est présente dans reponses et false sinon.
        // remarque 1 : utilise decoupeEnMots, rechercherSortiePourEntree, existeChaineDicho, rechercherSorties, fusion, maxOccurences
        // remarque 2 : Le vecteur reponses n'est pas trié. Afin d'éviter le coûteux parcours séquentiel du
        // vecteur, on utilise indexReponses pour trouver les réponses contenant tous les mots non outils de la
        // reponse, puis on vérifie si l'une d'entre elle est identique à reponse.}
        ArrayList<String> motsReponse = decoupeEnMots(reponse);
        ArrayList<Integer> repMotsTheme = new ArrayList<>();
        int comptMotTheme=0;
        for (int i = 0; i < motsReponse.size(); i++) {

            String motTraite=thesaurus.rechercherSortiePourEntree(motsReponse.get(i));

            if (!existeChaineDicho(motsOutils, motTraite)) {
                repMotsTheme=fusion(repMotsTheme,indexReponses.rechercherSorties(motTraite));
                comptMotTheme++;
            }
        }
        repMotsTheme=maxOccurences(repMotsTheme,comptMotTheme);
        if(repMotsTheme.isEmpty()){
            return false;
        }

        int j=0;
        while(j<repMotsTheme.size() && reponse.compareTo(reponses.get(repMotsTheme.get(j)))!=0){
            j++;
        }
        return j<repMotsTheme.size();

    }


    static public boolean formeQuestionReponseExiste(String question,
                                                     String reponse,
                                                     Index indexFormes,
                                                     ArrayList<String> formesReponses,
                                                     ArrayList<String> motsOutils,
                                                     Thesaurus thesaurus) {
        //{}=>{résultat = * true si la forme de reponse est présente dans formesReponses
        // et qu'elle est accessible à partir des mots de la question en utilisant indexFormes.
        //                * false sinon.
        // remarque 1 : utilise decoupeEnMots, rechercherSortiePourEntree, existeChaineDicho, rechercherSorties, fusion, maxOccurences, calculForme
        // remarque 2 : Le vecteur formesReponses n'est pas trié. Afin d'éviter le coûteux parcours séquentiel du
        // vecteur, et afin de vérifier l'accessibilité à partir des mots de la question en utilisant indexFormes,
        // on utilise indexFormes pour trouver les formes indexées par les mots-outils de la
        // question, puis on vérifie si l'une de ces formes est identique à la forme de reponse.
        // remarque 3 : seuls les NBMOTS_FORME premiers mots-outils de question sont pris en compte}

        String formeQuestion = calculForme(question, motsOutils, thesaurus);
        String formeReponse = calculForme(reponse, motsOutils, thesaurus);
        ArrayList<String> motsOutilsQuestion = decoupeEnMots(formeQuestion);

        while (motsOutilsQuestion.size() > NBMOTS_FORME) {
            motsOutilsQuestion.remove(motsOutilsQuestion.size() - 1);
        }

        if (motsOutilsQuestion.isEmpty()) {
            return false;
        }

        String premierMotPositionne = motsOutilsQuestion.get(0) + "_0";
        ArrayList<Integer> sortiesFusionnees = indexFormes.rechercherSorties(premierMotPositionne);

        for (int i = 1; i < motsOutilsQuestion.size(); i++) {
            String motPositionne = motsOutilsQuestion.get(i) + "_" + i;
            ArrayList<Integer> sortieMot = indexFormes.rechercherSorties(motPositionne);
            sortiesFusionnees = fusion(sortiesFusionnees, sortieMot);
        }

        ArrayList<Integer> indicesFormesCompatibles = maxOccurences(sortiesFusionnees, motsOutilsQuestion.size());

        if (indicesFormesCompatibles.isEmpty()) {
            return false;
        }

        int j=0;
        while(j<indicesFormesCompatibles.size() && formeReponse.compareTo(formesReponses.get(indicesFormesCompatibles.get(j)))!=0){
            j++;
        }

        return j<indicesFormesCompatibles.size();
    }
}
