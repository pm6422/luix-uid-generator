package com.luixtech.uidgenerator.usage.demo.controller;

import com.luixtech.uidgenerator.core.uid.UidGenerator;
import com.luixtech.uidgenerator.usage.demo.domain.IdGeneratorWorkerNode;
import com.luixtech.uidgenerator.usage.demo.repository.IdGeneratorWorkerNodeRepository;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
public class HomeController {

    @Resource
    private Environment                     env;
    @Autowired(required = false)
    private SpringDocConfigProperties       springDocConfigProperties;
    @Resource
    private UidGenerator                    uidGenerator;
    @Resource
    private IdGeneratorWorkerNodeRepository idGeneratorWorkerNodeRepository;

    /**
     * Home page.
     */
    @GetMapping("/")
    public ResponseEntity<String> home(HttpServletResponse response) throws IOException {
        if (springDocConfigProperties != null && springDocConfigProperties.getApiDocs().isEnabled()) {
            response.sendRedirect("swagger-ui/index.html");
        }
        return ResponseEntity.ok(env.getProperty("spring.application.name") + " Home Page");
    }

    @GetMapping("/api/uid")
    public long generateUid() {
        long uid = uidGenerator.generateUid();
        String str = uidGenerator.parseUid(uid);
        log.info("Parsed uid: {}", str);
        return uid;
    }

    @GetMapping(value = "/api/query")
    public Page<IdGeneratorWorkerNode> query(@Parameter(in = ParameterIn.QUERY, name = "filter", description = "query criteria",
            schema = @Schema(type = "string", defaultValue = "(id:1 and appId:'luix-uid-generator-usage-demo') or (id > 1)"))
                                             @Filter Specification<IdGeneratorWorkerNode> spec,
                                             @ParameterObject Pageable pageable) {
        return idGeneratorWorkerNodeRepository.findAll(spec, pageable);
    }
}
