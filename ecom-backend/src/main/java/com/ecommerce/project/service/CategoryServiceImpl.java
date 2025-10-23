package com.ecommerce.project.service;

import com.ecommerce.project.Repository.CategoryRepository;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  // private Long nextId = 1L;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ModelMapper modelMapper;

  @Override
  public CategoryResponse getAllCategories(
      int pageNumber, int pageSize, String sortBy, String sortOrder) {
    Sort sortCatgeories =
        sortOrder.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
    Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortCatgeories);
    Page<Category> categories = categoryRepository.findAll(pageDetails);
    List<CategoryDTO> categoriesDTO =
        categories.stream().map(cat -> modelMapper.map(cat, CategoryDTO.class)).toList();
    CategoryResponse categoryResponse = new CategoryResponse();
    categoryResponse.setContent(categoriesDTO);
    categoryResponse.setPageNumber(categories.getNumber());
    categoryResponse.setPageSize(categories.getSize());
    categoryResponse.setTotalElements(categories.getTotalElements());
    categoryResponse.setTotalPages(categories.getTotalPages());
    categoryResponse.setLastPage(categories.isLast());
    return categoryResponse;
  }

  @Override
  public CategoryDTO createCategory(CategoryDTO categoryDTO) {
    // category.setCategoryId(nextId++);
    Category category = modelMapper.map(categoryDTO, Category.class);
    Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
    if (savedCategory != null) {
      throw new APIException("Category already exists");
    }
    Category saveCategory = categoryRepository.save(category);
    return modelMapper.map(saveCategory, CategoryDTO.class);
  }

  @Override
  public CategoryDTO deleteCategory(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(
                () -> new ResourceNotFoundException("CategoryName", categoryId, "Category"));

    categoryRepository.delete(category);
    return modelMapper.map(category, CategoryDTO.class);
  }

  @Override
  public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
    // fetch existing entity
    Category existingCategory =
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("CategoryName", categoryId, "Category"));

    // check if another category with the same name exists
    Category duplicateCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
    if (duplicateCategory != null && !duplicateCategory.getCategoryId().equals(categoryId)) {
      throw new APIException("Category already exists");
    }
    existingCategory.setCategoryName(categoryDTO.getCategoryName());
    Category savedCategory = categoryRepository.save(existingCategory);
    return modelMapper.map(savedCategory, CategoryDTO.class);
  }

}
