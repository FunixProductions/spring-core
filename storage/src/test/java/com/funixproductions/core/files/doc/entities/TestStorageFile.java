package com.funixproductions.core.files.doc.entities;

import com.funixproductions.core.files.entities.ApiStorageFile;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TestStorageFile extends ApiStorageFile {

    private String data;

}
