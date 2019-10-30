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
/*Author : Deepthi Vemparala
When user is trying to login to the system, the user is authenticated by checking the username and password credentials
against the data that is present in the database and userAuthToken is generated
* */

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

        String encryptedPassword = passwordCryptographyProvider.encrypt(password, user.getSalt());
        if (encryptedPassword.equals(user.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);

            //When user is trying to sigin the UserAuthEntity is created
            UserAuthEntity userAuthEntity = new UserAuthEntity();
            userAuthEntity.setUser(user);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthEntity.setLoginAt(now);
            userAuthEntity.setExpiresAt(expiresAt);
            userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), now, expiresAt));
            userAuthEntity.setUuid(user.getUuid());
            userDao.updateUser(user,userAuthEntity);
            return userAuthEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }

    public UserAuthEntity signOut(final String accessToken)throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userDao.getUserByAuthToken(accessToken);
        if(userAuthEntity==null)throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        userAuthEntity.setLogoutAt(ZonedDateTime.now());
        UserEntity user = userAuthEntity.getUser();
        userDao.updateUser(user,userAuthEntity);
        return userAuthEntity;
    }
}
