package com.example.elasticsearch.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;

@Document(indexName = "i-order")
@Setting(settingPath = "static/es-setting.json")
@Data
public class Order {

    @Id
    @Field(type = FieldType.Keyword)
    private String orderNo;

    @Field(type = FieldType.Float)
    private Float validBetAmount;

    @Field(type = FieldType.Float)
    private Float BetAmount;

    @Field(type = FieldType.Float)
    private Float profitAmount = 0f;

    @Field(type = FieldType.Text)
    private String loginName;

    @Field(type = FieldType.Date)
    private Date betDate;

    @Field(type = FieldType.Text)
    private String platform;

    @Field(type = FieldType.Text)
    private String gameKind;

    @Field(type = FieldType.Text)
    private String gameType;
}
