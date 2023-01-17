package com.vitasoft.goodsgrapher.domain.model.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadImageDto {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS");

    private MultipartFile file;
    private String displayName;
    private String date;
    private String dateTime;
    private String fileName;
    private String fileSize;
    private String fileType;

    public UploadImageDto(MultipartFile file) {
        LocalDateTime now = LocalDateTime.now();
        this.date = now.format(dateFormatter);
        this.dateTime = now.format(dateTimeFormatter);
        this.file = file;
        this.displayName = dateTime + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        this.fileName = date + "/" + dateTime;
        this.fileSize = file.getSize() + "";
        this.fileType = "." + FilenameUtils.getExtension(file.getOriginalFilename());
    }
}
