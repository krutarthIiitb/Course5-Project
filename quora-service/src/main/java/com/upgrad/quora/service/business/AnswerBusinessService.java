package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDAO;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDAO answerDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity) throws NullPointerException {
        answerEntity = answerDAO.createAnswer(answerEntity);
        if (answerEntity.getId() != null)
            return answerEntity;
        else
            return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswerByQuestion(String uuid) throws NullPointerException {
        List<AnswerEntity> answerEntityList = answerDAO.getAllAnswerByQuestion(uuid);
        return answerEntityList;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(AnswerEntity answerEntity) throws NullPointerException, AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity1 = answerDAO.getAnswerByUuid(answerEntity.getUuid());
        if (answerEntity1 != null) {
            if (answerEntity1.getUser() != null && answerEntity1.getUser().getId() == answerEntity.getUser().getId()) {
                answerEntity1 = answerDAO.updateQuestion(answerEntity);
            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
            }
        } else {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        return answerEntity1;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(AnswerEntity answerEntity) throws NullPointerException, AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity1 = answerDAO.getAnswerByUuid(answerEntity.getUuid());
        if (answerEntity1 != null) {
            if (answerEntity1.getUser() != null && (answerEntity1.getUser().getId() == answerEntity.getUser().getId()
                    || !answerEntity1.getUser().getRole().equals("nonadmin"))) {
                answerDAO.deleteAnswer(answerEntity);
            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            }
        } else {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        return answerEntity1;
    }

}
