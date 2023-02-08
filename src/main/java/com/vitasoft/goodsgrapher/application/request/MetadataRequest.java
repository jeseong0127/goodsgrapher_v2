package com.vitasoft.goodsgrapher.application.request;

import com.sun.istack.NotNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Data
public class MetadataRequest {
    @NotNull
    private int modelSeq;
    //    @Size(max = 62, message = "사진은 최소 62장까지 첨부할 수 있습니다.")
//    private List<MultipartFile> images;

    private List<String> json;
}
