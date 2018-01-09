package com.avonniv.service.fetchdata;

import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantProgram;
import com.avonniv.domain.Publisher;
import com.avonniv.service.GrantProgramService;
import com.avonniv.service.GrantService;
import com.avonniv.service.PublisherService;
import com.avonniv.service.dto.GrantDTO;
import com.avonniv.service.dto.GrantProgramDTO;
import com.avonniv.service.dto.PublisherDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;


@Service
public class FetchDataTillvaxtVerketService {

    private final PublisherService publisherService;

    private final GrantService grantService;

    private final GrantProgramService grantProgramService;

    public FetchDataTillvaxtVerketService(PublisherService publisherService,
                                          GrantProgramService grantProgramService,
                                          GrantService grantService) {
        this.publisherService = publisherService;
        this.grantService = grantService;
        this.grantProgramService = grantProgramService;
    }

    //43200000 millisecond = 12 hour
    @Scheduled(fixedDelay = 21600000)
    public void autoFetchDataFromFormas() {
        try {
            String name = "Tillväxtverket";
            Optional<Publisher> publisherOptional = publisherService.getById(7L);
            PublisherDTO publisherDTO = publisherOptional.map(PublisherDTO::new).orElse(null);
            fetDataFromURL(name, publisherDTO, "https://tillvaxtverket.se/vara-tjanster/utlysningar", false);
            fetDataFromURL(name, publisherDTO, "https://tillvaxtverket.se/vara-tjanster/utlysningar/planerade-utlysningar", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetDataFromURL(String name, PublisherDTO publisherDTO, String externalUrl, boolean planned) {
        String externalIdGrantProgram = name + "_" + externalUrl;
        Optional<GrantProgram> optional = grantProgramService.getByExternalId(externalIdGrantProgram);
        GrantProgram grantProgram;
        if (optional.isPresent()) {
            grantProgram = optional.get();
        } else {
            GrantProgramDTO grantProgramDTO = new GrantProgramDTO(
                null, null, null, 0,
                planned ? "Tillväxtverket grant program planned" : "Tillväxtverket grant program",
                null,
                GrantProgramDTO.TYPE.PUBLIC.getValue(),
                publisherDTO,
                null,
                externalIdGrantProgram,
                externalUrl,
                null
            );
            grantProgram = grantProgramService.createGrant(grantProgramDTO);
        }
        GrantProgramDTO grantProgramDTO = new GrantProgramDTO(grantProgram);
        List<Long> listIgnore = new ArrayList<>();
        getData(name, externalUrl, grantProgramDTO, planned, listIgnore);
        if (planned) {
            grantService.updateStatusForList(
                grantService.getAllByGrantProgramIdAndStatus(grantProgram.getId(), GrantDTO.Status.coming.getValue(), listIgnore),
                GrantDTO.Status.un_publish.getValue()
            );
        }else {
            grantService.updateStatusForList(
                grantService.getAllByGrantProgramIdAndStatus(grantProgram.getId(), GrantDTO.Status.open.getValue(), listIgnore),
                GrantDTO.Status.un_publish.getValue()
            );
        }
    }

    private void getData(String name, String url, GrantProgramDTO grantProgramDTO, boolean planned, List<Long> list) {
        try {
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36")
                .timeout(30000)
                .get();
            Elements elements = doc.select(".sv-responsive .lp-main .lp-content .lp-list-page-list .lp-list-page-list-item strong a");
            if (elements.size() != 0) {
                for (Element element : elements) {
                    Long id = saveGrant(name, element.attr("href"), grantProgramDTO, planned, url);
                    if (id != null) {
                        list.add(id);
                    }
                }
            }
            Elements elementsNext = doc.select(".sv-responsive .lp-main .lp-content .lp-list-page-list .lp-list-page-list-pagination .lp-next a");
            if (elementsNext.size() == 1) {
                getData(name, "https://tillvaxtverket.se" + elementsNext.first().attr("href"), grantProgramDTO, planned, list);
            }
        } catch (Exception e) {
        }
    }

    private Long saveGrant(String name, String url, GrantProgramDTO grantProgramDTO, boolean planned, String dataFromUrl) {
        try {
            String externalIdGrant = name + "_" + url;
            Optional<Grant> grantOptional = grantService.getByExternalId(externalIdGrant);
            GrantDTO grantDTO = grantOptional.map(GrantDTO::new).orElseGet(() -> new GrantDTO(
                null, null, null,
                planned ? GrantDTO.Status.coming.getValue() : GrantDTO.Status.open.getValue(),
                grantProgramDTO,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                externalIdGrant,
                url,
                null,
                dataFromUrl
            ));
            grantDTO.setStatus(planned ? GrantDTO.Status.coming.getValue() : GrantDTO.Status.open.getValue());
            grantDTO.setGrantProgramDTO(grantProgramDTO);
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36")
                .timeout(30000)
                .get();
            Elements elements = doc.select(".sv-vertical .sv-layout .lp-application-metadata span");
            if (elements.size() == 1) {
                Instant date = getDateFromString(elements.first().text(), planned);
                if (planned) {
                    grantDTO.setOpenDate(date);
                } else {
                    grantDTO.setCloseDate(date, 2);
                }
            }
            Elements elementContent = doc.select(".sv-vertical .sv-layout .pagecontent");
            if (elementContent.size() == 1) {
                Element element = elementContent.first();
                grantDTO.setTitle(element.select(".sv-text-portlet-content h1").first().text());
                Elements elementDes = element.select(".sv-text-portlet-content");
                if (elementDes.size() > 2) {
                    grantDTO.setDescription(elementDes.get(1).text());
                }
//                    for (int i = 1; i < elementDes.size(); i++) {
//                        description = description + elementDes.get(i).text();
//                    }
            }
            if (grantOptional.isPresent()) {
                grantService.update(grantDTO);
                return grantDTO.getId();
            } else {
                return grantService.createGrantCall(grantDTO).getId();
            }
        } catch (Exception e) {
        }
        return null;
    }

    private Instant getDateFromURL(String url) {
        try {
            String[] strings = url.split("/");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
            String dateString = strings[strings.length - 1].substring(0, 10);
            return sdf.parse(dateString).toInstant();
        } catch (Exception e) {
            return null;
        }
    }

    private Instant getDateFromString(String closeDateString, boolean planned) {
        try {
            String dateString = closeDateString.replaceAll(planned ? "Utlysningen planeras öppna den:" : "Sista ansökningsdag:", "").trim();
            dateString = Util.convertStringMonthToNumber(dateString);
            if (dateString.length() == 9) {
                dateString = "0" + dateString;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
            return sdf.parse(dateString).toInstant();
        } catch (Exception e) {
            return null;
        }
    }
}
