/*
 * Copyright 2015-2020 Ritense BV, the Netherlands.
 *
 * Licensed under EUPL, Version 1.2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {BrowserModule} from '@angular/platform-browser';
import {Injector, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpBackend, HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {LayoutModule} from '@valtimo/layout';
import {TaskModule} from '@valtimo/task';
import {environment} from '../environments/environment';
import {SecurityModule} from '@valtimo/security';
import {
  BpmnJsDiagramModule,
  CardModule,
  MenuModule, registerFormioFileSelectorComponent,
  registerFormioUploadComponent,
  WidgetModule
} from '@valtimo/components';
import {
  DefaultTabs,
  DossierDetailTabAuditComponent,
  DossierDetailTabDocumentsComponent,
  DossierDetailTabProgressComponent,
  DossierDetailTabSummaryComponent,
  DossierModule,
} from '@valtimo/dossier';
import {ProcessModule} from '@valtimo/process';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {DocumentModule} from '@valtimo/document';
import {AccountModule} from '@valtimo/account';
import {ChoiceFieldModule} from '@valtimo/choice-field';
import {ResourceModule} from '@valtimo/resource';
import {FormModule} from '@valtimo/form';
import {SwaggerModule} from '@valtimo/swagger';
import {AnalyseModule} from '@valtimo/analyse';
import {ProcessManagementModule} from '@valtimo/process-management';
import {DecisionModule} from '@valtimo/decision';
import {MilestoneModule} from '@valtimo/milestone';
import {LoggerModule} from 'ngx-logger';
import {FormManagementModule} from '@valtimo/form-management';
import {MigrationModule} from '@valtimo/migration';
import {DossierManagementModule} from '@valtimo/dossier-management';
import {BootstrapModule} from '@valtimo/bootstrap';
import {ConfigModule, ConfigService, MultiTranslateHttpLoaderFactory} from '@valtimo/config';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {PluginManagementModule} from '@valtimo/plugin-management';
import {XentialPluginModule, XentialPluginSpecification} from '../../projects/valtimo/xential/src/public_api';
import {AccessControlManagementModule} from '@valtimo/access-control-management';
import {
  PLUGINS_TOKEN,
} from '@valtimo/plugin';
import {TaskManagementModule} from '@valtimo/task-management';
import {ProcessLinkModule} from "@valtimo/process-link";

export function tabsFactory() {
  return new Map<string, object>([
    [DefaultTabs.summary, DossierDetailTabSummaryComponent],
    [DefaultTabs.progress, DossierDetailTabProgressComponent],
    [DefaultTabs.audit, DossierDetailTabAuditComponent],
    [DefaultTabs.documents, DossierDetailTabDocumentsComponent],
  ]);
}

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    HttpClientModule,
    CommonModule,
    BrowserModule,
    LayoutModule,
    CardModule,
    WidgetModule,
    BootstrapModule,
    ConfigModule.forRoot(environment),
    LoggerModule.forRoot(environment.logger),
    environment.authentication.module,
    SecurityModule,
    MenuModule,
    TaskModule,
    DossierModule.forRoot(tabsFactory),
    ProcessModule,
    BpmnJsDiagramModule,
    FormsModule,
    ReactiveFormsModule,
    DocumentModule,
    AccountModule,
    ChoiceFieldModule,
    ResourceModule,
    FormModule,
    AnalyseModule,
    SwaggerModule,
    ProcessManagementModule,
    DecisionModule,
    MilestoneModule,
    FormManagementModule,
    MigrationModule,
    DossierManagementModule,
    PluginManagementModule,
    AccessControlManagementModule,
    XentialPluginModule,
    HttpClientModule, TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: MultiTranslateHttpLoaderFactory,
        deps: [HttpBackend, ConfigService]
      }
    }),
    TaskManagementModule,
    ProcessLinkModule
  ],
  providers: [{
    provide: PLUGINS_TOKEN,
    useValue: [
      XentialPluginSpecification,
    ]
  }],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(injector: Injector) {
    registerFormioUploadComponent(injector);
    registerFormioFileSelectorComponent(injector);
  }
}
