# Tsuro-Game

Implementation of classic board game Tsuro with Java 7

## Interface Definition

### Board
```
protected Tile[][] board;
protected final int SIZE = 6;
protected ArrayList<Token> token_list;
public Tile getTile(int x, int y)
public void placeTile(Tile t, int x, int y)
public void deleteTile(int x, int y)
public void addToken(Token t)
public void removeToken(Token inT)
public void updateToken(Token t)
```
### Server
```
private Board board;
private ArrayList<Tile> drawPile;
private ArrayList<Player> inPlayer;
private ArrayList<Player> outPlayer;
private boolean gameOver = false;
public boolean legalPlay(Player p, Board b, Tile t)
public ArrayList playATurn(ArrayList<Tile> drawPile, ArrayList<Player> inPlayer, ArrayList<Player> outPlayer, Board board, Tile t)
private Token simulateMove(Token token, Board board)
private int[] getAdjacentLocation(Token token)
public boolean outOfBoard(Token token)
*contains tests*
```
### Player
```
protected Token token;
protected ArrayList<Tile> hand;
protected boolean hasDragon = false;
public Token getToken()
public void updateToken(Token token)
public void draw(Tile t)
public ArrayList<Tile> getHand()  
public void getDragon() 
public void passDragon(Player p)
```
### Token 
```
private int color;
private int indexOnTile; //orientation int, not path int
private int[] position;
private Player owner;
public void setIndex(int index) 
public int getIndex() 
public void setPosition(int[] xy)
public int[] getPosition()
public void setOwner(Player p) 
public boolean equals(Token t) 
```
### Tile
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
public boolean isSameTile(Tile tile) 
public int getPathEnd (int startInt) 
```
### Deck
```
Tile[][] remainingTiles
Tile pop()				// get top tile, assign dragon tile if empty
void shuffle()			// shuffle all remaining tiles
void addAndShuffle(Tile[]) 	// add some tiles and shuffle
```


