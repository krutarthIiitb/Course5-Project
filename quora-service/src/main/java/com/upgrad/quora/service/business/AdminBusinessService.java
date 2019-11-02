package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(String uuid, String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = userDao.getUser(uuid);

        // If there is no entry in userEntity corresponding to the user in db
        if (userEntity == null)
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");

        UserAuthEntity userAuthEntity = userDao.getUserByAuthToken(accessToken);
        // if there is not authEntry then
        if (userAuthEntity == null) throw new AuthorizationFailedException("ATHR-001","User has not signed in.");

        //Check if the given user is admin, only then proceed else throw exception
        if (userEntity.getRole().equals("nonadmin"))
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");


        // if there is no entry for corresponding accessToken means user is not signin
        if (userAuthEntity == null) throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

        //  if there is an antry for logout in the userAuthEntity , this means user has logged out
        if (userAuthEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");

        //Now if it passes all above cases, its the admin and they can delete the user
        return userDao.deleteUser(userEntity);
    }
}
