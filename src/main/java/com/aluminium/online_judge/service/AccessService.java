package com.aluminium.online_judge.service;

import com.aluminium.online_judge.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccessService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    JwtUtils jwtUtils;

    public String validateRefreshToken(String token) {
        if (jwtUtils.isExpired(token)) {
            try {
                tokenService.logout(jwtUtils.extractJti(token));
            } catch (Exception ignored){};

            throw new IllegalArgumentException("Refresh token expired");
        }

        final UUID requestJti = jwtUtils.extractJti(token);
        final Token tokenFromDB = tokenService.getTokenByJti(requestJti);

        final UUID databaseJti = tokenFromDB.getJti();

        if (!databaseJti.equals(requestJti) || !tokenFromDB.getIsLoggedIn()) {
            throw new IllegalArgumentException("Refresh token holds an incorrect JTI");
        }

        return jwtUtils.generateToken(tokenFromDB.getUser());
    }

}
