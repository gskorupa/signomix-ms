/**
 * Copyright (C) Grzegorz Skorupa 2020.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.signomix.out.mailing;

import com.signomix.out.connector.CmsConnectorIface;
import com.signomix.out.connector.UserConnectorIface;
import org.cricketmsf.microsite.out.notification.EmailSenderIface;

/**
 *
 * @author greg
 */
public interface MailingIface {
    
    public Object sendMailing(String docUid, String target, UserConnectorIface userAdapter, CmsConnectorIface cmsAdapter, EmailSenderIface emailSender);
    
}
