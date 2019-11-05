package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
/* CREATE TABLE public.question
(
    id integer NOT NULL DEFAULT nextval('question_id_seq'::regclass),
    uuid character varying(200) COLLATE pg_catalog."default" NOT NULL,
    content character varying(500) COLLATE pg_catalog."default" NOT NULL,
    date timestamp without time zone NOT NULL,
    user_id integer NOT NULL,
    CONSTRAINT question_pkey PRIMARY KEY (id),
    CONSTRAINT question_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID
)
WITH (
    OIDS = FALSE
)*/

@Entity
@Table(name = "ANSWER")
@NamedQueries(
        {
                @NamedQuery(name = "answerByUuid", query = "select a from AnswerEntity a where a.uuid =:uuid"),
                @NamedQuery(name = "getAllAnswerByQuestion", query = "select q from AnswerEntity q where q.question.uuid=:uuid")
        }
)
public class AnswerEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "UUID")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "ans")
    @NotNull
    @Size(max = 500)
    private String content;


    @Column(name = "DATE")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private QuestionEntity question;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
