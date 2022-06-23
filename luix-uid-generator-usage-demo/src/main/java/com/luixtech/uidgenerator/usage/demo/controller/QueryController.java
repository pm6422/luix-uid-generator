package com.luixtech.uidgenerator.usage.demo.controller;

import com.luixtech.uidgenerator.usage.demo.domain.IdGeneratorWorkerNode;
import com.luixtech.uidgenerator.usage.demo.repository.IdGeneratorWorkerNodeRepository;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Spring filter
 */
@RestController
public class QueryController {

    @Resource
    private IdGeneratorWorkerNodeRepository idGeneratorWorkerNodeRepository;

    @GetMapping(value = "/api/query")
    public Page<IdGeneratorWorkerNode> query(@Parameter(in = ParameterIn.QUERY, name = "filter", description = "query criteria",
            schema = @Schema(type = "string", defaultValue = "(id:1 and appId:'luix-uid-generator-usage-demo') or (id > 1)"))
                                             @Filter Specification<IdGeneratorWorkerNode> spec,
                                             @ParameterObject Pageable pageable) {
        return idGeneratorWorkerNodeRepository.findAll(spec, pageable);
    }
}
