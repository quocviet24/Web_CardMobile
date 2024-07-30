package com.nishikatakagi.store.controller;

import com.nishikatakagi.store.mapper.ProductMapper;
import com.nishikatakagi.store.models.Product;
import com.nishikatakagi.store.models.ProductDto;
import com.nishikatakagi.store.models.ProductHistory;
import com.nishikatakagi.store.repository.ProductHistoryRepository;
import com.nishikatakagi.store.repository.ProductRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private ProductRepository pr;
    private ProductHistoryRepository phr;

    public ProductController(ProductRepository pr, ProductHistoryRepository phr) {
        this.pr = pr;
        this.phr = phr;
    }

    @GetMapping({"", "/"})
    public String showProductList(Model model){
        List<Product> products = pr.findAll();
        model.addAttribute("products",products);
        return "products/index";
    }
    
    @GetMapping("/incre")
    public String showProductListIncrebyPrice(Model model){
        List<Product> products = pr.findAll(Sort.by(Sort.Direction.ASC,"price"));
        model.addAttribute("products",products);
        return "products/incre";
    }
    
    // xóa một sản phẩm theo id và lưu trữ vào bảng producthistory
    @GetMapping("/delete/{id}")
    public String deleteAndShow(Model model,@PathVariable int id){
    	Product p = pr.findById(id).orElseThrow(() -> new RuntimeException("Account does not exist"));
    	ProductHistory ph = ProductMapper.convertt(p);
    	phr.save(ph);
    	pr.deleteById(id);
        List<Product> products = pr.findAll();
        model.addAttribute("products",products);
        return "products/index";
    }
    
    // hiện thị danh sách các sản phẩm đã xóa, lấy db từ bảng productHistory
    @GetMapping("/history")
    public String showProductListHistory(Model model){
        List<ProductHistory> products = phr.findAll();
        model.addAttribute("products",products);
        return "products/history";
    }
    
    @GetMapping("/restore/{id}")
    public String restoreProduct(Model model,@PathVariable int id){
    	ProductHistory p = phr.findById(id).orElseThrow(() -> new RuntimeException("Account does not exist"));
    	Product ph = ProductMapper.convertt(p);
    	pr.save(ph);
    	phr.deleteById(id);
        List<ProductHistory> products = phr.findAll();
        model.addAttribute("products",products);
        return "products/history";
    }
    
    @GetMapping("/create")
    public String showCreatePage(Model model) {
    	ProductDto productDto = new ProductDto();
    	model.addAttribute("productDto",productDto);
    	return "products/CreateProduct";
    }
    
}
