package com.avonniv.service;

import com.avonniv.domain.Investor;
import com.avonniv.domain.User;
import com.avonniv.repository.InvestorRepository;
import com.avonniv.service.dto.InvestorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InvestorService {

    private final Logger log = LoggerFactory.getLogger(InvestorService.class);

    private final InvestorRepository investorRepository;
    private final UserService userService;

    public InvestorService(InvestorRepository investorRepository,
                           UserService userService) {
        this.investorRepository = investorRepository;
        this.userService = userService;
    }

    public Page<InvestorDTO> getAllInvestor(Pageable pageable) {
        return investorRepository.findAll(pageable).map(InvestorDTO::new);
    }

    public Investor create(InvestorDTO investorDTO) {
        User user = userService.getUserWithAuthorities();
        Investor investor = new Investor();
        investor.setValue(investorDTO.getValue());
        investor.setTitle(investorDTO.getTitle());
        investor.setDescription(investorDTO.getDescription());
        investor.setFinancing(investorDTO.getFinancing());
        investor.setApplyBy(investorDTO.getApplyBy());
        investor.setStatus(Investor.Status.getStatusByName(investorDTO.getStatus()).getValue());
        investor.setUser(user);
        return investorRepository.save(investor);
    }

    public void delete(Long id) {
        investorRepository.delete(id);
    }
}
