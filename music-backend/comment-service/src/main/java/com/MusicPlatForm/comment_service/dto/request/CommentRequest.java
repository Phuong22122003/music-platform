package com.MusicPlatForm.comment_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CommentRequest {
    String trackId;
    @Size (min = 1, message = "Xin hãy nhập ký tự")
    String content;
}
