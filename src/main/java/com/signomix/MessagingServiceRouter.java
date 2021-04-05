package com.signomix;

import com.signomix.events.NewNotification;
import com.signomix.user.User;
import org.cricketmsf.annotation.EventHook;
import org.cricketmsf.microsite.out.user.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: Unable to find procedure@method 0@null for event class com.signomix.events.ScriptingProblem 
/**
 *
 * @author greg
 */
public class MessagingServiceRouter {

    private static final Logger logger = LoggerFactory.getLogger(MessagingServiceRouter.class);

    private MessagingService service;

    public MessagingServiceRouter(MessagingService service) {
        this.service = service;
    }

    @EventHook(className = "com.signomix.events.NewNotification")
    public Object handleNewData(NewNotification event) {
        String origin[];
        String tmps = (String) event.getData().get("origin");
        origin = tmps.split("\t");
        if (origin.length < 2) {
            logger.warn("event origin not properly set: {}", event.getOrigin());
            return null;
        }
        //TODO: save alert
        //AlertModule.getInstance().putAlert(event, thingsAdapter);
        User user;
        String payload;
        String[] params;
        user = service.userConnector.getUser(origin[0]);
        if (user == null) {
            //TODO: this shouldn't happen - log error?
            logger.warn("user is null");
            return null;
        }
        String nodeName = origin[1];
        String eventType = (String) event.getData().get("type");
        String[] channelConfig = user.getChannelConfig(eventType);
        if (channelConfig == null || channelConfig.length < 2) {
            return null; // OK its normal behaviour
        }
        String messageChannel = channelConfig[0];
        String address;
        if (channelConfig.length == 2) {
            address = channelConfig[1];
        } else {
            address = "";
            for (int i = 1; i < channelConfig.length - 1; i++) {
                address = address + channelConfig[i] + ":";
            }
            address = address + channelConfig[channelConfig.length - 1];
        }
        String message = (String) event.getData().get("payload");
        String response = "";
        switch (messageChannel.toUpperCase()) {
            case "SMTP":
                response = service.smtpNotification.send(address, nodeName, message);
                break;
            case "SMS":
                if (user.getCredits() > 0) {
                    response = service.smsNotification.send(user.getUid(), user.getPhonePrefix() + address, nodeName, message);
                }
                if (!response.startsWith("ERROR")) {
                    //TODO: decrease user credits
                }
                break;
            case "PUSHOVER":
                response = service.pushoverNotification.send(address, nodeName, message);
                break;
            case "SLACK":
                response = service.slackNotification.send(address, nodeName, message);
                break;
            case "TELEGRAM":
                response = service.telegramNotification.send(address, nodeName, message);
                break;
            case "DISCORD":
                response = service.discordNotification.send(address, nodeName, message);
                break;
            case "WEBHOOK":
                response = service.webhookNotification.send(address, nodeName, message);
                break;
            default:
                logger.warn("message channel {} not supported", messageChannel);
        }
        if (response.startsWith("ERROR")) {
            logger.warn(response);
        }
        return null;
    }

}
