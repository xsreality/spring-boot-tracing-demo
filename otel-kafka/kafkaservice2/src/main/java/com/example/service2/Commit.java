package com.example.service2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Commit {
    @Id
    @GeneratedValue
    private Long id;

    private String commitMessage;

    public Commit() {
    }

    public Commit(Long id, String commitMessage) {
        this.id = id;
        this.commitMessage = commitMessage;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String task) {
        this.commitMessage = task;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id=" + id +
                ", commitMessage='" + commitMessage + '\'' +
                '}';
    }
}
