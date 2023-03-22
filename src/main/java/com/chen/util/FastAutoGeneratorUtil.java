package com.chen.util;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FastAutoGeneratorUtil {
    public static void main(String[] args) {

        List<String> tables = new ArrayList<>();
        tables.add("account");
        tables.add("debt");
        tables.add("order");
        tables.add("problem");
        tables.add("report");

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/maoyourabbit?useSSL=true&useUnicode=true&characterEncoding=utf8","root","123456")
                .globalConfig(builder -> {
                    builder.author("Frozen")               //作者
                            .outputDir(System.getProperty("user.dir")+"\\src\\main\\java")    //输出路径(写到java目录)
                            //.enableSwagger()           //开启swagger
                            .commentDate("yyyy-MM-dd");
                            //.fileOverride();            //开启覆盖之前生成的文件
                })
                .packageConfig(builder -> {
                    builder.parent("com")
                            .moduleName("chen")
                            .entity("pojo")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            .mapper("mapper")
                            .xml("mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.xml,System.getProperty("user.dir")+"\\src\\main\\resources\\mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables)
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .entityBuilder()
                            .enableLombok()
                            .logicDeleteColumnName("deleted")
                            .enableTableFieldAnnotation()
                            .controllerBuilder()
                            // 映射路径使用连字符格式，而不是驼峰
                            .enableHyphenStyle()
                            .formatFileName("%sController")
                            .enableRestStyle()
                            .mapperBuilder()
                            /* //生成通用的resultMap
                             .enableBaseResultMap()*/
                            .superClass(BaseMapper.class)
                            .formatMapperFileName("%sMapper")
                            .enableMapperAnnotation()
                            .formatXmlFileName("%sMapper");
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}