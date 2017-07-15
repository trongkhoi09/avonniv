import {MissingTranslationHandler, MissingTranslationHandlerParams} from 'ng2-translate';
import {ConfigService} from 'ng-jhipster/src/config.service';

export function missingTranslationHandler(configService) {
    return new MyMissingTranslationHandler(configService);
}

export class MyMissingTranslationHandler implements MissingTranslationHandler {
    data = [];

    constructor(private configService: ConfigService) {
    }

    handle(params: MissingTranslationHandlerParams) {
        const key = params.key;
        return this.configService.getConfig().noi18nMessage + '[' + key + ']';
    }
}
