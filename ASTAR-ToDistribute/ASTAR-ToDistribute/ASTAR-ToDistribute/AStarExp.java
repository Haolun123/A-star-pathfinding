import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

public class AStarExp implements AIModule 
{
	private double e = Math.E;
	public TerrainMap map;
	private class Node	{
		public Point state;
		public Node(Point pt)	{
			this.state = pt;
		}
				
		public double getHeuristic(final TerrainMap map,final Node pt1, final Node pt2)
		{
			return 0.0;
		}
		public Node Parent;
		public Node getparent()	{
			return Parent;
		}
		
		public void setparent(Node Parent)	{
			this.Parent=Parent;
		}
		
		public double gcost;
		public double hcost;
		public double getgcost()	{
			return gcost;
		}
			
		public void setgcost(double gcost)	{
		
			this.gcost=gcost;
		}
		
		public void setgcost(final TerrainMap map,final Node ParentPoint)	{
			setgcost(ParentPoint.getgcost()+map.getCost(ParentPoint.state,this.state));
		}
		
		public double gethcost()	{
			return hcost;
		}
			
		public void sethcost(double hcost)	{
			this.hcost=hcost;
		}
		
		public void sethcost(final TerrainMap map)	{
			sethcost(getHeuristic(map,this,(new Node(map.getEndPoint()))));
		}
		
		public double getfCosts()	{
			return this.gcost+this.hcost;
		}
		
		public double calculatefcost(final TerrainMap map,final Node PreviousPoint)	{
		
			return (PreviousPoint.getfCosts()+map.getCost(PreviousPoint.state,this.state));
		}
		
		public void cleargcost()	{
			this.gcost=0;
		}
		public List<Point> CalculatePath()	{
			final LinkedList<Point> path = new LinkedList<Point>();
			Node current = this;
			
			while (current != null) {
				path.addFirst(current.state);
				current = (Node) current.getparent();
			}
			return path;
		}
	}
	
	
	/** list containing nodes not visited but adjacent to visited nodes. */
    private List<Node> openList;
    /** list containing nodes already visited/taken care of. */
    private List<Node> closedList;
	private boolean done = false;
	private Node CurrentPoint;
	
	
	
	private Node lowestFInOpen() {
        
        Node cheapest = openList.get(0);
        for (int i = 0; i < openList.size(); i++) {
            if (openList.get(i).getfCosts() < cheapest.getfCosts()) {
                cheapest = openList.get(i);
            }
        }
        return cheapest;
    }
	

	
	public final List<Point> createPath(final TerrainMap map) 
	{
		final Node StartPoint = new Node (map.getStartPoint());
		final Node TargetPoint =new Node (map.getEndPoint());
		StartPoint.cleargcost();
		StartPoint.sethcost(map);
		openList = new LinkedList<Node>();
        closedList = new LinkedList<Node>();
        openList.add(StartPoint); // add starting node to open list
		Node CurrentPoint = lowestFInOpen(); // get node with lowest fCosts from openList
		while (!done)	{
			while (CurrentPoint!=null){
				closedList.add(CurrentPoint); // add current node to closed list
				openList.remove(CurrentPoint); // delete current node from open list
								
				// for all adjacent nodes:
				for(Point Adj : map.getNeighbors(CurrentPoint.state)){
					Node CurrentAdj = new Node(Adj);
					if (!closedList.contains(CurrentAdj)){
						if (!openList.contains(CurrentAdj)) { // node is not in openList
							CurrentAdj.setparent(CurrentPoint); // set current node as previous for this node
							CurrentAdj.sethcost(map); // set h costs of this node (estimated costs to goal)
							CurrentAdj.setgcost(map,CurrentPoint); // set g costs of this node (costs from start to this node)
							openList.add(CurrentAdj); // add node to openList
						}
						else { // node is in openList
							if (CurrentAdj.getfCosts() > CurrentAdj.calculatefcost(map,CurrentPoint)) { // costs from current node are cheaper than previous costs
								CurrentAdj.setparent(CurrentPoint); // set current node as previous for this node
								CurrentAdj.setgcost(map,CurrentPoint); // set g costs of this node (costs from start to this node)
							}
						}
					}
					if ((CurrentAdj.state.x==TargetPoint.state.x) && (CurrentAdj.state.y==TargetPoint.state.y))  // found goal
						return CurrentAdj.CalculatePath();
						
				}
				CurrentPoint = lowestFInOpen(); // get node with lowest fCosts from openList
            }
			
				
			
        }
        return null; // unreachable
    }
	
	
	

}