package br.com.tonspersonalizados.usuarios_ms.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "login")
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private  String email;

    private String senhaHash;
    private LocalDateTime ultimoLogin;
    private LocalDateTime logout;
    private String tokenRecuperarSenha;
    private Date expiracaoToken;
    private Integer tentativasLogin;

    //relacionamento:
    @OneToOne
    private Usuario usuario;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public Integer getTentativasLogin() {
        return tentativasLogin;
    }

    public void setTentativasLogin(Integer tentativasLogin) {
        this.tentativasLogin = tentativasLogin;
    }

    public LocalDateTime getLogout() {
        return logout;
    }

    public void setLogout(LocalDateTime logout) {
        this.logout = logout;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getExpiracaoToken() {
        return expiracaoToken;
    }

    public void setExpiracaoToken(Date expiracaoToken) {
        this.expiracaoToken = expiracaoToken;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getTokenRecuperarSenha() {
        return tokenRecuperarSenha;
    }

    public void setTokenRecuperarSenha(String tokenRecuperarSenha) {
        this.tokenRecuperarSenha = tokenRecuperarSenha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
