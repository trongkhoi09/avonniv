package com.avonniv.service;

import com.avonniv.AvonnivApp;
import com.avonniv.domain.Area;
import com.avonniv.domain.Grant;
import com.avonniv.domain.Publisher;
import com.avonniv.repository.AreaRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.repository.PublisherRepository;
import com.avonniv.service.dto.AreaDTO;
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
public class GrantServiceTest {
    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private GrantRepository grantRepository;

    @Autowired
    private GrantService grantService;

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
        GrantDTO grantDTO = new GrantDTO(
            null, null, null, 0,
            "Grant",
            "Grant description",
            Grant.Type.PUBLIC.getValue(),
            publisherDTO,
            areaDTOSNew
        );
        grantService.createGrant(grantDTO);
        grantService.createGrant(grantDTO);
        GrantDTO grantDTO2 = new GrantDTO(
            null, null, null, 0,
            "Grant",
            "Grant description",
            Grant.Type.PUBLIC.getValue(),
            publisherDTO,
            areaDTOSNew2
        );
        grantService.createGrant(grantDTO2);
        Iterator<AreaDTO> iterator = areaDTOS.iterator();
        assertThat(grantRepository.findAllByAreasContains(areaRepository.findOne(iterator.next().getId())).size()).isEqualTo(2);
        assertThat(grantRepository.findAllByAreasContains(areaRepository.findOne(iterator.next().getId())).size()).isEqualTo(1);

        grantRepository.deleteAll();
    }

    @Test
    public void getAll() throws Exception {
        for (int i = 0; i < 4; i++) {
            Grant grant = new Grant();
            grant.setAreas(areaDTOS
                .stream()
                .map(areaDTO ->
                    areaRepository.findOne(areaDTO.getId()))
                .collect(Collectors.toSet()));
            grant.setPublisher(publisherRepository.findOne(publisherDTO.getId()));
            grant.setName("Grant" + (i + 1));
            grant.setDescription("Grant description");
            grant.setType(Grant.Type.PUBLIC.getValue());
            grantRepository.save(grant);
        }
        List<GrantDTO> grantList = grantService.getAll();
        assertThat(grantList.get(0).getName()).isEqualTo("Grant1");
        assertThat(grantList.get(1).getName()).isEqualTo("Grant2");
        assertThat(grantList.size()).isEqualTo(4);
        grantRepository.deleteAll();
    }

    @Test
    public void updateGrant() throws Exception {
        Grant grant = new Grant();
        grant.setAreas(areaDTOS
            .stream()
            .map(areaDTO ->
                areaRepository.findOne(areaDTO.getId()))
            .collect(Collectors.toSet()));
        grant.setPublisher(publisherRepository.findOne(publisherDTO.getId()));
        grant.setName("Grant");
        grant.setDescription("Grant description");
        grant.setType(Grant.Type.PUBLIC.getValue());
        grantRepository.save(grant);

        GrantDTO grantDTO = new GrantDTO(grant);
        grantDTO.setName("Grant2");
        grantService.updateGrant(grantDTO);
        Optional<Grant> maybeGrantDTO = grantRepository.findOneByName("Grant");
        assertThat(maybeGrantDTO.isPresent()).isFalse();
        maybeGrantDTO = grantRepository.findOneByName("Grant2");
        assertThat(maybeGrantDTO.isPresent()).isTrue();
        grantRepository.deleteAll();
    }

    @Test
    public void deleteGrant() throws Exception {
        Grant grant = new Grant();
        grant.setAreas(areaDTOS
            .stream()
            .map(areaDTO ->
                areaRepository.findOne(areaDTO.getId()))
            .collect(Collectors.toSet()));
        grant.setPublisher(publisherRepository.findOne(publisherDTO.getId()));
        grant.setName("Grant");
        grant.setDescription("Grant description");
        grant.setType(Grant.Type.PUBLIC.getValue());
        grantRepository.save(grant);

        Optional<Grant> maybeGrantDTO = grantRepository.findOneByName("Grant");
        assertThat(maybeGrantDTO.isPresent()).isTrue();

        GrantDTO grantDTO = new GrantDTO(grant);
        grantService.deleteGrant(grantDTO);

        maybeGrantDTO = grantRepository.findOneByName("Grant");
        assertThat(maybeGrantDTO.isPresent()).isFalse();

        grantRepository.deleteAll();
    }

}
