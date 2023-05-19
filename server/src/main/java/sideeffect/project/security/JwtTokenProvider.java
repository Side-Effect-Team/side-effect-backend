package sideeffect.project.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.service.RefreshTokenService;

import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expired}")
    private String expired;

    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenService refreshTokenService;

    public String getUserName(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("name", String.class);
    }
    public boolean validateAccessToken(String accessToken){
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
//                .getBody().getExpiration().before(new Date());
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
            return false;
        }catch (UnsupportedJwtException e){
            log.error("if the claimsJws argument does not represent an Claims JWS");
        }catch (MalformedJwtException e){
            log.error(" if the claimsJws string is not a valid JWS");
        }catch (SignatureException e){
            log.error("if the claimsJws JWS signature validation fails");
        }catch (ExpiredJwtException e){
            throw new AuthException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        }catch (IllegalStateException e){
            log.error("if the claimsJws string is null or empty or only whitespace");
        }
        return true;
    }

    public String createAccessToken(Authentication authentication){
        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();

        //access token
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(new Date(now + 1000 * 60 * 60 * 24 * 3))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return accessToken;
    }

    public Authentication getAuthentication(String token){
        String name = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(name);

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
