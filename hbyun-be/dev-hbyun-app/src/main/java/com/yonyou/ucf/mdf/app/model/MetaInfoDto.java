package com.yonyou.ucf.mdf.app.model;

import lombok.Data;

import java.util.List;

@Data
public class MetaInfoDto {

    private String sourceID;
    private String eventType;
    private String tenantId;
    private List<String> userObject;
    private String type;

}