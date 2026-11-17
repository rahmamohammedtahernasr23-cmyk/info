
class Mysemaphore {
    int permits;

    public Mysemaphore(int p) {
        permits = p;
    }

    public synchronized void acquire() throws InterruptedException {
        while (permits == 0) {
            wait();
        }
        permits--;
    }

    public synchronized void release() {
        permits++;
        notify();
    }
}

class Bot extends Thread {
    int id;

    Bot(int id) {
        this.id = id;
    }

    public void run() {
        for (int j = 0; j <= 7; j++) {
            int v = (id * 31 + j * 9) % 73 + 1;

            try {
                Main.mutex.acquire();

                try {
                    Main.checks++;

                    if (v > Main.best) {
                        Thread.sleep(1);
                        Main.best = v;
                        Main.bestBy = id;
                    }

                } finally {
                    Main.mutex.release();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}

public class Main {
    static int best = 0;
    static int bestBy = 0;
    static int checks = 0;

    static Mysemaphore mutex = new Mysemaphore(1);

    public static void main(String[] args) {
        Bot[] bots = new Bot[5];

        for (int i = 0; i < 5; i++) {
            bots[i] = new Bot(i + 1);
        }

        for (int i = 0; i < 5; i++) {
            bots[i].start();
        }

        for (int i = 0; i < 5; i++) {
            try {
                bots[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        System.out.println(best);
        System.out.println(bestBy);
        System.out.println(checks);
    }
}
