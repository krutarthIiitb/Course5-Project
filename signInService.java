package service.business;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import   com.upgrad.quora.service.entity.UserEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import   com.upgrad.quora.service.dao.userDao;

@Service
public class signInService {

    @Autowired
    private userDao userdao;

    @Autowired
    private PasswordCryptographyProvider cryptographyprovider;



    //Authetication method
    @Transactional(propagation= Propagation.REQUIRED)
    public com.upgrad.proman.service.entity.UserAuthTokenEntity signIn(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userdao.getUserByEmail(email, password);

        if (userEntity == null)
        {
            throw new AuthenticationFailedException("ATH-001", "User with email not found");
        }
        //Encrypt password entered by user and compare with value in DB
        final String encryptedpassword = cryptographyprovider.encrypt(password,userEntity.getSalt());
        if(encryptedpassword.equals(userEntity.getPassword()))
        {
            UserAuthTokenEntity userAuthToken=new UserAuthTokenEntity();
            JwtTokenProvider jwtTokenProvider=new JwtTokenProvider(encryptedpassword);
            userAuthToken.setUser(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthToken.setAcessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),now,expiresAt));
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);
            userAuthToken.setCreatedBy("api-backend");
            userAuthToken.setCreatedAt(now);

            userdao.createAuthToken(userAuthToken);
            userdao.updateUser(userEntity);

            userEntity.setLastLoginAt(now);

            return userAuthToken;

        }
        else
        {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }


    }
}