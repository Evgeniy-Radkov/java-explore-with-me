package ru.yandex.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new ConflictException("Категория с именем '" + dto.getName() + "' уже существует");
        }

        Category category = categoryMapper.toCategory(dto);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(saved);
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, NewCategoryDto dto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена: " + catId));

        String newName = dto.getName();
        if (!category.getName().equals(newName)
                && categoryRepository.existsByName(newName)) {
            throw new ConflictException("Категория с именем '" + newName + "' уже существует");
        }

        category.setName(newName);
        Category updated = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Категория не найдена: " + catId);
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return categoryRepository.findAll(pageRequest).stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена: " + catId));
        return categoryMapper.toCategoryDto(category);
    }
}
