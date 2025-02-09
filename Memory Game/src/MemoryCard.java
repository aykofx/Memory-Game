import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;

// The class for a card in the Memory game
// It extends Button
public class MemoryCard extends Button {
    // Instance variables
    // A unique ID to identify the image
    private int imageID;
    // Front and back images
    private ImageView frontImage, backImage;
    
    // Position of the card in the game field
    private int imagePosition;

    // Is the card flipped?
    private boolean flipped;
    // Is the card still in the game?
    private boolean stillInGame;
    
    // The game field for the card
    private MemoryField gameField;
    
    // Inner class for the event handler of the card
    class CardHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            // Check if the card is still in the game and if moves are allowed
            if (!stillInGame || !gameField.isMoveAllowed())
                return;
            
            // If the back side is showing, display the front side
            if (!flipped) {
                showFront();
                // Call the method cardOpened() in the game field
                // Pass the reference to this card (outer class)
                gameField.openCard(MemoryCard.this);
            }
        }
    }
    
    // Constructor
    // Sets the images
    public MemoryCard(String frontImagePath, int imageID, MemoryField gameField) {
        // Set the front image (file name is passed to the constructor)
        frontImage = new ImageView(frontImagePath);
        // Set the back image (fixed)
        backImage = new ImageView("graphics/back.jpg");
        setGraphic(backImage);
        
        // Set the image ID
        this.imageID = imageID;
        // The card is initially flipped down and still in play
        flipped = false;
        stillInGame = true;
        // Connect with the game field
        this.gameField = gameField;

        // Set the action handler
        setOnAction(new CardHandler());
    }
    
    // Method to show the front side of the card
    public void showFront() {
        setGraphic(frontImage);
        flipped = true;
    }

    // Method to show the back side of the card
    public void showBack(boolean removeFromGame) {
        // Should the card be completely removed from the game?
        if (removeFromGame) {
            // Show the revealed image and remove the card from the game
            setGraphic(new ImageView("graphics/revealed.jpg"));
            stillInGame = false;
        } else {
            // Otherwise, just show the back side
            setGraphic(backImage);
            flipped = false;
        }
    }
    
    // Method to get the image ID of the card
    public int getImageID() {
        return imageID;
    }

    // Method to get the position of the card
    public int getImagePosition() {
        return imagePosition;
    }
    
    // Method to set the position of the card
    public void setImagePosition(int imagePosition) {
        this.imagePosition = imagePosition;
    }
    
    // Method to check if the card is flipped
    public boolean isFlipped() {
        return flipped;
    }

    // Method to check if the card is still in the game
    public boolean isStillInGame() {
        return stillInGame;
    }
}
