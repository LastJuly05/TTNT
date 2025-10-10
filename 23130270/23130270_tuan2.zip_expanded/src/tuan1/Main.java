package tuan1;

public class Main {
	public static void main(String arg[])
	{
		Node node10 =new Node(10);
		Node node20 =new Node(20);
		Node node30 =new Node(30);
		Node node40 =new Node(40);
		Node node50 =new Node(50);
		Node node60 =new Node(60);
		Node node70 =new Node(70);
 //
		node40.addNeighbours(node10);
		node40.addNeighbours(node20);
		node10.addNeighbours(node30);
		node20.addNeighbours(node10);
		node20.addNeighbours(node30);
		node20.addNeighbours(node60);
		node20.addNeighbours(node50);
		node30.addNeighbours(node60);
		node60.addNeighbours(node70);
		node50.addNeighbours(node70);
 //bfs
		BFS bfsExample = new BFS(); 
		System.out.println("duyet qua BFS ");
		bfsExample.bfsUsingQueue(node40,70);
//dfs
		DFS dfsExample = new DFS(); 
		System.out.println("duyet qua DFS ");
		dfsExample.dfsUsingStack(node40,70);

}
}

