package com.zooting.api.domain.dm.entity;

import com.zooting.api.domain.BaseEntity;
import com.zooting.api.domain.file.entity.File;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dm")
public class DM extends BaseEntity {
    @Id
    @Column(name = "dm_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "dm")
    private List<File> files;
    @ManyToOne
    @JoinColumn(name = "dm_room_id")
    private DMRoom dmRoom;
    private String message;
    private String sender;

    @Builder
    public DM(List<File> files, DMRoom dmRoom, String message, String sender) {
        this.files = Objects.nonNull(files) ? files : new ArrayList<>();
        this.dmRoom = dmRoom;
        this.message = message;
        this.sender = sender;
    }
}
