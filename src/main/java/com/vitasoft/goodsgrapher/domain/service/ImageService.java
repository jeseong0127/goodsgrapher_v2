package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.domain.exception.image.CannotUploadImageException;
import com.vitasoft.goodsgrapher.domain.exception.image.CannotViewImageException;
import com.vitasoft.goodsgrapher.domain.exception.image.ImageNotFoundException;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ArticleFile;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImages;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ArticleFileRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelImagesRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    @Value("${image.upload-path.metadata}")
    private String imagePath;

    @Value("${image.upload-path.inspect}")
    private String inspectPath;

    @Value("${image.upload-path.fail}")
    private String failPath;

    private final ArticleFileRepository articleFileRepository;
    private final ModelImagesRepository modelImagesRepository;

    public ModelImages uploadMetadataImage(String memberId, ModelInfo modelInfo, MultipartFile file, int displayOrder, DesignInfo designInfo) {
        int lastIndex = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf("_");
        String viewPoint = file.getOriginalFilename().substring( lastIndex + 1, lastIndex + 2 );
        String realPath = file.getOriginalFilename().substring( 0 ,lastIndex);
        String brandCode = formatLastRightHolderName(designInfo.getLastRightHolderName());
        String fileType = "." + FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = formatFileName(memberId, modelInfo, designInfo, displayOrder, fileType, brandCode, viewPoint);
        String fileSize = String.valueOf(file.getSize());

        uploadImage(inspectPath, file, fileName);

        return new ModelImages(memberId, modelInfo, fileName, fileSize, fileType, displayOrder, brandCode, realPath, viewPoint);
    }

    private String formatFileName(String memberId, ModelInfo modelInfo, DesignInfo designInfo, int displayOrder, String fileType, String brandCode, String viewPoint) {
        String formatMetaSeq = String.format("%06d", modelInfo.getModelSeq());
        String[] folderNameParts = {memberId, brandCode, designInfo.getArticleName(), modelInfo.getModelName(), modelInfo.getRegistrationNumber().replaceAll("/", ""), designInfo.getClassCode().contains("|") ? designInfo.getClassCode().split("\\|")[0] : designInfo.getClassCode()};
        String folderName = designInfo.getClassCode().contains("|") ? designInfo.getClassCode().split("\\|")[0] + "/" + String.join("_", folderNameParts) : designInfo.getClassCode() + "/" + String.join("_", folderNameParts);
        String fileName =  "/VS_2023_" + formatMetaSeq + "_" + viewPoint + "_0_" + displayOrder + fileType;
        return folderName + fileName;
    }

    private String formatLastRightHolderName(String lastRightHolderName) {
        String[] brandNameList = {"애드크런치", "세라젬", "다이슨", "에이치피", "라네즈", "엘지전자", "현대모비스", "미쟝센", "슈피겐", "설화수", "삼성전자", "쓰리쎄븐"};
        String[] brandCodeList = {"ACR", "CRG", "DY", "HP", "LA", "LG", "MOB", "MSC", "SG", "SH", "SS", "TS"};

        int index = 0;
        String brandCode = "ZZ";

        for (String brandName : brandNameList) {
            if (lastRightHolderName.contains(brandName)) {
                brandCode = brandCodeList[index];
                break;
            }
            index++;
        }

        return brandCode;
    }

    private void uploadImage(String imagePath, MultipartFile file, String fileName) {
        try {
            File directory = new File(imagePath, fileName.substring(0, fileName.lastIndexOf("/")));
            File image = new File(imagePath, fileName);
            FileUtils.forceMkdir(directory);
            file.transferTo(image);
        } catch (IOException e) {
            throw new CannotUploadImageException();
        }
    }

    public byte[] viewImage(int imageId) {
        ArticleFile articleFile = articleFileRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException(imageId));
        return this.viewImage(new File(imagePath, articleFile.getFileName()));
    }

    public byte[] viewInspectImage(int uploadSeq) {
        ModelImages modelImages = modelImagesRepository.findById(uploadSeq).orElseThrow(() -> new ImageNotFoundException(uploadSeq));
        return this.viewImage(new File((modelImages.getInspectPf() == 'F' ? failPath : inspectPath), modelImages.getFileName()));
    }

    private byte[] viewImage(File image) {
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
        return this.viewImage(new File(this.imagePath, imagePath));
    }

    public void deleteMetadataImage(String filename) throws IOException {
        File file = new File(inspectPath + File.separator + filename);
        FileUtils.forceDelete(file);
    }
}
