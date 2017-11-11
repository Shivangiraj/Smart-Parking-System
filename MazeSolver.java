package parkstore;
import java.util.ArrayList;
import java.util.List;
public class MazeSolver {
    public static boolean searchPath(int[][] maze, int x, int y , ArrayList<Integer> path) {
        if(maze[x][y]==9)
        {
            path.add(y);
            path.add(x);
            return true;
            
        }
        if(maze[x][y]==2) {
            maze[x][y]=3;
        //visiting neighboring nodes
        //if path is found, filling path list with position
        int dx = -1, dy = 0;
        if(searchPath(maze, x+dx, y+dy, path))
        {
            path.add(x);
            path.add(y);
            return true;            
        }
        dx = 1;
        dy = 0;
        if(searchPath(maze, x+dx, y+dy, path))
        {
            path.add(x);
            path.add(y);
            return true;            
        }
        dx = 0;
        dy = -1;
        if(searchPath(maze, x+dx, y+dy, path))
        {
            path.add(x);
            path.add(y);
            return true;            
        }
        dx = 0;
        dy = 1;
        if(searchPath(maze, x+dx, y+dy, path))
        {
            path.add(x);
            path.add(y);
            return true;            
        }
        }
        return false;
    }
    public static void main(String args[])
    { 
        
    }
 }
