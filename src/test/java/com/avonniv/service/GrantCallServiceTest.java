package com.avonniv.service;

import com.avonniv.AvonnivApp;
import com.avonniv.domain.Area;
import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantCall;
import com.avonniv.domain.Publisher;
import com.avonniv.repository.AreaRepository;
import com.avonniv.repository.GrantCallRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.repository.PublisherRepository;
import com.avonniv.service.dto.AreaDTO;
import com.avonniv.service.dto.GrantCallDTO;
import com.avonniv.service.dto.GrantDTO;
import com.avonniv.service.dto.PublisherDTO;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AvonnivApp.class)
@Transactional
public class GrantCallServiceTest {
    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private GrantCallRepository grantCallRepository;

    @Autowired
    private GrantRepository grantRepository;

    @Autowired
    private GrantCallService grantCallService;

    private GrantDTO grantDTO;

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
        grantDTO = new GrantDTO(grantRepository.save(grant));
    }

    @After
    public void tearDown() throws Exception {
        grantCallRepository.deleteAll();
        grantRepository.deleteAll();
        publisherRepository.deleteAll();
        areaRepository.deleteAll();
    }

    @Test
    public void createGrantCall() throws Exception {
        assertThat(grantCallRepository.findOneByTitle("Title grant call").isPresent()).isFalse();
        GrantCallDTO grantCallDTO = new GrantCallDTO(
            null, null, null, 0,
            grantDTO,
            "Title grant call",
            "Excerpt grant call",
            "Description grant call",
            Instant.now(),
            Instant.now(),
            Instant.now(),
            Instant.now()
        );
        grantCallService.createGrantCall(grantCallDTO);
        assertThat(grantCallRepository.findOneByTitle("Title grant call").isPresent()).isTrue();
        grantCallRepository.deleteAll();
    }

    @Test
    public void getAll() throws Exception {
        assertThat(grantCallService.getAll().size()).isEqualTo(0);
        for (int i = 0; i < 4; i++) {
            GrantCall grantCall = new GrantCall();
            grantCall.setGrant(grantRepository.findOne(grantDTO.getId()));
            grantCall.setTitle("Title grant call" + (i + 1));
            grantCall.setExcerpt("Excerpt grant call" + (i + 1));
            grantCall.setDescription("Description grant call" + (i + 1));
            grantCall.setOpenDate(Instant.now());
            grantCall.setCloseDate(Instant.now());
            grantCall.setAnnouncedDate(Instant.now());
            grantCall.setProjectStartDate(Instant.now());
            grantCallRepository.save(grantCall);
        }
        assertThat(grantCallService.getAll().size()).isEqualTo(4);
        grantCallRepository.deleteAll();
    }

    @Test
    public void updateGrantCall() throws Exception {
        GrantCall grantCall = new GrantCall();
        grantCall.setGrant(grantRepository.findOne(grantDTO.getId()));
        grantCall.setTitle("Title grant call");
        grantCall.setExcerpt("Excerpt grant call");
        grantCall.setDescription("Description grant call");
        grantCall.setOpenDate(Instant.now());
        grantCall.setCloseDate(Instant.now());
        grantCall.setAnnouncedDate(Instant.now());
        grantCall.setProjectStartDate(Instant.now());
        GrantCallDTO grantCallDTO = new GrantCallDTO(grantCallRepository.save(grantCall));
        assertThat(grantCallRepository.findOneByTitle("Title grant call").isPresent()).isTrue();
        grantCallDTO.setTitle("Title grant call change");

        grantCallService.updateGrantCall(grantCallDTO);

        assertThat(grantCallRepository.findOneByTitle("Title grant call").isPresent()).isFalse();
        assertThat(grantCallRepository.findOneByTitle("Title grant call change").isPresent()).isTrue();
        grantCallRepository.deleteAll();
    }

    @Test
    public void deleteGrantCall() throws Exception {
        GrantCall grantCall = new GrantCall();
        grantCall.setGrant(grantRepository.findOne(grantDTO.getId()));
        grantCall.setTitle("Title grant call");
        grantCall.setExcerpt("Excerpt grant call");
        grantCall.setDescription("Description grant call");
        grantCall.setOpenDate(Instant.now());
        grantCall.setCloseDate(Instant.now());
        grantCall.setAnnouncedDate(Instant.now());
        grantCall.setProjectStartDate(Instant.now());
        GrantCallDTO grantCallDTO = new GrantCallDTO(grantCallRepository.save(grantCall));
        assertThat(grantCallRepository.findOneByTitle("Title grant call").isPresent()).isTrue();

        grantCallService.deleteGrantCall(grantCallDTO);

        assertThat(grantCallRepository.findOneByTitle("Title grant call").isPresent()).isFalse();
        grantCallRepository.deleteAll();
    }

}
