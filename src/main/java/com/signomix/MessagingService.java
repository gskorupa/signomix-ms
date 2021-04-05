/*
 * Copyright 2021 Grzegorz Skorupa <g.skorupa at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");

 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package com.signomix;

import com.signomix.out.connector.CmsConnectorIface;
import com.signomix.out.connector.UserConnectorIface;
import org.cricketmsf.services.MinimalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.signomix.out.mailing.MailingIface;
import com.signomix.out.notification.NotificationIface;
import org.cricketmsf.microsite.out.notification.EmailSenderIface;

public class MessagingService extends MinimalService {

    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    Invariants invariants = null;
    String apiGatewayUrl = "";
    
    //connectors
    UserConnectorIface userConnector;
    CmsConnectorIface cmsConnector;

    //notifications and emails
    NotificationIface smtpNotification = null;
    NotificationIface smsNotification = null;
    NotificationIface pushoverNotification = null;
    NotificationIface slackNotification = null;
    NotificationIface telegramNotification = null;
    NotificationIface discordNotification = null;
    NotificationIface webhookNotification = null;
    EmailSenderIface emailSender = null;
    MailingIface mailingAdapter = null;

    public MessagingService() {
        super();
        eventRouter = new MessagingServiceRouter(this);
        apiGatewayUrl=(String)properties.getOrDefault("api-gateway-url", "http://localhost:8080");
    }

    @Override
    public void getAdapters() {
        super.getAdapters();
    }

    @Override
    public void runInitTasks() {
        super.runInitTasks();
        invariants = new Invariants();
        //PlatformAdministrationModule.getInstance().initDatabases(database,thingsDB,iotDataDB, actuatorCommandsDB);
    }
}
