package com.avonniv.service;

import com.avonniv.AvonnivApp;
import com.avonniv.domain.Area;
import com.avonniv.domain.CallDescription;
import com.avonniv.domain.FileInfo;
import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantCall;
import com.avonniv.domain.Publisher;
import com.avonniv.repository.AreaRepository;
import com.avonniv.repository.CallDescriptionRepository;
import com.avonniv.repository.FileInfoRepository;
import com.avonniv.repository.GrantCallRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.repository.PublisherRepository;
import com.avonniv.service.dto.CallDescriptionDTO;
import com.avonniv.service.dto.FileInfoDTO;
import com.avonniv.service.dto.GrantCallDTO;
import com.avonniv.service.dto.GrantDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AvonnivApp.class)
@Transactional
public class CallDescriptionServiceTest {
    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private GrantCallRepository grantCallRepository;

    @Autowired
    private GrantRepository grantRepository;
    @Autowired
    private CallDescriptionService callDescriptionService;

    @Autowired
    private CallDescriptionRepository callDescriptionRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;


    private GrantCallDTO grantCallDTO;

    private FileInfoDTO fileInfoDTO;

    @Before
    public void setUp() throws Exception {
        Set<Area> areas = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            Area area = new Area();
            area.setName("Area_Test_" + (i + 1));
            areas.add(areaRepository.save(area));
        }
        Publisher publisher = new Publisher();
        publisher.setName("Publisher_Test_1");
        publisher.setDescription("Description");
        publisher.setAddress("address");
        publisher.setEmail("email@email.com");
        publisher.setPhone("09876454321");
        publisher.setUrl("google.com");

        publisher = publisherRepository.save(publisher);

        Grant grant = new Grant();
        grant.setAreas(areas);
        grant.setPublisher(publisher);
        grant.setName("Grant");
        grant.setDescription("Grant description");
        grant.setType(Grant.Type.PUBLIC.getValue());
        grant = grantRepository.save(grant);

        GrantCall grantCall = new GrantCall();
        grantCall.setGrant(grant);
        grantCall.setTitle("Title grant call");
        grantCall.setExcerpt("Excerpt grant call");
        grantCall.setDescription("Description grant call");
        grantCall.setOpenDate(Instant.now());
        grantCall.setCloseDate(Instant.now());
        grantCall.setAnnouncedDate(Instant.now());
        grantCall.setProjectStartDate(Instant.now());
        grantCallDTO = new GrantCallDTO(grantCallRepository.save(grantCall));

        FileInfo fileInfo = new FileInfo();
        fileInfo.setName("File_Info_Test_1");
        fileInfo.setPath("/path/file_1.jpg");
        fileInfo.setMineType("jpg");
        fileInfo.setCheckSum("CheckSum1");
        fileInfoDTO = new FileInfoDTO(fileInfoRepository.save(fileInfo));
    }

    @After
    public void tearDown() throws Exception {
        callDescriptionRepository.deleteAll();
        grantCallRepository.deleteAll();
        grantRepository.deleteAll();
        publisherRepository.deleteAll();
        areaRepository.deleteAll();
    }

    @Test
    public void createCallDescription() throws Exception {
        assertThat(callDescriptionRepository.findOneByTitle("Title Call Description").isPresent()).isFalse();
        CallDescriptionDTO callDescriptionDTO = new CallDescriptionDTO(
            null,
            null,
            null,
            0,
            "Title Call Description",
            "Description",
            grantCallDTO,
            fileInfoDTO
        );
        callDescriptionService.createCallDescription(callDescriptionDTO);
        assertThat(callDescriptionRepository.findOneByTitle("Title Call Description").isPresent()).isTrue();
        callDescriptionRepository.deleteAll();
    }

    @Test
    public void getAll() throws Exception {
        assertThat(callDescriptionService.getAll().size()).isEqualTo(0);
        for (int i = 0; i < 4; i++) {
            CallDescription callDescription = new CallDescription();
            callDescription.setTitle("Title Call Description" + (i + 1));
            callDescription.setDescription("Description" + (i + 1));
            callDescription.setGrantCall(grantCallRepository.findOne(grantCallDTO.getId()));
            callDescription.setFileInfo(fileInfoRepository.findOne(fileInfoDTO.getId()));
            callDescriptionRepository.save(callDescription);
        }
        assertThat(callDescriptionService.getAll().size()).isEqualTo(4);
        callDescriptionRepository.deleteAll();
    }

    @Test
    public void updateCallDescription() throws Exception {
        CallDescription callDescription = new CallDescription();
        callDescription.setTitle("Title Call Description");
        callDescription.setDescription("Description");
        callDescription.setGrantCall(grantCallRepository.findOne(grantCallDTO.getId()));
        callDescription.setFileInfo(fileInfoRepository.findOne(fileInfoDTO.getId()));
        CallDescriptionDTO callDescriptionDTO = new CallDescriptionDTO(callDescriptionRepository.save(callDescription));

        assertThat(callDescriptionRepository.findOneByTitle("Title Call Description").isPresent()).isTrue();

        callDescriptionDTO.setTitle("Title Call Description Change");
        callDescriptionService.updateCallDescription(callDescriptionDTO);

        assertThat(callDescriptionRepository.findOneByTitle("Title Call Description").isPresent()).isFalse();
        assertThat(callDescriptionRepository.findOneByTitle("Title Call Description Change").isPresent()).isTrue();

        callDescriptionRepository.deleteAll();
    }

    @Test
    public void deleteCallDescription() throws Exception {
        CallDescription callDescription = new CallDescription();
        callDescription.setTitle("Title Call Description");
        callDescription.setDescription("Description");
        callDescription.setGrantCall(grantCallRepository.findOne(grantCallDTO.getId()));
        callDescription.setFileInfo(fileInfoRepository.findOne(fileInfoDTO.getId()));

        CallDescriptionDTO callDescriptionDTO = new CallDescriptionDTO(callDescriptionRepository.save(callDescription));

        assertThat(callDescriptionRepository.findOneByTitle("Title Call Description").isPresent()).isTrue();

        callDescriptionService.deleteCallDescription(callDescriptionDTO);

        assertThat(callDescriptionRepository.findOneByTitle("Title Call Description").isPresent()).isFalse();

        callDescriptionRepository.deleteAll();
    }

}
