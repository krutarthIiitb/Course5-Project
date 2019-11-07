package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity deleteUser(UserEntity userEntity){
        UserEntity deleteduserEntity = userEntity;
        entityManager.remove(userEntity);
        //This should delete on cascade the entry of the corresponding userAuthEntity too
        return deleteduserEntity;
    }

    public UserEntity getUserbyUsername(final String username) {
        try {
            return entityManager.createNamedQuery("userByName", UserEntity.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public UserEntity getUserByEmail(final String email, final String Password) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;

        }
    }
    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUser(String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity getUserByAuthToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("userByAuthToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity createAuthEntity(UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public UserAuthEntity getUserAuthEntity(String uuid){
        try{
            return entityManager.createNamedQuery("userAuthTokenByuuid", UserAuthEntity.class).setParameter("uuid",uuid).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    // deletes the authentity entry
    public void deleteUserAuthEntity(UserAuthEntity userAuthEntity){
        entityManager.remove(userAuthEntity);
    }
    // updates the authentity
    public void updateUserAuth(UserAuthEntity userAuthEntity){
        entityManager.merge(userAuthEntity);
    }
}
