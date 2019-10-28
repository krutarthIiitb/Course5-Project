package com.upgrad.quora.api.controller;


/*This controller has only transformation logic as we get the request model in Json format
 * This Json format is transformed to entity model, only entity model is understood by business service
 * From here the logic is delegated to SignupBusinessService, where it is abstracted as DAO and is persisted into DB
 */

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private SignupBusinessService signupBusinessService;


    @RequestMapping(method= RequestMethod.POST, path="/user/signup", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest)
    {
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

        SignupUserResponse userResponse = new SignupUserResponse().id(createdUser.getUuid()).status("Registered");

        return new ResponseEntity<SignupUserResponse>(userResponse,HttpStatus.CREATED);
    }
}
