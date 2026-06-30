package epiis.unamba.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import epiis.unamba.model.Usuario;

@Component
public class JwtProvider {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-minutes}")
    private long expirationMinutes;

    public String generateToken(Usuario usuario) {
        long expiration = Instant.now().plusSeconds(expirationMinutes * 60).getEpochSecond();
        String header = json(Map.of("alg", "HS256", "typ", "JWT"));
        String payload = json(Map.of(
            "sub", usuario.getUsername(),
            "role", "ROLE_" + usuario.getRol().name(),
            "userId", usuario.getId(),
            "exp", expiration
        ));
        String unsigned = encode(header) + "." + encode(payload);
        return unsigned + "." + sign(unsigned);
    }

    public boolean isValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            String unsigned = parts[0] + "." + parts[1];
            if (!sign(unsigned).equals(parts[2])) return false;
            return getExpiration(token) > Instant.now().getEpochSecond();
        } catch (Exception ex) {
            return false;
        }
    }

    public String getUsername(String token) {
        return extractString(payload(token), "sub");
    }

    private long getExpiration(String token) {
        return Long.parseLong(extractNumber(payload(token), "exp"));
    }

    private String payload(String token) {
        return new String(DECODER.decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
    }

    private String encode(String value) {
        return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo firmar el token JWT", ex);
        }
    }

    private String json(Map<String, Object> values) {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (!first) builder.append(',');
            builder.append('"').append(entry.getKey()).append('"').append(':');
            Object value = entry.getValue();
            if (value instanceof Number) builder.append(value);
            else builder.append('"').append(value.toString().replace("\\", "\\\\").replace("\"", "\\\"")).append('"');
            first = false;
        }
        return builder.append('}').toString();
    }

    private String extractString(String json, String key) {
        Matcher matcher = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"").matcher(json);
        if (!matcher.find()) throw new IllegalArgumentException("Token sin campo " + key);
        return matcher.group(1);
    }

    private String extractNumber(String json, String key) {
        Matcher matcher = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*(\\d+)").matcher(json);
        if (!matcher.find()) throw new IllegalArgumentException("Token sin campo " + key);
        return matcher.group(1);
    }
}
