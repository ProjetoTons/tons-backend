package br.com.tonspersonalizados.service.usuarios;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CloudinaryService")
class CloudinaryServiceTest {

    private final CloudinaryService cloudinaryService = new CloudinaryService("cloudinary://key:secret@cloud");

    @Test
    @DisplayName("Deve extrair publicId de uma URL válida")
    void deveExtrairPublicId() {
        String url = "https://res.cloudinary.com/demo/image/upload/v1234567890/folder/imagem.jpg";
        String resultado = cloudinaryService.extrairPublicId(url);
        assertEquals("folder/imagem", resultado);
    }

    @Test
    @DisplayName("Deve retornar null para URL null")
    void deveRetornarNullParaUrlNull() {
        assertNull(cloudinaryService.extrairPublicId(null));
    }

    @Test
    @DisplayName("Deve retornar null para URL em branco")
    void deveRetornarNullParaUrlEmBranco() {
        assertNull(cloudinaryService.extrairPublicId("   "));
    }

    @Test
    @DisplayName("Deve retornar null para URL sem /upload/")
    void deveRetornarNullParaUrlSemUpload() {
        assertNull(cloudinaryService.extrairPublicId("https://example.com/image.jpg"));
    }

    @Test
    @DisplayName("Deve extrair publicId sem versão")
    void deveExtrairSemVersao() {
        String url = "https://res.cloudinary.com/demo/image/upload/folder/imagem.png";
        String resultado = cloudinaryService.extrairPublicId(url);
        assertEquals("folder/imagem", resultado);
    }
}
