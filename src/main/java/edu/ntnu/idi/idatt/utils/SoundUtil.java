package edu.ntnu.idi.idatt.utils;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for handling sound effects in the game.
 * 
 * @author Markus Øyen Lund
 * @since v1.1.0
 */
public class SoundUtil {
    private static final Logger LOGGER = Logger.getLogger(SoundUtil.class.getName());
    private static final String DICE_ROLL_SOUND = "/sounds/dice_roll.mp3";
    private static final String VICTORY_SOUND = "/sounds/victory.mp3";
    private static AudioClip diceRollSound;
    private static AudioClip victorySound;

    static {
        try {
            // Dice roll sound
            URL diceSoundUrl = SoundUtil.class.getResource(DICE_ROLL_SOUND);
            if (diceSoundUrl != null) {
                diceRollSound = new AudioClip(diceSoundUrl.toExternalForm());
                diceRollSound.setVolume(0.4); 
            } else {
                LOGGER.warning("Could not find dice roll sound file at: " + DICE_ROLL_SOUND);
            }

            // Victory sound
            URL victorySoundUrl = SoundUtil.class.getResource(VICTORY_SOUND);
            if (victorySoundUrl != null) {
                victorySound = new AudioClip(victorySoundUrl.toExternalForm());
                victorySound.setVolume(0.3);
            } else {
                LOGGER.warning("Could not find victory sound file at: " + VICTORY_SOUND);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load sound effects", e);
        }
    }

    /**
     * Plays the dice roll sound effect.
     */
    public static void playDiceRollSound() {
        if (diceRollSound != null) {
            diceRollSound.play();
        }
    }

    /**
     * Plays the victory sound effect.
     */
    public static void playVictorySound() {
        if (victorySound != null) {
            victorySound.play();
        }
    }
} 