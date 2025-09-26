package BFSNode;

public class BFS {
	public void bfsUsingQueue(Node initial, int goal) {
		Node node = initial;
		
		if (node.state==goal)
		{
			String s="";
			while (node!=initial)
			{
				s=node.state+" "+s;
				node=node.parent;
			}
			System.out.println(initial.state+ " "+s);
			return;
		}

	}

}
