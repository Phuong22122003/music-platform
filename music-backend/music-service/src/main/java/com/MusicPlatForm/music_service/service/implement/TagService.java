package com.MusicPlatForm.music_service.service.implement;

import com.MusicPlatForm.music_service.dto.reponse.TagResponse;
import com.MusicPlatForm.music_service.dto.request.TagRequest;
import com.MusicPlatForm.music_service.entity.Tag;
import com.MusicPlatForm.music_service.exception.AppException;
import com.MusicPlatForm.music_service.exception.ErrorCode;
import com.MusicPlatForm.music_service.mapper.TagMapper;
import com.MusicPlatForm.music_service.repository.TagRepository;
import com.MusicPlatForm.music_service.service.iface.TagServiceInterface;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TagService implements TagServiceInterface {
    TagRepository tagRepository;
    TagMapper tagMapper;
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TagResponse createTag(TagRequest tagRequest) {
        if(tagRepository.findByName(tagRequest.getName()).isPresent()) {
            throw new AppException(ErrorCode.TAG_EXISTED);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Tag tag = Tag.builder().createdAt(LocalDateTime.now()).name(tagRequest.getName()).userId(userId).isUsed(true).build();
        Tag newTag = tagRepository.save(tag);
        return tagMapper.toTagResponseFromTag(newTag);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public String deleteTag(String id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        tag.setUsed(false);
        tagRepository.save(tag);
        return "Delete successfully";
    }
    @Override
    public List<TagResponse> getTags() {
        List<Tag> tags = tagRepository.findAllByIsUsedTrue();
        return  tagMapper.toTagResponsesFromTags(tags);
    }

    public List<TagResponse> getTagsByIds(List<String>ids){
        List<Tag> tags = tagRepository.findAllById(ids);
        return  tagMapper.toTagResponsesFromTags(tags);
    }
}
