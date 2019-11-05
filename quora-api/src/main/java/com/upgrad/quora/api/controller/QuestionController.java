package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;
    @Autowired
    private CommonBusinessService commonBusinessService;
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQustion(QuestionRequest questionRequest,@RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {

        if(authorization == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Checking for the user associated to the access found in the header , returns exception if not found.
        System.out.println(authorization);
        UserAuthEntity userAuthEntity = commonBusinessService.getUser(authorization);
        if(userAuthEntity==null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else{
            QuestionEntity questionEntity = new QuestionEntity();
            questionEntity.setContent(questionRequest.getContent());
            questionEntity.setDate(LocalDateTime.now());
            questionEntity.setUser(userAuthEntity.getUser());
            questionEntity.setUuid(UUID.randomUUID().toString());
            questionEntity = questionBusinessService.createQuestion(questionEntity);
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setId(questionEntity.getUuid());
            questionResponse.setStatus("QUESTION CREATED");
            return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
        }




    }

}
