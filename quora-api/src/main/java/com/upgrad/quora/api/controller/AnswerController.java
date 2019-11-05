package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private QuestionBusinessService questionBusinessService;
    @Autowired
    private AnswerBusinessService answerBusinessService;
    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(AnswerRequest answerRequest, @PathVariable("questionId") String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {
        QuestionEntity questionEntity1 = questionBusinessService.getQuestionByUuid(questionId);
        if (questionEntity1 == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (authorization == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Checking for the user associated to the access found in the header , returns exception if not found.
        UserAuthEntity userAuthEntity = commonBusinessService.getUser(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if ((userAuthEntity.getLogoutAt() != null)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details.");
            } else {
                AnswerEntity answerEntity = new AnswerEntity();
                answerEntity.setContent(answerRequest.getAnswer());
                answerEntity.setDate(LocalDateTime.now());
                answerEntity.setUser(userAuthEntity.getUser());
                answerEntity.setQuestion(questionEntity1);
                answerEntity.setUuid(UUID.randomUUID().toString());
                answerEntity = answerBusinessService.createAnswer(answerEntity);
                AnswerResponse questionResponse = new AnswerResponse();
                questionResponse.setId(answerEntity.getUuid());
                questionResponse.setStatus("ANSWER CREATED");
                return new ResponseEntity<AnswerResponse>(questionResponse, HttpStatus.OK);
            }
        }


    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> updateQuestion(AnswerEditRequest answerEditRequest, @PathVariable("answerId") String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException, AnswerNotFoundException {
        AnswerEditResponse answerResponse = new AnswerEditResponse();
        if (authorization == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Checking for the user associated to the access found in the header , returns exception if not found.
        UserAuthEntity userAuthEntity = commonBusinessService.getUser(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            /*
                Need to add this condition as we have needed to check wehther user's token is expired or not
                (userAuthEntity.getExpiresAt() != null && LocalDateTime.now().isAfter(userAuthEntity.getExpiresAt())) ||
             */
            if ((userAuthEntity.getLogoutAt() != null)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details.");
            } else {
                AnswerEntity answerEntity = new AnswerEntity();
                answerEntity.setUuid(questionId);
                answerEntity.setContent(answerEditRequest.getContent());
                answerEntity.setUser(userAuthEntity.getUser());
                answerEntity = answerBusinessService.updateAnswer(answerEntity);
                if (answerEntity != null) {
                    answerResponse.setId(answerEntity.getUuid());
                    answerResponse.setStatus("ANSWER EDITED");
                }
            }
            return new ResponseEntity<AnswerEditResponse>(answerResponse, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") String answerId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, AnswerNotFoundException {
        AnswerDeleteResponse answerResponse = new AnswerDeleteResponse();
        if (authorization == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Checking for the user associated to the access found in the header , returns exception if not found.
        UserAuthEntity userAuthEntity = commonBusinessService.getUser(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            /*
                Need to add this condition as we have needed to check wehther user's token is expired or not
                (userAuthEntity.getExpiresAt() != null && LocalDateTime.now().isAfter(userAuthEntity.getExpiresAt())) ||
             */
            if ((userAuthEntity.getLogoutAt() != null)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details.");
            } else {
                AnswerEntity answerEntity = new AnswerEntity();
                answerEntity.setUuid(answerId);
                answerEntity.setUser(userAuthEntity.getUser());
                answerEntity = answerBusinessService.deleteAnswer(answerEntity);
                if (answerEntity != null) {
                    answerResponse.setId(answerEntity.getUuid());
                    answerResponse.setStatus("ANSWER DELETED");
                }
            }
            return new ResponseEntity<AnswerDeleteResponse>(answerResponse, HttpStatus.OK);
        }

    }

    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> getAllAnswer(@PathVariable("questionId") String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {
        List<AnswerDetailsResponse> responseList = new ArrayList<AnswerDetailsResponse>();

        if (authorization == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Checking for the user associated to the access found in the header , returns exception if not found.
        UserAuthEntity userAuthEntity = commonBusinessService.getUser(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            /*
                Need to add this condition as we have needed to check wehther user's token is expired or not
                (userAuthEntity.getExpiresAt() != null && LocalDateTime.now().isAfter(userAuthEntity.getExpiresAt())) ||
             */
            if ((userAuthEntity.getLogoutAt() != null)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details.");
            } else {
                QuestionEntity questionEntity = questionBusinessService.getQuestionByUuid(questionId);
                if (questionEntity == null) {
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
                } else {
                    List<AnswerEntity> list = answerBusinessService.getAllAnswerByQuestion(questionEntity.getUuid());
                    for (AnswerEntity q : list) {
                        AnswerDetailsResponse response = new AnswerDetailsResponse();
                        response.setId(q.getUuid());
                        response.setAnswerContent(q.getContent());
                        response.setQuestionContent(q.getQuestion().getContent());
                        responseList.add(response);
                    }
                }
            }
        }
        return new ResponseEntity(responseList, HttpStatus.OK);

    }
}
