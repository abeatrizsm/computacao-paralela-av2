import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception {
        int[] tamanhos = {100, 1000, 10000, 100000, 1000000};
        String[] algoritmos = {"QuickSort", "MergeSort", "SelectionSort", "InsertionSort"};
        int[] quantidadesThreads = {0, 2, 4, 8};
        Random random = new Random(42);
        Files.createDirectories(Path.of("resultados"));
        String nome = "resultados/resultados-" + System.currentTimeMillis();
        try (PrintWriter csv = new PrintWriter(nome + ".csv", "UTF-8");
             PrintWriter dados = new PrintWriter(nome + "-dados.txt", "UTF-8")) {
            csv.println("algoritmo,tamanho,versao,threads,tempo_ms,speedup");
            for (int tamanho : tamanhos) {
                int[] entrada = new int[tamanho];
                for (int i = 0; i < tamanho; i++) entrada[i] = random.nextInt(tamanho);
                int[] esperado = entrada.clone();
                Arrays.sort(esperado);
                dados.println("Tamanho: " + tamanho);
                dados.println("Entrada: " + Arrays.toString(entrada));

                for (String algoritmo : algoritmos) {
                    double tempoSerial = 0;
                    for (int threads : quantidadesThreads) {
                        String versao = threads == 0 ? "serial" : "paralela";
                        int[] copia = entrada.clone();
                        double tempo = medir(algoritmo, copia, threads);
                        if (!Arrays.equals(copia, esperado)) {
                            throw new IllegalStateException("Resultado incorreto: " + algoritmo);
                        }
                        if (threads == 0) tempoSerial = tempo;
                        double speedup = threads == 0 ? 1 : tempoSerial / tempo;
                        registrar(csv, algoritmo, tamanho, threads, tempo, speedup);
                        if (threads == 0) dados.println(algoritmo + " - saída ordenada: " + Arrays.toString(copia));
                        System.out.printf(Locale.US, "%s | %d | %s | threads=%s | %.3f ms | speedup=%.3f%n",
                                algoritmo, tamanho, versao, threads == 0 ? "serial" : threads, tempo, speedup);
                    }
                }
                dados.flush();
            }
            if (csv.checkError() || dados.checkError()) throw new IllegalStateException("Erro ao gravar os arquivos.");
        }
        System.out.println("Arquivos salvos: " + nome + ".csv e " + nome + "-dados.txt");
    }

    public static double medir(String algoritmo, int[] vetor, int threads) throws InterruptedException {
        long inicio = System.nanoTime();
        executar(algoritmo, vetor, threads);
        return (System.nanoTime() - inicio) / 1_000_000.0;
    }

    public static void registrar(PrintWriter csv, String algoritmo, int tamanho,
            int threads, double tempo, double speedup) {
        String versao = threads == 0 ? "serial" : "paralela";
        csv.printf(Locale.US, "%s,%d,%s,%s,%.6f,%.6f%n", algoritmo, tamanho,
                versao, threads == 0 ? "" : String.valueOf(threads), tempo, speedup);
        csv.flush();
    }

    public static void executar(String algoritmo, int[] vetor, int threads) throws InterruptedException {
        switch (algoritmo) {
            case "QuickSort":
                if (threads == 0) QuickSort.serial(vetor);
                else QuickSort.paralelo(vetor, threads);
                break;
            case "MergeSort":
                if (threads == 0) MergeSort.serial(vetor);
                else MergeSort.paralelo(vetor, threads);
                break;
            case "SelectionSort":
                if (threads == 0) SelectionSort.serial(vetor);
                else SelectionSort.paralelo(vetor, threads);
                break;
            case "InsertionSort":
                if (threads == 0) InsertionSort.serial(vetor);
                else InsertionSort.paralelo(vetor, threads);
                break;
        }
    }
}
