/*
 * @(#)WebContent.java     18 Aug 2011
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package ext.deployit.community.plugin.notifications.email.deployed;

import static com.google.common.base.Strings.nullToEmpty;
import static com.xebialabs.deployit.plugin.api.reflect.DescriptorRegistry.getDescriptor;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.generic.ci.Resource;
import com.xebialabs.deployit.plugin.generic.deployed.ProcessedTemplate;
import com.xebialabs.deployit.plugin.generic.step.ScriptExecutionStep;
import com.xebialabs.deployit.plugin.overthere.Host;
import com.xebialabs.overthere.OperatingSystemFamily;

import ext.deployit.community.plugin.notifications.email.ci.MailServer;
import ext.deployit.community.plugin.notifications.email.step.EmailSendStep;
import ext.deployit.community.plugin.notifications.email.step.LiteralEmailSendStep;

@SuppressWarnings("serial")
@Metadata(virtual = true, description = "An email sent via a notify.MailServer")
public class SentEmail extends ProcessedTemplate<Resource> {
    public static final String SUBJECT_PROPERTY = "subject";
    public static final String FROM_PROPERTY = "from";
    public static final String TO_PROPERTY = "to";
    public static final String CC_PROPERTY = "cc";
    public static final String BCC_PROPERTY = "bcc";
    public static final String BODY_PROPERTY = "body";

    private static final String AWAIT_CONFIRMATION_PROPERTY = "awaitConfirmation";
    private static final String AWAIT_CONFIRMATION_SCRIPT_PROPERTY = "awaitConfirmationScript";
    private static final String ADDRESS_SEPARATOR = ",";
    
    private final Host localhost;

    public SentEmail() {
    	localhost = getDescriptor("overthere.LocalHost").newInstance("dummy-id");
    	localhost.setOs(OperatingSystemFamily.getLocalHostOperatingSystemFamily());
    }
    
    @Override
    public void executeCreate(DeploymentPlanningContext ctx, Delta delta) {
        ctx.addStep(getEmailSendStep());
        if (TRUE.equals(this.<Boolean>getProperty(AWAIT_CONFIRMATION_PROPERTY))) {
        	ctx.addStep(new ScriptExecutionStep(getCreateOrder() + 2, 
                    this.<String>getProperty(AWAIT_CONFIRMATION_SCRIPT_PROPERTY),
                    localhost, Collections.<String, Object>singletonMap("deployed", this), 
                    format("Await confirmation of '%s'", getName())));
        }
    }

    // override me!
    protected EmailSendStep getEmailSendStep() {
    	return new LiteralEmailSendStep(getCreateOrder(), getDescription(getCreateVerb()), 
                (MailServer) getContainer(), getFromAddress(), getToAddresses(), 
                getCcAddresses(), getBccAddresses(), getSubject(), 
                resolveExpression(this.<String>getProperty(BODY_PROPERTY)));
    }

    @Override
    public void executeDestroy(DeploymentPlanningContext ctx, Delta delta) {
        // not supported
    }
    
    protected String getSubject() {
    	return resolveExpression(this.<String>getProperty(SUBJECT_PROPERTY));
    }
    
    protected String getFromAddress() {
        return getProperty(FROM_PROPERTY);
    }
    
    protected List<String> getToAddresses() {
        return splitAddresses(this.<String>getProperty(TO_PROPERTY));
    }

    protected List<String> getCcAddresses() {
        return splitAddresses(this.<String>getProperty(CC_PROPERTY));
    }
    
    protected List<String> getBccAddresses() {
        return splitAddresses(this.<String>getProperty(BCC_PROPERTY));
    }
    
    private static List<String> splitAddresses(String commaSeparatedAddresses) {
        return (((commaSeparatedAddresses != null) && !commaSeparatedAddresses.isEmpty())
                ? ImmutableList.copyOf(commaSeparatedAddresses.split(ADDRESS_SEPARATOR))
                : ImmutableList.<String>of());
    }

    @Override
	public String getDescription(String verb) {
        return format("%s email '%s' to: '%s' (cc: '%s', bcc: '%s')", verb, 
                getProperty(SUBJECT_PROPERTY), 
                nullToEmpty(this.<String>getProperty(TO_PROPERTY)), 
                nullToEmpty(this.<String>getProperty(CC_PROPERTY)), 
                nullToEmpty(this.<String>getProperty(BCC_PROPERTY)));
    }
}
