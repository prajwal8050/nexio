package com.example.nexio.repository;

import com.example.nexio.model.WaitContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WaitContentRepository extends JpaRepository<WaitContent, Long> {
    List<WaitContent> findByMinWaitTimeLessThanEqual(int waitTime);
}
