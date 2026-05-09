package com.MusicPlatForm.search_service.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// FieldType.Text: Cho phép full-text search (có phân tích token).
// 🔹 FieldType.Keyword: Không phân tích, chỉ tìm kiếm chính xác.
// 🔹 index = false: Field này sẽ không thể tìm kiếm, chỉ lưu trữ dữ liệu.
// @Field(type = FieldType.Text) 

@Document(indexName = "tracks")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Track{
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;
    
    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword,index = false)
    private String trackId;
    
}
