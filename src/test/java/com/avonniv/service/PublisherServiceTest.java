package com.avonniv.service;

import com.avonniv.AvonnivApp;
import com.avonniv.domain.Publisher;
import com.avonniv.repository.PublisherRepository;
import com.avonniv.service.dto.PublisherDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AvonnivApp.class)
@Transactional
public class PublisherServiceTest {

    private PublisherService publisherService;

    @Autowired
    private PublisherRepository publisherRepository;

    @Before
    public void setup() {
        publisherService = new PublisherService(publisherRepository);
    }
    @Test
    public void createPublisher() throws Exception {
        assertThat(publisherRepository.findOneByName("Publisher_Test_1").isPresent()).isFalse();
        PublisherDTO publisherDTO = new PublisherDTO(
            null,
            null,
            null,
            0,
            "Publisher_Test_1",
            "Description",
            "address",
            "email@email.com",
            "09876454321",
            "google.com"
        );

        publisherService.createPublisher(publisherDTO);
        assertThat(publisherRepository.findOneByName("Publisher_Test_1").isPresent()).isTrue();
        publisherRepository.deleteAll();
    }

    @Test
    public void getAll() throws Exception {
//        publisherRepository.deleteAll();
//        List<PublisherDTO> publisherDTOS = publisherService.getAll();
//        assertThat(publisherDTOS.size()).isEqualTo(0);
//        for (int i = 0; i < 4; i++) {
//            Publisher publisher = new Publisher();
//            publisher.setName("Publisher_Test_" + (i + 1));
//            publisher.setDescription("Description" + (i + 1));
//            publisher.setAddress("address" + (i + 1));
//            publisher.setEmail("email@email.com" + (i + 1));
//            publisher.setPhone("09876454321" + (i + 1));
//            publisher.setUrl("google.com" + (i + 1));
//            publisherRepository.save(publisher);
//        }
//
//        publisherDTOS = publisherService.getAll();
//        assertThat(publisherDTOS.size()).isEqualTo(4);
//        assertThat(publisherDTOS.get(0).getName()).isEqualTo("Publisher_Test_1");
//        assertThat(publisherDTOS.get(1).getName()).isEqualTo("Publisher_Test_2");
//
//        Publisher publisher = new Publisher();
//        publisher.setName("Publisher_Test_" + 5);
//        publisherRepository.save(publisher);
//
//        publisherDTOS = publisherService.getAll();
//        assertThat(publisherDTOS.size()).isEqualTo(5);
//        publisherRepository.deleteAll();
    }

    @Test
    public void updatePublisher() throws Exception {
        Publisher publisher = new Publisher();
        publisher.setName("Publisher_Test_1");
        publisher.setDescription("Description");
        publisher.setAddress("address");
        publisher.setEmail("email@email.com");
        publisher.setPhone("09876454321");
        publisher.setUrl("google.com");
        publisherRepository.save(publisher);

        assertThat(publisherRepository.findOneByName("Publisher_Test_1").isPresent()).isTrue();
        PublisherDTO publisherDTO = new PublisherDTO(publisher);
        publisherDTO.setName("Publisher_Test_2");
        publisherService.updatePublisher(publisherDTO);
        assertThat(publisherRepository.findOneByName("Publisher_Test_1").isPresent()).isFalse();
        assertThat(publisherRepository.findOneByName("Publisher_Test_2").isPresent()).isTrue();
        publisherRepository.deleteAll();
    }

    @Test
    public void deletePublisher() throws Exception {
        Publisher publisher = new Publisher();
        publisher.setName("Publisher_Test_1");
        publisher.setDescription("Description");
        publisher.setAddress("address");
        publisher.setEmail("email@email.com");
        publisher.setPhone("09876454321");
        publisher.setUrl("google.com");
        publisherRepository.save(publisher);

        assertThat(publisherRepository.findOneByName("Publisher_Test_1").isPresent()).isTrue();
        PublisherDTO publisherDTO = new PublisherDTO(publisher);
        publisherService.deletePublisher(publisherDTO);
        assertThat(publisherRepository.findOneByName("Publisher_Test_1").isPresent()).isFalse();
        publisherRepository.deleteAll();
    }

}
