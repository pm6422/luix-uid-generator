package com.luixtech.uidgenerator.usage.demo.repository;

import com.luixtech.uidgenerator.usage.demo.domain.IdGeneratorWorkerNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdGeneratorWorkerNodeRepository extends JpaRepository<IdGeneratorWorkerNode, Long> {
}
