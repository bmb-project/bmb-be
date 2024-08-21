package BookmyBook.bmb.security;

import BookmyBook.bmb.response.ExceptionResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long validityInMilliseconds = 3600000; // 1 hour

    //JWT token 생성 메소드
    public String createToken(String user_id, String nickname) {
        Claims claims = Jwts.claims().setSubject(user_id); //user_id를 subject로 선정
        claims.put("nickname", nickname); //nickname을 claim에 추가

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)  //  Claims 객체에 포함된 클레임 설정
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //JWT에서 claim 추출
    public Claims extractClaims(String token){
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (JwtException | IllegalArgumentException e){
            throw new ExceptionResponse(401, "유효하지 않은 token", "INVALID_TOKEN");
        }
    }

    //user_id 추출
    public String getUserId(String token){
        Claims claims;
        try {
            claims = extractClaims(token);
        }catch (Exception e){
            throw new ExceptionResponse(400, "token 추출 실패", "TOKEN_EXTRACTION_FAILED");
        }

        if(claims == null || claims.getSubject() == null){
            throw new ExceptionResponse(401, "유효하지 않은 token", "INVALID_TOKEN");
        }

        return claims.getSubject(); //subject에서 user_id 추출
    }

    //nickname 추출
    public String getNickname(String token) {
        Claims claims;
        try {
            claims = extractClaims(token);
        }catch (Exception e){
            throw new ExceptionResponse(400, "token 추출 실패", "TOKEN_EXTRACTION_FAILED");
        }

        if(claims == null || claims.getSubject() == null){
            throw new ExceptionResponse(401, "유효하지 않은 token", "INVALID_TOKEN");
        }

        return claims.get("nickname", String.class);
    }

    public boolean validateToken(String token, String nickname) {
        return (nickname.equals(getNickname(token)) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractClaims(token);
        return (claims != null) ? claims.getExpiration().before(new Date()) : true;
    }
}
