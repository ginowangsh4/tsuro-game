# tsuro

Implementation of classic board game Tsuro with a taste of Java

## Interface Definition

### Board
```
Tile[][] locations
HashMap<Token, Tile> tokenMap, e.g. key = token, value = tile which 
has the location in its field
```

### Player
```
Hand hand
```
### Token 
```
Player player
```
### Hand
```
Tile[] tiles
bool hasDragon()
```
### Tile
```
int[][] map // map[0] = 1 indicates there is a path connecting 0th point and 1st point; int[2] location, array of size 2 indicating the x,y location of the tile on the board

```

### Deck
```
Tile[][] remainingTiles
```

### Card
### DragonCard
```
Player player
```


