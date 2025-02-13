
// For the Arrays and Collections classes
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class MemoryField {
    // An inner class for the event handler of the timer
    class TimerHandler implements EventHandler<ActionEvent> {
        @Override
        // This method calls the closeCard() method
        public void handle(ActionEvent arg0) {
            closeCard();
        }
    }

    // Array for the cards
    private MemoryCard[] cards;
	
    // Array for the names of the images
    private String[] images = {"graphics/apple.jpg", "graphics/pear.jpg", "graphics/flower.jpg", "graphics/flower2.jpg",
            "graphics/duck.jpg", "graphics/fish.jpg", "graphics/fox.jpg", "graphics/hedgehog.jpg",
            "graphics/kangaroo.jpg", "graphics/cat.jpg", "graphics/cow.jpg", "graphics/mouse1.jpg",
            "graphics/mouse2.jpg", "graphics/mouse3.jpg", "graphics/melon.jpg", "graphics/mushroom.jpg",
            "graphics/ronny.jpg", "graphics/butterfly.jpg", "graphics/sun.jpg",
            "graphics/cloud.jpg", "graphics/mouse4.jpg"};
	
    // For the points
    private int humanPoints, computerPoints;
    // Two labels for the points
    private Label humanPointsLabel, computerPointsLabel;
    
    // A label to indicate whose turn it is
    private Label turnLabel;
    
    // How many cards are currently flipped?
    private int flippedCards;
    
    // For the currently flipped pair
    private MemoryCard[] pair;
    
    // For the current player
    private int player;
    
    // The "memory" for the computer
    // Stores the location of the matching pair
    private int[][] rememberedCards;
    
    // For the difficulty level
    private int difficulty;
    
    // List for the Cheat
    private List<MemoryCard> alreadyRevealed = new ArrayList<>();
    
    // For the timer
    private Timeline timer;
	
    // Constructor
    public MemoryField() {
        // Create the array for the cards, 42 in total
        cards = new MemoryCard[42];

        // For the pair
        pair = new MemoryCard[2];

        // For the memory
        // Stores the position on the game board for each pair of cards
        rememberedCards = new int[2][21];
        
        // No one has points at the beginning
        humanPoints = 0;
        computerPoints = 0;
        
        // No cards are flipped
        flippedCards = 0;
        
        // The human starts
        player = 0;
        
        // Difficulty level is set to 10
        difficulty = 10;
        
        // No remembered cards
        for (int outer = 0; outer < 2; outer++)
            for (int inner = 0; inner < 21; inner++)
                rememberedCards[outer][inner] = -1;
    }

    // This method creates the UI and draws the cards using a separate method
    // A FlowPane is passed in
    public FlowPane initGUI(FlowPane field) {
        // For displaying the cards
        drawCards(field);
        humanPointsLabel = new Label();
        computerPointsLabel = new Label();
        turnLabel = new Label(); // Create new label
        turnLabel.setText("Human"); // Human starts, so set the text accordingly
        humanPointsLabel.setText(Integer.toString(humanPoints));
        computerPointsLabel.setText(Integer.toString(computerPoints));
        
        // Display in two columns
        GridPane tempGrid = new GridPane();
        // Insert into grid with coordinates
        tempGrid.add(new Label("Human: "), 0 , 0 );
        tempGrid.add(humanPointsLabel, 1, 0);
        
        tempGrid.add(new Label("Computer: "), 0, 1);
        tempGrid.add(computerPointsLabel, 1, 1);
        
        tempGrid.add(new Label("Current turn: "), 0, 2);
        tempGrid.add(turnLabel, 1, 2); 
        
        Button cheat = new Button("Cheat");
        cheat.setDisable(!isMoveAllowed());
        cheat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                cheat();
            }
        });
        
        tempGrid.add(cheat, 1, 3);
        
        field.getChildren().add(tempGrid);
        return field;
    }
	
    // Create the actual game board
    private void drawCards(FlowPane field) {
        int count = 0;
        for (int i = 0; i <= 41; i++) {
            // Create a new card
            cards[i] = new MemoryCard(images[count], count, this);
            // Every second card gets a new image
            if ((i + 1) % 2 == 0)
                count++;
        }
        // Shuffle the cards
        Collections.shuffle(Arrays.asList(cards));

        // Place them on the game board
        for (int i = 0; i <= 41; i++) {
            field.getChildren().add(cards[i]);
            // Set the position of the card
            cards[i].setImagePosition(i);
        }
    }
	
    // This method controls the core game logic
    // It is executed when clicking a card
    public void openCard(MemoryCard card) {
        // Temporary storage for ID and position
        int cardID, cardPos;

        // Store the cards temporarily
        pair[flippedCards] = card;
        
        // Get ID and position
        cardID = card.getImageID();
        cardPos = card.getImagePosition();
        
        // Store the card in the computer's memory
        if (rememberedCards[0][cardID] == -1) 
            rememberedCards[0][cardID] = cardPos;
        else if (rememberedCards[0][cardID] != cardPos) 
            rememberedCards[1][cardID] = cardPos;
        
        flippedCards++;
        
        // If two cards are flipped, check if they match
        if (flippedCards == 2) {
            checkPair(cardID);
            timer = new Timeline(new KeyFrame(Duration.millis(5000), new TimerHandler()));
            timer.play();
        }
        
        // If all 21 pairs are found, end the game
        if (computerPoints + humanPoints == 21) {
            if (computerPoints > humanPoints) {
                JOptionPane.showMessageDialog(null, "The computer wins!\nComputer Points: " + computerPoints + " Your Points: " + humanPoints);
                Platform.exit();
            }
            if (humanPoints > computerPoints) {
                JOptionPane.showMessageDialog(null, "You win!\nYour Points: " + humanPoints + " Computer Points: " + computerPoints);
                Platform.exit();
            }    
        }
    }
	
 // The method flips the cards back to the back side or removes them from the game
    private void closeCard() {
        boolean remove = false;
        // Is it a pair?
        if (pair[0].getImageID() == pair[1].getImageID()) 
            remove = true;
        // If it's a pair, remove the cards from the game
        // Otherwise, just flip them back
        pair[0].showBack(remove);
        pair[1].showBack(remove);
        // No more cards are open
        flippedCards = 0;
        // If the player didn't find a pair, switch turns
        if (!remove) 
            switchPlayer();
        else 
            // If the computer found a pair, it gets another turn
            if (player == 1)
                computerMove();
    }

    // The method checks if a pair has been found
    private void checkPair(int cardID) {
        if (pair[0].getImageID() == pair[1].getImageID()) {
            // Update the score
            pairFound();
            // Remove the cards from memory
            rememberedCards[0][cardID] = -2;
            rememberedCards[1][cardID] = -2;
        }
    }

    // The method updates the score when a pair is found
    private void pairFound() {
        // Is the human playing?
        if (player == 0) {
            humanPoints++;
            humanPointsLabel.setText(Integer.toString(humanPoints));
        } else {
            computerPoints++;
            computerPointsLabel.setText(Integer.toString(computerPoints));
        }
    }

    // The method switches the player
    private void switchPlayer() {
        // If it was the human's turn, now it's the computer's turn
        if (player == 0) {
            player = 1;
            turnLabel.setText("The Computer"); // Update text when computer plays
            computerMove();
        } else {
            player = 0;
            turnLabel.setText("The Human"); // Update text when human plays
        }
    }

    // The method executes the computer's moves
    private void computerMove() {
        int cardCounter = 0;
        int randomIndex = 0;
        boolean foundPair = false;
        
        // Control based on difficulty level
        if ((int)(Math.random() * difficulty) == 0) {
            // First, search for a pair
            // Scan the rememberedCards array to find a match in both dimensions
            while ((cardCounter < 21) && (!foundPair)) {
                // Is there a value >= 0 in both dimensions?
                if ((rememberedCards[0][cardCounter] >= 0) && (rememberedCards[1][cardCounter] >= 0)) {
                    // A pair has been found
                    foundPair = true;
                    // Show the front of the first card
                    cards[rememberedCards[0][cardCounter]].showFront();
                    // Open the card
                    openCard(cards[rememberedCards[0][cardCounter]]);
                    // Show the front of the second card
                    cards[rememberedCards[1][cardCounter]].showFront();
                    openCard(cards[rememberedCards[1][cardCounter]]);
                }
                cardCounter++;
            }
        }
        
        // If no pair was found, randomly flip two cards
        if (!foundPair) {
            // Find a random card that is still in the game
            do {
                randomIndex = (int)(Math.random() * cards.length);
            } while (!cards[randomIndex].isStillInGame());
            // Flip the first card
            cards[randomIndex].showFront();
            openCard(cards[randomIndex]);

            // Find the second card, ensuring it's not already displayed
            do {
                randomIndex = (int)(Math.random() * cards.length);
            } while (!cards[randomIndex].isStillInGame() || cards[randomIndex].isFlipped());
            // Flip the second card
            cards[randomIndex].showFront();
            openCard(cards[randomIndex]);
        }
    }

    // The method determines if the human player is allowed to make a move
    // Returns false if the computer is playing or two cards are already flipped
    // Otherwise, returns true
    public boolean isMoveAllowed() {
        boolean allowed = true;
        // Is the computer playing?
        if (player == 1)
            allowed = false;
        // Are two cards already flipped?
        if (flippedCards == 2)
            allowed = false;
        return allowed;
    }

    // Cheat method - temporarily shows all cards
    private void cheat() {
        if (isMoveAllowed()) { 
            alreadyRevealed.clear(); // Reset the list before each cheat

            for (MemoryCard card : cards) {  
                if (card.isStillInGame()) {  
                    if (card.isFlipped()) { 
                        alreadyRevealed.add(card); // Store already revealed cards
                    }
                    card.showFront();
                }
            }

            // Create a timer for 2 seconds
            Timeline timer = new Timeline(new KeyFrame(Duration.millis(2000), new CheatHandler()));
            timer.play();
        }
    }

    // Class for the cheat timer
    class CheatHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            for (MemoryCard card : cards) {
                if (card.isStillInGame() && !alreadyRevealed.contains(card)) { 
                    // Only flip back the ones that were not already revealed
                    card.showBack(false);
                }
            }
        }
    }

	
}
