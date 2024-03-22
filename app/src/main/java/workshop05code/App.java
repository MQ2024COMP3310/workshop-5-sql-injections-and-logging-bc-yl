package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//For regex
import java.util.regex.Pattern;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.FINE,"Wordle created and connected.");
        } else {
            logger.log(Level.SEVERE, "Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.FINE, "Wordle structures in place.");
        } else {
            logger.log(Level.SEVERE, "Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                logger.log(Level.FINE, line);
                wordleDatabaseConnection.addValidWord(i, line);
                i++;
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Not able to load . Sorry!");
            logger.log(Level.SEVERE, e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();
            String regex = "^[a-zA-Z]{4}$";
            while (!guess.equals("q")) {
                if (Pattern.matches(regex, guess)){
                    System.out.println("You've guessed '" + guess+"'.");
                    String result = wordleDatabaseConnection.isValidWord(guess) ? "Success! It is in the list. \n" :"Sorry. This word is NOT in the list. \n";
                    System.out.println(result);
                }
                else {
                    String input = "This is their input: " + guess;
                    logger.log(Level.SEVERE, input);
                    System.out.println("Sorry. Your input is NOT valid. Please try again.");
                }
                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
        }

    }
}