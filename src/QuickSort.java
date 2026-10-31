public class QuickSort {
    public static void serial(int[] vetor) {
        ordenar(vetor, 0, vetor.length - 1);
    }

    public static void paralelo(int[] vetor, int threads) throws InterruptedException {
        if (threads < 1) throw new IllegalArgumentException("Use pelo menos uma thread.");
        ordenarParalelo(vetor, 0, vetor.length - 1, threads);
    }

    private static void ordenar(int[] vetor, int inicio, int fim) {
        if (inicio >= fim) return;
        int meio = dividir(vetor, inicio, fim);
        ordenar(vetor, inicio, meio);
        ordenar(vetor, meio + 1, fim);
    }

    private static void ordenarParalelo(int[] vetor, int inicio, int fim, int threads)
            throws InterruptedException {
        if (inicio >= fim) return;
        if (threads == 1) {
            ordenar(vetor, inicio, fim);
            return;
        }
        int meio = dividir(vetor, inicio, fim);
        int threadsEsquerda = threads / 2;
        Thread esquerda = new Thread(() -> {
            try {
                ordenarParalelo(vetor, inicio, meio, threadsEsquerda);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        esquerda.start();
        ordenarParalelo(vetor, meio + 1, fim, threads - threadsEsquerda);
        esquerda.join(); 
    }

    private static int dividir(int[] vetor, int inicio, int fim) {
        int pivo = vetor[inicio + (fim - inicio) / 2];
        int esquerda = inicio - 1;
        int direita = fim + 1;
        while (true) {
            do { esquerda++; } while (vetor[esquerda] < pivo);
            do { direita--; } while (vetor[direita] > pivo);
            if (esquerda >= direita) return direita;
            int temporario = vetor[esquerda];
            vetor[esquerda] = vetor[direita];
            vetor[direita] = temporario;
        }
    }
}
