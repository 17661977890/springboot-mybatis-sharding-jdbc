package com.example.shardingjdbc.entity;

import com.alibaba.druid.support.monitor.annotation.MTable;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体user
 */
@Data
public class User implements Serializable{

    private Long id;
	private String name;
	private String phone;
	private String email;
	private String password;
	private Integer cityId;
    private Date createTime;
    private Integer sex;
}