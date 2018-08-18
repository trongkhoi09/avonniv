package com.avonniv.service;

import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantFilter;
import com.avonniv.domain.GrantProgram;
import com.avonniv.domain.User;
import com.avonniv.repository.GrantProgramRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.service.dto.GrantDTO;
import com.avonniv.service.dto.PreferencesDTO;
import com.avonniv.service.fetchdata.Util;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class GrantService {

    private final Logger log = LoggerFactory.getLogger(GrantService.class);

    private final GrantRepository grantRepository;

    private final UserService userService;

    private final PreferencesService preferencesService;

    private final GrantProgramRepository grantProgramRepository;

    private final MailService mailService;

    public GrantService(GrantRepository grantRepository,
                        UserService userService,
                        MailService mailService,
                        PreferencesService preferencesService,
                        GrantProgramRepository grantProgramRepository) {
        this.grantRepository = grantRepository;
        this.grantProgramRepository = grantProgramRepository;
        this.mailService = mailService;
        this.userService = userService;
        this.preferencesService = preferencesService;
    }

    public Grant createGrantCall(GrantDTO grantDTO) {
        Grant newGrant = new Grant();
        newGrant.setTitle(grantDTO.getTitle());
        newGrant.setExcerpt(grantDTO.getExcerpt());
        newGrant.setDescription(grantDTO.getDescription());
        newGrant.setOpenDate(grantDTO.getOpenDate());
        newGrant.setCloseDate(grantDTO.getCloseDate());
        newGrant.setAnnouncedDate(grantDTO.getAnnouncedDate());
        newGrant.setProjectStartDate(grantDTO.getProjectStartDate());
        newGrant.setExternalId(grantDTO.getExternalId());
        newGrant.setExternalUrl(grantDTO.getExternalUrl());
        newGrant.setFinanceDescription(grantDTO.getFinanceDescription());
        GrantProgram grantProgram = grantProgramRepository.findOne(grantDTO.getGrantProgramDTO().getId());
        newGrant.setGrantProgram(grantProgram);
        newGrant.setStatus(grantDTO.getStatus());
        newGrant.setDataFromUrl(grantDTO.getDataFromUrl());

        grantRepository.save(newGrant);
        log.debug("Created Information for Grant: {}", newGrant);
        return newGrant;
    }

    public List<GrantDTO> getAll() {
        return grantRepository.findAll().stream().map(GrantDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Grant> getByExternalId(String externalId) {
        return grantRepository.findOneByExternalId(externalId);
    }


    @Transactional(readOnly = true)
    public List<Grant> getAllByGrantProgramIdAndStatus(Long grantProgramId, int status, List<Long> listIgnore) {
        return grantRepository.findAllByGrantProgramIdAndStatusAndIdIsNotIn(grantProgramId, status, listIgnore);
    }

    @Transactional(readOnly = true)
    public Optional<Grant> getById(Long id) {
        return Optional.of(grantRepository.findOne(id));
    }

    @Transactional(readOnly = true)
    public Optional<List<Grant>> getRecentGrants() {
        return Optional.of(grantRepository.findAllByStatusInOrderByCreatedDateDesc(
            new PageRequest(0, 4),
            Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue()))
        );
    }

    public Optional<GrantDTO> update(GrantDTO grantDTO) {
        return Optional.of(grantRepository
            .findOne(grantDTO.getId()))
            .map(grantCall -> {
                grantCall.setTitle(grantDTO.getTitle());
                grantCall.setExcerpt(grantDTO.getExcerpt());
                if (grantDTO.getDescription() != null && !grantDTO.getDescription().trim().isEmpty()) {
                    grantCall.setDescription(grantDTO.getDescription());
                }
                grantCall.setOpenDate(grantDTO.getOpenDate());
                grantCall.setCloseDate(grantDTO.getCloseDate());
                grantCall.setAnnouncedDate(grantDTO.getAnnouncedDate());
                grantCall.setProjectStartDate(grantDTO.getProjectStartDate());
                grantCall.setFinanceDescription(grantDTO.getFinanceDescription());
                grantCall.setExternalUrl(grantDTO.getExternalUrl());
                Util.setStatus(grantDTO, Instant.now());
                grantCall.setStatus(grantDTO.getStatus());
                grantCall.setDataFromUrl(grantDTO.getDataFromUrl());

                GrantProgram grantProgram = grantProgramRepository.findOne(grantDTO.getGrantProgramDTO().getId());
                grantCall.setGrantProgram(grantProgram);
                log.debug("Changed Information for Grant: {}", grantCall);
                return grantCall;
            })
            .map(GrantDTO::new);
    }

    public void deleteGrantCall(GrantDTO grantDTO) {
        grantRepository.delete(grantDTO.getId());
    }

    @Transactional(readOnly = true)
    public Page<GrantDTO> getAll(GrantFilter grantFilter, Pageable pageable) {
        if (grantFilter.getSearch() != null && !grantFilter.getSearch().trim().isEmpty()) {
            String searchDescription = "%" + formatStringSearch(grantFilter.getSearch().trim()) + "%";
            return grantRepository.findAllByTitleLikeAndStatusInOrDescriptionLikeAndStatusIn(pageable, "%" + grantFilter.getSearch().trim() + "%", Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue()), searchDescription, Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue())).map(GrantDTO::new);
        }
        List<String> listPublisher = new ArrayList<>();
        listPublisher.addAll(grantFilter.getPublisherDTOs());
        Sort.Order publisherOrder = pageable.getSort().getOrderFor("publisher");
        if (publisherOrder != null) {
            Iterator<Sort.Order> iterator = pageable.getSort().iterator();
            List<Sort.Order> result = new ArrayList<>();
            while (iterator.hasNext()) {
                Sort.Order order = iterator.next();
                if (!order.getProperty().equals("publisher")) {
                    result.add(order);
                }
            }
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(result));
            if (publisherOrder.isAscending()) {
                if (grantFilter.isOpenGrant()) {
                    if (grantFilter.isComingGrant()) {
                        return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusInOrderByGrantProgram_Publisher_NameAsc(pageable, listPublisher, Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue())).map(GrantDTO::new);
                    } else {
                        return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusInOrderByGrantProgram_Publisher_NameAsc(pageable, listPublisher, Collections.singletonList(GrantDTO.Status.open.getValue())).map(GrantDTO::new);
                    }
                } else {
                    if (grantFilter.isComingGrant()) {
                        return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusInOrderByGrantProgram_Publisher_NameAsc(pageable, listPublisher, Collections.singletonList(GrantDTO.Status.coming.getValue())).map(GrantDTO::new);
                    } else {
                        return new PageImpl<>(new ArrayList<>(), pageable, 0L);
                    }
                }
            } else {
                if (grantFilter.isOpenGrant()) {
                    if (grantFilter.isComingGrant()) {
                        return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusInOrderByGrantProgram_Publisher_NameDesc(pageable, listPublisher, Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue())).map(GrantDTO::new);
                    } else {
                        return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusInOrderByGrantProgram_Publisher_NameDesc(pageable, listPublisher, Collections.singletonList(GrantDTO.Status.open.getValue())).map(GrantDTO::new);
                    }
                } else {
                    if (grantFilter.isComingGrant()) {
                        return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusInOrderByGrantProgram_Publisher_NameDesc(pageable, listPublisher, Collections.singletonList(GrantDTO.Status.coming.getValue())).map(GrantDTO::new);
                    } else {
                        return new PageImpl<>(new ArrayList<>(), pageable, 0L);
                    }
                }
            }
        }
        if (grantFilter.isOpenGrant()) {
            if (grantFilter.isComingGrant()) {
                return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusIn(pageable, listPublisher, Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue())).map(GrantDTO::new);
            } else {
                return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusIn(pageable, listPublisher, Collections.singletonList(GrantDTO.Status.open.getValue())).map(GrantDTO::new);
            }
        } else {
            if (grantFilter.isComingGrant()) {
                return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusIn(pageable, listPublisher, Collections.singletonList(GrantDTO.Status.coming.getValue())).map(GrantDTO::new);
            } else {
                return new PageImpl<>(new ArrayList<>(), pageable, 0L);
            }
        }
    }

    public int getCount() {
        return grantRepository.countAllByStatusInAndGrantProgram_Publisher_CrawledIsTrue(Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue()));
    }

    @Scheduled(fixedDelay = 3600000)
    public void updateStatusForGrant() {
        Instant instant = Instant.now();
        updateStatusForList(
            grantRepository.findAllByCloseDateBefore(instant),
            GrantDTO.Status.close.getValue()
        );
        updateStatusForList(
            grantRepository.findAllByStatusInAndOpenDateAfter(Collections.singletonList(GrantDTO.Status.undefined.getValue()), instant),
            GrantDTO.Status.coming.getValue()
        );
        updateStatusForList(
            grantRepository.findAllByStatusInAndOpenDateBefore(Arrays.asList(GrantDTO.Status.coming.getValue(), GrantDTO.Status.undefined.getValue()), instant),
            GrantDTO.Status.open.getValue()
        );
    }

    public void updateStatusForList(List<Grant> list, int status) {
        List<GrantDTO> grantsOpen = list.stream()
            .map(GrantDTO::new)
            .collect(Collectors.toList());
        for (GrantDTO grant : grantsOpen) {
            grant.setStatus(status);
            update(grant);
        }
    }

    public void notificationEmail() {
        List<User> users = userService.getAllUserNotification();
        List<User> usersPublisherEmpty = new ArrayList<>();
        for (User user : users) {
            List<String> listPublisher = new ArrayList<>();
            List<PreferencesDTO> preferencesDTOS = preferencesService.getAll(user.getLogin());
            for (PreferencesDTO preferencesDTO : preferencesDTOS) {
                if (preferencesDTO.isNotification()) {
                    listPublisher.add(preferencesDTO.getPublisherDTO().getName());
                }
            }
            if (listPublisher.isEmpty()) {
                usersPublisherEmpty.add(user);
            } else {
                List<GrantDTO> grantDTOS = grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusInAndCreatedDateAfter(listPublisher, Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue()), Instant.now().minus(7, ChronoUnit.DAYS))
                    .stream().map(grant -> {
                        GrantDTO grantDTO = new GrantDTO(grant);
                        if (grantDTO.getDescription() != null) {
                            String description = Jsoup.parse(grantDTO.getDescription()).text();
                            grantDTO.setDescription(description);
                        }
                        return grantDTO;
                    }).collect(Collectors.toList());
                if (!grantDTOS.isEmpty()) {
                    mailService.sendNotificationGrantMail(user, grantDTOS);
                }
            }
        }
        if (!usersPublisherEmpty.isEmpty()) {
            List<GrantDTO> grantDTOS = grantRepository.findAllByStatusInAndCreatedDateAfterOrderByCreatedDateDesc(new PageRequest(0, 10), Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue()), Instant.now().minus(7, ChronoUnit.DAYS))
                .stream().map(grant -> {
                    GrantDTO grantDTO = new GrantDTO(grant);
                    if (grantDTO.getDescription() != null) {
                        String description = Jsoup.parse(grantDTO.getDescription()).text();
                        grantDTO.setDescription(description);
                    }
                    return grantDTO;
                }).collect(Collectors.toList());
            if (!grantDTOS.isEmpty()) {
                for (User user : usersPublisherEmpty) {
                    mailService.sendNotificationGrantMail(user, grantDTOS);
                }
            }
        }
    }

    private String formatStringSearch(String search) {
        String regex = "(:|d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(search);
        while (matcher.find()) {
            String resultCharacter = matcher.group(1).trim();
            search = search.replaceAll(resultCharacter, " " + resultCharacter + " ");
        }
        search = search.replaceAll("\\s+", "%");

        return search;
    }
}
