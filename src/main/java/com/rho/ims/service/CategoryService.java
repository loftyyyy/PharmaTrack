package com.rho.ims.service;


import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.dto.CategoryDTO;
import com.rho.ims.dto.UpdateCategoryDTO;
import com.rho.ims.model.Category;
import com.rho.ims.respository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll(){
        return categoryRepository.findAll();

    }

    public Category getCategory(long id){
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Category saveCategory(CategoryDTO categoryDTO){
        if(categoryRepository.existsByName(categoryDTO.getName())){
            throw new DuplicateCredentialException("name", categoryDTO.getName());
        }

        Category category = new Category();
        category.setName(categoryDTO.getName());

        return categoryRepository.save(category);

    }

    public Category updateCategory(Long id, UpdateCategoryDTO updateCategoryDTO){

        Category category = categoryRepository.findById(id).orElseThrow( () -> new RuntimeException("Category doesn't exist"));
        category.setName(updateCategoryDTO.getName());

        return categoryRepository.save(category);

    }



}
