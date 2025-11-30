package ru.yandex.practicum.category;

import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto dto);

    CategoryDto update(Long catId, NewCategoryDto dto);

    void delete(Long catId);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(Long catId);
}
