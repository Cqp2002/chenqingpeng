package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryBrandMapper categoryBrandMapper;




    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

        //分页
        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        //排序
        if(!StringUtils.isEmpty(brandDTO.getSort())) PageHelper.orderBy(brandDTO.getOrderBy());
        //条件查询
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        Example example = new Example(BrandEntity.class);
        example.createCriteria().andLike("name","%" + brandEntity.getName() + "%");
        //查询
        List<BrandEntity> brandEntities = brandMapper.selectByExample(example);
        PageInfo<BrandEntity> objectPageInfo = new PageInfo<>(brandEntities);


        return setResultSuccess(objectPageInfo);
    }

    @Override
    @Transactional
    public Result<JSONObject> postBrandInfo(BrandDTO brandDTO) {
        //获取新增的数据
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        //截取名称的首字母
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]), false).toCharArray()[0]);
        brandMapper.insertSelective(brandEntity);
        String categories = brandDTO.getCategories();//得到分类集合字符串
        if(StringUtils.isEmpty(brandDTO.getCategories())) {//数据不为空
            return this.setResultError("");
        }
        List<CategoryBrandEntity> categoryBrandEntities = new ArrayList<>();//定义list集合
        if(categories.contains(",")){//多个分类 --> 批量新增
            String[] categoryArr = categories.split(",");//根据逗号分割
            for (String s : categoryArr) {//遍历
                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();//实体类
                categoryBrandEntity.setBrandId(brandEntity.getId());//获得品牌id
                categoryBrandEntity.setCategoryId(Integer.valueOf(s));//获得分类的数组
                categoryBrandEntities.add(categoryBrandEntity);//实体类给集合赋值
            }
            categoryBrandMapper.insertList(categoryBrandEntities);
        }else{//普通单个新增
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(brandEntity.getId());
            categoryBrandEntity.setCategoryId(Integer.valueOf(categories));
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> editBrandInfo(BrandDTO brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]), false).toCharArray()[0]);
        brandMapper.updateByPrimaryKeySelective(brandEntity);
        //先通过brandId删除中间表的数据
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",brandEntity.getId());
        categoryBrandMapper.deleteByExample(example);
        //批量新增 / 新增
        String categories = brandDTO.getCategories();//得到分类集合字符串
        if(StringUtils.isEmpty(brandDTO.getCategories())) return this.setResultError("");
        //判断分类集合字符串中是否包含,
        if(categories.contains(",")){//多个分类 --> 批量新增
            String[] categoryArr = categories.split(",");
            categoryBrandMapper.insertList(Arrays.asList(categoryArr).stream().map(categoryIdStr -> {
                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                categoryBrandEntity.setCategoryId(Integer.valueOf(categoryIdStr));
                categoryBrandEntity.setBrandId(brandEntity.getId());
                return categoryBrandEntity;
            }).collect(Collectors.toList()));
        }else{//普通单个新增

            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(brandEntity.getId());
            categoryBrandEntity.setCategoryId(Integer.valueOf(categories));

            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> deleteBrandInfo(Integer id) {
        //删除品牌
        brandMapper.deleteByPrimaryKey(id);
        //删除品牌关联的分类
        this.deleteCategoryBrandByBrandId(id);

        return this.setResultSuccess();
    }

    private void deleteCategoryBrandByBrandId(Integer brandId){

        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",brandId);
        categoryBrandMapper.deleteByExample(example);

    }


}
