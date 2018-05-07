# Tsuro-Game

Implementation of classic board game Tsuro with Java 7

## Interface Definition

### tsuro.Board
```
protected tsuro.Tile[][] board;
protected final int SIZE = 6;
protected ArrayList<tsuro.Token> token_list;
public tsuro.Tile getTile(int x, int y)
public void placeTile(tsuro.Tile t, int x, int y)
public void deleteTile(int x, int y)
public void addToken(tsuro.Token t)
public void removeToken(tsuro.Token inT)
public void updateToken(tsuro.Token t)
```
### tsuro.Server
```
private tsuro.Board board;
private ArrayList<tsuro.Tile> drawPile;
private ArrayList<tsuro.SPlayer> inSPlayer;
private ArrayList<tsuro.SPlayer> outSPlayer;
private boolean gameOver = false;
public boolean legalPlay(tsuro.SPlayer p, tsuro.Board b, tsuro.Tile t)
public ArrayList playATurn(ArrayList<tsuro.Tile> drawPile, ArrayList<tsuro.SPlayer> inSPlayer, ArrayList<tsuro.SPlayer> outSPlayer, tsuro.Board board, tsuro.Tile t)
private tsuro.Token simulateMove(tsuro.Token token, tsuro.Board board)
private int[] getAdjacentLocation(tsuro.Token token)
public boolean outOfBoard(tsuro.Token token)
*contains tests*
```
### tsuro.SPlayer
```
protected tsuro.Token token;
protected ArrayList<tsuro.Tile> hand;
protected boolean hasDragon = false;
public tsuro.Token getToken()
public void updateToken(tsuro.Token token)
public void draw(tsuro.Tile t)
public ArrayList<tsuro.Tile> getHand()
public void getDragon() 
public void passDragon(tsuro.SPlayer p)
```
### tsuro.Token
```
private int color;
private int indexOnTile; //orientation int, not path int
private int[] position;
private tsuro.SPlayer owner;
public void setIndex(int index) 
public int getIndex() 
public void setPosition(int[] xy)
public int[] getPosition()
public void setOwner(tsuro.SPlayer p)
public boolean equals(tsuro.Token t)
```
### tsuro.Tile
```
protected int[][] paths; // mutable
public final HashMap<Integer, Integer> neighborIndex = new HashMap<Integer, Integer>() {{
        put(0, 5);
        put(1, 4);
        put(2, 7);
        put(3, 6);
        put(4, 1);
        put(5, 0);
        put(6, 3);
        put(7, 2);
    }};
public int[][] getPaths()
public void rotateTile() 
public boolean isSameTile(tsuro.Tile tile)
public int getPathEnd (int startInt) 
```
### tsuro.Deck
```
tsuro.Tile[][] remainingTiles
tsuro.Tile pop()				// get top tile, assign dragon tile if empty
void shuffle()			// shuffle all remaining tiles
void addAndShuffle(tsuro.Tile[]) 	// add some tiles and shuffle
```


