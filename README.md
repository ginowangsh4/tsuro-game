# Tsuro-Game

Implementation of classic board game Tsuro with a taste of Java

## Interface Definition

### Board
```
Tile[][] locations
HashMap<Token, Tile> tokenMap
void placeTile(Tile, int, int)
void placeToken(Token, int, int)
```
### Server
```
void registerPlayer(Player)
String play() 		// run game
```
### SPlayer
```
void inform(String) 	// accept invitation from server
void turn(Turn)		// play turn
void record()		// record result
void cheat()		// detect attempt to cheat
```
### Player
```
Hand hand
Token token
int[2] startLocation
Token getToken()
void inform(String) 	// accept invitation from server
void turn(Turn)		// play turn
```
### Token 
```
Player player
Player getPlayer()
```
### Hand
```
Tile[] tiles
bool hasDragon()
```
### Turn
```
void placeToken(Token, Location)
void placeTile(Tile)
```
### Tile
```
/*
pairs[0] = 1 indicates there is a path connecting 0th point 
and 1st point; int[2] location, array of size 2 indicating 
the x,y location of the tile on the board
*/
int[][] pairs 
HashMap<Token, int> map 	// mark token location on tile (1-8) 
boolean onBoard()			// true if already place on board
void clearMap()			// reset tile when placed back in deck
```
### DragonTile
```
Player player
```
### Deck
```
Tile[][] remainingTiles
Tile pop()				// get top tile, assign dragon tile if empty
void shuffle()			// shuffle all remaining tiles
void addAndShuffle(Tile[]) 	// add some tiles and shuffle
```


