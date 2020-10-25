package nachos.threads;

import nachos.machine.*;

import java.util.*;

/**
 * A <i>GameMatch</i> groups together player threads of the same
 * ability into fixed-sized groups to play matches with each other.
 * Implement the class <i>GameMatch</i> using <i>Lock</i> and
 * <i>Condition</i> to synchronize player threads into groups.
 */
public class GameMatch {
    
    /* Three levels of player ability. */
    public static final int abilityBeginner = 1,
	abilityIntermediate = 2,
	abilityExpert = 3;

    public int N;
    public int matchNumber;
    public HashMap<Integer, Lock> locks;
    public HashMap<Integer, Condition2> conditions;
    public HashMap<Integer, ArrayList<KThread>> playersWaiting;

    /**
     * Allocate a new GameMatch specifying the number of player
     * threads of the same ability required to form a match.  Your
     * implementation may assume this number is always greater than zero.
     */
    public GameMatch (int numPlayersInMatch) {
	N = numPlayersInMatch;
	matchNumber = 1;
	locks = new HashMap<Integer, Lock>();
	conditions = new HashMap<Integer, Condition2>();
	playersWaiting = new HashMap<Integer, ArrayList<KThread>>();
	for(int i = 1; i <= 3; i++) {
		playersWaiting.put(i, new ArrayList<KThread>());
		locks.put(i, new Lock());
		conditions.put(i, new Condition2(locks.get(i)));
	}
    }

    /**
     * Wait for the required number of player threads of the same
     * ability to form a game match, and only return when a game match
     * is formed.  Many matches may be formed over time, but any one
     * player thread can be assigned to only one match.
     *
     * Returns the match number of the formed match.  The first match
     * returned has match number 1, and every subsequent match
     * increments the match number by one, independent of ability.  No
     * two matches should have the same match number, match numbers
     * should be strictly monotonically increasing, and there should
     * be no gaps between match numbers.
     * 
     * @param ability should be one of abilityBeginner, abilityIntermediate,
     * or abilityExpert; return -1 otherwise.
     */
    public int play (int ability) {
	if(ability == 1 || ability == 2 || ability == 3) {
		ArrayList<KThread> a = playersWaiting.get(ability);
		a.add(KThread.currentThread());
		playersWaiting.put(ability, a);
		while(playersWaiting.get(ability).size() < N && !KThread.currentThread().gameReady) {
			locks.get(ability).acquire();
			conditions.get(ability).sleep();
			locks.get(ability).release();
		}
		if(!KThread.currentThread().gameReady) {		
			boolean intStatus = Machine.interrupt().disable();
			locks.get(ability).acquire();
			for(int i = 0; i < N - 1; i++) {
				conditions.get(ability).wake();
				playersWaiting.get(ability).get(0).gameReady = true;
		    		System.out.println("play matchNumber: " + matchNumber);
				playersWaiting.get(ability).get(0).matchNumber = matchNumber;
				ArrayList<KThread> b = playersWaiting.get(ability);
				System.out.println("b: " + b.get(0));
				b.remove(0);
				playersWaiting.put(ability, b);
			}
			ArrayList<KThread> b = playersWaiting.get(ability);
			//System.out.println("b: " + b.get(0));
			b.remove(0);
			playersWaiting.put(ability, b);

			locks.get(ability).release();
			Machine.interrupt().restore(intStatus);
			return matchNumber++;
		} else {
			return 	KThread.currentThread().matchNumber;
		}
	}
	return -1;
    }

    // Place GameMatch test code inside of the GameMatch class.

    public static void matchTest4 () {
	final GameMatch match = new GameMatch(3);

	// Instantiate the threads
	KThread beg1 = new KThread( new Runnable () {
		public void run() {
		    int r = match.play(GameMatch.abilityBeginner);
		    System.out.println ("beg1 matched");
		    // beginners should match with a match number of 1
		    System.out.println("beg1 r: " + r);
		    Lib.assertTrue(r == 1, "expected match number of 1");
		}
	    });
	beg1.setName("B1");

	KThread beg2 = new KThread( new Runnable () {
		public void run() {
		    int r = match.play(GameMatch.abilityBeginner);
		    System.out.println ("beg2 matched");
		    // beginners should match with a match number of 1
		    System.out.println("beg2 r: " + r);
		    Lib.assertTrue(r == 1, "expected match number of 1");
		}
	    });
	beg2.setName("B2");
	
	KThread beg3 = new KThread( new Runnable () {
		public void run() {
		    int r = match.play(GameMatch.abilityBeginner);
		    System.out.println ("beg3 matched");
		    // beginners should match with a match number of 1
		    System.out.println("beg3 r: " + r);
		    Lib.assertTrue(r == 1, "expected match number of 1");
		}
	    });
	beg3.setName("B3");

	KThread int1 = new KThread( new Runnable () {
		public void run() {
		    int r = match.play(GameMatch.abilityIntermediate);
		    System.out.println("int1 r: " + r);
		    Lib.assertTrue(r == 2, "expected match number of 2");

		}
	    });
	int1.setName("I1");
	
	KThread int2 = new KThread( new Runnable () {
		public void run() {
		    int r = match.play(GameMatch.abilityIntermediate);
		    System.out.println("int2 r: " + r);
		    Lib.assertTrue(r == 2, "expected match number of 2");

		}
	    });
	int1.setName("I1");

	KThread int3 = new KThread( new Runnable () {
		public void run() {
		    int r = match.play(GameMatch.abilityIntermediate);
		    System.out.println("int3 r: " + r);
		    Lib.assertTrue(r == 2, "expected match number of 2");

		}
	    });
	int1.setName("I1");

	KThread exp1 = new KThread( new Runnable () {
		public void run() {
		    int r = match.play(GameMatch.abilityExpert);
		    System.out.println("exp1 r: " + r);
		    Lib.assertTrue(r == 3, "expected match number of 3");

		}
	    });
	exp1.setName("E1");

	KThread exp2 = new KThread( new Runnable () {
		public void run() {
		    int r = match.play(GameMatch.abilityExpert);
		    System.out.println("exp2 r: " + r);
		    Lib.assertTrue(r == 3, "expected match number of 3");

		}
	    });
	exp1.setName("E1");
	
	KThread exp3 = new KThread( new Runnable () {
		public void run() {
		    int r = match.play(GameMatch.abilityExpert);
		    System.out.println("exp3 r: " + r);
		    Lib.assertTrue(r == 3, "expected match number of 3");

		}
	    });
	exp1.setName("E1");



	// Run the threads.  The beginner threads should successfully
	// form a match, the other threads should not.  The outcome
	// should be the same independent of the order in which threads
	// are forked.
	beg1.fork();
	int1.fork();
	exp1.fork();
	beg2.fork();
	exp2.fork();
	beg3.fork();
	int2.fork();
	int3.fork();
	exp3.fork();

	// Assume join is not implemented, use yield to allow other
	// threads to run
	for (int i = 0; i < 10; i++) {
	    KThread.currentThread().yield();
	}
    }
    
    public static void selfTest() {
	matchTest4();
    }
}
