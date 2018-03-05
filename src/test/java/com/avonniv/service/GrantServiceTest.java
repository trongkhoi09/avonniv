package com.avonniv.service;

import com.avonniv.AvonnivApp;
import com.avonniv.domain.Area;
import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantProgram;
import com.avonniv.domain.Publisher;
import com.avonniv.repository.AreaRepository;
import com.avonniv.repository.GrantProgramRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.repository.PublisherRepository;
import com.avonniv.service.dto.GrantDTO;
import com.avonniv.service.dto.GrantProgramDTO;
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
public class GrantServiceTest {
    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private GrantRepository grantRepository;

    @Autowired
    private GrantProgramRepository grantProgramRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PreferencesService preferencesService;

    @Autowired
    private MailService mailService;

    private GrantService grantService;

    private GrantProgramDTO grantProgramDTO;

    @Before
    public void setUp() throws Exception {
        grantService = new GrantService(grantRepository, userService, mailService, preferencesService, grantProgramRepository);
        Set<Area> areas = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            Area area = new Area();
            area.setName("Area_Test_" + (i + 1));
            areas.add(areaRepository.save(area));
        }
        Publisher publisher = new Publisher();
        publisher.setName("Publisher_Test_1");
        publisher.setDescriptionSWE("Description");
        publisher.setDescriptionEN("beskrivning");
        publisher.setAddress("address");
        publisher.setEmail("email@email.com");
        publisher.setPhone("09876454321");
        publisher.setUrl("google.com");

        publisher = publisherRepository.save(publisher);

        GrantProgram grantProgram = new GrantProgram();
        grantProgram.setAreas(areas);
        grantProgram.setPublisher(publisher);
        grantProgram.setName("GrantProgram");
        grantProgram.setDescription("GrantProgram description");
        grantProgram.setType(GrantProgram.Type.PUBLIC.getValue());
        grantProgramDTO = new GrantProgramDTO(grantProgramRepository.save(grantProgram));
    }

    @After
    public void tearDown() throws Exception {
        grantRepository.deleteAll();
        grantProgramRepository.deleteAll();
        publisherRepository.deleteAll();
        areaRepository.deleteAll();
    }

    @Test
    public void createGrantCall() throws Exception {
        assertThat(grantRepository.findOneByTitle("Title grant call").isPresent()).isFalse();
        GrantDTO grantDTO = new GrantDTO(
            null, null, null, 0,
            grantProgramDTO,
            "Title grant call",
            "Excerpt grant call",
            "Description grant call",
            Instant.now(),
            Instant.now(),
            Instant.now(),
            Instant.now(),
            null,
            null,
            null,
            null
        );
        grantService.createGrantCall(grantDTO);
        assertThat(grantRepository.findOneByTitle("Title grant call").isPresent()).isTrue();
        grantRepository.deleteAll();
    }

    @Test
    public void getAll() throws Exception {
//        grantRepository.deleteAll();
//        assertThat(grantService.getAll().size()).isEqualTo(0);
//        for (int i = 0; i < 4; i++) {
//            Grant grant = new Grant();
//            grant.setGrantProgram(grantProgramRepository.findOne(grantProgramDTO.getId()));
//            grant.setTitle("Title grant call" + (i + 1));
//            grant.setExcerpt("Excerpt grant call" + (i + 1));
//            grant.setDescription("Description grant call" + (i + 1));
//            grant.setOpenDate(Instant.now());
//            grant.setCloseDate(Instant.now());
//            grant.setAnnouncedDate(Instant.now());
//            grant.setProjectStartDate(Instant.now());
//            grantRepository.save(grant);
//        }
//        assertThat(grantService.getAll().size()).isEqualTo(4);
//        grantRepository.deleteAll();
    }

    @Test
    public void updateGrantCall() throws Exception {
        Grant grant = new Grant();
        grant.setGrantProgram(grantProgramRepository.findOne(grantProgramDTO.getId()));
        grant.setTitle("Title grant call");
        grant.setExcerpt("Excerpt grant call");
        grant.setDescription("Description grant call");
        grant.setOpenDate(Instant.now());
        grant.setCloseDate(Instant.now());
        grant.setAnnouncedDate(Instant.now());
        grant.setProjectStartDate(Instant.now());
        GrantDTO grantDTO = new GrantDTO(grantRepository.save(grant));
        assertThat(grantRepository.findOneByTitle("Title grant call").isPresent()).isTrue();
        grantDTO.setTitle("Title grant call change");

        grantService.update(grantDTO);

        assertThat(grantRepository.findOneByTitle("Title grant call").isPresent()).isFalse();
        assertThat(grantRepository.findOneByTitle("Title grant call change").isPresent()).isTrue();
        grantRepository.deleteAll();
    }

    @Test
    public void deleteGrantCall() throws Exception {
        Grant grant = new Grant();
        grant.setGrantProgram(grantProgramRepository.findOne(grantProgramDTO.getId()));
        grant.setTitle("Title grant call");
        grant.setExcerpt("Excerpt grant call");
        grant.setDescription("Description grant call");
        grant.setOpenDate(Instant.now());
        grant.setCloseDate(Instant.now());
        grant.setAnnouncedDate(Instant.now());
        grant.setProjectStartDate(Instant.now());
        GrantDTO grantDTO = new GrantDTO(grantRepository.save(grant));
        assertThat(grantRepository.findOneByTitle("Title grant call").isPresent()).isTrue();

        grantService.deleteGrantCall(grantDTO);

        assertThat(grantRepository.findOneByTitle("Title grant call").isPresent()).isFalse();
        grantRepository.deleteAll();
    }

}
