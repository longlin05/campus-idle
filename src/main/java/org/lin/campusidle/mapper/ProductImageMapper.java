package org.lin.campusidle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.lin.campusidle.entity.ProductImage;

import java.util.List;

@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {
    //根据商品ID查询商品图片列表
    @Select("select * from idle_product_image where product_id=#{productId} order by sort_order")
    List<ProductImage> findByProductId(Long productId);
}