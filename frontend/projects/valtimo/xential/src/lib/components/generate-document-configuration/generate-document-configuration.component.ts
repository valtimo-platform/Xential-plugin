import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FunctionConfigurationComponent} from '@valtimo/plugin';
import {BehaviorSubject, combineLatest, Observable, Subscription, take} from 'rxjs';
import {FileFormat, GenerateDocumentConfig} from "../../models";
import {SelectItem} from "@valtimo/components";

@Component({
    selector: 'xential-generate-document-configuration',
    templateUrl: './generate-document-configuration.component.html'
})
export class GenerateDocumentConfigurationComponent implements FunctionConfigurationComponent, OnInit, OnDestroy {
    @Input() save$: Observable<void>;
    @Input() disabled$: Observable<boolean>;
    @Input() pluginId: string;
    @Input() prefillConfiguration$: Observable<GenerateDocumentConfig>;
    @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
    @Output() configuration: EventEmitter<GenerateDocumentConfig> =
        new EventEmitter<GenerateDocumentConfig>();

    public fileFormats$ = new BehaviorSubject<SelectItem[]>(
        ['DOCX', 'HTML', 'PDF', 'XML']
            .map(format => {
                return {
                    id: format,
                    text: format
                }
            })
    );

    private saveSubscription!: Subscription;

    private readonly formValue$ = new BehaviorSubject<GenerateDocumentConfig | null>(null);
    private readonly valid$ = new BehaviorSubject<boolean>(false);

    ngOnInit(): void {
        this.openSaveSubscription();
    }

    ngOnDestroy() {
        this.saveSubscription?.unsubscribe();
    }

    formValueChange(formValue: GenerateDocumentConfig): void {
        this.formValue$.next(formValue);
        this.handleValid(formValue);
    }

    private handleValid(formValue: GenerateDocumentConfig): void {
        const valid = !!(
            formValue.templateId &&
            formValue.documentId &&
            formValue.fileFormat &&
            !formValue.templateData.find((entry) => !(entry.key && entry.value))
        );

        this.valid$.next(valid);
        this.valid.emit(valid);
    }

    private openSaveSubscription(): void {
        this.saveSubscription = this.save$?.subscribe(save => {
            combineLatest([this.formValue$, this.valid$])
                .pipe(take(1))
                .subscribe(([formValue, valid]) => {
                    if (valid) {
                        this.configuration.emit(formValue);
                    }
                });
        });
    }
}
