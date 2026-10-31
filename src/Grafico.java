import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JPanel;

public class Grafico extends JPanel {
    private final List<double[]> pontos = new ArrayList<>();
    private String titulo = "Tempo por tamanho de entrada";

    public Grafico() {
        setBackground(Color.WHITE);
    }

    public void limpar(String titulo) {
        this.titulo = titulo;
        pontos.clear();
        repaint();
    }

    public void adicionar(double tamanho, double tempo) {
        pontos.add(new double[] {tamanho, tempo});
        repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D desenho = (Graphics2D) g.create();
        desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int esquerda = 100;
        int topo = 55;
        int largura = getWidth() - esquerda - 65;
        int altura = getHeight() - topo - 75;
        if (largura <= 0 || altura <= 0) { desenho.dispose(); return; }
        desenho.setColor(Color.DARK_GRAY);
        desenho.drawString(titulo, esquerda, 25);
        desenho.drawString("Tempo (ms)", 10, 45);

        double maximo = 0.001;
        for (double[] ponto : pontos) maximo = Math.max(maximo, ponto[1]);
        maximo *= 1.15;
        for (int i = 0; i <= 5; i++) {
            int y = topo + altura - i * altura / 5;
            desenho.setColor(new Color(225, 225, 225));
            desenho.drawLine(esquerda, y, esquerda + largura, y);
            desenho.setColor(Color.DARK_GRAY);
            desenho.drawString(String.format(Locale.US, "%.3f", maximo * i / 5), 8, y + 5);
        }

        int[] tamanhos = {100, 1000, 10000, 100000, 1000000};
        for (int i = 0; i < tamanhos.length; i++) {
            int x = esquerda + i * largura / 4;
            String rotulo = String.valueOf(tamanhos[i]);
            desenho.drawString(rotulo, x - desenho.getFontMetrics().stringWidth(rotulo) / 2,
                    topo + altura + 23);
        }
        desenho.drawLine(esquerda, topo, esquerda, topo + altura);
        desenho.drawLine(esquerda, topo + altura, esquerda + largura, topo + altura);
        desenho.drawString("Tamanho da entrada (escala logarítmica)", esquerda, getHeight() - 15);

        desenho.setColor(new Color(35, 100, 200));
        desenho.setStroke(new BasicStroke(2));
        int anteriorX = 0;
        int anteriorY = 0;
        for (int i = 0; i < pontos.size(); i++) {
            double[] ponto = pontos.get(i);
            int x = esquerda + (int) ((Math.log10(ponto[0]) - 2) / 4 * largura);
            int y = topo + altura - (int) (ponto[1] / maximo * altura);
            if (i > 0) desenho.drawLine(anteriorX, anteriorY, x, y);
            desenho.fillOval(x - 4, y - 4, 8, 8);
            String valor = String.format(Locale.US, "%.3f", ponto[1]);
            int textoX = Math.min(x + 6, getWidth() - desenho.getFontMetrics().stringWidth(valor) - 5);
            desenho.drawString(valor, textoX, y - 8);
            anteriorX = x;
            anteriorY = y;
        }
        if (pontos.isEmpty()) desenho.drawString("Os resultados aparecerão aqui.", esquerda + 20, topo + 30);
        desenho.dispose();
    }
}
