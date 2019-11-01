package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    // implementing polymorphism here with different signatures but same method name
    public UserAuthEntity getUser(String uuid, String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userDao.getUserByAuthToken(accessToken);
        UserEntity userEntity = userDao.getUser(uuid);

        if(userAuthEntity==null)throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        if(userEntity==null)throw new UserNotFoundException("USR-001","User with entered uuid does not exist");

        if (userAuthEntity != null && userEntity != null) {
            // if we came here it means there is a user and there is an authToken, lets compare both
            if (userAuthEntity.getUuid().equals(uuid)) {
                // if there are both for same user , check if user loggedout
                if (userAuthEntity.getLogoutAt() != null) {
                    // There is entry for user log out
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details.");
                }
            }
        }
        return userAuthEntity;
    }
}
