package com.baidu.shop.entity;

import lombok.Data;
import javax.persistence.Table;

@Table(name = "tb_category_brand")
@Data
public class CategoryBrandEntity {

    private Integer categoryId;

    private Integer brandId;

}
