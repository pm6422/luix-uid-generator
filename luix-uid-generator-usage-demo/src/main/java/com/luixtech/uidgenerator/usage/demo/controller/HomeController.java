package com.luixtech.uidgenerator.usage.demo.controller;

import com.luixtech.uidgenerator.core.uid.UidGenerator;
import com.luixtech.uidgenerator.usage.demo.domain.IdGeneratorWorkerNode;
import com.luixtech.uidgenerator.usage.demo.domain.QIdGeneratorWorkerNode;
import com.luixtech.uidgenerator.usage.demo.repository.IdGeneratorWorkerNodeRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.luixtech.uidgenerator.usage.demo.domain.QIdGeneratorWorkerNode.idGeneratorWorkerNode;

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
     * SpringBoot+Querydsl 框架，大大简化复杂查询操作
     * https://www.toutiao.com/article/7097084424718369317/?log_from=52e11f521e4a8_1655891095452
     *
     *
     * /api/query?id=1&appId=luix-uid-generator-usage-demo
     *
     * @param predicate
     * @return
     */
    @GetMapping("/api/query")
    public ResponseEntity<List<IdGeneratorWorkerNode>> find(@QuerydslPredicate(root = IdGeneratorWorkerNode.class) Predicate predicate) {
        Predicate pred = idGeneratorWorkerNode.id.eq(1L)
                .and(idGeneratorWorkerNode.appId.startsWith("luix-uid-generator"));
        Iterable<IdGeneratorWorkerNode> all = idGeneratorWorkerNodeRepository.findAll(pred);
        System.out.println(all);

        Iterable<IdGeneratorWorkerNode> iterable = idGeneratorWorkerNodeRepository.findAll(predicate);
        List<IdGeneratorWorkerNode> nodes = StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(nodes);
    }
}
