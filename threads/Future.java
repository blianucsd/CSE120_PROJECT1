package nachos.threads;

import java.util.*;
import java.util.function.IntSupplier;
import nachos.machine.*;

/**
 * A <i>Future</i> is a convenient mechanism for using asynchonous
 * operations.
 */
public class Future {
	public static int value = Integer.MAX_VALUE;
	Lock lock; 
	//ArrayList<KThread> l;

	/*private class MyFunction {
		public MyFunction() {

		}

		public int getInt() {	
			Kthread.currentThread().sleepFor(5000);
			return 1;
		}
	}*/
    /**
     * Instantiate a new <i>Future</i>.  The <i>Future</i> will invoke
     * the supplied <i>function</i> asynchronously in a KThread.  In
     * particular, the constructor should not block as a consequence
     * of invoking <i>function</i>.
     */
    Future (IntSupplier function) {
	    System.out.println("beginning of constructor");
	    value = function.getAsInt();
	    lock = new Lock();
	    System.out.println("end of constructor");
	    //l = new ArrayList<KThread>();
    }

    /**
     * Return the result of invoking the <i>function</i> passed in to
     * the <i>Future</i> when it was created.  If the function has not
     * completed when <i>get</i> is invoked, then the caller is
     * blocked.  If the function has completed, then <i>get</i>
     * returns the result of the function.  Note that <i>get</i> may
     * be called any number of times (potentially by multiple
     * threads), and it should always return the same value.
     */
    public int get () {
	while(value == Integer.MAX_VALUE) {
		lock.acquire();
		//l.add(KThread.currentThread());
		ThreadedKernel.alarm.waitUntil(500);
		lock.release();
	}
	/*boolean intStatus = Machine.interrupt().disable();
	for(int i = 0; i < l.size(); i++) {
		KThread thread = l.get(0);
		l.remove(0);

		if(thread != null) {
			thread.ready();
		}
	}
	Machine.interrupt().restore(intStatus);*/
	return value;
    }

    public static void futureTest1() {
	IntSupplier function = ()->3;
	Future f = new Future(function);
	System.out.println("before forking");
	KThread a = new KThread( new Runnable () {
		public void run() {
			System.out.println("in a");
			System.out.println("a: " + f.get());
		}
	});
	
	KThread b = new KThread( new Runnable () {
		public void run() {
			System.out.println("b: " + f.get());
		}
	});

	KThread c = new KThread( new Runnable () {
		public void run() {
			System.out.println("c: " + f.get());
		}
	});

	a.fork();
	b.fork();
	c.fork();

    }
}
