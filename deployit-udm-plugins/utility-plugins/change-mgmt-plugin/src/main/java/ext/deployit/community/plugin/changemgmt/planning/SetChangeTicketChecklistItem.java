package ext.deployit.community.plugin.changemgmt.planning;

import static com.google.common.base.Preconditions.checkState;
import static ext.deployit.community.plugin.changemgmt.util.Predicates2.deltaOf;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.xebialabs.deployit.plugin.api.deployment.execution.DeploymentStep;
import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.deployment.specification.Operation;
import com.xebialabs.deployit.plugin.api.reflect.DescriptorRegistry;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.DeploymentPackage;
import com.xebialabs.deployit.plugin.api.udm.Version;

public class SetChangeTicketChecklistItem {
    @VisibleForTesting
    static final String CHANGE_TICKET_CHECKLIST_ITEM_NAME_PROPERTY = "changeTicketChecklistItemSuffix";
    private static final Type DEPLOYMENT_PACKAGE_TYPE = Type.valueOf(DeploymentPackage.class);
    private static final Type CHANGE_MANAGER_TYPE = Type.valueOf("chg.ChangeManager");
    private static final Type CHANGE_TICKET_TYPE = Type.valueOf("chg.ChangeTicket");
    private static final List<DeploymentStep> NO_STEPS = ImmutableList.of();

    private static final Logger LOGGER = LoggerFactory.getLogger(SetChangeTicketChecklistItem.class);

    @PrePlanProcessor(order = 95)
    public static List<DeploymentStep> setChecklistItem(DeltaSpecification spec) {
        setChangeTicketChecklistItem(spec);
        return NO_STEPS;
    }

    protected static void setChangeTicketChecklistItem(DeltaSpecification spec) {
        DeployedApplication deployedApplication = spec.getDeployedApplication();
        String checklistItemSuffix = getChecklistName();
        if (!TRUE.equals(deployedApplication.getEnvironment().getProperty(
                format("requires%s", checklistItemSuffix)))) {
            LOGGER.debug("No change ticket checklist item required for target environment '{}'",
                    deployedApplication.getEnvironment());
            return;
        }

        Version deploymentPackage = deployedApplication.getVersion();
        String checklistPropertyName = format("satisfies%s", checklistItemSuffix);
        checkState(deploymentPackage.hasProperty(checklistPropertyName),
                "No checklist property '%s' defined for %s. Define a boolean, hidden property of this name on %s or change the value of property '%s' of %s.",
                checklistPropertyName, DEPLOYMENT_PACKAGE_TYPE, DEPLOYMENT_PACKAGE_TYPE, CHANGE_TICKET_CHECKLIST_ITEM_NAME_PROPERTY, CHANGE_MANAGER_TYPE);
        // can't use a constant in case the descriptor registry is refreshed
        checkState(DescriptorRegistry.getDescriptor(DEPLOYMENT_PACKAGE_TYPE)
        		   .getPropertyDescriptor(checklistPropertyName).isHidden(),
                "Checklist property '%s' is not defined as 'hidden' on '%s'. Hide it or change the value of property '%s' of %s.",
                checklistPropertyName, DEPLOYMENT_PACKAGE_TYPE, CHANGE_TICKET_CHECKLIST_ITEM_NAME_PROPERTY, CHANGE_MANAGER_TYPE);
        LOGGER.debug("Calculating value of hidden change ticket checklist property '{}'",
                checklistPropertyName);

        /*
         * Always allow undeployments. Not great, but where would a user put the
         * change ticket number? For initial/upgrade installations, looks for 
         * a creation or modification of a Change Ticket.
         */
        boolean hasChangeTicket = spec.getOperation().equals(Operation.DESTROY)
                || Iterables.any(spec.getDeltas(), deltaOf(CHANGE_TICKET_TYPE));
        deploymentPackage.setProperty(checklistPropertyName, hasChangeTicket);
    }

    // not a constant because the class may (?) be loaded before the registry is initialized
    private static String getChecklistName() {
        return DescriptorRegistry.getDescriptor(CHANGE_MANAGER_TYPE).newInstance("dummy-id")
               .getProperty(CHANGE_TICKET_CHECKLIST_ITEM_NAME_PROPERTY);
    }
}
