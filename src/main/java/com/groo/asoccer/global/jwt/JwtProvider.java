package com.groo.asoccer.global.jwt;

import com.groo.asoccer.domain.member.member.entity.Member;
import com.groo.asoccer.global.util.Ut;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    @Value("${custom.jwt.secretKey}")
    private String secretKeyOrigin;

    //@Value("${custom.token.expirationSeconds}") 시크릿에서 가져오는 게 문자열 오류가나서 일단 보류
    @Value("#{60 * 10}")
    private int tokenExpirationSeconds;

    //시크릿키를 한번 만들면 저장시키는 메소드
    private SecretKey cachedSecretKey;

    public SecretKey getCachedSecretKey(){
        if(cachedSecretKey == null){
            cachedSecretKey = cachedSecretKey = _getSecretKey(); //_내부적으로 사용제한을 둔 변수에게 붙여줌
        }
        return cachedSecretKey;
    }

    //시크릿 키 가공 정형화되어있는 패턴
    private SecretKey _getSecretKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyOrigin.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    /* 실무에서 자주 쓰는패턴으로 보안이 더 좋다고 나옴 리팩토리 예정
    SecretKey secretKey = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode(secretKeyBase64)
    );
    */

    //입장 토큰
    public String generateAccessToken(Member member){
        return generateToken(member,tokenExpirationSeconds);
    }

    //리프레시 토큰
    public String generateRefreshToken(Member member){
        return generateToken(member,60 * 60 * 24 * 365 * 1);
    }

    //토큰 만들기
    public String generateToken(Member member, int seconds){
        Map<String,Object> claims = new HashMap<>();

        //claims 만들기
        claims.put("id",member.getMemberUserId());
        claims.put("username",member.getMemberName());
        long now = new Date().getTime();

        //유효기간 변수 만들기
        Date accessTokenExpirationIn = new Date(now + 1000L * seconds);

        return Jwts.builder()
                //바디에 유틸에 있는 직렬화하여 집어넣기
                .claim("body", Ut.json.toStr(claims))
                .setExpiration(accessTokenExpirationIn)
                //서명 부분을 만드는 인터페이스
                .signWith(getCachedSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

}
