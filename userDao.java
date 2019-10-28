package com.upgrad.quora.service.dao;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.upgrad.quora.service.entity.UserEntity;

import javax.persistence.EntityManager;

@Repository
public class userDao{

    @Autowired
     private EntityManager entitymanager;


    //Function to fetch userdetails
    public UserEntity getUserByEmail(final String email, final String Password) {
        try {
            return entitymanager.CreateNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (Noresultexception nre) {
            return null;

        }
    }

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity){
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }


    public void updateUser(final UserEntity updatedUserEntity){
        entityManager.merge(updatedUserEntity);
    }
}