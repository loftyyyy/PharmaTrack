package com.rho.ims.service;


import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.dto.CategoryCreateDTO;
import com.rho.ims.dto.CategoryUpdateDTO;
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

    public Category saveCategory(CategoryCreateDTO categoryCreateDTO){
        if(categoryRepository.existsByName(categoryCreateDTO.getName())){
            throw new DuplicateCredentialException("name", categoryCreateDTO.getName());
        }

        Category category = new Category();
        category.setName(categoryCreateDTO.getName());
        category.setActive(Boolean.TRUE);

        return categoryRepository.save(category);

    }

    public Category updateCategory(Long id, CategoryUpdateDTO categoryUpdateDTO){

        Category category = categoryRepository.findById(id).orElseThrow( () -> new RuntimeException("Category doesn't exist"));
        if(categoryRepository.existsByName(categoryUpdateDTO.getName())){
            throw new DuplicateCredentialException("Name", categoryUpdateDTO.getName());

        }
        category.setName(categoryUpdateDTO.getName());
        category.setActive(categoryUpdateDTO.getActive());

        return categoryRepository.save(category);

    }



}
