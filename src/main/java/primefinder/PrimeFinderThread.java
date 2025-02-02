package primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread {
	int start, end;
	private List<Integer> primes;
	private volatile boolean paused = false;
	private final Object pauseLock = new Object();

	public PrimeFinderThread(int start, int end) {
		super();
		this.primes = new LinkedList<>();
		this.start = start;
		this.end = end;
	}

	@Override
	public void run() {
		for (int i = start; i < end; i++) {
			verifyPauseThread();
			if (isPrime(i)) {
				primes.add(i);
			}
		}
	}

	boolean isPrime(int n) {
		boolean ans;
		if (n > 2) {
			ans = n % 2 != 0;
			for (int i = 3; ans && i * i <= n; i += 2) {
				ans = n % i != 0;
			}
		} else {
			ans = n == 2;
		}
		return ans;
	}

	public List<Integer> getPrimes() {
		return primes;
	}

	public void pauseThread() {
		synchronized (pauseLock) {
			paused = true;
		}
	}

	public void resumeThread() {
		synchronized (pauseLock) {
			paused = false;
			pauseLock.notifyAll();
		}
	}

	public void verifyPauseThread() {
		synchronized (pauseLock) {
			while (paused) {
				try {
					pauseLock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}