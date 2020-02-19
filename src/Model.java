import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    public int score;
    public int maxTile;


    public Model() {
        resetGameTiles();
    }

    public void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        fillGameTilesWithZeroTiles();
        addTile();
        addTile();
        score = 0;
        maxTile = 0;
    }

    private void fillGameTilesWithZeroTiles() {
        for (int i = 0; i < FIELD_WIDTH; ++i)
            for (int j = 0; j < FIELD_WIDTH; ++j)
                gameTiles[i][j] = new Tile();
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            int changingTileIndex = (int) (emptyTiles.size() * Math.random());
            int addingValue = Math.random() < 0.9 ? 2 : 4;
            emptyTiles.get(changingTileIndex).value = addingValue;
            refreshMaxValue(addingValue);
        }
    }

    private void refreshMaxValue(int newValue) {
        if (newValue > maxTile)
            maxTile = newValue;
    }

    public boolean canMove() {
        if (!getEmptyTiles().isEmpty())
            return true;
        for (int i = 0; i < 4; i++) {
            for (Tile[] tiles : gameTiles)
                for (int j = 1; j < tiles.length; ++j)
                    if (tiles[j - 1].value == tiles[j].value)
                        return true;
            rotateGameTiles();
        }
        return false;
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; ++i)
            for (int j = 0; j < FIELD_WIDTH; ++j)
                if (gameTiles[i][j].value == 0)
                    emptyTiles.add(gameTiles[i][j]);
        return emptyTiles;
    }

    public boolean hasBoardChanged() {
        if (previousStates.isEmpty())
            return false;
        return getTilesWeight(gameTiles) != getTilesWeight(previousStates.peek());
    }

    public MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        MoveEfficiency moveEfficiency;
        if (hasBoardChanged())
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        else
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        rollback();
        return moveEfficiency;
    }

    private int getTilesWeight(Tile[][] tiiiles) {
        int sum = 0;
        for (Tile[] tiiile : tiiiles)
            for (Tile tile : tiiile)
                sum += tile.value;
        return sum;
    }

    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> pq = new PriorityQueue<>(4, Collections.reverseOrder());
        pq.offer(getMoveEfficiency(this::left));
        pq.offer(getMoveEfficiency(this::up));
        pq.offer(getMoveEfficiency(this::right));
        pq.offer(getMoveEfficiency(this::down));
        pq.poll().getMove().move();
    }

    public void left() {
        if (isSaveNeeded)
            saveState(gameTiles);
        for (Tile[] tiles : gameTiles) {
            compressTiles(tiles);
            mergeTiles(tiles);
        }
        addTile();
        isSaveNeeded = true;
    }

    public void up() {
        saveState(gameTiles);
        rotateGameTiles();
        left();
        rotateGameTiles();
        rotateGameTiles();
        rotateGameTiles();
    }


    public void down() {
        saveState(gameTiles);
        rotateGameTiles();
        rotateGameTiles();
        rotateGameTiles();
        left();
        rotateGameTiles();
    }


    public void right() {
        saveState(gameTiles);
        rotateGameTiles();
        rotateGameTiles();
        left();
        rotateGameTiles();
        rotateGameTiles();
    }

    private void rotateGameTiles() {
        // Consider all squares one by one
        for (int x = 0; x < gameTiles.length / 2; x++) {
            // Consider elements in group of 4 in
            // current square
            for (int y = x; y < gameTiles.length - x - 1; y++) {
                // store current cell in temp variable
                Tile temp = gameTiles[x][y];

                // move values from right to top
                gameTiles[x][y] = gameTiles[y][gameTiles.length - 1 - x];

                // move values from bottom to right
                gameTiles[y][gameTiles.length - 1 - x] = gameTiles[gameTiles.length - 1 - x][gameTiles.length - 1 - y];

                // move values from left to bottom
                gameTiles[gameTiles.length - 1 - x][gameTiles.length - 1 - y] = gameTiles[gameTiles.length - 1 - y][x];

                // assign temp to left
                gameTiles[gameTiles.length - 1 - y][x] = temp;
            }
        }
    }

    private boolean compressTiles(Tile[] tiles) {
        int amountOfNonZeroElements = 0;
        int[] values = new int[tiles.length];
        for (int i = 0; i < tiles.length; i++)
            values[i] = tiles[i].value;

        for (Tile tile : tiles)
            if (tile.value != 0)
                tiles[amountOfNonZeroElements++].value = tile.value;

        while (amountOfNonZeroElements < tiles.length && amountOfNonZeroElements != 0)
            tiles[amountOfNonZeroElements++].value = 0;

        boolean wasChanged = false;
        for (int i = 0; i < tiles.length && !wasChanged; i++)
                wasChanged = values[i] != tiles[i].value;

        return wasChanged;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean wasChanged = false;
        for (int i = 1; i < tiles.length; ++i) {
            if (tiles[i - 1].value != 0 && tiles[i - 1].value == tiles[i].value) {
                wasChanged = true;
                tiles[i - 1].value *= 2;
                tiles[i].value = 0;
                compressTiles(tiles);
                refreshMaxValue(tiles[i - 1].value);
                refreshScore(tiles[i - 1].value);
            }
        }
        return wasChanged;
    }

    private void refreshScore(int delta) {
        score += delta;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    private void saveState(Tile[][] tiles) {
        previousStates.push(cloneTiles(tiles));
        previousScores.push(score);
        isSaveNeeded = false;
    }

    private Tile[][] cloneTiles(Tile[][] tiles) {
        Tile[][] saveTiles = new Tile[tiles.length][tiles[0].length];
        for (int i = 0, tilesLength = tiles.length; i < tilesLength; ++i) {
            for (int j = 0; j < tiles[i].length; ++j)
                saveTiles[i][j] = new Tile(tiles[i][j].value);
        }
        return saveTiles;
    }

    public void rollback() {
        if (!previousScores.isEmpty() && !previousStates.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

}
