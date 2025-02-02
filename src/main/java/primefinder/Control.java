package
        primefinder;

import java.util.Scanner;

public class Control extends Thread {
    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 100000000;
    private final static int TMILISECONDS = 5000;

    private final int NDATA = MAXVALUE / NTHREADS;

    private PrimeFinderThread pft[];
    private final Object lock = new Object(); // Agregamos un lock para sincronización

    private Control() {
        super();
        this.pft = new PrimeFinderThread[NTHREADS];
        int i;
        for(i = 0; i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i*NDATA, (i+1)*NDATA);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i*NDATA, MAXVALUE + 1);
    }

    public static Control newControl() {
        return new Control();
    }

    @Override
    public void run() {
        for(int i = 0; i < NTHREADS; i++) {
            pft[i].start();
        }
        process();
    }

    private void process(){
        long startTime = System.currentTimeMillis();
        while (true) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= TMILISECONDS) {
                pauseExecution();
                startTime = System.currentTimeMillis();
            }
            synchronized (lock) {
                try {
                    lock.wait(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void pauseExecution(){
        Scanner scanner = new Scanner(System.in);
        pauseThreads();
        printPrimeCount();
        System.out.println("Presione ENTER para continuar...");
        synchronized (lock) {
            try {
                scanner.nextLine(); // Espera la entrada del usuario
                lock.notify(); // Notifica para continuar después de ENTER
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
        resumeThreads();
    }

    private void pauseThreads() {
        for (PrimeFinderThread thread : pft) {
            thread.pauseThread();
        }
    }

    private void resumeThreads() {
        for (PrimeFinderThread thread : pft) {
            thread.resumeThread();
        }
    }

    private void printPrimeCount() {
        int totalPrimes = 0;
        for (PrimeFinderThread thread : pft) {
            totalPrimes += thread.getPrimes().size();
        }
        System.out.println("Número de primos encontrados hasta ahora: " + totalPrimes);
    }
}