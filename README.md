# Mappeoppgave-Boardgame
Boardgame is an application that lets you choose between two boardgames to play with your friends.
The application is developed as a student project at the Norwegian University of Science and Technology (NTNNU).

## Features
In the latest v2.0.0 release, the following core functionalities are implemented:
- Play the board game "Ladder game"
  - Pick between three variations of the Ladder game:
    - Ladder Game Classic
    - Ladder Game Advanced
    - Ladder Game Extreme
  - Each variation contain the following action tiles:
    - Ladder Action
          - Move up ladders when landing on green tiles
          - Move down ladder when landing on red tiles
    - Back To Start Action, triggered when landing on a blue tile and moves you back to tile 1
    - Wait Action, triggered when landing on a yellow tile and forces you to wait a turn before rolling again
  - A player wins by reaching the final tile (tile 90) and the game is over
    
- Play the board game "Monopoly"
  - Buy properties when landing on a property tile, collecting all properties of a type grants a bonus to rent
  - A player that lands on another player's owned property must pay a certain amount of rent
  - Players who land on a free parking tile is exempt from paying rent for their next turn
  - Landing on a chance tile triggers one of five random actions
  - When a player is in jail, they either wait three turns, pay money or roll doubles to get out
  - Each player gains 20000$ for passing start
  - Landing on tax tiles forces players to pay a certain amount of money
  - The last player who is bankrupt wins the game.

- Choose player-count (up to five players)
- Each player can write their own name and pick between five tokens
- While playing:
    - Pressing the "Roll dice" button rolls two die for the player whose turn it is and moves accordingly
    - Player's tokens are animated when moving
    - When the game is over, you can click "play again" to restart the game with the same players and tokens
    - Press "Quit to menu" to quit to the game selection menu
    - Press "Save Game" to save the current game state
- Load game from files
- Delete saved game files

## Tech stack
The application is built using the following technologies:
- Java
- JavaFX
- Maven
- Github

## User Manual
To play the game, type the following in the console:
"mvn javafx:run"
When the game window has started, follow the menu screens, select your players and have fun!

## Contributors
This project is developed by the following team members:
- Ola Syrstad Berg
- Markus Øyen Lund
