package com.baidu.shop.feign;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "xxx-server",contextId = "GoodsService")
public interface GoodsFeign extends GoodsService {
}