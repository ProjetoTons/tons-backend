package br.com.tonspersonalizados.service.usuarios;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${cloudinary.url}") String cloudinaryUrl){
        cloudinary = new Cloudinary(cloudinaryUrl);
    }

    public void deletar(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            if (!"ok".equals(result.get("result"))) {
                throw new RuntimeException("Falha ao deletar arquivo: " + result.get("result"));
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao comunicar com Cloudinary", e);
        }
    }
}
