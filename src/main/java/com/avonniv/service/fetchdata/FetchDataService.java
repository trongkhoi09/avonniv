package com.avonniv.service.fetchdata;

import com.avonniv.service.GrantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class FetchDataService {

    private final Logger log = LoggerFactory.getLogger(FetchDataService.class);

    private FetchDataVinnovaService fetchDataVinnovaService;

    private FetchDataTillvaxtVerketService fetchDataTillvaxtVerketService;

    private FetchDataSMEService fetchDataSMEService;

    private FetchDataEnergimyndighetenService fetchDataEnergimyndighetenService;

    private FetchDataFORMASService fetchDataFORMASService;

    private GrantService grantService;

    public FetchDataService(FetchDataVinnovaService fetchDataVinnovaService,
                            FetchDataTillvaxtVerketService fetchDataTillvaxtVerketService,
                            FetchDataSMEService fetchDataSMEService,
                            FetchDataEnergimyndighetenService fetchDataEnergimyndighetenService,
                            FetchDataFORMASService fetchDataFORMASService,
                            GrantService grantService) {
        this.fetchDataVinnovaService = fetchDataVinnovaService;
        this.fetchDataTillvaxtVerketService = fetchDataTillvaxtVerketService;
        this.fetchDataSMEService = fetchDataSMEService;
        this.fetchDataEnergimyndighetenService = fetchDataEnergimyndighetenService;
        this.fetchDataFORMASService = fetchDataFORMASService;
        this.grantService = grantService;
    }

    //43200000 millisecond = 12 hour
    //run taks every 00h and 12h PM everyday
    @Scheduled(cron = "0 0 0,12 * * ?", zone = "CET")
    public void autoFetchData() {
        this.fetchDataVinnovaService.autoFetchDataFromVinnova();
        log.debug("finish fetch data from Vinnova at {}", new Date());
        this.fetchDataTillvaxtVerketService.autoFetchDataFromFormas();
        log.debug("finish fetch data from DataTillvaxtVerket at {}", new Date());
        this.fetchDataSMEService.autoFetchDataFromSME();
        log.debug("finish fetch data from SME at {}", new Date());
        this.fetchDataFORMASService.autoFetchDataFromFormas();
        log.debug("finish fetch data from FORMAS at {}", new Date());
        this.fetchDataEnergimyndighetenService.autoFetchDataFromFormas();
        log.debug("finish fetch data from Energimyndigheten at {}", new Date());

        TimeZone timeZone = TimeZone.getTimeZone("CET");
        Calendar c = Calendar.getInstance(timeZone);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.WEDNESDAY && c.get(Calendar.AM_PM) == Calendar.PM)
            grantService.notificationEmail();
    }
}
