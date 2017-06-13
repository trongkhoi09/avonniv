package com.avonniv.service;

import com.avonniv.AvonnivApp;
import com.avonniv.domain.FileInfo;
import com.avonniv.repository.FileInfoRepository;
import com.avonniv.service.dto.FileInfoDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AvonnivApp.class)
@Transactional
public class FileInfoServiceTest {
    @Autowired
    FileInfoService fileInfoService;

    @Autowired
    FileInfoRepository fileInfoRepository;

    @Test
    public void createFileInfo() throws Exception {
        Optional<FileInfo> maybeFileInfo = fileInfoRepository.findOneByPath("/path/file.jpg");
        assertThat(maybeFileInfo.isPresent()).isFalse();
        FileInfoDTO fileInfoDTO = new FileInfoDTO(
            null,
            null,
            null,
            0,
            "file",
            "jpg",
            1024,
            "/path/file.jpg",
            "checkSum"
        );
        fileInfoService.createFileInfo(fileInfoDTO);
        maybeFileInfo = fileInfoRepository.findOneByPath("/path/file.jpg");
        assertThat(maybeFileInfo.isPresent()).isTrue();
        fileInfoRepository.deleteAll();
    }

    @Test
    public void getAll() throws Exception {
        List<FileInfoDTO> fileInfoDTOS = fileInfoService.getAll();
        assertThat(fileInfoDTOS.size()).isEqualTo(0);
        for (int i = 0; i < 4; i++) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName("File_Info_Test_" + (i + 1));
            fileInfo.setPath("/path/file_" + (i + 1) + ".jpg");
            fileInfo.setMineType("jpg");
            fileInfo.setCheckSum("CheckSum" + (i + 1));
            fileInfoRepository.save(fileInfo);
        }

        fileInfoDTOS = fileInfoService.getAll();
        assertThat(fileInfoDTOS.size()).isEqualTo(4);
        assertThat(fileInfoDTOS.get(0).getName()).isEqualTo("File_Info_Test_1");
        assertThat(fileInfoDTOS.get(1).getName()).isEqualTo("File_Info_Test_2");

        FileInfo fileInfo = new FileInfo();
        fileInfo.setName("File_Info_Test_5");
        fileInfoRepository.save(fileInfo);
        fileInfoDTOS = fileInfoService.getAll();

        assertThat(fileInfoDTOS.size()).isEqualTo(5);
        fileInfoRepository.deleteAll();
    }

    @Test
    public void updateFileInfo() throws Exception {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName("File_Info_Test_1");
        fileInfo.setPath("/path/file_1.jpg");
        fileInfo.setMineType("jpg");
        fileInfo.setCheckSum("CheckSum1");
        fileInfoRepository.save(fileInfo);

        FileInfoDTO fileInfoDTO = new FileInfoDTO(fileInfo);
        fileInfoDTO.setPath("/path/file_2.jpg");
        fileInfoService.updateFileInfo(fileInfoDTO);
        Optional<FileInfo> maybeArea = fileInfoRepository.findOneByPath("/path/file_1.jpg");
        assertThat(maybeArea.isPresent()).isFalse();
        maybeArea = fileInfoRepository.findOneByPath("/path/file_2.jpg");
        assertThat(maybeArea.isPresent()).isTrue();
        fileInfoRepository.deleteAll();
    }

    @Test
    public void deleteFileInfo() throws Exception {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName("File_Info_Test_1");
        fileInfo.setPath("/path/file_1.jpg");
        fileInfo.setMineType("jpg");
        fileInfo.setCheckSum("CheckSum1");
        fileInfoRepository.save(fileInfo);

        Optional<FileInfo> maybeArea = fileInfoRepository.findOneByPath("/path/file_1.jpg");
        assertThat(maybeArea.isPresent()).isTrue();
        FileInfoDTO fileInfoDTO = new FileInfoDTO(fileInfo);
        fileInfoService.deleteFileInfo(fileInfoDTO);
        maybeArea = fileInfoRepository.findOneByPath("/path/file_1.jpg");
        assertThat(maybeArea.isPresent()).isFalse();
        fileInfoRepository.deleteAll();
    }

}
