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
import java.util.Optional;


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
    @Scheduled(fixedDelay = 43200000, initialDelay = 3000)
    public void autoFetchDataFromFormas() {
        try {
            String name = "Tillväxtverket";
            Optional<Publisher> publisherOptional = publisherService.getPublisherByName(name);
            PublisherDTO publisherDTO = publisherOptional.map(PublisherDTO::new).orElse(null);
            String externalUrl = "https://tillvaxtverket.se/vara-tjanster/utlysningar";
            String externalIdGrantProgram = name + "_" + externalUrl;
            Optional<GrantProgram> optional = grantProgramService.getByExternalId(externalIdGrantProgram);
            GrantProgram grantProgram;
            if (optional.isPresent()) {
                grantProgram = optional.get();
            } else {
                GrantProgramDTO grantProgramDTO = new GrantProgramDTO(
                    null, null, null, 0,
                    "Tillväxtverket grant program",
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
            getData(name, externalUrl, grantProgramDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData(String name, String url, GrantProgramDTO grantProgramDTO) {
        try {
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36")
                .timeout(30000)
                .get();
            Elements elements = doc.select(".sv-responsive .lp-main .lp-content .lp-list-page-list .lp-list-page-list-item strong a");
            if (elements.size() != 0) {
                for (Element element : elements) {
                    saveGrant(name, element.attr("href"), grantProgramDTO);
                }
            }
            Elements elementsNext = doc.select(".sv-responsive .lp-main .lp-content .lp-list-page-list .lp-list-page-list-pagination .lp-next a");
            if (elementsNext.size() == 1) {
                getData(name, "https://tillvaxtverket.se" + elementsNext.first().attr("href"), grantProgramDTO);
            }
        } catch (Exception e) {
        }
    }

    private void saveGrant(String name, String url, GrantProgramDTO grantProgramDTO) {
        try {
            String externalIdGrant = name + "_" + url;
            Optional<Grant> grantOptional = grantService.getByExternalId(externalIdGrant);
            if (!grantOptional.isPresent()) {
                Instant openDate = getDateFromURL(url);
                GrantDTO grantDTO = new GrantDTO(
                    null, null, null, 0,
                    grantProgramDTO,
                    null,
                    null,
                    null,
                    openDate,
                    null,
                    null,
                    null,
                    externalIdGrant,
                    url,
                    null
                );

                Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36")
                    .timeout(30000)
                    .get();
                Elements elements = doc.select(".sv-vertical .sv-layout .lp-application-metadata span");
                if (elements.size() == 1) {
                    Instant closeDate = getDateFromString(elements.first().text());
                    grantDTO.setCloseDate(closeDate);
                }
                Elements elementContent = doc.select(".sv-vertical .sv-layout .pagecontent");
                if (elementContent.size() == 1) {
                    Element element = elementContent.first();
                    grantDTO.setTitle(element.select(".sv-text-portlet-content h1").first().text());
                    Elements elementDes = element.select(".sv-text-portlet-content");

                    String description = "";
                    for (int i = 1; i < elementDes.size(); i++) {
                        description = description + elementDes.get(i).text();
                    }
                    grantDTO.setDescription(description);
                }
                grantService.createGrantCall(grantDTO);
            }
        } catch (Exception e) {
        }
    }

    private Instant getDateFromURL(String url) {
        try {
            String[] strings = url.split("/");
            return new SimpleDateFormat("yyyy-MM-dd").parse(strings[strings.length - 1].substring(0, 10)).toInstant();
        } catch (Exception e) {
            return null;
        }
    }

    private Instant getDateFromString(String closeDateString) {
        try {
            String dateString = closeDateString.replaceAll("Sista ansökningsdag:", "").trim();
            dateString = dateString.replaceAll("januari", "01");
            dateString = dateString.replaceAll("februari", "02");
            dateString = dateString.replaceAll("mars", "03");
            dateString = dateString.replaceAll("april", "04");
            dateString = dateString.replaceAll("maj", "05");
            dateString = dateString.replaceAll("juni", "06");
            dateString = dateString.replaceAll("juli", "07");
            dateString = dateString.replaceAll("augusti", "08");
            dateString = dateString.replaceAll("september", "09");
            dateString = dateString.replaceAll("oktober", "10");
            dateString = dateString.replaceAll("november", "11");
            dateString = dateString.replaceAll("december", "12");
            if (dateString.length() == 9) {
                dateString = "0" + dateString;
            }
            return new SimpleDateFormat("dd MM yyyy").parse(dateString).toInstant();
        } catch (Exception e) {
            return null;
        }
    }
}
