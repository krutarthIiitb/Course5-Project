package com.upgrad.quora.service.business;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import   com.upgrad.quora.service.entity.UserEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import   com.upgrad.quora.service.dao.UserDao;

import java.time.ZonedDateTime;

@Service
public class signInService {

    @Autowired
    private UserDao userdao;

    @Autowired
    private PasswordCryptographyProvider cryptographyprovider;



    //Authetication method
    @Transactional(propagation= Propagation.REQUIRED)
    public UserAuthEntity signIn(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userdao.getUserByEmail(username , password);

        if (userEntity == null)
        {
            throw new AuthenticationFailedException("ATH-001", "User with email not found");
        }
        //Encrypt password entered by user and compare with value in DB
        final String encryptedpassword = cryptographyprovider.encrypt(password,userEntity.getSalt());
        if(encryptedpassword.equals(userEntity.getPassword()))
        {
            UserAuthEntity userAuthToken=new UserAuthEntity();
            JwtTokenProvider jwtTokenProvider=new JwtTokenProvider(encryptedpassword);
            userAuthToken.setUser(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),now,expiresAt));
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);
            userAuthToken.setLoginAt(now);

            userdao.createAuthEntity(userAuthToken);
            userdao.updateUserAuth(userAuthToken);



            return userAuthToken;

        }
        else
        {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }


    }
}