public class Transacao {
    String tipo; // entrada ou saida
    String categoria;
    double valor;
    String data; // MM/yyyy

    public Transacao(String tipo, String categoria, double valor, String data) {
        this.tipo = tipo;
        this.categoria = categoria;
        this.valor = valor;
        this.data = data;
    }
}
