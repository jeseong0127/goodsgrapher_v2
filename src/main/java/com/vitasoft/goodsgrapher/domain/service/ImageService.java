package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.domain.exception.image.CannotUploadImageException;
import com.vitasoft.goodsgrapher.domain.exception.image.CannotViewImageException;
import com.vitasoft.goodsgrapher.domain.exception.image.ImageNotFoundException;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ArticleFile;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Metadata;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ArticleFileRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    public ArticleFile uploadMetadataImage(String memberId, Metadata metadata, MultipartFile file, int displayOrder) {
        try {
            String fileType = "." + FilenameUtils.getExtension(file.getOriginalFilename());
            String fileSize = String.valueOf(file.getSize());
            String fileName = formatFileName(memberId, metadata, displayOrder, fileType);
            uploadImage(inspectPath, file, fileName);
            return new ArticleFile(metadata.getMetaSeq(), fileName.substring(fileName.lastIndexOf("/") + 1), fileName, fileSize, fileType, memberId, displayOrder);
        } catch (IOException e) {
            throw new CannotUploadImageException();
        }
    }

    private String formatFileName(String memberId, Metadata metadata, int displayOrder, String fileType) {
        String formatMetaSeq = String.format("%06d", metadata.getMetaSeq());

        String[] folderNameParts = {memberId, formatLastRightHolderName(metadata.getLastRightHolderName()), metadata.getArticleName(), metadata.getModelName(), metadata.getRegistrationNumber().replaceAll("/", ""), metadata.getDsshpclsscd().contains("|") ? metadata.getDsshpclsscd().split("\\|")[0] : metadata.getDsshpclsscd()};
        String folderName = metadata.getDsshpclsscd().contains("|") ? metadata.getDsshpclsscd().split("\\|")[0] + "/" + String.join("_", folderNameParts) : metadata.getDsshpclsscd() + "/" + String.join("_", folderNameParts);
        String fileName = "/VS_2022_" + formatMetaSeq + "_0_-1_" + (displayOrder + 1) + fileType;
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

    private void uploadImage(String imagePath, MultipartFile file, String fileName) throws IOException {
        File directory = new File(imagePath + File.separator + fileName.substring(0, fileName.lastIndexOf("/")));
        File image = new File(imagePath + File.separator + fileName);

        FileUtils.forceMkdir(directory);
        file.transferTo(image);
    }

    public byte[] viewImage(int imageId) {
        ArticleFile articleFile = articleFileRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException(imageId));
        return this.viewImage(new File(imagePath, articleFile.getFileName()));
    }

    public byte[] viewInspectImage(int imageId) {
        ArticleFile articleFile = articleFileRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException(imageId));
        return this.viewImage(new File((articleFile.getInspectPf() == 'F' ? failPath : inspectPath), articleFile.getFileName()));
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
