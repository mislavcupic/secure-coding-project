package hr.algebra.semregprojectbackend.configuration;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JwtKeyGenerator {
    private static final Logger logger = Logger.getLogger("JwtKeyGenerator");
    public static void main(String[] args) {
        // Generira siguran, nasumični ključ od 256 bita (32 bajta) za HS256 algoritam
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
        // Kodira ga u Base64 format
        String base64Key = Encoders.BASE64.encode(keyBytes);
        logger.log(Level.parse(base64Key),"Your new secure Base64 JWT Secret:");

    }
}
