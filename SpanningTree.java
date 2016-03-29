import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.lang.StringBuilder;

/**
 * @Author Carter Hay 
 *
 * TODO: Implement removal of the duplicate paths to the root
 * Find a good way to draw the tree
 *  
 */
public class SpanningTree {

  public static void main (String[] args) {
    SpanningTree st = new SpanningTree();
    if (args.length == 1) {
      System.out.println("Opening file: " + args[0]);
      File file = new File(args[0]);
      if (file.exists()) {
        if (file.canRead()) {
          try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
              System.out.println(line);
              if (line.matches("[0-9]* R")) {
                line = st.calculateRandomConnections(line);
                System.out.println(line);
                ArrayList<Node> nodes = st.calculateConnections(line);
                st.findAllRoots(nodes);
              } else if (line.matches("[0-9]* ([0-9]*-[0-9]* )*")) {
                ArrayList<Node> nodes = st.calculateConnections(line);
                st.findAllRoots(nodes);
                st.removeDuplicatePaths(nodes);
                st.draw(nodes);
              } else {
                System.out.println("Invalid line");
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
          System.out.println("That file exists, but it cannot be read.");
        }
      } else {
        System.out.println("That file does not exist.");
      }
    } else {
      System.out.println("Usage: java SpanningTree [Filename]");
    }
  }

  /**
   * @param nodes An ArrayList of nodes to calculate the Roots for
   */
  private void findAllRoots(ArrayList<Node> nodes) {
    draw(nodes);
    String previous = "";
    for (int i = 0; i < nodes.size(); i++) {
      this.calculateTree(nodes);
      if (previous.equals(nodes.toString())) {
        break;
      }
      draw(nodes);
      previous = nodes.toString();
    }
  }

  /**
   * @param line The line to be calculated
   */
  private String calculateRandomConnections (String line) {
    StringBuilder sb = new StringBuilder();
    Random rand = new Random();
    int numOfNodes = Character.getNumericValue(line.charAt(0));
    sb.append(line.charAt(0) + " ");
    for (int i = 1; i <= numOfNodes; i++) {
      int con = rand.nextInt(numOfNodes)+1;
      if (i == con && con < numOfNodes) {
        con++;
      } else if (con == numOfNodes) {
        con--;
      }
      sb.append(i + "-" + con + " ");
    }
    return sb.toString();
  }

  /**
   * @param line The line to be calculated
   */
  private ArrayList<Node> calculateConnections (String line) {
    String[] splitLine = line.split(" ");
    ArrayList<Node> nodes = new ArrayList<Node>();
    for (int i = 0; i < Integer.parseInt(splitLine[0]); i++) {
      nodes.add(new Node(i+1, i+1, 0));
    }
    for (int i = 1; i < splitLine.length; i++) {
      String[] connect = splitLine[i].split("-");
      nodes.get(Integer.parseInt(connect[0])-1).addConnection(nodes.get(Integer.parseInt(connect[1])-1));
      nodes.get(Integer.parseInt(connect[1])-1).addConnection(nodes.get(Integer.parseInt(connect[0])-1));
    }
    return nodes;
  }

  /**
   * @param nodes The nodes to calculate the tree for
   */
  private void calculateTree(ArrayList<Node> nodes) {
    ArrayList<Node> temp = new ArrayList<Node>();
    for (Node node : nodes) {
      temp.add(new Node(node.getMyself(), node.getRoot(), node.getHops(), node.getConnections()));
    }
    for (Node node : temp) {
      for (Node connection : node.getConnections()) {
        connection.calculateRoot(node);
      }
    }
  }

  private void removeDuplicatePaths(ArrayList<Node> nodes) {
    for (Node node : nodes) {
      //Remove duplicate connections to the same node
      Set<Node> set = new HashSet<Node>();
      set.addAll(node.getConnections());
      node.setConnections(new ArrayList<Node>(set));

      //Remove duplicate paths to the root.
      //Each node should only have two connections at max and only one with a smaller number of hops.
    }
    
  }

  private void draw (ArrayList<Node> nodes) {
    for (Node node : nodes) {
      System.out.print("(" + node + " : " + node.getConnections() + "), ");
    }
    System.out.println("");
  }

  private class Node {
    private ArrayList<Node> connections;
    private int myself;
    private int root;
    private int hops;

    public Node (int myself, int root, int hops, ArrayList<Node> connections) {
      this.connections = connections;
      this.myself = myself;
      this.root = root;
      this.hops = hops;
    }

    public Node (int myself, int root, int hops) {
      this.connections = new ArrayList<Node>();
      this.myself = myself;
      this.root = root;
      this.hops = hops;
    }

    /**
     * @param node The node to be added to the connections
     */
    public void addConnection(Node node) {
      connections.add(node);
    }

    /**
     * @return The connections for the node
     */
    public ArrayList<Node> getConnections() {
      return this.connections;
    }

    /**
     * @param con The connection list to be set
     */
    public void setConnections(ArrayList<Node> con) {
      this.connections = con;
    }

    /**
     * @return Myself for the node
     */
    public int getMyself() {
      return this.myself;
    }

    /**
     * @return The root of the node
     */
    public int getRoot() {
      return this.root;
    }

    /**
     * @return The hops till the root of the node
     */
    public int getHops() {
      return this.hops;
    }

    /**
     * @return String that represents the node
     */
    public String toString() {
      return "[" + this.myself + " " + this.root + " " + this.hops + "]";
    }

    /**
     * @param node The node to compare with
     */
    public void calculateRoot(Node node) {
      if (node.getRoot() < this.root) {
        this.root = node.getRoot();
        this.hops = node.getHops() + 1;
      }
    }
  }

}
