package com.luixtech.uidgenerator.usage.demo.controller;

import com.luixtech.uidgenerator.core.uid.UidGenerator;
import com.luixtech.uidgenerator.usage.demo.domain.IdGeneratorWorkerNode;
import com.luixtech.uidgenerator.usage.demo.repository.IdGeneratorWorkerNodeRepository;
import com.turkraft.springfilter.boot.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class HomeController {

    @Resource
    private UidGenerator                    uidGenerator;
    @Resource
    private IdGeneratorWorkerNodeRepository idGeneratorWorkerNodeRepository;

    /**
     * Home page.
     */
    @GetMapping("/")
    public long home() {
        long uid = uidGenerator.generateUid();
        String str = uidGenerator.parseUid(uid);
        log.info("Parsed uid: {}", str);
        return uid;
    }

    /**
     * https://github.com/turkraft/spring-filter
     * <p>
     * /api/search?filter=id:1 and appId:'luix-uid-generator-usage-demo'
     */
    @GetMapping(value = "/api/search")
    public Page<IdGeneratorWorkerNode> search(@Filter Specification<IdGeneratorWorkerNode> spec, Pageable page) {
        return idGeneratorWorkerNodeRepository.findAll(spec, page);
    }
}
