package com.avonniv.web.rest;

import com.avonniv.repository.UserRepository;
import com.avonniv.service.InvestorService;
import com.avonniv.service.MailService;
import com.avonniv.service.dto.InvestorDTO;
import com.avonniv.web.rest.util.PaginationUtil;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class InvestorResource {

    private final Logger log = LoggerFactory.getLogger(InvestorResource.class);

    private final UserRepository userRepository;

    private final MailService mailService;

    private final InvestorService investorService;

    public InvestorResource(UserRepository userRepository, MailService mailService,
                            InvestorService investorService) {

        this.userRepository = userRepository;
        this.mailService = mailService;
        this.investorService = investorService;
    }

    /**
     * GET  /users : get all users.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body all users
     */
    @GetMapping("/investors")
    @Timed
    public ResponseEntity<List<InvestorDTO>> getAllInvestor(@ApiParam Pageable pageable) {
        List<Long> listId = new ArrayList<>();
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 1", "Title 1", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 2", "Title 2", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        listId.add(investorService.create(new InvestorDTO(null, "Title 3", "Title 3", "Description",
            "Financing", "Apply by", "open", null,
            null, null, null)).getId());
        final Page<InvestorDTO> page = investorService.getAllInvestor(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/investors");
        for (Long id : listId) {
            investorService.delete(id);
        }
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
