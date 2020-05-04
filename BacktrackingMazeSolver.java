import java.util.EmptyStackException;
import java.util.Stack;

public class BacktrackingMazeSolver {

    /**
     * Moves a rat from (x1,y1) to (x2,y2), filling in the cells as it goes, and
     * notifying a listener at each step.
     */
    public boolean solve(Maze maze, Maze.MazeListener listener) {

        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        var path = new Stack<Maze.Location>();

        var current = maze.getInitialRatPosition();

        // Solution loop. At each step, place the rat and notify listener.
        while (true) {
            current.place(Maze.Cell.RAT);
            listener.mazeChanged(maze);

            if (current.isAt(maze.getInitialCheesePosition())) {
                return true;
            }

            current.place(Maze.Cell.RAT);

            listener.mazeChanged(maze);

            if (current.above().canBeMovedTo()) { // above
                path.push(current);
                current.place(Maze.Cell.PATH);
                listener.mazeChanged(maze);
                current = current.above();
            } else if (current.below().canBeMovedTo()) { // below
                path.push(current);
                current.place(Maze.Cell.PATH);
                listener.mazeChanged(maze);
                current = current.below();
            } else if (current.toTheLeft().canBeMovedTo()) { // left
                path.push(current);
                current.place(Maze.Cell.PATH);
                listener.mazeChanged(maze);
                current = current.toTheLeft();
            } else if (current.toTheRight().canBeMovedTo()) { // right
                path.push(current);
                current.place(Maze.Cell.PATH);
                listener.mazeChanged(maze);
                current = current.toTheLeft();
            } else {
                current.place(Maze.Cell.TRIED);
                listener.mazeChanged(maze);
                try {
                    current = path.pop();
                } catch (EmptyStackException e) {
                    // System.out.println("You have no area to go back to!");
                    return false;
                }
            }
        }
    }
}