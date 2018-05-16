import java.util.*;
@SuppressWarnings("unused")




public class Search {
	Map<ArrayList<Integer>,NodeState> closedMap;
	Queue<NodeState> queue;
	int Cmax = 6;
	int Rmax = 6;
	int numCarPer = 3; //Max number of cars/trucks per row - number of options per row ie 2 cars 1 truck for 6, 3 cars 2 trucks 7
	int GoalC = Cmax-1;
	int GoalR = 2;
	int GoalCar = (Cmax+Rmax-1)*numCarPer+1;
	int GenC = 1;
	int GenR = 2;
	boolean gen=false;
	private static final int  VERT =0;
	private static final int  HORIZ =1; //Horizontal sliding

	
	
	//private NodeState arr;
	public Search() {
	}

	

	/**
	 * @param state Finished state
	 * @param movesMin Minimum nubmer of moves
	 * @param movesMax Maximum number of moves
	 * Returns a board with number of moves to solve as close to upper bound movesMax 
	 * If moves < movesMin - no solution with required difficulty
	 */
	//public ArrayList<Integer> GenBoard(BoardState state, int movesMin) {
	public NodeState GenBoard(BoardState state, int movesMin) {
		
		queue =  new LinkedList<NodeState>();
		closedMap = new HashMap<ArrayList<Integer>,NodeState>();
		Queue<NodeState> solveList = new LinkedList<NodeState>();
		ArrayList<Integer> arr = state.GetBoard();
		NodeState curr = new NodeState(arr,-1);
		addQueue(curr,null);
		boolean solved = false;
		gen=false;
		while(!queue.isEmpty()) {
			curr = queue.remove();
			if(isGoal(curr)) {
				solveList.add(curr);
			}
			FindNeighbour(curr);
		}
		queue =  new LinkedList<NodeState>();
		closedMap = new HashMap<ArrayList<Integer>,NodeState>();

		while(!solveList.isEmpty()) {
			curr = solveList.remove();
			addQueue(curr,null); //Add to queue and closed map
		}
		while(!queue.isEmpty()) {
			curr = queue.remove();
			curr.incMoves();
			FindNeighbour(curr);
		}
		if(curr.getMoves()>movesMin) {
			return curr;
		}
		return null;
	}
	
	

	
	public int SearchBoard(BoardState state) {
		queue =  new LinkedList<NodeState>();
		closedMap = new HashMap<ArrayList<Integer>,NodeState>();

		int j=0;
		ArrayList<Integer> arr = state.GetBoard();
		//NodeState nullArr = new NodeState();
		NodeState curr = new NodeState(arr,-1);
		addQueue(curr, null);
		boolean solved = false;
		
		while(!queue.isEmpty()) {
			curr = queue.remove();
			curr.incMoves();
			if(isGoal(curr) && !solved) {
				solved = true;
				//FindPrev(curr);
				break; //Finished
			}
			FindNeighbour(curr);
			j++;
		}
		int i=1;
		i++;
		if(solved==true) {
		   // System.out.println("Puzzle Solved in " + curr.getMoves() +" moves");

			return curr.getMoves();
		}
		else{
		    System.out.println("Puzzle Cannot Be Solved");
		    return -1;
		}
	}
	
	
	private NodeState FindMoves(NodeState state, int moves) {
		while(state.getMoves()> moves) {
			state = closedMap.get(state.arr);
		}
		return state;
	}
	
	
	private int FindPrev(NodeState state) {
		NodeState prev = closedMap.get(state.arr);
        int step = (prev == null) ? 0 : FindPrev(prev) + 1;
        System.out.println(step);
        state.printBoard();
        //System.out.println((state));
        return step;

	}
	

	
	private void addQueue(NodeState next, NodeState prev) {
		NodeState next1 = copyNodeState(next);
		NodeState prev1 = null;
		if(prev!=null && gen==false) {
			prev1 = copyNodeState(prev);
		}

		if(!closedMap.containsKey(next1.arr)) {
			closedMap.put(next1.arr, prev1);
			//closedMap.put(next1,prev1);
			queue.add(next1);
		}
	}
	
	private boolean isGoal(NodeState state) {
		if(state.get(RCtoI(GoalR,GoalC)) == GoalCar) {
			return true;
		}
		return false;
	}
	
/*	private int isGen(NodeState state, int moves) {
		if(state.getMoves()>=moves) {
			if(state.get(RCtoI(GenR,GenC))==GoalCar) {
				//return 1;
				BoardState bs = new BoardState(6);
				Integer[] search2= new Integer[state.arr.size()];
				search2 = state.arr.toArray(search2);
				bs.GivenBoard(search2);
				Search search = new Search();
				int realnum = search.SearchBoard(bs);
				return realnum;
				//if(realnum==moves) {
				//	return moves;
				//}
			}
		}
		return -1;
	}*/
	
	private void FindNeighbour(NodeState curr) {
		for( int r=0; r < Rmax; r++) {
			for (int c = 0; c<Cmax; c++) {

				int carId = curr.get(RCtoI(r,c));
				if(carId ==0) {
					continue;
				}
				if(carId == GoalCar && gen==true) {
					continue;
				}
				if(IDtoType(carId)== HORIZ ) {
					if(c>0) {
					LeftSpaces(curr,r,c);
					}
					if(c<Cmax-1) {
						RightSpaces(curr,r,c);
					}
				}
					
				else {
					if(r>0) {
					UpSpaces(curr,r,c);
					}
					if(r<Rmax-1) {
					DownSpaces(curr,r,c);
					}
				}
			}
		}
	}
	
	
	
	
	
	

	
	private int UpSpaces(NodeState state, int r, int c) {
		int j=1;
		int id = state.get(RCtoI(r,c));
		int length = getSize(id);
		NodeState nState = copyNodeState(state);
		
		
		while((r-j >= 0) && state.get(RCtoI(r-j,c))==0){ //If in bounds and space above is empty
			//Moving up - add to queue
			nState.set(RCtoI(r-j,c), id); 
			nState.set(RCtoI(r-j+length,c), 0); //Clear end
			addQueue(nState,state);
			j++;
		}
		return j;
	}
	
	
	private int DownSpaces(NodeState state, int r, int c) {
		int j=1;
		int id = state.get(RCtoI(r,c));
		int length = getSize(id);
		NodeState nState = copyNodeState(state);
		
		while((r+j < Rmax) && state.get(RCtoI(r+j,c))==0){ //If in bounds and space above is empty
			//Moving up - add to queue
			nState.set(RCtoI(r+j,c), id); 
			nState.set(RCtoI(r+j-length,c), 0); //Clear end
			addQueue(nState,state);
			j++;
		}
		return j;
	
	}
	
	private int LeftSpaces(NodeState state, int r, int c) {
		int j=1;
		int id = state.get(RCtoI(r,c));
		int length = getSize(id);
		NodeState nState = copyNodeState(state);

		while((c-j >= 0) && state.get(RCtoI(r,c-j))==0){
			nState.set(RCtoI(r,c-j), id); 
			nState.set(RCtoI(r,c-j+length), 0); //Clear end
			addQueue(nState,state);

			j++;
		}
		return j;
	}
	
	
	
	private int RightSpaces(NodeState state, int r, int c) {
		int j=1;
		int id = state.get(RCtoI(r,c));
		int length = getSize(id);
		NodeState nState = copyNodeState(state);
		
		while((c+j < Cmax) && state.get(RCtoI(r,c+j))==0){
			nState.set(RCtoI(r,c+j), id);
			nState.set(RCtoI(r,c+j-length),0);
			addQueue(nState,state);

			j++;
		}
		return j;
	}
	
	
	private int RCtoI(int r, int c) {
		return r * Cmax + c;
	}
	
	private int IDtoType(int id) {
		if (id<=Cmax*numCarPer) {
			return VERT;
		}
		return HORIZ;
	}
	
	private int getSize(int id) { //Check this for scaling
		if(id==0) {
			return 0;
		}
		if(id==GoalCar) {
			return 2;
		}
		else if(id%numCarPer==0) { //Truck
			return 3;
		}
		return 2;
	}

	
	public NodeState copyNodeState(NodeState orig) {
		//ArrayList<Integer> arr2 = 
		ArrayList<Integer> next1 = new ArrayList<Integer>(orig.getArr());
		NodeState n= new NodeState(next1,orig.getMoves());
		return n;
	}
	

}