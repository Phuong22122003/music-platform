package com.MusicPlatForm.music_service.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
@Table(name = "Track_tag")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackTag {
    public TrackTag(Tag tag, Track track){
        this.tag = tag;
        this.track =track;
    }
    @Id
    @Column(name = "Id")
    String id;
    
    @ManyToOne
    @JoinColumn(name = "Track_id")
    Track track;
    
    @ManyToOne
    @JoinColumn(name = "Tag_id")
    Tag tag;
    
    @PrePersist
    public void generateId() {
        if (id == null) {
            this.id = UUID.randomUUID().toString(); // Generate Id
        }
    }
}
