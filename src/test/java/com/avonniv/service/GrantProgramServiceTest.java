package com.avonniv.service;

import com.avonniv.AvonnivApp;
import com.avonniv.domain.Area;
import com.avonniv.domain.GrantProgram;
import com.avonniv.domain.Publisher;
import com.avonniv.repository.AreaRepository;
import com.avonniv.repository.GrantProgramRepository;
import com.avonniv.repository.PublisherRepository;
import com.avonniv.service.dto.AreaDTO;
import com.avonniv.service.dto.GrantProgramDTO;
import com.avonniv.service.dto.PublisherDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AvonnivApp.class)
@Transactional
public class GrantProgramServiceTest {
    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private GrantProgramRepository grantProgramRepository;

    @Autowired
    private GrantProgramService grantProgramService;

    private PublisherDTO publisherDTO;

    private Set<AreaDTO> areaDTOS = new HashSet<>();

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < 4; i++) {
            Area area = new Area();
            area.setName("Area_Test_" + (i + 1));
            areaDTOS.add(new AreaDTO(areaRepository.save(area)));
        }
        Publisher publisher = new Publisher();
        publisher.setName("Publisher_Test_1");
        publisher.setDescription("Description");
        publisher.setAddress("address");
        publisher.setEmail("email@email.com");
        publisher.setPhone("09876454321");
        publisher.setUrl("google.com");

        publisherDTO = new PublisherDTO(publisherRepository.save(publisher));
    }

    @After
    public void tearDown() throws Exception {
        areaRepository.deleteAll();
        publisherRepository.deleteAll();
    }

    @Test
    public void createGrant() throws Exception {
        Set<AreaDTO> areaDTOSNew = new HashSet<>();
        Set<AreaDTO> areaDTOSNew2 = new HashSet<>();
        final int[] i = {0};
        areaDTOS.forEach(areaDTO -> {
            if (i[0] % 2 == 0) {
                areaDTOSNew.add(areaDTO);
            } else {
                areaDTOSNew2.add(areaDTO);
            }
            i[0]++;
        });
        GrantProgramDTO grantProgramDTO = new GrantProgramDTO(
            null, null, null, 0,
            "GrantProgram",
            "GrantProgram description",
            GrantProgram.Type.PUBLIC.getValue(),
            publisherDTO,
            areaDTOSNew,
            null,
            null
        );
        grantProgramService.createGrant(grantProgramDTO);
        grantProgramService.createGrant(grantProgramDTO);
        GrantProgramDTO grantProgramDTO2 = new GrantProgramDTO(
            null, null, null, 0,
            "GrantProgram",
            "GrantProgram description",
            GrantProgram.Type.PUBLIC.getValue(),
            publisherDTO,
            areaDTOSNew2,
            null,
            null
        );
        grantProgramService.createGrant(grantProgramDTO2);
        Iterator<AreaDTO> iterator = areaDTOS.iterator();
        assertThat(grantProgramRepository.findAllByAreasContains(areaRepository.findOne(iterator.next().getId())).size()).isEqualTo(2);
        assertThat(grantProgramRepository.findAllByAreasContains(areaRepository.findOne(iterator.next().getId())).size()).isEqualTo(1);

        grantProgramRepository.deleteAll();
    }

    @Test
    public void getAll() throws Exception {
        for (int i = 0; i < 4; i++) {
            GrantProgram grantProgram = new GrantProgram();
            grantProgram.setAreas(areaDTOS
                .stream()
                .map(areaDTO ->
                    areaRepository.findOne(areaDTO.getId()))
                .collect(Collectors.toSet()));
            grantProgram.setPublisher(publisherRepository.findOne(publisherDTO.getId()));
            grantProgram.setName("GrantProgram" + (i + 1));
            grantProgram.setDescription("GrantProgram description");
            grantProgram.setType(GrantProgram.Type.PUBLIC.getValue());
            grantProgramRepository.save(grantProgram);
        }
        List<GrantProgramDTO> grantList = grantProgramService.getAll();
        assertThat(grantList.get(0).getName()).isEqualTo("GrantProgram1");
        assertThat(grantList.get(1).getName()).isEqualTo("GrantProgram2");
        assertThat(grantList.size()).isEqualTo(4);
        grantProgramRepository.deleteAll();
    }

    @Test
    public void updateGrant() throws Exception {
        GrantProgram grantProgram = new GrantProgram();
        grantProgram.setAreas(areaDTOS
            .stream()
            .map(areaDTO ->
                areaRepository.findOne(areaDTO.getId()))
            .collect(Collectors.toSet()));
        grantProgram.setPublisher(publisherRepository.findOne(publisherDTO.getId()));
        grantProgram.setName("GrantProgram");
        grantProgram.setDescription("GrantProgram description");
        grantProgram.setType(GrantProgram.Type.PUBLIC.getValue());
        grantProgramRepository.save(grantProgram);

        GrantProgramDTO grantProgramDTO = new GrantProgramDTO(grantProgram);
        grantProgramDTO.setName("GrantProgram2");
        grantProgramService.updateGrant(grantProgramDTO);
        Optional<GrantProgram> maybeGrantDTO = grantProgramRepository.findOneByName("GrantProgram");
        assertThat(maybeGrantDTO.isPresent()).isFalse();
        maybeGrantDTO = grantProgramRepository.findOneByName("GrantProgram2");
        assertThat(maybeGrantDTO.isPresent()).isTrue();
        grantProgramRepository.deleteAll();
    }

    @Test
    public void deleteGrant() throws Exception {
        GrantProgram grantProgram = new GrantProgram();
        grantProgram.setAreas(areaDTOS
            .stream()
            .map(areaDTO ->
                areaRepository.findOne(areaDTO.getId()))
            .collect(Collectors.toSet()));
        grantProgram.setPublisher(publisherRepository.findOne(publisherDTO.getId()));
        grantProgram.setName("GrantProgram");
        grantProgram.setDescription("GrantProgram description");
        grantProgram.setType(GrantProgram.Type.PUBLIC.getValue());
        grantProgramRepository.save(grantProgram);

        Optional<GrantProgram> maybeGrantDTO = grantProgramRepository.findOneByName("GrantProgram");
        assertThat(maybeGrantDTO.isPresent()).isTrue();

        GrantProgramDTO grantProgramDTO = new GrantProgramDTO(grantProgram);
        grantProgramService.deleteGrant(grantProgramDTO);

        maybeGrantDTO = grantProgramRepository.findOneByName("GrantProgram");
        assertThat(maybeGrantDTO.isPresent()).isFalse();

        grantProgramRepository.deleteAll();
    }

}
