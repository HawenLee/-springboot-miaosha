package com.hawen.miaosha.domain;

import lombok.Data;

import java.util.Date;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/7
 */
@Data
public class User {
    private Long id;
    private String nickname;
    private String password;
    private String salt;
    private String head;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;
}
