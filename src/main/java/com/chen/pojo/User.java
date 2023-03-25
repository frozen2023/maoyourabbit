package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private Long userId; //id

    @NotNull
    private String password; //账号

    @NotNull
    private String username; //密码

    private String nickname; //昵称

    private String headUrl; //头像地址(不含域名)

    private String authority; //权限

    private String email; //邮箱

    private String phoneNumber; //手机号码

    private Integer authenticated; //是否认证

    private String realName; //真实姓名

    private String identityCard; //身份证号

    private BigDecimal balance; //余额

    private Integer frozen; //是否被冻结

    @TableField(fill = FieldFill.INSERT)
    private Date mgtCreate; //创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date mgtModify; //修改时间

    @TableLogic
    private Integer deleted; //逻辑删除
}
