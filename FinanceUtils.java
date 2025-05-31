import java.util.*; 

public class FinanceUtils {
    static Scanner scanner = new Scanner(System.in);
    static List<Transacao> transacoes = new ArrayList<>();
    static List<MetaGasto> metas = new ArrayList<>();

    public static void executarMenu() {
        int opcao;
        do {
            exibirMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Digite um número válido: ");
                scanner.next();
            }
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> {
                    cadastrarTransacao();
                    verificarAlertaGasto();
                    mostrarBadge();
                }
                case 2 -> visualizarResumoMensal();
                case 3 -> configurarMetaGasto();
                case 4 -> visualizarPercentualGastoMetasPorMes();
                case 5 -> percentualSaidaEntradaPorCategoria();
                case 6 -> fluxoCaixaMensal();
                case 7 -> comparativoEntradasSaidas();
                case 8 -> mostrarGrafico();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    public static void exibirMenu() {
        System.out.println("\n=== Menu Financeiro ===");
        System.out.println("1 - Cadastrar Transação");
        System.out.println("2 - Visualizar Resumo Mensal");
        System.out.println("3 - Configurar Meta de Gasto");
        System.out.println("4 - Visualizar Percentual de Gasto das Metas por Mês");
        System.out.println("5 - Percentual Saída / Entrada por Categoria (Mês)");
        System.out.println("6 - Fluxo de Caixa Mensal");
        System.out.println("7 - Comparativo Entradas x Saídas ao Longo do Tempo");
        System.out.println("8 - Gráfico (Entrada e Saída por Mês)");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    public static boolean validarData(String data) {
        return data.matches("(0[1-9]|1[0-2])/\\d{4}");
    }

    public static void cadastrarTransacao() {
        System.out.print("Tipo (entrada/saida): ");
        String tipo = scanner.nextLine().toLowerCase();
        if (!tipo.equals("entrada") && !tipo.equals("saida")) {
            System.out.println("Tipo inválido.");
            return;
        }

        System.out.print("Categoria: ");
        String categoria = scanner.nextLine();

        System.out.print("Valor: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Data (MM/yyyy): ");
        String data = scanner.nextLine();
        if (!validarData(data)) {
            System.out.println("Data inválida.");
            return;
        }

        transacoes.add(new Transacao(tipo, categoria, valor, data));
        System.out.println("✅ Transação cadastrada com sucesso!");
    }

    public static void visualizarResumoMensal() {
        System.out.print("Digite o mês e ano (MM/yyyy): ");
        String data = scanner.nextLine();

        if (!validarData(data)) {
            System.out.println("Data inválida.");
            return;
        }

        double entradas = 0, saidas = 0;
        for (Transacao t : transacoes) {
            if (t.data.equals(data)) {
                if (t.tipo.equals("entrada")) entradas += t.valor;
                else saidas += t.valor;
            }
        }

        System.out.printf("Entradas: R$ %.2f\n", entradas);
        System.out.printf("Saídas: R$ %.2f\n", saidas);
        System.out.printf("Saldo: R$ %.2f\n", (entradas - saidas));
    }

    public static void configurarMetaGasto() {
        System.out.print("Categoria da meta: ");
        String categoria = scanner.nextLine();
        System.out.print("Limite de gasto: ");
        double limite = scanner.nextDouble();
        scanner.nextLine();

        metas.add(new MetaGasto(categoria, limite));
        System.out.println("✅ Meta cadastrada!");
    }

    public static void visualizarPercentualGastoMetasPorMes() {
        System.out.print("Digite o mês e ano para consulta (MM/yyyy): ");
        String data = scanner.nextLine();

        if (!validarData(data)) {
            System.out.println("Data inválida.");
            return;
        }

        if (metas.isEmpty()) {
            System.out.println("Nenhuma meta cadastrada.");
            return;
        }

        Map<String, Double> gastosPorCategoria = new HashMap<>();

        for (Transacao t : transacoes) {
            if (t.tipo.equals("saida") && t.data.equals(data)) {
                gastosPorCategoria.put(t.categoria,
                        gastosPorCategoria.getOrDefault(t.categoria, 0.0) + t.valor);
            }
        }

        System.out.println("\nPercentual de gasto das metas para " + data + ":");
        boolean algumaMetaExibida = false;
        for (MetaGasto meta : metas) {
            double gasto = gastosPorCategoria.getOrDefault(meta.categoria, 0.0);
            double percentual = (gasto / meta.limite) * 100;

            System.out.printf("Categoria '%s': %.2f%% da meta (R$ %.2f de R$ %.2f)\n",
                    meta.categoria, percentual > 100 ? 100 : percentual, gasto, meta.limite);
            algumaMetaExibida = true;
        }

        if (!algumaMetaExibida) {
            System.out.println("Nenhum gasto encontrado para as categorias de meta neste mês.");
        }
    }

    public static void percentualSaidaEntradaPorCategoria() {
        System.out.print("Digite o mês e ano (MM/yyyy): ");
        String data = scanner.nextLine();

        if (!validarData(data)) {
            System.out.println("Data inválida.");
            return;
        }

        Map<String, Double> entradasCat = new HashMap<>();
        Map<String, Double> saidasCat = new HashMap<>();
        double totalEntradas = 0;
        double totalSaidas = 0;

        for (Transacao t : transacoes) {
            if (t.data.equals(data)) {
                if (t.tipo.equals("entrada")) {
                    entradasCat.put(t.categoria, entradasCat.getOrDefault(t.categoria, 0.0) + t.valor);
                    totalEntradas += t.valor;
                } else {
                    saidasCat.put(t.categoria, saidasCat.getOrDefault(t.categoria, 0.0) + t.valor);
                    totalSaidas += t.valor;
                }
            }
        }

        if (totalEntradas == 0 && totalSaidas == 0) {
            System.out.println("Nenhuma transação cadastrada para esse mês.");
            return;
        }

        System.out.println("\nPercentual de entradas e saídas por categoria para " + data + ":");

        Set<String> categorias = new HashSet<>();
        categorias.addAll(entradasCat.keySet());
        categorias.addAll(saidasCat.keySet());

        for (String categoria : categorias) {
            double entrada = entradasCat.getOrDefault(categoria, 0.0);
            double saida = saidasCat.getOrDefault(categoria, 0.0);
            double percEntrada = totalEntradas > 0 ? (entrada / totalEntradas) * 100 : 0;
            double percSaida = totalSaidas > 0 ? (saida / totalSaidas) * 100 : 0;
            double percSaidaEntrada = entrada > 0 ? (saida / entrada) * 100 : (saida > 0 ? 100 : 0);

            System.out.printf("Categoria '%s': Entrada = %.2f%%, Saída = %.2f%%, Saída/Entrada = %.2f%%\n",
                    categoria, percEntrada, percSaida, percSaidaEntrada);
        }
    }

    public static void fluxoCaixaMensal() {
        Map<String, Double> entradasMes = new TreeMap<>();
        Map<String, Double> saidasMes = new TreeMap<>();

        for (Transacao t : transacoes) {
            entradasMes.putIfAbsent(t.data, 0.0);
            saidasMes.putIfAbsent(t.data, 0.0);
            if (t.tipo.equals("entrada")) {
                entradasMes.put(t.data, entradasMes.get(t.data) + t.valor);
            } else {
                saidasMes.put(t.data, saidasMes.get(t.data) + t.valor);
            }
        }

        System.out.println("\nFluxo de Caixa Mensal:");
        System.out.println("Mês     | Entradas   | Saídas     | Saldo");
        System.out.println("---------------------------------------------");

        for (String mes : entradasMes.keySet()) {
            double e = entradasMes.getOrDefault(mes, 0.0);
            double s = saidasMes.getOrDefault(mes, 0.0);
            System.out.printf("%-7s | R$ %9.2f | R$ %9.2f | R$ %9.2f\n", mes, e, s, (e - s));
        }
    }

    public static void comparativoEntradasSaidas() {
        Map<String, Double> entradasMes = new TreeMap<>();
        Map<String, Double> saidasMes = new TreeMap<>();

        for (Transacao t : transacoes) {
            entradasMes.putIfAbsent(t.data, 0.0);
            saidasMes.putIfAbsent(t.data, 0.0);
            if (t.tipo.equals("entrada")) {
                entradasMes.put(t.data, entradasMes.get(t.data) + t.valor);
            } else {
                saidasMes.put(t.data, saidasMes.get(t.data) + t.valor);
            }
        }

        System.out.println("\nComparativo Entradas x Saídas ao Longo do Tempo:");
        System.out.println("Mês     | Entradas   | Saídas     ");

        System.out.println("------------------------------");
        for (String mes : entradasMes.keySet()) {
            double e = entradasMes.getOrDefault(mes, 0.0);
            double s = saidasMes.getOrDefault(mes, 0.0);
            System.out.printf("%-7s | R$ %9.2f | R$ %9.2f\n", mes, e, s);
        }
    }

    public static void mostrarGrafico() {
        System.out.print("Digite o mês e ano para o gráfico (MM/yyyy): ");
        String data = scanner.nextLine();

        if (!validarData(data)) {
            System.out.println("Data inválida.");
            return;
        }

        double totalEntrada = 0;
        double totalSaida = 0;

        for (Transacao t : transacoes) {
            if (t.data.equals(data)) {
                if (t.tipo.equals("entrada")) totalEntrada += t.valor;
                else totalSaida += t.valor;
            }
        }

        double total = totalEntrada + totalSaida;
        if (total == 0) {
            System.out.println("Nenhuma transação nesse mês.");
            return;
        }

        int fatiaEntrada = (int) Math.round((totalEntrada / total) * 20);
        int fatiaSaida = (int) Math.round((totalSaida / total) * 20);

        System.out.println("\nGráfico de Pizza (Entrada vs Saída) para " + data + ":");
        System.out.print("Entrada: ");
        for (int i = 0; i < fatiaEntrada; i++) System.out.print("●");
        System.out.printf(" (R$ %.2f)\n", totalEntrada);

        System.out.print("Saída:   ");
        for (int i = 0; i < fatiaSaida; i++) System.out.print("●");
        System.out.printf(" (R$ %.2f)\n", totalSaida);
    }

    public static void verificarAlertaGasto() {
        Map<String, Double> saidasMes = new HashMap<>();

        for (Transacao t : transacoes) {
            if (t.tipo.equals("saida")) {
                saidasMes.put(t.data, saidasMes.getOrDefault(t.data, 0.0) + t.valor);
            }
        }

        for (Map.Entry<String, Double> entry : saidasMes.entrySet()) {
            if (entry.getValue() > 200) {
                System.out.println("⚠️ Alerta: gasto anormal no mês " + entry.getKey() +
                        " (R$ " + String.format("%.2f", entry.getValue()) + ")");
                System.out.println("Notificação enviada para gabrielneri2012@hotmail.com");
            }
        }
    }

    public static void mostrarBadge() {
        Map<String, Double> saidasMes = new HashMap<>();

        for (Transacao t : transacoes) {
            if (t.tipo.equals("saida")) {
                saidasMes.put(t.data, saidasMes.getOrDefault(t.data, 0.0) + t.valor);
            }
        }

        for (Map.Entry<String, Double> entry : saidasMes.entrySet()) {
            if (entry.getValue() < 100) {
                System.out.println("🏅 Badge conquistado para o mês " + entry.getKey() +
                        ": Parabéns! Boa saúde financeira com saída menor que R$ 100.");
            }
        }
    }
}
