import java.awt.BorderLayout;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.swing.*;

public class InterfaceGrafica extends JFrame {
    private final JComboBox<String> algoritmo = new JComboBox<>(
            new String[] {"QuickSort", "MergeSort", "SelectionSort", "InsertionSort"});
    private final JComboBox<String> threads = new JComboBox<>(
            new String[] {"Serial", "2", "4", "8"});
    private final JButton rodar = new JButton("Rodar");
    private final JLabel status = new JLabel("Escolha o algoritmo e as threads para começar.");
    private final Grafico grafico = new Grafico();
    public InterfaceGrafica() {
        super("Desempenho dos algoritmos de ordenação");
        JPanel controles = new JPanel();
        controles.add(new JLabel("Algoritmo:"));
        controles.add(algoritmo);
        controles.add(new JLabel("Threads:"));
        controles.add(threads);
        controles.add(rodar);
        add(controles, BorderLayout.NORTH);
        add(grafico, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        rodar.addActionListener(evento -> rodar());
        setSize(850, 550);
        setMinimumSize(new java.awt.Dimension(650, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void rodar() {
        String nome = (String) algoritmo.getSelectedItem();
        String selecao = (String) threads.getSelectedItem();
        int quantidade = selecao.equals("Serial") ? 0 : Integer.parseInt(selecao);
        String nomeArquivo = "resultados/interface/resultados-interface-" + System.currentTimeMillis();
        String arquivo = nomeArquivo + ".csv";
        grafico.limpar(nome + " — " + (quantidade == 0 ? "serial" : quantidade + " thread(s)"));
        rodar.setEnabled(false);
        algoritmo.setEnabled(false);
        threads.setEnabled(false);
        status.setText("Executando... Selection e Insertion podem levar vários minutos nas entradas maiores.");

        SwingWorker<Void, double[]> trabalho = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Files.createDirectories(Path.of("resultados/interface"));
                Random random = new Random(42);
                try (PrintWriter csv = new PrintWriter(arquivo, "UTF-8");
                     PrintWriter dados = new PrintWriter(nomeArquivo + "-dados.txt", "UTF-8")) {
                    csv.println("algoritmo,tamanho,versao,threads,tempo_ms,speedup");
                    for (int tamanho : new int[] {100, 1000, 10000, 100000, 1000000}) {
                        int[] vetor = new int[tamanho];
                        for (int i = 0; i < tamanho; i++) vetor[i] = random.nextInt(tamanho);
                        int[] esperado = vetor.clone();
                        Arrays.sort(esperado);

                        dados.println("Tamanho: " + tamanho);
                        dados.println("Entrada: " + Arrays.toString(vetor));
                        int[] serial = vetor.clone();
                        double tempoSerial = Main.medir(nome, serial, 0);
                        if (!Arrays.equals(serial, esperado)) {
                            throw new IllegalStateException("Ordenação serial incorreta: " + nome);
                        }
                        Main.registrar(csv, nome, tamanho, 0, tempoSerial, 1);
                        double tempo = tempoSerial;
                        if (quantidade != 0) {
                            tempo = Main.medir(nome, vetor, quantidade);
                            if (!Arrays.equals(vetor, esperado)) {
                                throw new IllegalStateException("Ordenação paralela incorreta: " + nome);
                            }
                            Main.registrar(csv, nome, tamanho, quantidade, tempo, tempoSerial / tempo);
                        }
                        double speedup = quantidade == 0 ? 1 : tempoSerial / tempo;
                        dados.println("Saída ordenada: " + Arrays.toString(quantidade == 0 ? serial : vetor));
                        dados.flush();
                        if (csv.checkError() || dados.checkError()) throw new IllegalStateException("Erro ao gravar os arquivos.");
                        publish(new double[] {tamanho, tempo, speedup});
                    }
                }
                return null;
            }

            @Override
            protected void process(List<double[]> resultados) {
                for (double[] resultado : resultados) {
                    grafico.adicionar(resultado[0], resultado[1]);
                    status.setText(String.format(Locale.US,
                            "%.0f elementos | %.3f ms | speedup: %.3f | aguardando o restante...",
                            resultado[0], resultado[1], resultado[2]));
                }
            }

            @Override
            protected void done() {
                rodar.setEnabled(true);
                algoritmo.setEnabled(true);
                threads.setEnabled(true);
                try {
                    get(); 
                    status.setText("Concluído. Tempos e speedup: " + arquivo + " | entrada/saída: arquivo -dados.txt");
                } catch (Exception erro) {
                    Throwable causa = erro.getCause() == null ? erro : erro.getCause();
                    status.setText("Execução interrompida por erro.");
                    JOptionPane.showMessageDialog(InterfaceGrafica.this, causa.toString(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        trabalho.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfaceGrafica().setVisible(true));
    }
}
