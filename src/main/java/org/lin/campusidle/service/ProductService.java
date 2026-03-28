package org.lin.campusidle.service;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Product;
import org.lin.campusidle.vo.PageV0;
import org.lin.campusidle.vo.ProductV0;

import java.util.List;

public interface ProductService {

    //查询所有商品功能
    Result<PageV0<ProductV0>> getProductList(Long current, Long size);

    //按商品id查询商品信息（包含商品表，商品图片表）功能
    Result<ProductV0> getProductDetail(Long productId);

    //查询商品类别功能
    Result<List<String>> getCategories();

    //按商品类别查询商品功能
    Result<PageV0<ProductV0>> searchProductsByCategory(Integer categoryId, Long current, Long size);

    //按商品名称查询商品功能
    Result<PageV0<ProductV0>> searchProductsByKeyword(String keyword, Long current, Long size);

    //综合查询商品功能
    Result<PageV0<ProductV0>> searchProducts(Integer categoryId, String keyword, Long current, Long size);

    //发布商品功能
    Result<?> publishProduct(Product product, List<String> images);

    //修改商品信息功能
    Result<?> editProduct(Product product, List<String> images);

    //下架商品功能（仅修改商品状态）
    Result<?> offShelfProduct(Long productId, Long userId);

    //删除商品功能
    Result<?> deleteProduct(Long productId, Long userId);


}
