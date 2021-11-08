package com.example.service2;

import org.springframework.stereotype.Service;

@Service
public class CommitService {
    private final CommitRepository commitRepository;

    public CommitService(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    public Iterable<Commit> getCommits() {
        return commitRepository.findAll();
    }

    public void save(Commit commit) {
        this.commitRepository.save(commit);
    }
}
