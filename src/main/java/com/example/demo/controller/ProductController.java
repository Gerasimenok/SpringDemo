package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import com.example.demo.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.findAll();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("products", products);
        model.addAttribute("isAdmin", isAdmin);
        return "products";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String createProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String createProduct(@Valid @ModelAttribute("product") Product product,
                                BindingResult result,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) throws IOException {
        if (result.hasErrors()) {
            return "product-form";
        }
        if (!imageFile.isEmpty()) {
            try {
                product.setImage(imageFile.getBytes());
            } catch (IOException e) {
                result.rejectValue("image", "error.image", "Ошибка загрузки изображения");
                return "product-form";
            }
        }
        product.setCreatedDate(LocalDate.now());
        product.setUser(userService.getCurrentUser());
        productService.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Продукт успешно создан!");
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        var currentUser = userService.getCurrentUser();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !product.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Вы можете редактировать только свои продукты");
        }
        model.addAttribute("product", product);
        return "product-form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("product") Product product,
                                BindingResult result,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) throws IOException {
        if (result.hasErrors()) {
            return "product-form";
        }
        Product existingProduct;
        try {
            existingProduct = productService.findById(id);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Продукт не найден");
            return "redirect:/products";
        }
        var currentUser = userService.getCurrentUser();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !existingProduct.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Вы можете редактировать только свои продукты");
        }
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        if (!imageFile.isEmpty()) {
            try {
                existingProduct.setImage(imageFile.getBytes());
            } catch (IOException e) {
                result.rejectValue("image", "error.image", "Ошибка загрузки изображения");
                return "product-form";
            }
        }
        productService.save(existingProduct);
        redirectAttributes.addFlashAttribute("successMessage", "Продукт успешно обновлен!");
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Product product;
        try {
            product = productService.findById(id);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Продукт не найден");
            return "redirect:/products";
        }
        var currentUser = userService.getCurrentUser();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !product.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Вы можете удалять только свои продукты");
        }
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Продукт успешно удален!");
        return "redirect:/products";
    }

    @GetMapping("/api")
    @ResponseBody
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Product> getAllProductsJson() {
        return productService.findAll();
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public byte[] getProductImage(@PathVariable Long id) {
        Product product = productService.findById(id);
        return product.getImage();
    }
}