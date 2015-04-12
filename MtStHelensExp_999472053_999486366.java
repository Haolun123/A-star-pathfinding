/*
modified the heuristic and by using bidirectional methods .
when search from the start and end point at the same time, it
can reduce the time efficiently.
*/
import java.awt.Point;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class MtStHelensExp_999472053_999486366 extends AStarBiEXP
{
	public final boolean done = false;
}
class AStarEXP extends AIHeuristic
{
	private double e = Math.E;
	public final boolean done = false;
	public double getHeuristic(final TerrainMap map, final Point pt1, final Point pt2)
	{
		final double dx = Math.abs(pt2.x - pt1.x);
		final double dy = Math.abs(pt2.y - pt1.y);
		final double dheight = (map.getTile(pt2)- map.getTile(pt1));
		double dy1 = (int) (Math.abs(dheight)/dy);
		double dy2 = Math.abs(dheight)%dy;
		double dy3 = dy-dy2;
		double dx1 = (int) (Math.abs(dheight)/dx);
		double dx2 = Math.abs(dheight)%dx;
		double dx3 = dx-dx2;
		if (dy>=dx)	
			if (dheight<0)
				if ((Math.abs(dheight)-dy)<=0)
					return dy-Math.abs(dheight)+Math.abs(dheight)*1/e;
				else
					return (dy2*1/e + dy3) * Math.exp(-dy1);
					
			else 
				if ((dheight-dy)<=0)
					return dy-dheight+dheight*e;
				else
					return dheight-dy+dheight*e;
		else
			if (dheight<0)
				if ((Math.abs(dheight)-dx)<=0)
						return dx-Math.abs(dheight)+Math.abs(dheight)*1/e;
					else
						return (dx2*1/e + dx3) * Math.exp(-dx1);
			else 
				if ((dheight-dx)<=0)
					return dx-dheight+dheight*e;
				else
					return dheight-dx+dheight*e; 
	}
	public List<Point> createPath(final TerrainMap map)
	{
		this.map = map;
		this.start = this.map.getStartPoint();
		this.end = this.map.getEndPoint();
		SearchAlgorithm search = new AStarSearch(this, this.start, this.end);
		Node current;
		while(!done) {
			current = search.step();
			if(current.equals(this.end)) {
				List<Point> path = current.tracePath();
				return path;
			}
		}
	}
}
class AStarBiEXP extends AIHeuristic
{
	private double e = Math.E;
	public final boolean done = false;
	public int phase = 1;
	public double g = Double.MAX_VALUE;
	AStarSearch forward, reverse;
	Node fnode, rnode;
	@Override
	public double getHeuristic(final TerrainMap map, final Point pt1, final Point pt2)
	{
		final double dx = Math.abs(pt2.x - pt1.x);
		final double dy = Math.abs(pt2.y - pt1.y);
		final double dheight = (map.getTile(pt2)- map.getTile(pt1));
		double dy1 = (int) (Math.abs(dheight)/dy);
		double dy2 = Math.abs(dheight)%dy;
		double dy3 = dy-dy2;
		double dx1 = (int) (Math.abs(dheight)/dx);
		double dx2 = Math.abs(dheight)%dx;
		double dx3 = dx-dx2;
		if (dy>=dx)	
			if (dheight<0)
				if ((Math.abs(dheight)-dy)<=0)
					return dy-Math.abs(dheight)+Math.abs(dheight)*1/e;
				else
					return (dy2*1/e + dy3) * Math.exp(-dy1);
					
			else 
				if ((dheight-dy)<=0)
					return dy-dheight+dheight*e;
				else
					return dheight-dy+dheight*e;
		else
			if (dheight<0)
				if ((Math.abs(dheight)-dx)<=0)
						return dx-Math.abs(dheight)+Math.abs(dheight)*1/e;
					else
						return (dx2*1/e + dx3) * Math.exp(-dx1);
			else 
				if ((dheight-dx)<=0)
					return dx-dheight+dheight*e;
				else
					return dheight-dx+dheight*e; 
	}
	public List<Point> createPath(final TerrainMap map)
	{
		this.map = map;
		this.start = this.map.getStartPoint();
		this.end = this.map.getEndPoint();
		final AStarBiEXP me = this;
		forward = new AStarSearch(this, this.start, this.end) {
			public AStarBiEXP bistarai = me;
			public boolean allowExpand(Point p) {
				return!(this.bistarai.phase == 3 && !this.bistarai.reverse.isclosed(p));
			}
		};
		fnode = forward.step();
		fnode = forward.step();
		reverse = new AStarSearch(this, this.end, this.start) {
			public double getCost(Point pt1, Point pt2) {
				return super.getCost(pt2, pt1);
			}
		};
		rnode = reverse.step();
		rnode = reverse.step();
		while(true) {
			if(phase == 1) { // Phase 1
				if(forward.top().f() < reverse.top().f()) {
					fnode = forward.step();
					if(reverse.isclosed(fnode)) {
						phase = 2;
						g = fnode.g() + reverse.getCost(fnode);
					}
				} 
				else{
					rnode = reverse.step();
					if(forward.isclosed(rnode)) {
						phase = 2;
						g = rnode.g() + forward.getCost(rnode);
					}
				}
			} 
			else if(phase == 2) { // Phase 2
				rnode = reverse.step();
				if(rnode.f() > g) {
					phase = 3;
				}
			} 
			else if(phase == 3) {
				fnode = forward.step();
				if(fnode.equals(this.end)) {
					List<Point> path = fnode.tracePath();
					return path;
				}
			}
		}
	}
}

class Node extends Point implements Comparable {
	private SearchAlgorithm search;
	public Node parent;
	public int z;
	private double g;
	private double h;
	public Node(SearchAlgorithm search, int x, int y, double g, Node parent) {
		this.search = search;
		this.x = x;
		this.y = y;
		this.z = (int)this.search.ai.map.getTile(this);
		this.g = g;
		this.h = -1;
		this.parent = parent;
	}
	public Node(SearchAlgorithm search, Point p, double g, Node parent) {
		this(search, p.x, p.y, g, parent);
	}
	public double f() {
		return this.g() + this.h();
	}
	public double g() {
		return this.g;
	}
	public double h() {
		if(this.h != -1) return this.h;
		return this.h = this.search.getHeuristic(this, this.search.end);
	}
	public List<Point> tracePath() {
		LinkedList<Point> path = new LinkedList<Point>();
		Node node = this;
		while(node != null) {
			path.addFirst(node);
			node = node.parent;
		}
		return path;
	}
	public List<Point> traceReversePath() {
		LinkedList<Point> path = new LinkedList<Point>();
		Node node = this;
		if(node != null) node = node.parent;
		while(node != null) {
			path.add(node);
			node = node.parent;
		}
		return path;
	}
	public int compareTo(Object node) {
		double a = this.f();
		double b = ((Node)node).f();
		if(a < b) return -1;
		if(a > b) return 1;
		return 0;
	}
}
abstract class AIHeuristic implements AIModule {
	public TerrainMap map;
	public Point start;
	public Point end;
	public double getHeuristic(final TerrainMap map, final Point pt1, final Point pt2) {
		return 0.0;
	}
	public final double getHeuristic(final Point pt1, final Point pt2) {
		return this.getHeuristic(this.map, pt1, pt2);
	}
	public final double getHeuristic(final Point p) {
		return this.getHeuristic(this.map, p, this.end);
	}
	public final double getHeuristic() {
		return this.getHeuristic(this.map, this.start, this.end);
	}

}

abstract class SearchAlgorithm {
	public AIHeuristic ai;
	public Node start, end, current;
	public abstract Node step();
	public abstract double getCost(Point pt1, Point pt2);
	public abstract double getHeuristic(Point pt1, Point pt2);
	public boolean allowExpand(Point p) {return true;}
}
class AStarSearch extends SearchAlgorithm {
	protected PriorityQueue<Point> open;
	protected HashSet<Point> closed;
	protected HashMap<Point, Double> gcost;
	public AStarSearch(AIHeuristic ai, Point start, Point end) {
		// Initialize
		this.ai = ai;
		this.open = new PriorityQueue<Point>();
		this.closed = new HashSet<Point>();
		this.gcost = new HashMap<Point, Double>(); 
		// Start
		this.start = new Node(this, start, 0, null);
		this.open(this.start, 0);
		// End
		this.end = new Node(this, end, 0, null);
	}
	public Node step() {
		// Expand Current Node
		this.expand(this.current);
		// Get Next Best Node
		this.current = this.pop();
		// lose Current Node
		if(this.current != null)
		this.close(this.current, this.current.g());
		// Return Current Node for Examination
		return current;
	}
	protected void close(Point p, double g) {
		if(p == null) return;
		this.closed.add(p);
	}
	protected void unclose(Point p) {
		if(p == null) return;
		this.closed.remove(p);
	}
	public boolean isclosed(Point p) {
		if(p == null) return false;
		return this.closed.contains(p);
	}
	protected void open(Point p, double g) {
		if(p == null) return;
		this.open.add(p);
		this.gcost.put(p, g);
	}
	public void unopen(Point p) {
		if(p == null) return;
		this.open.remove(p);
	}
	public boolean isopen(Point p) {
		if(p == null) return false;
		return this.open.contains(p);
	}
	public double getCost(Point p) {
		Double c = this.gcost.get(p);
		return(c == null? Double.MAX_VALUE : c.doubleValue());
	}
	public double getCost(Point pt1, Point pt2) {
		return this.ai.map.getCost(pt1, pt2);
	}
	public double getHeuristic(Point pt1, Point pt2) {
		return this.ai.getHeuristic(pt1, pt2);
	}
	public Node top() {
		if(this.open == null|| this.open.size() == 0) return null;
		return(Node)this.open.peek();
	}
	protected Node pop() {
		if(this.open == null|| this.open.size() == 0) return null;
		return(Node)this.open.poll();
	}
	protected void expand(Node n) {
		if(n == null) return;
		for(Point neighbor : this.ai.map.getNeighbors(n)) {
			// If neighbor is closed 
			if(this.isclosed(neighbor))
			continue;
			// Get potential new path value
			double cost = n.g() + this.getCost(n, neighbor);
			// If neighbor is open
			if(cost >= this.getCost(neighbor))
			continue;
			// Custom pruning
			if(!this.allowExpand(neighbor))
			continue;
			// If neighbor has been open at one point in time before
			if(this.getCost(neighbor) != Double.MAX_VALUE)
			this.unopen(neighbor);
			// Open neighbor
			this.open(new Node(this, neighbor, cost, n), cost);
		}
	}
}

