package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.domain.exception.image.CannotUploadImageException;
import com.vitasoft.goodsgrapher.domain.exception.image.CannotViewImageException;
import com.vitasoft.goodsgrapher.domain.exception.image.ImageNotFoundException;
import com.vitasoft.goodsgrapher.domain.model.enums.ImageType;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignImage;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImage;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignImageRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelImageRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelInfoRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    @Value("${image.upload-path.modelImagesWorker}")
    private String modelImagesWorkerPath;

    private final ModelImageRepository modelImageRepository;
    private final DesignImageRepository designImageRepository;
    private final ModelInfoRepository modelInfoRepository;

    public ModelImage uploadMetadataImage(String memberId, ModelInfo modelInfo, MultipartFile file, int displayOrder, DesignInfo designInfo, JSONObject jsonObject) {
        int dashIndex = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf("_");
        int dotIndex = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".");
        String viewPoint = file.getOriginalFilename().substring(dashIndex + 1, dotIndex);
        String brandCode = formatLastRightHolderName(designInfo.getLastRightHolderName(), designInfo.getApplicantName());
        String fileType = "." + FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = formatFileName(memberId, modelInfo, designInfo, displayOrder, fileType, brandCode, viewPoint);
        String fileSize = String.valueOf(file.getSize());
        String registrationNumber = modelInfo.getRegistrationNumber().contains("/") ? modelInfo.getRegistrationNumber().replace("/", "") : modelInfo.getRegistrationNumber();

        uploadImage(modelImagesWorkerPath + File.separator + registrationNumber, file, fileName);

        return new ModelImage(memberId, modelInfo, registrationNumber + "/" + fileName, fileSize, fileType, displayOrder, brandCode, viewPoint, jsonObject);
    }

    private String formatFileName(String memberId, ModelInfo modelInfo, DesignInfo designInfo, int displayOrder, String fileType, String brandCode, String viewPoint) {
        String formatMetaSeq = String.format("%06d", modelInfo.getModelSeq());
        String[] folderNameParts = {memberId, brandCode, designInfo.getArticleName(), modelInfo.getModelName(), modelInfo.getRegistrationNumber().replaceAll("/", ""), designInfo.getClassCode().contains("|") ? designInfo.getClassCode().split("\\|")[0] : designInfo.getClassCode()};
        String folderName = String.join("_", folderNameParts);
        String fileName = "/VS_2023_" + formatMetaSeq + "_" + viewPoint + "_0_" + displayOrder + fileType;
        return folderName + fileName;
    }

    private String formatLastRightHolderName(String lastRightHolderName, String applicantName) {
        String[] brandNameList = {"애드크런치", "세라젬", "다이슨", "에이치피", "라네즈", "엘지전자", "현대모비스", "미쟝센", "슈피겐", "설화수", "삼성전자", "쓰리쎄븐"};
        String[] brandCodeList = {"ACR", "CRG", "DY", "HP", "LA", "LG", "MOB", "MSC", "SG", "SH", "SS", "TS"};
        int index = 0;
        String brandCode = "ZZ";
        if (lastRightHolderName == null) {
            return applicantName;
        }
        for (String brandName : brandNameList) {
            if (lastRightHolderName.contains(brandName)) {
                brandCode = brandCodeList[index];
                break;
            }
            index++;
        }
        return brandCode;
    }

    public void uploadImage(String imagePath, MultipartFile file, String fileName) {
        try {
            File directory = new File(imagePath, fileName.substring(0, fileName.lastIndexOf("/")));
            File image = new File(imagePath, fileName);
            FileUtils.forceMkdir(directory);
            file.transferTo(image);
        } catch (IOException e) {
            throw new CannotUploadImageException();
        }
    }

    public byte[] viewImage(ImageType type, int seq) {
        File image;
        if (type == ImageType.DESIGN_IMAGE) {
            DesignImage designImage = designImageRepository.findById(seq)
                    .orElseThrow(() -> new ImageNotFoundException(seq));
            image = new File(designImage.getImgPath());
        } else {
            ModelImage modelImage = modelImageRepository.findById(seq)
                    .orElseThrow(() -> new ImageNotFoundException(seq));
            image = new File(modelImagesWorkerPath, modelImage.getFileName());
        }
        return this.viewImage(image);
    }

    public byte[] viewImage(File image) {
        try (
                FileInputStream inputStream = new FileInputStream(image);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, length);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new CannotViewImageException(image);
        }
    }

    public byte[] viewThumbnailImage(String imagePath) {
        String registrationNumber = FilenameUtils.getBaseName(imagePath);

        ModelInfo modelInfo = modelInfoRepository.findTopByRegistrationNumber(registrationNumber);

        String fileName;
        if (modelInfo != null) {
            fileName = modelInfo.getPathImgGoods();
        } else {
            fileName = imagePath;
        }

        return this.viewImage(new File(fileName));
    }
}
