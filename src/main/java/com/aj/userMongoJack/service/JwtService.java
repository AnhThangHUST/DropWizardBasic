package com.aj.userMongoJack.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

// trong cai io.jsonwebtoken nay da co cai signature algorithm
import io.jsonwebtoken.*;


import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;

public class JwtService {

    private static String SECRET_KEY = "abcvdsgfkjalgjaf@alwq1";

    // ham nay tra ve string JWT sau khi duoc ma hoa
    public static String createJWT(String id, String issuer, String subject, long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    // ham nay se decode JWT ra Claim - Chua biet Claim nay se de lam j
//    public static Claims decodeJWT(String jwt) {
//
//        //This line will throw an exception if it is not a signed JWS (as expected)
//        Claims claims = Jwts.parser()
//                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
//                .parseClaimsJws(jwt).getBody();
//        return claims;
//    }
}
