package edu.scau.forestproject.pojo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<E> {
    private long total;
    private List<E> rows;
}
