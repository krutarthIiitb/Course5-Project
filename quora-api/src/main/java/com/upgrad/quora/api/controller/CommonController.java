package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {

        // Checking for the user associated to the access found in the header , returns exception if not found or the user if found.
        UserAuthEntity userAuthEntity = commonBusinessService.getUser(userId,authorization);
        UserEntity userEntity = userAuthEntity.getUser();

        if(userAuthEntity==null)throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        if(userEntity==null)throw new UserNotFoundException("USR-001","User with entered uuid does not exist");

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse.setUserName(userEntity.getUsername());
        userDetailsResponse.setFirstName(userEntity.getFirstName());
        userDetailsResponse.setLastName(userEntity.getLastName());
        userDetailsResponse.setAboutMe(userEntity.getAboutme());
        userDetailsResponse.setContactNumber(userEntity.getContactnumber());
        userDetailsResponse.setCountry(userEntity.getCountry());
        userDetailsResponse.setEmailAddress(userEntity.getEmail());
        userDetailsResponse.setDob(userEntity.getDob());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
