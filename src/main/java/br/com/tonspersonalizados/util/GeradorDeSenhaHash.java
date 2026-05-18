package br.com.tonspersonalizados.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário para gerar senhas com hash BCrypt.
 * Esta classe simula exatamente o que o PasswordEncoder faz na tua aplicação.
 * É útil para criar senhas para utilizadores inseridos manualmente via script SQL.
 */
public class GeradorDeSenhaHash {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String senhaTextoPuro = "Davi123456789@";
        String senhaHasheada = encoder.encode(senhaTextoPuro);

        System.out.println("==================================================");
        System.out.println("🔐 GERADOR DE HASH PARA BANCO DE DADOS");
        System.out.println("==================================================");
        System.out.println("Senha Original : " + senhaTextoPuro);
        System.out.println("Senha com Hash : " + senhaHasheada);
        System.out.println("==================================================");
        System.out.println("Instrução: Copie apenas o texto do hash acima (começa com $2a$10$)");
        System.out.println("e cole no comando INSERT ou UPDATE do seu banco de dados.");
    }
}