package com.upgrad.quora.api.controller;


/*This controller has only transformation logic as we get the request model in Json format
 * This Json format is transformed to entity model, only entity model is understood by business service
 * From here the logic is delegated to SignupBusinessService, where it is abstracted as DAO and is persisted into DB
 */

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.business.UserAuthenticationService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

/*@Author : Deepthi Vemparala
 * This module is used to build signup , Signin and Signout Endpoint in UserController
 * Exceptions handled : SignUpRestrictedException
 * The Json format generated is consumed and the output generated is also in same format*/


@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private SignupBusinessService signupBusinessService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        /*We get all the user attributes from the signupUSerRequest Json, we set it to the userEntity object and send it to DAO in
        signupBusinessService to be persisted into the DB*/
        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setUsername(signupUserRequest.getUserName());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutme(signupUserRequest.getAboutMe());
        userEntity.setRole("nonadmin");
        userEntity.setSalt("1234abc");
        userEntity.setContactnumber(signupUserRequest.getContactNumber());

        //This method returns to us a persisted userentity object

        final UserEntity createdUser = signupBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUser.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signIn(@RequestHeader("authorization") String authorization) throws AuthorizationFailedException, AuthenticationFailedException, SignUpRestrictedException,ArrayIndexOutOfBoundsException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        //UserAuthToken is generated once the user is allowed to signin

        UserAuthEntity userAuthToken = userAuthenticationService.authenticate(decodedArray[0], decodedArray[1]);

        UserEntity user = userAuthToken.getUser();
        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(user.getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, httpHeaders, HttpStatus.OK);
    }

    //*We will be getting the user access token added to the when user signin , we will use it to help user logout *//*
    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> singOut(@RequestHeader("authorization") String authorization) throws SignOutRestrictedException {

        UserAuthEntity userAuthEntity = userAuthenticationService.signOut(authorization);

        //if (userAuthEntity == null) throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        UserEntity userEntity = userAuthEntity.getUser();

        //Building the signout Response for the logged in user
        SignoutResponse signoutResponse = new SignoutResponse();
        signoutResponse.setId(userEntity.getUuid());
        signoutResponse.setMessage("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}
