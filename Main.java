import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Estoque estoque = new Estoque();
        int opcao;

        do {
            System.out.println("\nControle de Estoque da PetShop");
            System.out.println("1. Adicionar produto ao estoque");
            System.out.println("2. Remover produto do estoque");
            System.out.println("3. Consultar estoque");
            System.out.println("4. Buscar produto");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcao) {
                case 1:
                    System.out.print("Nome do produto: ");
                    String nomeAdicionar = scanner.nextLine();
                    System.out.print("Quantidade: ");
                    int quantidadeAdicionar = scanner.nextInt();
                    System.out.print("Preço: ");
                    double precoAdicionar = scanner.nextDouble();
                    scanner.nextLine(); 
                    Produtos produtoAdicionar = new Produtos(nomeAdicionar, quantidadeAdicionar, precoAdicionar);
                    estoque.adicionarProduto(produtoAdicionar);
                    System.out.println("Produto adicionado com sucesso.");
                    break;

                case 2:
                    System.out.print("Nome do produto a remover: ");
                    String nomeRemover = scanner.nextLine();
                    System.out.print("Quantidade: ");
                    int quantidadeRemover = scanner.nextInt();
                    scanner.nextLine();  
                    if (estoque.removerProduto(nomeRemover, quantidadeRemover)) {
                        System.out.println("Produto removido com sucesso.");
                    } else {
                        System.out.println("Erro ao remover o produto. Verifique o nome ou a quantidade.");
                    }
                    break;

                case 3:
                    estoque.consultarEstoque();
                    break;

                case 4:
                    System.out.print("Nome do produto para buscar: ");
                    String nomeBuscar = scanner.nextLine();
                    Produtos produtoBuscado = estoque.buscarProduto(nomeBuscar);
                    if (produtoBuscado != null) {
                        System.out.println(produtoBuscado);
                    } else {
                        System.out.println("Produto não encontrado.");
                    }
                    break;

                case 5:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 5);

        scanner.close();
}
}

