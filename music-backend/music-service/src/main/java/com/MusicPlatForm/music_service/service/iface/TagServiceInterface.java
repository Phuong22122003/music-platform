package com.MusicPlatForm.music_service.service.iface;

import com.MusicPlatForm.music_service.dto.reponse.TagResponse;
import com.MusicPlatForm.music_service.dto.request.TagRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TagServiceInterface {
    /**
     * Create tag
     * @param tagRequest
     */
    public TagResponse createTag(TagRequest tagRequest);
    public String deleteTag(String id);
    public List<TagResponse> getTags();
}
