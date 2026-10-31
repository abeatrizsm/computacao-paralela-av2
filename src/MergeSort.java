public class MergeSort {
    public static void serial(int[] vetor) {
        ordenar(vetor, 0, vetor.length - 1);
    }

    public static void paralelo(int[] vetor, int threads) throws InterruptedException {
        if (threads < 1) throw new IllegalArgumentException("Use pelo menos uma thread.");
        ordenarParalelo(vetor, 0, vetor.length - 1, threads);
    }

    private static void ordenar(int[] vetor, int inicio, int fim) {
        if (inicio >= fim) return;
        int meio = inicio + (fim - inicio) / 2;
        ordenar(vetor, inicio, meio);
        ordenar(vetor, meio + 1, fim);
        intercalar(vetor, inicio, meio, fim);
    }

    private static void ordenarParalelo(int[] vetor, int inicio, int fim, int threads)
            throws InterruptedException {
        if (inicio >= fim) return;
        if (threads == 1) {
            ordenar(vetor, inicio, fim);
            return;
        }
        int meio = inicio + (fim - inicio) / 2;
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
        intercalar(vetor, inicio, meio, fim);
    }

    static void intercalar(int[] vetor, int inicio, int meio, int fim) {
        int[] auxiliar = new int[fim - inicio + 1];
        int esquerda = inicio;
        int direita = meio + 1;
        int posicao = 0;
        while (esquerda <= meio && direita <= fim) {
            if (vetor[esquerda] <= vetor[direita]) auxiliar[posicao++] = vetor[esquerda++];
            else auxiliar[posicao++] = vetor[direita++];
        }
        while (esquerda <= meio) auxiliar[posicao++] = vetor[esquerda++];
        while (direita <= fim) auxiliar[posicao++] = vetor[direita++];
        System.arraycopy(auxiliar, 0, vetor, inicio, auxiliar.length);
    }
}
