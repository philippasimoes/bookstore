package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.TagDto;
import com.bookstore.catalog_service.model.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface TagMapper {
    TagDto tagToTagDto(Tag tag);

    Tag tagDtoToTag(TagDto tagDto);

    List<TagDto> tagLisToTagDtoList(List<Tag> tagList);

    List<Tag> tagDtoLisToTagList(List<TagDto> tagDtoList);
}
