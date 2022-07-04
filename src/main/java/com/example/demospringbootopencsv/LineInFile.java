package com.example.demospringbootopencsv;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LineInFile {
    private String[] content;
    private int size;
}
