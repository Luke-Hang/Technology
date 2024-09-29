package com.myspringboot;

import com.myspringboot.pojo.ProductPo;
import com.myspringboot.service.ProductService;
import org.apache.commons.math.stat.descriptive.summary.Product;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * @author xiehang
 * @date 2023/2/24 9:31
 */
public class ProductTest {
    @MockBean
    private ProductService productService;

    public void getProduct(){
        ProductPo mockProduct=new ProductPo();
        mockProduct.setId(1L);
        mockProduct.setProductName("productPo"+1);
        mockProduct.setNote("note"+1);
//        BDDMockito.given(this.productService)
    }
}
