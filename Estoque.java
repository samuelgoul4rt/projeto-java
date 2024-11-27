import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Estoque {
    private Connection conn;
    private List<Produtos> produtos;

    public Estoque() {
        try {
            // Cria a conexão com o banco de dados
            conn = DriverManager.getConnection("jdbc:sqlite:estoque.db");
            conn.setAutoCommit(false); // Desativa o auto-commit para melhor desempenho
            String sql = "CREATE TABLE IF NOT EXISTS produtos (nome TEXT, quantidade INTEGER, preco DOUBLE)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            try {
                conn.rollback(); // Desfaz alterações em caso de erro
            } catch (SQLException rollbackEx) {
                System.err.println("Erro ao reverter alterações: " + rollbackEx.getMessage());
            }
        }
    }

    public void adicionarProduto(Produtos produto) {
        String nome = produto.getNome();
        Integer qtd = produto.getQuantidade();
        Double preco = produto.getPreco();
        Integer saldo = 0;
        PreparedStatement pstmt ;

        String sql = "SELECT quantidade FROM produtos WHERE nome=?";

        try {
//            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                saldo = qtd + rs.getInt("quantidade");
                sql = "UPDATE produtos SET quantidade=?, preco=? WHERE nome=?";
                try (PreparedStatement updatePstmt = conn.prepareStatement(sql)) {
                    updatePstmt.setInt(1, saldo);
                    updatePstmt.setDouble(2, preco);
                    updatePstmt.setString(3, nome);
                    updatePstmt.executeUpdate();
                }
            } else {
                sql = "INSERT INTO produtos(nome, quantidade, preco) VALUES(?, ?, ?)";
                try (PreparedStatement insertPstmt = conn.prepareStatement(sql)) {
                    insertPstmt.setString(1, nome);
                    insertPstmt.setInt(2, qtd);
                    insertPstmt.setDouble(3, preco);
                    insertPstmt.executeUpdate();
                }
            }
            conn.commit(); // Comita as alterações
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar produto: " + e.getMessage());
            try {
                conn.rollback(); // Desfaz alterações em caso de erro
            } catch (SQLException rollbackEx) {
                System.err.println("Erro ao reverter alterações: " + rollbackEx.getMessage());
            }
        }
    }
    
    public boolean removerProduto(String nome, Integer qtd) {
        Integer saldo = 0;
        PreparedStatement pstmt ;
        String sql = "SELECT quantidade FROM produtos WHERE nome=?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                saldo = rs.getInt("quantidade") - qtd;
                sql = "UPDATE produtos SET quantidade=? WHERE nome=?";
                try (PreparedStatement updatePstmt = conn.prepareStatement(sql)) {
                    updatePstmt.setInt(1, saldo);
                    updatePstmt.setString(2, nome);
                    updatePstmt.executeUpdate();
                }
            }
            conn.commit(); // Comita as alterações
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao remover produto: " + e.getMessage());
            try {
                conn.rollback(); // Desfaz alterações em caso de erro
            } catch (SQLException rollbackEx) {
                System.err.println("Erro ao reverter alterações: " + rollbackEx.getMessage());
            }
            return false;
        }
    }

    public void consultarEstoque() {
        String sql = "SELECT nome, quantidade, preco FROM produtos ";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            int itens = 0;
            while (rs.next()) {
                String nome = rs.getString("nome");
                int quantidade = rs.getInt("quantidade");
                double preco = rs.getDouble("preco");
                Produtos produtos = new Produtos(nome, quantidade, preco); // Presumindo que Produto tem um construtor
                System.out.println(produtos);
                itens++;
            }
            
            if (itens == 0) {
                System.out.println("Nenhum produto encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar produto: " + e.getMessage());
        }
    }

    public Produtos buscarProduto(String nome) {
        String sql = "SELECT quantidade, preco FROM produtos WHERE nome=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
    
            if (rs.next()) {
                int quantidade = rs.getInt("quantidade");
                double preco = rs.getDouble("preco");
                return new Produtos(nome, quantidade, preco);
            } else {
                System.out.println("Produto não encontrado.");
                return null; // Produto não encontrado
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar produto: " + e.getMessage());
            return null; // Retorna null em caso de erro
        }
    }

    public void fecharConexao() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão: " + e.getMessage());
        }
    }
}