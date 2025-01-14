package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signUp(UserEntity userEntity) throws SignUpRestrictedException ,NullPointerException{
        //We are going to check if the user is already present int he database using the username
        // Do not accept , throw exception if user with same username exisit
            if (userDao.getUserbyUsername(userEntity.getUsername()) != null) {
                throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
            } else if (userDao.getUserByEmail(userEntity.getEmail()) != null) {
                throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
            } else {
                String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
                userEntity.setSalt(encryptedText[0]);
                userEntity.setPassword(encryptedText[1]);
                userDao.createUser(userEntity);
            }
        return userEntity;
    }
}
