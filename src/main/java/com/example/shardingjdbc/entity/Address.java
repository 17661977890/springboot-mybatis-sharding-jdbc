package com.example.shardingjdbc.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 实体address
 */
@Data
public class Address implements Serializable{
    private Long id;
	private String code;
	private String name;
	private String pid;
	private Integer type;
	private Integer lit;
}