package com.luixtech.uidgenerator.usage.demo.controller;

import com.luixtech.uidgenerator.core.uid.UidGenerator;
import com.luixtech.uidgenerator.usage.demo.repository.IdGeneratorWorkerNodeRepository;
import lombok.extern.slf4j.Slf4j;
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
}
