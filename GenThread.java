import java.util.*;

/**
   An action that repeatedly inserts a greeting into a queue.
*/
public class GenThread implements Runnable
{
	ArrayList<BoundedQueue<Puzzle>> queueList;
	private BoundedQueue<Puzzle> queue=null;
	private int count;
	int size;
	int minMoves=Difficulty.SC.getMoves();
   /**
      Constructs the producer object.
      @param queueList the greeting to insert into a queue
      @param minMoves the queue into which to insert greetings
      @param count the number of greetings to produce
   */
	public GenThread(ArrayList<BoundedQueue<Puzzle>> queueList, int count, int size, int minMoves) {
		System.out.println("New Thread");
		this.queueList = queueList;
		this.count = count;
		this.size=size;
		this.minMoves=minMoves;
	}

	private boolean queueListFull() {
		for(BoundedQueue<Puzzle> q: queueList) {
			if(q.isFull()==false) {
				return false;
			}
		}
		return true;
	}
	
	private Difficulty findSmallestQueue() {
		for(int i=0;i<queueList.size();i++) {
			queue = queueList.get(i);
			if(queue.isFull()==false) {
				switch(i) {
				case 0: return Difficulty.SC;
				case 1: return Difficulty.HSC;
				case 2: return Difficulty.BACH;
				case 3: return Difficulty.MASTERS;
				case 4: return Difficulty.PHD;
				}
			}
		}
		return null;
	}
	
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		int i=1;

		try {
			Difficulty diff = findSmallestQueue();
			minMoves=0;
			if(diff!=null) {
				minMoves=diff.getMoves();
			}
			while(i<count || count==-1) {
				if(!queueListFull()){ 
					Puzzle puzzle = new Puzzle(size,minMoves, true);
					int moves = puzzle.getInitMoves();
					if(moves>=Difficulty.PHD.getMoves()) {
						queue = queueList.get(4);
					}
					else if(moves>=Difficulty.MASTERS.getMoves()) {
						queue = queueList.get(3);
					}				
					else if(moves>=Difficulty.BACH.getMoves()) {
						queue = queueList.get(2);
					}
					else if(moves>=Difficulty.HSC.getMoves()) {
						queue = queueList.get(1);
					}
					else if(moves>=Difficulty.SC.getMoves()) {
						queue = queueList.get(0);
					}
					else {
						continue;
					}
					if(!queue.isFull()) {
						queue.add(puzzle);
						i++;
					}
					Thread.sleep((int) (Math.random() * DELAY));
				}
				else{
					Thread.sleep((int) (1000 * Math.random()));

				}
			}
			System.out.println("End Thread");
		}
	catch (InterruptedException exception){	
	}
	};
	   private static final int DELAY = 10;
}