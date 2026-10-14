import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Chatbot {

    private static final String MESSAGE_IGNORANCE = "Je ne sais pas.";
    private static final String MESSAGE_APPRENTISSAGE = "Je vais te l'apprendre.";
    private static final String MESSAGE_BIENVENUE = "J'attends tes questions de culture générale.";
    private static final String MESSAGE_QUITTER = "Au revoir.";
    private static final String MESSAGE_INVITATION = "Je t'écoute.";
    private static final String MESSAGE_CONFIRMATION = "Très bien, c'est noté.";

    private static Index indexThemes; // index pour trouver rapidement les réponses à partir des mots NON outils de la question
    private static Index indexFormes; // index pour trouver rapidement les formes de réponse possibles à partir des mots-outils de la question

    static private ArrayList<String> motsOutils; // vecteur trié des mots outils
    static private ArrayList<String> reponses; // vecteur des réponses
    private static ArrayList<String> formesReponses; //vecteur des formes de réponses
    private static Thesaurus thesaurus; //thésaurus

    public static void main(String[] args) {

        // initialisation du vecteur des mots outils
        motsOutils = Utilitaire.lireMotsOutils("mots-outils.txt");
        // tri du vecteur des mots outils
        Utilitaire.trierChaines(motsOutils);

        // initialisation du vecteur des réponses
        reponses = Utilitaire.lireReponses("reponses.txt");
        //reponses = Utilitaire.lireReponses("mini_reponses.txt");


        // initialisation du thésaurus (partie 2)
        thesaurus = new Thesaurus("thesaurus.txt");

        // construction de l'index pour retrouver rapidement les réponses sur leurs thématiques
        indexThemes = Utilitaire.constructionIndexReponses(reponses, motsOutils, thesaurus);
        indexThemes.afficher();

        // construction de la table des formes de réponses
        formesReponses = Utilitaire.constructionTableFormes(reponses,motsOutils, thesaurus);
        System.out.println(formesReponses);

        // initialisation du vecteur des questions/réponses idéales
        ArrayList<String> questionsReponses = Utilitaire.lireQuestionsReponses("questions-reponses.txt");
        //ArrayList<String> questionsReponses = Utilitaire.lireQuestionsReponses("mini_questions-reponses.txt");

        // construction de l'index pour retrouver rapidement les formes possibles de réponses à partir des mots outils de la question
        indexFormes = Utilitaire.constructionIndexFormes(questionsReponses,formesReponses,motsOutils, thesaurus);
        indexFormes.afficher();


        String reponse = "";
        String entreeUtilisateur = ""; // la dernière entrée de l'utilisateur
        String questionPrecedente = "";



        Scanner lecteur = new Scanner(System.in);
        System.out.println();
        System.out.print("> ");
        System.out.println(MESSAGE_BIENVENUE);

        do { // on attend des questions
            System.out.print("> ");
            entreeUtilisateur = lecteur.nextLine();

            if (entreeUtilisateur.compareToIgnoreCase(MESSAGE_QUITTER) != 0) { //tant que l'utilisateur ne veut pas arrêter
                if (entreeUtilisateur.compareToIgnoreCase(MESSAGE_APPRENTISSAGE) != 0) {
                    if (Utilitaire.entierementInclus(motsOutils, entreeUtilisateur)) {
                        reponse = repondreEnContexte(entreeUtilisateur, questionPrecedente);
                    } else {
                        reponse = repondre(entreeUtilisateur);
                        questionPrecedente = entreeUtilisateur;
                    }
                    System.out.println("> " + reponse);
                }
                else {
                    System.out.println("> " + MESSAGE_INVITATION);
                    System.out.print("> ");
                    String nouvelleReponse = lecteur.nextLine();
                    boolean existe = Utilitaire.reponseExiste(nouvelleReponse, indexThemes, reponses, motsOutils, thesaurus);

                    if (!existe) {
                        Utilitaire.IntegrerNouvelleReponse(nouvelleReponse, reponses, indexThemes, motsOutils, thesaurus);
                    }
                    Utilitaire.integrerNouvelleQuestionReponse(questionPrecedente, nouvelleReponse, formesReponses, indexFormes, motsOutils, thesaurus);

                    System.out.println("> " + MESSAGE_CONFIRMATION);
                }
            }

        } while (entreeUtilisateur.compareToIgnoreCase(MESSAGE_QUITTER) != 0);


    }


    static private String repondre(String question) {
        ArrayList<Integer> reponsesCandidates;
        reponsesCandidates=Utilitaire.constructionReponsesCandidates(question,indexThemes,motsOutils, thesaurus);
        //ArrayList<Integer> reponsesSelectionnees;
        if (reponsesCandidates.isEmpty()){
            return (MESSAGE_IGNORANCE);
        }
        ArrayList<Integer> reponsesSelectionnees = Utilitaire.selectionReponsesCandidates(question, reponsesCandidates, indexFormes, reponses, formesReponses, motsOutils, thesaurus);
        if (reponsesSelectionnees.isEmpty()) {
            return MESSAGE_IGNORANCE;
        }
        Random random = new Random();
        int index = random.nextInt(reponsesSelectionnees.size());
        int idReponse = reponsesSelectionnees.get(index);
        return reponses.get(idReponse);
    }

    // partie 2
    static private String repondreEnContexte(String question, String questionPrecedente) {
        ArrayList<Integer> reponsesCandidates = Utilitaire.constructionReponsesCandidates(questionPrecedente, indexThemes, motsOutils, thesaurus);
        if (reponsesCandidates.isEmpty()) {
            return MESSAGE_IGNORANCE;
        }
        ArrayList<Integer> reponsesSelectionnees = Utilitaire.selectionReponsesCandidates(question, reponsesCandidates, indexFormes, reponses, formesReponses, motsOutils, thesaurus);
        if (reponsesSelectionnees.isEmpty()) {
            return MESSAGE_IGNORANCE;
        }
        Random random = new Random();
        int index = random.nextInt(reponsesSelectionnees.size());
        int idReponse = reponsesSelectionnees.get(index);
        return reponses.get(idReponse);

    }


}