import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, Renderer} from '@angular/core';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Router, ActivatedRoute, Params} from '@angular/router';
import {EventManager, JhiLanguageService} from 'ng-jhipster';

import {LoginService} from './login.service';
import {StateStorageService} from '../auth/state-storage.service';
import {Register} from '../../account/register/register.service';
import {PasswordResetModalService} from '../../account/password-reset/init/password-reset-init-modal.service';
import {TermsModalComponent} from './terms/terms.component';

@Component({
    selector: 'jhi-login-modal',
    templateUrl: './login.component.html',
    styleUrls: [
        'login.scss'
    ]
})
export class JhiLoginModalComponent implements OnInit, OnDestroy, AfterViewInit {
    authenticationError: boolean;
    password: string;
    rememberMe: boolean;
    terms1: boolean;
    terms2: boolean;
    terms3: boolean;
    username: string;
    credentials: any;
    showPassword = false;
    isRegistration = false;
    // confirmPassword: string;
    // doNotMatch: string;
    error: string;
    errorEmailExists: string;
    errorUserExists: string;
    errorReSendEmail: string;
    registerAccount: any;
    success: boolean;
    reSendEmail: boolean;
    validatorEmail: boolean;
    validatorPasword: boolean;

    constructor(private eventManager: EventManager,
                private languageService: JhiLanguageService,
                private loginService: LoginService,
                private registerService: Register,
                private stateStorageService: StateStorageService,
                private passwordResetModalService: PasswordResetModalService,
                private elementRef: ElementRef,
                private renderer: Renderer,
                private router: Router,
                public activeModal: NgbActiveModal,
                public modalService: NgbModal,
                private activatedRoute: ActivatedRoute) {
        this.credentials = {};
    }

    ngOnInit() {
        this.renderer.setElementClass(document.body, 'position-active', true);
        this.languageService.addLocation('login');
        this.success = false;
        this.reSendEmail = false;
        this.registerAccount = {};
    }

    ngOnDestroy() {
        this.renderer.setElementClass(document.body, 'position-active', false);
    }

    ngAfterViewInit() {
        if (this.isRegistration) {
            this.renderer.invokeElementMethod(this.elementRef.nativeElement.querySelector('#login'), '', []);
        } else {
            this.renderer.invokeElementMethod(this.elementRef.nativeElement.querySelector('#usernameLogin'), '', []);
        }
    }

    cancel() {
        this.credentials = {
            username: null,
            password: null,
            rememberMe: true,
            terms1: true,
            terms2: true,
            terms3: true
        };
        this.authenticationError = false;
        this.activeModal.dismiss('cancel');
    }

    login() {
        this.loginService.login({
            username: this.username,
            password: this.password,
            rememberMe: this.rememberMe
        }).then(() => {
            this.authenticationError = false;
            this.activeModal.dismiss('login success');
            if (this.router.url === '/register' || (/activate/.test(this.router.url)) ||
                this.router.url === '/finishReset' || this.router.url === '/requestReset') {
                this.router.navigate(['']);
            }

            this.eventManager.broadcast({
                name: 'authenticationSuccess',
                content: 'Sending Authentication Success'
            });

            this.activatedRoute.queryParams.subscribe((params: Params) => {
                const tab = params['tab'];
                console.log('page: ' + tab, params);
                if (tab != null) {
                    this.router.navigate([tab]);
                }
            });

        }).catch(() => {
            this.authenticationError = true;
        });
    }

    requestResetPassword() {
        this.activeModal.dismiss('to state requestReset');
        this.passwordResetModalService.open();
        // this.router.navigate(['/reset', 'request']);
    }

    register() {
        this.error = null;
        this.errorUserExists = null;
        this.errorEmailExists = null;
        this.errorReSendEmail = null;
        this.languageService.getCurrent().then((key) => {
            this.registerAccount.langKey = key;
            this.registerService.save(this.registerAccount).subscribe(() => {
                this.success = true;
            }, (response) => this.processError(response));
        });
    }

    private processError(response) {
        this.success = null;
        if (response.status === 400 && response._body === 'login already in use') {
            this.errorUserExists = 'ERROR';
        } else if (response.status === 400 && response._body === 'email address already in use') {
            this.errorEmailExists = 'ERROR';
        } else {
            this.error = 'ERROR';
        }
    }

    openTerms() {
        this.activeModal.dismiss('cancel');
        const modalRef = this.modalService.open(TermsModalComponent);
    }

    resendActivateEmail() {
        this.success = null;
        this.registerService.reset(this.registerAccount.login).subscribe(() => {
            this.reSendEmail = true;
        }, (response) => this.processErrorReSendEmail(response));
    }

    private processErrorReSendEmail(response) {
        if (response.status === 400 && response._body === 'this user does not exist') {
            this.errorReSendEmail = 'ERROR';
        }
    }

}
