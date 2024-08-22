package BookmyBook.bmb.security;

import BookmyBook.bmb.domain.UserRole;
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

    //JWT token 생성 메소드
    public String createToken(String user_id, UserRole role) {
        Claims claims = Jwts.claims().setSubject(user_id); //user_id를 subject로 선정
        claims.put("role", role.name()); //role을 claims에 포함

        Date now = new Date();
        // 1 hour
        long validityInMilliseconds = 3600000;
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

    //role 추출
    public String  getRole(String token){
        Claims claims;
        try{
            claims = extractClaims(token);
        } catch (Exception e){
            throw new ExceptionResponse(400, "token 추출 실패", "TOKEN_EXTRACTION_FAILED");
        }

        if(claims == null || claims.getSubject() == null){
            throw new ExceptionResponse(401, "유효하지 않은 token", "INVALID_TOKEN");
        }

        return (String) claims.get("role");
    }

    public boolean validateToken(String token, String user_id) {
        return (user_id.equals(getUserId(token)) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractClaims(token);
        return claims == null || claims.getExpiration().before(new Date());
    }
}
