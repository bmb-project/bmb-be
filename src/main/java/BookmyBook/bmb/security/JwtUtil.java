package BookmyBook.bmb.security;

import BookmyBook.bmb.domain.UserRole;
import BookmyBook.bmb.response.ExceptionResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String accessSecretKey;

    @Value("${jwt.refresh-secret}")
    private String refreshSecretKey;

    private static final long ACCESS_TOKEN_VALIDITY = 3600000; // 1 hour
    private static final long REFRESH_TOKEN_VALIDITY = 604800000; // 7 days

    //JWT token 생성 메소드
    public String createAccessToken(String user_id, UserRole role) {
        Claims claims = Jwts.claims().setSubject(user_id); //user_id를 subject로 선정
        claims.put("role", role.name()); //role을 claims에 포함
        Date now = new Date();
        Date validity = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

        return Jwts.builder()
                .setClaims(claims)  //  Claims 객체에 포함된 클레임 설정
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)
                .compact();
    }

    //JWT RefreshToken 생성 메소드
    public String createRefreshToken(String user_id){
        Claims claims = Jwts.claims().setSubject(user_id); //user_id를 subject로 선정
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY);

        return Jwts.builder()
                .setClaims(claims)  //  Claims 객체에 포함된 클레임 설정
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
                .compact();
    }

    //Cookie에서 token 추출
    public String getTokenFromCookies(Cookie[] cookies, String cookieName) {
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    //JWT에서 claim 추출
    public Claims extractClaims(String token, String secretKey){
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (JwtException | IllegalArgumentException e){
            System.err.println(e.getMessage());
            throw new ExceptionResponse(401, "유효하지 않은 access token", "INVALID_ACCESS_TOKEN");
        }
    }

    //user_id 추출
    public String getUserId(String token, String tokenType){
        String secretKey = getSecretKeyForTokenType(tokenType);
        Claims claims;
        try {
            claims = extractClaims(token, secretKey);
        }catch (Exception e){
            throw new ExceptionResponse(400, "token 추출 실패", "TOKEN_EXTRACTION_FAILED");
        }

        if(claims == null || claims.getSubject() == null){
            throw new ExceptionResponse(401, "유효하지 않은 access token", "INVALID_ACCESS_TOKEN");
        }

        return claims.getSubject(); //subject에서 user_id 추출
    }

    //role 추출
    public String  getRole(String token){
        Claims claims;
        try{
            claims = extractClaims(token, accessSecretKey);
        } catch (Exception e){
            throw new ExceptionResponse(400, "token 추출 실패", "TOKEN_EXTRACTION_FAILED");
        }

        if(claims == null || claims.getSubject() == null){
            throw new ExceptionResponse(401, "유효하지 않은 access token", "INVALID_ACCESS_TOKEN");
        }

        return (String) claims.get("role");
    }

    public boolean validateToken(String token, String user_id, String tokenType) {
        return (user_id.equals(getUserId(token, tokenType)) && !isTokenExpired(token, tokenType));
    }

    public boolean isTokenExpired(String token, String tokenType) {
        String secretKey = (tokenType.equals("access")) ? accessSecretKey : refreshSecretKey;
        Claims claims = extractClaims(token, secretKey);
        return claims == null || claims.getExpiration().before(new Date());
    }


    // Token 타입에 따른 Secret Key 선택
    private String getSecretKeyForTokenType(String tokenType) {
        return switch (tokenType) {
            case "access" -> accessSecretKey;
            case "refresh" -> refreshSecretKey;
            default -> throw new ExceptionResponse(404, "존재하지 않는 token type", "UNDEFINED_TOKEN_TYPE");
        };
    }
}