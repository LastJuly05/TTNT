package tuan3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Node {
	int n;
	List<Integer> state;
	public Node (int n,List<Integer> state)
	{
		this.n=n;
		this.state=new ArrayList<Integer>();
		this.state = state;
		
		for (int i=0;i<n;i++)
		{
			Random r=new Random();	
	    this.state.add(r.nextInt(n));
		}
	}
	
}