package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.application.request.DeleteMetadataRequest;
import com.vitasoft.goodsgrapher.domain.exception.metadata.DuplicationReserveIdException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ExceededReservedCountLimitException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ModelInfoNotFoundException;
import com.vitasoft.goodsgrapher.domain.model.dto.GetCategoryDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDetailDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetModelImageDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImage;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.CodeRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelImageRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.WorkRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MetadataService {

    private final ImageService imageService;

    private final ModelInfoRepository modelInfoRepository;

    private final ModelImageRepository modelImageRepository;

    private final DesignInfoRepository designInfoRepository;

    private final WorkRepository workRepository;

    private final CodeRepository codeRepository;

    @Value("${image.upload-path.inspect}")
    private String inspectPath;

    private final JSONParser parser = new JSONParser();

    public List<GetMetadataDto> getMetadataList() {
        return modelInfoRepository.findAll().stream().map(GetMetadataDto::new).collect(Collectors.toList());
    }

    public List<GetMetadataDto> getSearchMetadata(String searchWord, String codeId) {
        return modelInfoRepository.findAllByMetadata(codeId, searchWord == null ? "" : searchWord, searchWord, searchWord).stream().map(GetMetadataDto::new).collect(Collectors.toList());
    }

    public List<GetMetadataDto> getImageSearchMetadata(List<String> images) {
        List<GetMetadataDto> metadataDtos = new ArrayList<>();

        List<ModelInfo> modelInfoList = modelInfoRepository.findAllByModelNameIsNotNullAndUseYn('Y');

        cancelExcessReserveTime();

        for (String pathImage : images) {
            modelInfoList.forEach(modelInfo -> {
                if (modelInfo.getPathImgGoods() != null && modelInfo.getPathImgGoods().contains(pathImage)) {
                    metadataDtos.add(new GetMetadataDto(modelInfo, "photo"));
                } else {
                    if (modelInfo.getDesignInfo().getImgPath() != null && modelInfo.getDesignInfo().getImgPath().contains(pathImage))
                        metadataDtos.add(new GetMetadataDto(modelInfo, "drawing", modelInfo.getDesignInfo().getImgPath()));
                }
            });
        }

        return metadataDtos;
    }

    public void cancelExcessReserveTime() {
        LocalDateTime now = LocalDateTime.now();

        workRepository.findAllByStatus("1").stream().filter(work -> work.getRegDate().plusDays(2).isBefore(now)).forEach(work -> cancelReserveMetadata(work.getWorkSeq(), work.getRegId()));
    }

    public void reserveMetadata(String memberId, int modelSeq) {
        int maxReserveCount = 3;
        if (workRepository.countByRegIdAndStatus(memberId, "1") >= maxReserveCount)
            throw new ExceededReservedCountLimitException();

        Work worked = workRepository.findTopByModelSeqAndRegIdOrderByRegDateDesc(modelSeq, memberId);

        if (worked != null && !Objects.equals(worked.getStatus(), "0"))
            throw new DuplicationReserveIdException(modelSeq);

        Work work = new Work(memberId, modelSeq);
        work.reserve();
        workRepository.save(work);
    }

    public void cancelReserveMetadata(int modelSeq, String memberId) {
        List<ModelImage> modelImage = modelImageRepository.findAllByModelSeqAndRegId(modelSeq, memberId);
        modelImage.forEach(image -> image.setIsDeleted("1"));
        modelImageRepository.saveAll(modelImage);

        Work work = workRepository.findTopByModelSeqAndRegIdOrderByRegDate(modelSeq, memberId);
        work.setStatus("0");
        workRepository.save(work);
    }

    @Transactional(readOnly = true)
    public GetMetadataDetailDto getMetadataDetail(int modelSeq) {
        ModelInfo modelInfo = modelInfoRepository.findById(modelSeq).orElseThrow(() -> new ModelInfoNotFoundException(modelSeq));

        DesignInfo designInfo = designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber());

        return new GetMetadataDetailDto(modelInfo, designInfo);
    }

    @Transactional(readOnly = true)
    public List<GetModelImageDto> getMetadataImages(int modelSeq, String memberId) {
        return modelImageRepository.findByModelSeqAndIsDeletedAndRegId(modelSeq, "0", memberId).stream().map(GetModelImageDto::new).collect(Collectors.toList());
    }

    public void uploadMetadata(String memberId, int modelSeq, List<String> jsonArray, List<MultipartFile> images) {
        List<JSONObject> jsonObjectList = parseJsonArray(jsonArray);

        List<File> deletedFiles = deleteWorkedMetadata(modelSeq, memberId);

        ModelInfo modelInfo = modelInfoRepository.findById(modelSeq).orElseThrow(() -> new ModelInfoNotFoundException(modelSeq));

        DesignInfo designInfo = designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber());

        Work work = new Work(memberId, modelSeq);
        work.start();
        workRepository.save(work);

        List<File> insertedFiles = uploadMetadataImages(memberId, modelInfo, designInfo, jsonObjectList, images);
        deleteMetadataImage(insertedFiles, deletedFiles);
    }

    private List<JSONObject> parseJsonArray(List<String> jsonArrayAsString) {
        JSONArray jsonArray = convertJson(jsonArrayAsString.toString());
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (Object o : jsonArray) {
            jsonObjectList.add((JSONObject) o);
        }
        return jsonObjectList;
    }

    private JSONArray convertJson(String jsonArrayAsString) {
        try {
            return (JSONArray) parser.parse(jsonArrayAsString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private List<File> uploadMetadataImages(String memberId, ModelInfo modelInfo, DesignInfo designInfo, List<JSONObject> jsonObjectList, List<MultipartFile> images) {
        List<File> files = new ArrayList<>();

        List<ModelImage> imgArray = new ArrayList<>();
        Map<Integer, List<MultipartFile>> imageGroups = getImageGroups(images);
        imageGroups.forEach((key, subImages) -> {
            for (int i = 0; i < subImages.size(); i++) {
                ModelImage modelImage = imageService.uploadMetadataImage(memberId, modelInfo, subImages.get(i), i + 1, designInfo, jsonObjectList.get((key * 10) + i));
                imgArray.add(modelImage);
                files.add(new File(inspectPath, modelImage.getFileName()));
            }
        });
        modelImageRepository.saveAll(imgArray);

        return files;
    }

    private Map<Integer, List<MultipartFile>> getImageGroups(List<MultipartFile> images) {
        Map<Integer, List<MultipartFile>> groups = new TreeMap<>();

        images.forEach(image -> {
            int lastIndex = Objects.requireNonNull(image.getOriginalFilename()).lastIndexOf("_");
            int indexNumber = Integer.parseInt(image.getOriginalFilename().substring(lastIndex + 1, lastIndex + 2));

            groups.putIfAbsent(indexNumber, new ArrayList<>());
            List<MultipartFile> subImages = groups.get(indexNumber);
            subImages.add(image);
        });

        return groups;
    }

    public void deleteMetadata(DeleteMetadataRequest deleteMetadataRequest) throws IOException {
        for (int uploadSeq : deleteMetadataRequest.getUploadSeqList()) {
            ModelImage modelImage = modelImageRepository.findById(uploadSeq).orElseThrow(ArithmeticException::new);
            modelImage.setIsDeleted("1");

            FileUtils.forceDelete(new File(inspectPath, modelImage.getFileName()));
        }
    }

    public List<File> deleteWorkedMetadata(int modelSeq, String memberId) {
        ModelInfo modelInfo = modelInfoRepository.findById(modelSeq).orElseThrow(() -> new ModelInfoNotFoundException(modelSeq));

        List<ModelImage> modelImages = modelImageRepository.findAllByModelSeqAndRegId(modelInfo.getModelSeq(), memberId);
        List<File> deletedFiles = modelImages.stream().map(image -> new File(inspectPath, image.getFileName())).collect(Collectors.toList());
        modelImageRepository.deleteAllInBatch(modelImages);

        return deletedFiles;
    }

    public void deleteMetadataImage(List<File> insertedFiles, List<File> deletedFiles) {
        Set<String> filenames = insertedFiles.stream().map(File::getName).collect(Collectors.toSet());

        deletedFiles.stream()
                .filter(deletedFile -> !filenames.contains(deletedFile.getName()))
                .forEach(deletedFile -> {
                    try {
                        FileUtils.forceDelete(deletedFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Transactional(readOnly = true)
    public List<GetCategoryDto> getHighCategory() {
        return codeRepository.findAllByHighCodeIdAndCodeGroup("TOPCODE", "GOODS").stream().map(GetCategoryDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GetCategoryDto> getDownCategory(String codeId) {
        return codeRepository.findAllByHighCodeId(codeId).stream().map(GetCategoryDto::new).collect(Collectors.toList());
    }
}
