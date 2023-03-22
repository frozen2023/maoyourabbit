package com.chen.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {

        log.info("starting insert fill.....");
        this.setFieldValByName("mgtCreate",new Date(),metaObject);
        this.setFieldValByName("mgtModify",new Date(),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("starting update fill.....");
        this.setFieldValByName("mgtModify",new Date(),metaObject);
    }
}
