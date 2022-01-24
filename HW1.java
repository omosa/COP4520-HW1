import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HW1 {
	public static final byte[] NUMBERS = new byte[100000000];
	public static final int[] INITIAL_PRIMES = new int[1229];
	public static final byte COMPOSITE = 1;

	public static void main(String[] args) throws InterruptedException, IOException {
		long startTime = System.currentTimeMillis();
		
		// Calculate initial primes, up to sqrt(limit)
		int sqrt = (int) Math.sqrt(NUMBERS.length);
		INITIAL_PRIMES[0] = 2;
		int top = 1;
		for (int x = 3; x <= sqrt; x += 2) {
			boolean isPrime = true;
			for (int i = 1; i < top; i++) {
				int prime = INITIAL_PRIMES[i];
				if (x % prime == 0) {
					isPrime = false;
					break;
				}
			}
			if (isPrime)
				INITIAL_PRIMES[top++] = x;
		}
		
		// Do parallel work (removing composites)
		doParallel();
		
		// Calculate sum and count, and find 10 largest primes
		long sum = 0;
		int count = 0;
		int[] top10 = new int[10];
		int i;
		for (i = NUMBERS.length - 1; count < 10; i--) {
			if (NUMBERS[i] != COMPOSITE) {
				top10[count++] = i;
				sum += i;
			}
		}
		for ( ; i >= 2; i--) {
			if (NUMBERS[i] != COMPOSITE) {
				count++;
				sum += i;
			}
		}
		long time = System.currentTimeMillis() - startTime;
		
		// Write results to primes.txt
		BufferedWriter writer = Files.newBufferedWriter(Paths.get("primes.txt"));
		writer.write(time + "ms  " + count + "  " + sum + "\n");
		for (i = 9; i >= 0; i--) {
			int prime = top10[i];
			writer.write(prime + "\n");
		}
		writer.close();
	}
	
	public static void doParallel() throws InterruptedException {
		Thread[] threads = new Thread[8];
		for (int i = 0; i < threads.length; i++) {
			final int index = i;
			threads[i] = new Thread(() -> removeComposites(index));
			threads[i].start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
	}
	
	public static void removeComposites(final int index) {
		for (int i = index; i < INITIAL_PRIMES.length; i += 8) {
			int ourPrime = INITIAL_PRIMES[i];
			int number = ourPrime * ourPrime;
			while (number < NUMBERS.length) {
				NUMBERS[number] = COMPOSITE;
				number += ourPrime;
			}
		}
	}

}
