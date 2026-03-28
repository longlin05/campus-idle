package org.lin.campusidle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.lin.campusidle.entity.Category;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    //查询所有分类
    @Select("select * from idle_category order by sort_order")
    List<Category> findAllActive();
}