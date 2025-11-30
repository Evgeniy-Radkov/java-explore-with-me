package ru.yandex.practicum.category;

import org.mapstruct.Mapper;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    Category toCategory(NewCategoryDto dto);
}
