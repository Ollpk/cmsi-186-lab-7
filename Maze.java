import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Maze {

    // A maze is a rectangular array of cells. The reason we use arrays is that
    // the maze has a fixed size, and arrays are the fastest when indexing by
    // position, which is exactly what we do when we search a maze.
    private final Cell[][] cells;

    private Location initialRatLocation;
    private Location initialCheeseLocation;

    /**
     * Builds and returns a new maze given a description in the form of an array of
     * strings, one for each row of the maze, with each string containing o's and
     * w's and r's and c's. o=Open space, w=Wall, r=Rat, c=Cheese.
     *
     * The maze must be rectangular and contain nothing but legal characters. There
     * must be exactly one 'r' and exactly one 'c'.
     *
     * The constructor is private to force users to only construct mazes through one
     * of the factory methods fromString, fromFile, or fromScanner.
     */
    private Maze(String[] lines) {
        var height = lines.length;
        var width = lines[0].length();
        // Ensures greater than one row
        if (height == 0) {
            throw new IllegalArgumentException("Maze does not have rows");
        }
        // Creates cells and checks values inputted.
        cells = new Cell[height][width];
        for (var row = 0; row < height; row++) {
            var line = lines[row];
            if (line.length() != width) {
                throw new IllegalArgumentException("Non-rectangular maze");
            }

            for (int column = 0; column < width; column++) {
                switch (line.charAt(column)) {
                    case 'r':
                        if (initialRatLocation != null) {
                            throw new IllegalArgumentException("Maze can only have one rat");
                        }
                        initialRatLocation = new Location(row, column);
                        cells[row][column] = Cell.RAT;
                        break;
                    case 'c':
                        if (initialCheeseLocation != null) {
                            throw new IllegalArgumentException("Maze can only have one cheese");
                        }
                        initialCheeseLocation = new Location(row, column);
                        cells[row][column] = Cell.CHEESE;
                        break;
                    case 'w':
                        cells[row][column] = Cell.WALL;
                        break;
                    case 'o':
                        cells[row][column] = Cell.OPEN;
                        break;
                    default:
                        System.out.println(line.charAt(column));
                        throw new IllegalArgumentException("Illegal characters in maze description");
                }
            }
        }
        // Checks to make sure there was a rat inputted for maze.
        if (initialRatLocation == null) {
            throw new IllegalArgumentException("Maze has no rat");
        }
        // Checks to make sure there was a cheese inputted for maze.
        if (initialCheeseLocation == null) {
            throw new IllegalArgumentException("Maze has no cheese");
        }
    }

    public static Maze fromString(final String description) {
        return new Maze(description.trim().split("\\s+"));
    }

    public static Maze fromFile(final String filename) throws FileNotFoundException {
        try {
            return Maze.fromScanner(new Scanner(new File(filename)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Maze fromScanner(final Scanner scanner) {
        final var lines = new ArrayList<String>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());

        }
        return new Maze(lines.toArray(new String[0]));

    }

    /**
     * A nice representation of a Location, so we don't have to litter our code with
     * separate row and column variables! A location object bundles these two values
     * together. It also includes a whole bunch of nice little methods so that our
     * code reads nicely.
     */
    public class Location {
        private final int row;
        private final int column;

        Location(final int row, final int column) {
            this.row = row;
            this.column = column;
        }

        boolean isInMaze() {
            return row >= 0 && row < getHeight() && column >= 0 && column < getWidth();
        }

        boolean canBeMovedTo() {
            return isInMaze() && (contents() == Cell.OPEN || contents() == Cell.CHEESE);
        }

        boolean hasCheese() {
            return isInMaze() && contents() == Cell.CHEESE;

        }

        Location above() {
            return new Location(row - 1, column);

        }

        Location below() {
            return new Location(row + 1, column);

        }

        Location toTheLeft() {
            return new Location(row, column - 1);

        }

        Location toTheRight() {
            return new Location(row, column + 1);

        }

        void place(Cell cell) {
            cells[row][column] = cell;
        }

        Cell contents() {
            return cells[row][column];
        }

        boolean isAt(final Location other) {
            return row == other.row && column == other.column;

        }
    }

    /**
     * A simple cell value. A cell can be open (meaning a rat has never visited it),
     * a wall, part of the rat's current path, or "tried" (meaning the rat found it
     * to be part of a dead end.
     */
    public static enum Cell {
        CHEESE('c'), OPEN(' '), PATH('.'), RAT('r'), TRIED('x'), WALL('\u2588');

        private char symbol; // value within grid

        private Cell(final char symbol) {
            this.symbol = symbol;
        }

        public String toString() {
            return Character.toString(symbol);
        }
    }

    public interface MazeListener {
        void mazeChanged(Maze maze);
    }

    public int getWidth() {
        return cells[0].length;
    }

    public int getHeight() {
        return cells.length;
    }

    public Location getInitialRatPosition() {
        return initialRatLocation;
    }

    public Location getInitialCheesePosition() {
        return initialCheeseLocation;
    }

    /**
     * Returns a textual description of the maze, separating each row with a
     * newline.
     */
    public String toString() {
        return Stream.of(cells).map(row -> Stream.of(row).map(Cell::toString).collect(joining()))
                .collect(joining("\n"));
    }
}
