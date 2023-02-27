package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.application.request.DeleteMetadataRequest;
import com.vitasoft.goodsgrapher.domain.exception.metadata.DuplicationReserveIdException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ExceededReservedCountLimitException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ModelInfoNotFoundException;
import com.vitasoft.goodsgrapher.domain.model.dto.GetCategoryDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetDesignImageDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetDesignInfoDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetImageSearchDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDetailDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetModelImageDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImage;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.CodeRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignImageRepository;
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

    private final DesignImageRepository designImageRepository;

    private final WorkRepository workRepository;

    private final CodeRepository codeRepository;

    @Value("${image.upload-path.modelImagesWorker}")
    private String modelImagesWorkerPath;

    private final JSONParser parser = new JSONParser();

    public List<GetMetadataDto> getMetadataList() {
        return modelInfoRepository.findAll().stream().map(modelInfo ->
                new GetMetadataDto(modelInfo,
                        new GetDesignInfoDto(designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber()),
                                designImageRepository.findAllByDesignSeqAndUseYn(designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber()).getDesignSeq(), "Y")
                                        .stream().map(designImage -> new GetDesignImageDto(designImage.getImgNumber(), designImage.getImgPath(), designImage.getUseYn(), designImage.getDesignImgSeq()))
                                        .collect(Collectors.toList())))
        ).collect(Collectors.toList());
    }

    public List<GetMetadataDto> getSearchMetadata(String searchWord, String codeId) {
        return modelInfoRepository.findAllByMetadata(codeId, searchWord == null ? "" : searchWord, searchWord, searchWord).stream().map(modelInfo ->
                new GetMetadataDto(modelInfo,
                        new GetDesignInfoDto(designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber()),
                                designImageRepository.findAllByDesignSeqAndUseYn(designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber()).getDesignSeq(), "Y")
                                        .stream().map(designImage -> new GetDesignImageDto(designImage.getImgNumber(), designImage.getImgPath(), designImage.getUseYn(), designImage.getDesignImgSeq()))
                                        .collect(Collectors.toList())))
        ).collect(Collectors.toList());
    }

    public List<GetMetadataDto> getImageSearchMetadata(List<String> images, List<String> confidence) {
        List<GetMetadataDto> metadataDtos = new ArrayList<>();

        List<ModelInfo> modelInfoList = modelInfoRepository.findAllByModelNameIsNotNullAndUseYn('Y');

//        cancelExcessReserveTime();

        Map<String, String> keyValueMap = new TreeMap<>();
        for (int i = 0; i < images.size(); i++) {
            keyValueMap.put(images.get(i), confidence.get(i));
        }

        for (String pathImage : images) {
            String pathName = getPathName(pathImage);
            GetImageSearchDto getImageSearchDto = designInfoRepository.findDistinctByRegistrationNumber(pathName);
            modelInfoList.forEach(modelInfo -> {
                if (modelInfo.getPathImgGoods() != null && !modelInfo.getPathImgGoods().isEmpty()) {
                    if (modelInfo.getRegistrationNumber().equals(pathName)) {
                        metadataDtos.add(new GetMetadataDto(modelInfo, "photo", getImageSearchDto, keyValueMap.get(pathImage)));
                    }
                } else if (getImageSearchDto.getImgPath() != null && !getImageSearchDto.getImgPath().isEmpty()) {
                    if (modelInfo.getRegistrationNumber().equals(pathName))
                        metadataDtos.add(new GetMetadataDto(modelInfo, "drawing", getImageSearchDto, keyValueMap.get(pathImage)));
                }
            });
        }

        return metadataDtos;
    }

    private String getPathName(String pathImage) {
        String pathName = pathImage.substring(0, pathImage.lastIndexOf("."));
        if (pathName.startsWith("DM")) {
            pathName = pathName.replace("DM", "DM/");
        }
        return pathName;
    }

    public void cancelExcessReserveTime(int modelSeq) {
        LocalDateTime now = LocalDateTime.now();

        workRepository.findAllByStatus("1").stream()
                .filter(work -> now.minusDays(2).isAfter(work.getRegDate()))
                .forEach(work -> cancelReserveMetadata(modelSeq, work.getRegId()));
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

        Work work = workRepository.findTopByModelSeqAndRegIdOrderByRegDateDesc(modelSeq, memberId);
        work.cancel();
        workRepository.save(work);
    }

    public void finishMetadata(String memberId, int modelSeq) {
        Work work = workRepository.findTopByModelSeqAndRegIdOrderByRegDateDesc(modelSeq, memberId);
        work.finish();
        work.setRegDate(LocalDateTime.now());
        workRepository.save(work);
    }

    @Transactional(readOnly = true)
    public GetMetadataDetailDto getMetadataDetail(int modelSeq, String memberId) {
        ModelInfo modelInfo = modelInfoRepository.findById(modelSeq).orElseThrow(() -> new ModelInfoNotFoundException(modelSeq));

        DesignInfo designInfo = designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber());

        Work work = workRepository.findByRegIdAndModelSeq(memberId, modelSeq);

        List<GetDesignImageDto> designImages = designImageRepository.findAllByDesignSeqAndUseYn(designInfo.getDesignSeq(), "Y")
                .stream().map(designImage -> new GetDesignImageDto(designImage.getImgNumber(), designImage.getImgPath(), designImage.getUseYn(), designImage.getDesignImgSeq())).collect(Collectors.toList());
        return new GetMetadataDetailDto(modelInfo, new GetDesignInfoDto(designInfo, designImages), work.getStatus());
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

//        Work work = new Work(memberId, modelSeq);
        Work work = workRepository.findTopByModelSeqAndRegIdOrderByRegDateDesc(modelSeq, memberId);
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
        int divNum = images.size() / 10;
        imageGroups.forEach((key, subImages) -> {
            for (int i = 0; i < subImages.size(); i++) {
                ModelImage modelImage = imageService.uploadMetadataImage(memberId, modelInfo, subImages.get(i), i + 1, designInfo, key == 300 && designInfo.getDesignSeq() != 286 ? jsonObjectList.get(((divNum - 1) * 10) + i) : jsonObjectList.get((key * 10) + i));
                imgArray.add(modelImage);
                files.add(new File(modelImagesWorkerPath, modelImage.getFileName()));
            }
        });
        modelImageRepository.saveAll(imgArray);

        return files;
    }

    private Map<Integer, List<MultipartFile>> getImageGroups(List<MultipartFile> images) {
        Map<Integer, List<MultipartFile>> groups = new TreeMap<>();

        images.forEach(image -> {
            int dashIndex = Objects.requireNonNull(image.getOriginalFilename()).lastIndexOf("_");
            int dotIndex = Objects.requireNonNull(image.getOriginalFilename()).lastIndexOf(".");
            int indexNumber = Integer.parseInt(image.getOriginalFilename().substring(dashIndex + 1, dotIndex));
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

            FileUtils.forceDelete(new File(modelImagesWorkerPath, modelImage.getFileName()));
        }
    }

    public List<File> deleteWorkedMetadata(int modelSeq, String memberId) {
        ModelInfo modelInfo = modelInfoRepository.findById(modelSeq).orElseThrow(() -> new ModelInfoNotFoundException(modelSeq));

        List<ModelImage> modelImages = modelImageRepository.findAllByModelSeqAndRegId(modelInfo.getModelSeq(), memberId);
        List<File> deletedFiles = modelImages.stream().map(image -> new File(modelImagesWorkerPath, image.getFileName())).collect(Collectors.toList());
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
