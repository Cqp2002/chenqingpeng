package com.baidu.shop.feign;

import com.baidu.shop.service.SpecificationService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "xxx-server",contextId = "SpecificationService")
public interface SpecificationFeign extends SpecificationService {
}
