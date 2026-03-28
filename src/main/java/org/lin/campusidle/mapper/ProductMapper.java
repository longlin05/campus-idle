package org.lin.campusidle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.lin.campusidle.entity.Product;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    //根据商品ID查询商品
    @Select("select * from idle_product where product_id=#{productId}")
    Product findByProductId(Long productId);
    
    //根据用户ID查询商品
    @Select("select * from idle_product where publish_user_id=#{userId}")
    Product findByUserId(Long userId);
    
    //根据分类ID查询商品
    @Select("select * from idle_product where category_id=#{categoryId}")
    Product findByCategoryId(Integer categoryId);
    
    //根据商品名称模糊查询
    @Select("select * from idle_product where title like concat('%', #{keyword}, '%')")
    Product findByKeyword(String keyword);
}