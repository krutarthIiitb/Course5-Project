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

        // fetch the user with the uuid
        UserEntity userToBeDeleted = userDao.getUser(uuid);

        // If there is no entry in userEntity corresponding to the user in db
        if (userToBeDeleted == null)
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");

        // Fetching user using the uuid , this user needs to be deleted
        UserAuthEntity userToBeDeletedAuthEntity = userDao.getUserAuthEntity(uuid);

        // if there is no authEntry then
        if (userToBeDeletedAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");


        //  if there is an entry for logout in the userAuthEntity , this means admin has logged out
        if (userToBeDeletedAuthEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");

        //Fetch details of admin user
        UserAuthEntity adminUserAuthEntity = userDao.getUserByAuthToken(accessToken);
        if(adminUserAuthEntity==null)throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");
        //fetch the admin user
        UserEntity adminUser = adminUserAuthEntity.getUser();
        if(adminUser==null)throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");

        //Check if the given user is admin, only then proceed else throw exception
        if (adminUser.getRole().equals("nonadmin"))
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");

        //Now if it passes all above cases, its the admin and they can delete the user
        UserEntity deletedUser = userDao.deleteUser(userToBeDeletedAuthEntity.getUser());
        return deletedUser;
    }
}
