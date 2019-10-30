package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserAuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {

        UserEntity user = userDao.getUserbyUsername(username);
        if (user == null) throw new AuthenticationFailedException("ATH-001", "This username does not exist");

        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, user.getSalt());
        if (encryptedPassword.equals(user.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthEntity = new UserAuthEntity();
            userAuthEntity.setUser(user);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userDao.createAuthToken(userAuthEntity);
            userDao.updateUser(user);
            return userAuthEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }

    public UserAuthEntity signOut(final String accessToken) throws SignOutRestrictedException {
            UserAuthEntity userAuthEntity = userDao.getUserByAuthToken(accessToken);
            if(userAuthEntity==null)throw  new SignOutRestrictedException("SGR-001","User is not Signed in");
            userAuthEntity.setLogoutAt(ZonedDateTime.now());
            userDao.updateUser(userAuthEntity.getUser());
            return userAuthEntity;
    }
}
