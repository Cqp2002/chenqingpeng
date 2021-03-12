package com.baidu.shop.feign;

import com.baidu.shop.service.CategoryService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "xxx-server",contextId = "CategoryService")
public interface CategoryFeign extends CategoryService {
}
