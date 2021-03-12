package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "品牌接口")
public interface BrandService {

    @GetMapping(value = "brand/list")
    @ApiOperation(value = "查询品牌列表")
    Result<PageInfo<BrandEntity>> getBrandInfo(@SpringQueryMap BrandDTO brandDTO);

    @PostMapping(value = "brand/save")
    @ApiOperation(value = "新增品牌")
    Result<JSONObject> postBrandInfo(@Validated({MingruiOperation.Add.class}) @RequestBody BrandDTO brandDTO);

    @PutMapping(value = "brand/save")
    @ApiOperation(value = "查询品牌列表")
    Result<JSONObject> editBrandInfo(@Validated({MingruiOperation.Update.class}) @RequestBody BrandDTO brandDTO);

    @DeleteMapping(value = "brand/delete")
    @ApiOperation(value = "查询品牌列表")
    Result<JSONObject> deleteBrandInfo(Integer id);

    @GetMapping(value = "brand/getBrandInfoByCategoryId")
    @ApiOperation(value = "通过分类id查询品牌")
    Result<List<BrandEntity>> getBrandInfoByCategoryId(Integer cid);

    @GetMapping(value = "brand/getBrandByIdList")
    @ApiOperation(value = "通过分类id查询品牌")
    Result<List<BrandEntity>> getBrandByIdList(@RequestParam String ids);
}
