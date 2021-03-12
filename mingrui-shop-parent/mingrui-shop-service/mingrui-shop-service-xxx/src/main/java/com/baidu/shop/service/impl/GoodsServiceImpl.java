package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;
    @Resource
    private BrandMapper brandMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SpuDetailMapper spuDetailMapper;
    @Resource
    private SkuMapper skuMapper;
    @Resource
    private StockMapper stockMapper;

    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {

        //分页
        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        //排序
        if(!StringUtils.isEmpty(spuDTO.getSort())) PageHelper.orderBy(spuDTO.getOrderBy());

        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();

        //条件查询

        if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() < 2)
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        if(!StringUtils.isEmpty(spuDTO.getTitle()))
            criteria.andLike("title","%" + spuDTO.getTitle() + "%");
        if(ObjectUtil.isNotNull(spuDTO.getId()))
            criteria.andEqualTo("id",spuDTO.getId());

        //分页查询
        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);

        List<SpuDTO> spuDTOList = spuEntities.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);
            //通过分类id集合查询数据
            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3()));
            // 遍历集合并且将分类名称用 / 拼接
            // 3/1/2
            //ajax --> 并不是所有情况都要用异步 jquery.validate 验证用户名存在不存在
            String categoryName = categoryEntities.stream().map(categoryEntity -> categoryEntity.getName()).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(categoryName);

            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spuEntity.getBrandId());
            spuDTO1.setBrandName(brandEntity.getName());
            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> spuEntityPageInfo = new PageInfo<>(spuEntities);
        return this.setResult(HTTPStatus.OK,spuEntityPageInfo.getTotal() + "",spuDTOList);
    }

    @Override
    @Transactional
    public Result<JSONObject> saveGoods(SpuDTO spuDTO) {
        //final finally finalize()的区别????
        final Date date = new Date();
        //新增spu,新增返回主键, 给必要字段赋默认值
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);

        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);

        //新增spuDetail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDetail, SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);

        //新增sku list插入顺序有序 b,a set a,b treeSet b,a
        List<SkuDTO> skus = spuDTO.getSkus();
        skus.stream().forEach(skuDTO -> {

            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuEntity.getId());
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> editGoods(SpuDTO spuDTO) {

        final Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        spuDetailMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(),SpuDetailEntity.class));

        this.deleteSkusAndStock(spuEntity.getId());
        this.saveSkusAndStockInfo(spuDTO,spuEntity.getId(),date);

        return this.setResultSuccess();
    }

    private void saveSkusAndStockInfo(SpuDTO spuDTO,Integer spuId,Date date){

        List<SkuDTO> skus = spuDTO.getSkus();
        skus.stream().forEach(skuDTO -> {

            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }

    private void deleteSkusAndStock(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        //得到skuId集合
        List<Long> skuIdList = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(skuIdList);//通过skuId集合删除sku信息
        stockMapper.deleteByIdList(skuIdList);//通过skuId集合删除stock信息
    }

    @Override
    public Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId) {
        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);
    }

    @Override
    public Result<List<SkuDTO>> getSkusBySquId(Integer spuId) {
        List<SkuDTO> list = skuMapper.getSkusAndStockBySpuId(spuId);
        return this.setResultSuccess(list);
    }

    @Override
    public Result<JSONObject> deleteGoods(Integer spuId) {
        spuMapper.deleteByPrimaryKey(spuId);
        spuDetailMapper.deleteByPrimaryKey(spuId);
        this.deleteSkusAndStock(spuId);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> xiajia(Integer spuId) {
        SpuEntity spuEntity = spuMapper.selectByPrimaryKey(spuId);
        if(spuEntity.getSaleable() == 1){
            spuEntity.setSaleable(0);
        }else {
            spuEntity.setSaleable(1);
        }
        spuMapper.updateByPrimaryKeySelective(spuEntity);
        return this.setResultSuccess(spuEntity.getSaleable());
    }

}
