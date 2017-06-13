package com.avonniv.service;

import com.avonniv.domain.FileInfo;
import com.avonniv.domain.FileInfo;
import com.avonniv.domain.GrantCall;
import com.avonniv.repository.FileInfoRepository;
import com.avonniv.repository.FileInfoRepository;
import com.avonniv.repository.GrantCallRepository;
import com.avonniv.service.dto.FileInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FileInfoService {

    private final Logger log = LoggerFactory.getLogger(FileInfoService.class);

    private final FileInfoRepository fileInfoRepository;

    public FileInfoService(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    public FileInfo createFileInfo(FileInfoDTO fileInfoDTO) {
        FileInfo newFileInfo = new FileInfo();
        newFileInfo.setName(fileInfoDTO.getName());
        newFileInfo.setMineType(fileInfoDTO.getMineType());
        newFileInfo.setSize(fileInfoDTO.getSize());
        newFileInfo.setPath(fileInfoDTO.getPath());
        newFileInfo.setCheckSum(fileInfoDTO.getCheckSum());

        fileInfoRepository.save(newFileInfo);
        log.debug("Created Information for File Info: {}", newFileInfo);
        return newFileInfo;
    }

    public List<FileInfoDTO> getAll() {
        return fileInfoRepository.findAll().stream().map(FileInfoDTO::new).collect(Collectors.toList());
    }

    public Optional<FileInfo> updateFileInfo(FileInfoDTO fileInfoDTO) {
        return Optional.of(fileInfoRepository
            .findOne(fileInfoDTO.getId()))
            .map(fileInfo -> {
                fileInfo.setName(fileInfoDTO.getName());
                fileInfo.setMineType(fileInfoDTO.getMineType());
                fileInfo.setSize(fileInfoDTO.getSize());
                fileInfo.setPath(fileInfoDTO.getPath());
                fileInfo.setCheckSum(fileInfoDTO.getCheckSum());
                log.debug("Changed Information for File Info: {}", fileInfo);
                return fileInfo;
            });
    }

    public void deleteFileInfo(FileInfoDTO fileInfoDTO) {
        fileInfoRepository.delete(fileInfoDTO.getId());
    }
}
