package com.avonniv.service;

import com.avonniv.AvonnivApp;
import com.avonniv.domain.Area;
import com.avonniv.repository.AreaRepository;
import com.avonniv.service.dto.AreaDTO;
import org.junit.After;
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
public class AreaServiceTest {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private AreaService areaService;

    @After
    public void tearDown() throws Exception {
        areaRepository.deleteAll();
    }

    @Test
    public void createArea() throws Exception {
        Optional<Area> maybeArea = areaService.getByName("Area_Test");
        assertThat(maybeArea.isPresent()).isFalse();
        Area area = areaService.createArea("Area_Test");
        maybeArea = areaService.getByName("Area_Test");
        assertThat(maybeArea.isPresent()).isTrue();
        areaRepository.delete(area);
    }

    @Test
    public void getAll() throws Exception {
        List<AreaDTO> areaDTOS = areaService.getAll();
        assertThat(areaDTOS.size()).isEqualTo(0);
        for (int i = 0; i < 4; i++) {
            Area area = new Area();
            area.setName("Area_Test_" + (i + 1));
            areaRepository.save(area);
        }

        areaDTOS = areaService.getAll();
        assertThat(areaDTOS.size()).isEqualTo(4);
        assertThat(areaDTOS.get(0).getName()).isEqualTo("Area_Test_1");
        assertThat(areaDTOS.get(1).getName()).isEqualTo("Area_Test_2");
        Area area = new Area();
        area.setName("Area_Test_5");
        areaRepository.save(area);
        areaDTOS = areaService.getAll();
        assertThat(areaDTOS.size()).isEqualTo(5);
        areaRepository.deleteAll();
    }

    @Test
    public void getByName() throws Exception {
        Optional<Area> maybeArea = areaService.getByName("Area_Test");
        assertThat(maybeArea.isPresent()).isFalse();

        Area area = new Area();
        area.setName("Area_Test");
        areaRepository.save(area);

        maybeArea = areaService.getByName("Area_Test");
        assertThat(maybeArea.isPresent()).isTrue();

        areaRepository.deleteAll();
    }

    @Test
    public void updateArea() throws Exception {
        Area area = new Area();
        area.setName("Area_Test");
        area = areaRepository.save(area);
        AreaDTO areaDTO = new AreaDTO(area);
        areaDTO.setName("Area_Test_2");
        areaService.updateArea(areaDTO);
        Optional<Area> maybeArea = areaService.getByName("Area_Test");
        assertThat(maybeArea.isPresent()).isFalse();
        maybeArea = areaService.getByName("Area_Test_2");
        assertThat(maybeArea.isPresent()).isTrue();
        areaRepository.deleteAll();
    }

    @Test
    public void deleteArea() throws Exception {
        Area area = new Area();
        area.setName("Area_Test");
        areaRepository.save(area);
        Optional<Area> maybeArea = areaService.getByName("Area_Test");
        assertThat(maybeArea.isPresent()).isTrue();
        areaService.delete("Area_Test");
        maybeArea = areaService.getByName("Area_Test");
        assertThat(maybeArea.isPresent()).isFalse();
        areaRepository.deleteAll();
    }

}
