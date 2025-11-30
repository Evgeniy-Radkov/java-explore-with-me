package ru.practicum.category;

import org.mapstruct.Mapper;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    Category toCategory(NewCategoryDto dto);
}
