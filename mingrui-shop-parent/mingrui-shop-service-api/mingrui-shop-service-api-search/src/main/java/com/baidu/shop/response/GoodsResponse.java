package com.baidu.shop.response;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.status.HTTPStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class GoodsResponse extends Result<List<GoodsDoc>> {

    private Long total;

    private Long totalPage;

    private List<CategoryEntity> categoryList;

    private List<BrandEntity> brandList;

    private Map<String,List<String>> specMap;

    public GoodsResponse(Long total,Long totalPage,List<CategoryEntity> categoryList,List<BrandEntity> brandList,Map<String,List<String>> specMap,List<GoodsDoc> goodsDocList){

        super(HTTPStatus.OK,"",goodsDocList);

        this.total = total;
        this.totalPage = totalPage;
        this.categoryList = categoryList;
        this.brandList = brandList;
        this.specMap = specMap;

    }


}
