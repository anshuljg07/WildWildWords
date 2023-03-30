import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The class GameTesting contains the test for the Game Class
 */
public class GameTesting {
    /**
     * Method to read in the words and the value of each letter
     */
    @BeforeAll
    public static void initializeGameClass(){
        Game.fillLetterScores();
        Game.readInWords();
    }

    /**
     * checkNextRound Method tests that
     */
    @Test
    public void checkNextRound(){
        Game game = new Game(3);
        String oldLetters = game.getCurrentLetters();
        game.nextRound();
        int rd = game.getRoundNumber();
        String nextLetters = game.getCurrentLetters();
        Assertions.assertEquals(rd,2);
        Assertions.assertNotEquals(oldLetters,nextLetters);

    }

    @Test
    public void checkNumRound1(){
        Game game = new Game(-1);
        int rdval1 = game.getNumberOfRounds();
        Assertions.assertEquals(rdval1,5);
    }
    @Test
    public void checkNumRound2(){
        Game game = new Game(0);
        int rdval2 = game.getNumberOfRounds();
        Assertions.assertEquals(rdval2,5);
    }

    @Test
    public void checkNumRound3(){
        Game game = new Game(4);
        int rdval3 = game.getNumberOfRounds();
        Assertions.assertEquals(rdval3,4);
    }

    @Test
    public void checkNumRound4(){
        Game game = new Game(10);
        int rdval4 = game.getNumberOfRounds();
        Assertions.assertEquals(rdval4,10);
    }

    @Test
    public void checkScoreWord(){
        Game game = new Game(3);
        game.setCurrentLetters("egnoftym");
        int score1 = game.scoreWord("gone");
        int score2 = game.scoreWord("eggnog");
        int score3 = game.scoreWord("gym");
        int score4 = game.scoreWord("");
        Assertions.assertEquals(score1,5);
        Assertions.assertEquals(score2,21);
        Assertions.assertEquals(score3,9);
        Assertions.assertEquals(score4,0);
    }

    @Test
    public void checkScoreWord2(){
        Game game = new Game(3);
        game.setCurrentLetters("dfuge");
        int score5 = game.scoreWord("fudge");
        int score6 = game.scoreWord("bin");
        Assertions.assertEquals(score5,20);
        Assertions.assertEquals(score6,0);
    }

    @Test
    public void checkScoreWord3(){
        Game game = new Game(3);
        game.setCurrentLetters("");
        int score5 = game.scoreWord("food");
        int score6 = game.scoreWord("");
        Assertions.assertEquals(score5,0);
        Assertions.assertEquals(score6,0);
    }
    @Test
    public void checkScoreWord4(){
        Game game = new Game(3);
        game.setCurrentLetters("");
        int score5 = game.scoreWord("food");
        int score6 = game.scoreWord("");
        Assertions.assertEquals(score5,0);
        Assertions.assertEquals(score6,0);

    }

    @Test
    public void checkGenerateLetters(){
    Game game = new Game(3);
    String x = game.generateLetters();
    game.setCurrentLetters(x);
    String currLetters = game.getCurrentLetters();
    Assertions.assertEquals(currLetters,x);

    }
    @Test
    public void checkGenerateLetters2(){
        Game game = new Game(0);
        String x1 = game.generateLetters();
        game.setCurrentLetters(x1);
        String currLetters1 = game.getCurrentLetters();
        Assertions.assertEquals(currLetters1,x1);

    }

    @Test
    public void checkGenerateLetters3(){
        Game game = new Game(-1);
        String x2 = game.generateLetters();
        game.setCurrentLetters(x2);
        String currLetters2 = game.getCurrentLetters();
        Assertions.assertEquals(currLetters2,x2);

    }
}
