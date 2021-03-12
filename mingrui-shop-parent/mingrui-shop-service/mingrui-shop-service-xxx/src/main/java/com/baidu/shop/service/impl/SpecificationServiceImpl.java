package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.shop.service.SpecificationService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@RestController
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;


    //陈辞v 永远的神

    @Override
    public Result<List<SpecGroupEntity>> getSpecGroupInfo(SpecGroupDTO specGroupDTO) {
        Example example = new Example(SpecGroupEntity.class);
        example.createCriteria().andEqualTo("cid",
                BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class).getCid());
        List<SpecGroupEntity> specGroupEntities = specGroupMapper.selectByExample(example);
        return this.setResultSuccess(specGroupEntities);
    }

    @Override
    public Result<JSONObject> saveSpecGroupInfo(SpecGroupDTO specGroupDTO) {

        specGroupMapper.insertSelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> editSpecGroupInfo(SpecGroupDTO specGroupDTO) {

        specGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteSpecGroupInfo(Integer id) {


        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",id);
        List<SpecParamEntity> specParamEntities = specParamMapper.selectByExample(example);
        if(specParamEntities.size() >= 1) return this.setResultError("当前组下有其他主体,无法进行删除");

        specGroupMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<SpecParamEntity>> getSpecParamInfo(SpecParamDTO specParamDTO) {

        SpecParamEntity specParamEntity = BaiduBeanUtil.copyProperties(specParamDTO, SpecParamEntity.class);
        Example example = new Example(SpecParamEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(ObjectUtil.isNotNull(specParamEntity.getGroupId()))
            criteria.andEqualTo("groupId",specParamEntity.getGroupId());
        if(ObjectUtil.isNotNull(specParamEntity.getCid()))
            criteria.andEqualTo("cid",specParamEntity.getCid());
        /*
         * 史诗级错误start
         * 括号中 是specParamDTO
         * 我写成了specParamEntity 低级错误 md
         * 2021年3月8号 晚自习因为这个错找了一节课半 心态裂开
         * */
        if(ObjectUtil.isNotNull(specParamDTO.getGeneric())){
            criteria.andEqualTo("generic",specParamEntity.getGeneric());
        }
        /*
        * 史诗级错误end
        * */

        List<SpecParamEntity> specParamEntities = specParamMapper.selectByExample(example);

        return this.setResultSuccess(specParamEntities);
    }

    @Override
    public Result<JSONObject> saveSpecParamInfo(SpecParamDTO specParamDTO) {

        specParamMapper.insertSelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> editSpecParamInfo(SpecParamDTO specParamDTO) {

        specParamMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteSpecParamInfo(Integer id) {





        specParamMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }
}
