import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Game {
    private final char[] vowels = "aeiou".toCharArray();
    private final char[] consonants = "qwrtypsdfghjklzxcvbnm".toCharArray();
    private static final HashMap<Character, Integer> letterScores = new HashMap<>();
    private HashSet<Character> letterSet;
    private static final HashSet<String> englishWords = new HashSet<>(370105);
    private String currentLetters;
    private int roundNumber;
    private final int numberOfRounds;
    private Boolean gameDone;
    private Boolean gameStarted;
    private ArrayList<String> scrambles;

    private int scramblesIndex;

    /**
     * Constructs a new game
     * @param numRounds number of rounds
     */
    public Game(int numRounds) {
        if(numRounds <= 0){
            numRounds = 5;
        }
        numberOfRounds = numRounds;
        gameStarted = false;
        gameDone = false;
        roundNumber = 1;
        scramblesIndex = 0;
        scrambles = new ArrayList<>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader("resources/scrambles.txt"));
            String scramble = fileReader.readLine();
            while (scramble != null) {
                scrambles.add(scramble);
                scramble = fileReader.readLine();
            }
            currentLetters = scrambles.get(0);
            letterSet = new HashSet<>(currentLetters.length());
            for(int i = 0; i < currentLetters.length(); i++){
                letterSet.add(currentLetters.charAt(i));
            }

        } catch (IOException e) {
            System.out.println("Could not open scrambles.txt");
            currentLetters = generateLetters();
        }
    }

    /**
     * Constructs a new game
     * @param roundNumber the current round number
     * @param scrambles the list of scrambles
     * @param numberOfRounds the number of rounds
     * @param gameStarted boolean representing if game has started
     * @param gameEnded boolean representing if game has ended
     * @param scramblesIndex the current index of scrambles
     */
    public Game(int roundNumber, ArrayList<String> scrambles, int numberOfRounds, boolean gameStarted, boolean gameEnded, int scramblesIndex) {
        if(roundNumber <= 0){
            roundNumber = 1;
        }
        if(numberOfRounds <= 0){
            numberOfRounds = 5;
        }
        this.roundNumber = roundNumber;
        this.scrambles = scrambles;
        this.currentLetters = scrambles.get(scramblesIndex);
        this.numberOfRounds = numberOfRounds;
        this.gameStarted = gameStarted;
        this.gameDone = gameEnded;
        this.scramblesIndex = scramblesIndex;

        letterSet = new HashSet<>();
        for (char c: currentLetters.toCharArray()) {
            letterSet.add(c);
        }
    }

    /**
     * Reads in all the words in the english dictionary and stores them in englishWords
     */
    public static void readInWords() {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader("resources/words.txt"));
            String word = fileReader.readLine();
            while (word != null) {
                englishWords.add(word);
                word = fileReader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Could not read words.txt");
        }
    }

    /**
     * Moves the game to the next round
     */
    public void nextRound(){
        DatabaseInteractor db = new DatabaseInteractor();

        if(roundNumber + 1 >= numberOfRounds){
            gameDone = true;
            db.changegamedone();
            return;
        }
        db.updateRoundNumber();
        roundNumber++;
        currentLetters = scrambles.get(scramblesIndex); // TODO: replace with the code for pre-defined scramble letters

        letterSet.clear();
        for(int i = 0; i < currentLetters.length(); i++){
            letterSet.add(currentLetters.charAt(i));
        }

        db.updateScrambleIndex();
        scramblesIndex ++;
    }

    /**
     * Scores a string
     * @param word the string to be scored
     * @return the score
     */
    public int scoreWord(String word) {
        if (englishWords.contains(word.trim().toLowerCase())) {
            for (char c: word.toCharArray()) {
                if (!letterSet.contains(c)) {
                    return 0;
                }
            }
            int score = 0;
            for (char c: word.toCharArray()) {
                score += letterScores.get(c);
            }

            HashMap<Character, Integer> letterFrequency = new HashMap<>();
            for (char c: word.toCharArray()) {
                if (letterFrequency.containsKey(c)) {
                    letterFrequency.replace(c, letterFrequency.get(c)+1);
                }
                else {
                    letterFrequency.put(c, 1);
                }
            }
            if (letterFrequency.size() == 5) {
                score *= 2;
            }
            //check back to see if move up to line 55
            for (char c: word.toCharArray()) {
                if (letterFrequency.containsKey(c)) {
                    score += (letterFrequency.get(c) - 1) * letterScores.get(c);
                }
            }
            if (word.length() > 5) {
                int i = 0;
                for (char c: word.toCharArray()) {
                    if (i > 5) {
                        score += letterScores.get(c);
                    }
                    i++;
                }
            }
            return score;
        }
        return 0;
    }

    /**
     * returns a String of the 5 letters.
     * @return String of the 5 letters
     */
    public String generateLetters() {
        HashSet<Character> letterSet = new HashSet<>(5);
        Random r = new Random();
        while (letterSet.size() < 2) {
            letterSet.add(vowels[r.nextInt(5)]);
        }
        while (letterSet.size() < 5) {
            letterSet.add(consonants[r.nextInt(21)]);
        }
        StringBuilder letters = new StringBuilder();
        for (Character c : letterSet) {
            letters.append(c);
        }
        this.letterSet = letterSet;
        return letters.toString();
    }

    /**
     * Shuffles the letters in a random order
     * @param letters the letters to be shuffled
     * @return the shuffled letters
     */
    public static String shuffleLetters(String letters) {
        // the following code is adapted from: https://www.hellocodeclub.com/how-to-shuffle-a-string-in-java/
        List<Character> characters = new LinkedList<>();
        for(char c : letters.toCharArray()){
            characters.add(c);
        }
        StringBuilder result = new StringBuilder();
        for (int index=0; index<letters.length(); index++){
            int randomPosition = new Random().nextInt(characters.size());
            result.append(characters.get(randomPosition));
            characters.remove(randomPosition);
        }
        return result.toString();
    }

    /**
     * Fills letterScores
     */
    public static void fillLetterScores() {
        letterScores.put('a',1);
        letterScores.put('b',3);
        letterScores.put('c',3);
        letterScores.put('d',2);
        letterScores.put('e',1);
        letterScores.put('f',4);
        letterScores.put('g',2);
        letterScores.put('h',4);
        letterScores.put('i',1);
        letterScores.put('j',1);
        letterScores.put('k',5);
        letterScores.put('l',1);
        letterScores.put('m',3);
        letterScores.put('n',1);
        letterScores.put('o',1);
        letterScores.put('p',3);
        letterScores.put('q',10);
        letterScores.put('r',1);
        letterScores.put('s',1);
        letterScores.put('t',1);
        letterScores.put('u',1);
        letterScores.put('v',4);
        letterScores.put('w',4);
        letterScores.put('x',8);
        letterScores.put('y',4);
        letterScores.put('z',10);
    }

    /**
     * Gets the round number
     * @return roundNumber
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Gets the current letters
     * @return currentLetters
     */
    public String getCurrentLetters() {
        return currentLetters;
    }

    /**
     * Sets the current letters for the round
     * @param currentLetters  current letters for the round
     */
    public void setCurrentLetters(String currentLetters) {
        this.currentLetters = currentLetters;
        HashSet<Character> tmp = new HashSet<>();
        for(char c: currentLetters.toCharArray()) {
            tmp.add(c);
        }
        letterSet = tmp;
    }

    /**
     * Gets the number of rounds
     * @return the number of rounds
     */
    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    /**
     * Gets the gameDone boolean
     * @return gameDone
     */
    public Boolean getGameDone() {
        return gameDone;
    }

    /**
     * Gets gameStarted boolean
     * @return gameStarted
     */
    public Boolean getGameStarted() {
        return gameStarted;
    }

    /**
     * Gets the scrambles
     * @return scrambles
     */
    public ArrayList<String> getScrambles() {
        return scrambles;
    }

    /**
     * Gets the scramblesIndex
     * @return scramblesIndex
     */
    public int getScramblesIndex() {
        return scramblesIndex;
    }

    public static void main(String[] args) {
        Game game = new Game(3);
        game.setCurrentLetters("dfgeu");
        System.out.println(game.scoreWord("dud"));
        System.out.println(game.scoreWord("dug"));
        System.out.println(game.scoreWord("due"));
        System.out.println(game.scoreWord("ue"));
        System.out.println(game.scoreWord("hello"));
    }

    public void setGameStarted(boolean b) {
        gameStarted = b;
    }
}
