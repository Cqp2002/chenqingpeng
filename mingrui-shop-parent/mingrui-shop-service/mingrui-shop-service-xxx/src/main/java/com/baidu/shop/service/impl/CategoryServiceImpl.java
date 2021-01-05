package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.utils.ObjectUtil;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;


    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);
        List<CategoryEntity> list = categoryMapper.select(categoryEntity);
        return this.setResultSuccess(list);
    }

    @Override
    @Transactional
    public Result<JsonObject> deleteCategoryById(Integer id) {

        //判断id是否为空 或 小于0
        if(ObjectUtil.isNull(id) || id <= 0) return this.setResultError("id不合法");
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);
        //判断查询的数据是否为空
        if(ObjectUtil.isNull(categoryEntity)) return this.setResultError("数据不存在");
        //判断查询数据的父节点下有无其他节点
        if(categoryEntity.getIsParent() >= 1) return this.setResultError("当前节点为父节点");
        //获取父节点下的其他子节点的数据

        //如果当前分类被品牌绑定的话不能被删除 --> 通过分类id查询中间表是否有数据 true : 当前分类不能被删除 false:继续执行
        Example example1 = new Example(CategoryBrandEntity.class);
        example1.createCriteria().andEqualTo("brandId",id);
        List<CategoryBrandEntity> categoryBrandEntities = categoryBrandMapper.selectByExample(example1);
        if(categoryBrandEntities.size()>=1)return this.setResultError("当前分类被其他品牌绑定,无法进行删除");

        Example example = new Example(categoryEntity.getClass());
        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());
        List<CategoryEntity> categoryList = categoryMapper.selectByExample(example);
        //判断其他子节点的数据条数是否小于等于1SAFJSA
        if(categoryList.size() <= 1){
            CategoryEntity categoryEntity1 = new CategoryEntity();
            //当父节点下只有一个或没有子节点时ASDAS
            //将该节点的父节点的值修改为0
            categoryEntity1.setIsParent(0);
            //将该节点的id修改为该节点的父id
            categoryEntity1.setId(categoryEntity.getParentId());
            categoryMapper.updateByPrimaryKeySelective(categoryEntity1);


        }
        //执行删除操作
        categoryMapper.deleteByPrimaryKey(id);
        //返回一个删除成功的信息
        return this.setResultSuccess();
    }


    @Override
    @Transactional
    public Result<JsonObject> putCategoryById(CategoryEntity categoryEntity) {
        categoryMapper.updateByPrimaryKeySelective(categoryEntity);
        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JsonObject> addCategoryEntity(CategoryEntity categoryEntity) {

        CategoryEntity parentCategoryEntity = new CategoryEntity();
        parentCategoryEntity.setId(categoryEntity.getParentId());
        parentCategoryEntity.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(parentCategoryEntity);

        categoryMapper.insertSelective(categoryEntity);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<CategoryEntity>> getCategoryByBrandId(Integer brandId) {

        List<CategoryEntity> list = categoryMapper.getCategoryByBrandId(brandId);
        return this.setResultSuccess(list);

    }

}
