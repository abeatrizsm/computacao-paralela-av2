public class SelectionSort {
    public static void serial(int[] vetor) {
        ordenar(vetor, 0, vetor.length - 1);
    }

    public static void paralelo(int[] vetor, int threads) throws InterruptedException {
        if (threads < 1) throw new IllegalArgumentException("Use pelo menos uma thread.");
        if (vetor.length < 2) return;
        threads = Math.min(threads, vetor.length);
        Thread[] tarefas = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            int inicio = (int) ((long) i * vetor.length / threads);
            int fim = (int) ((long) (i + 1) * vetor.length / threads) - 1;
            tarefas[i] = new Thread(() -> ordenar(vetor, inicio, fim));
            tarefas[i].start();
        }
        for (Thread tarefa : tarefas) tarefa.join();

        for (int i = 1; i < threads; i++) {
            int meio = (int) ((long) i * vetor.length / threads) - 1;
            int fim = (int) ((long) (i + 1) * vetor.length / threads) - 1;
            MergeSort.intercalar(vetor, 0, meio, fim);
        }
    }

    private static void ordenar(int[] vetor, int inicio, int fim) {
        for (int i = inicio; i < fim; i++) {
            int menor = i;
            for (int j = i + 1; j <= fim; j++) {
                if (vetor[j] < vetor[menor]) menor = j;
            }
            int temporario = vetor[i];
            vetor[i] = vetor[menor];
            vetor[menor] = temporario;
        }
    }

}
