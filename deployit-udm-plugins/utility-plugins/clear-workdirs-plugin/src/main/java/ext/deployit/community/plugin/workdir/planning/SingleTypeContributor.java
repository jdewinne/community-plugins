package ext.deployit.community.plugin.workdir.planning;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newLinkedList;
import static com.xebialabs.deployit.plugin.api.deployment.specification.Operation.CREATE;
import static com.xebialabs.deployit.plugin.api.deployment.specification.Operation.DESTROY;
import static com.xebialabs.deployit.plugin.api.deployment.specification.Operation.MODIFY;
import static com.xebialabs.deployit.plugin.api.deployment.specification.Operation.NOOP;
import static ext.deployit.community.plugin.workdir.util.Predicates2.deltaOf;
import static ext.deployit.community.plugin.workdir.util.Predicates2.operationIs;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.Operation;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.Deployed;

public abstract class SingleTypeContributor<D extends Deployed<?, ?>> {
    protected final Predicate<Delta> isOfType;

    protected List<D> deployedsCreated;
    protected List<TypedDelta> deployedsModified;
    protected List<TypedDelta> deployedsNoop;
    protected List<D> deployedsRemoved;

    private final Function<Delta, TypedDelta> toTypedDelta =
        new Function<Delta, TypedDelta>() {
        @Override
        public TypedDelta apply(Delta input) {
            return new TypedDelta(input);
        }
    };

    protected SingleTypeContributor(Class<? extends D> classOfDeployed) {
        this(Type.valueOf(classOfDeployed));
    }

    protected SingleTypeContributor(Type typeOfDeployed) {
        isOfType = deltaOf(typeOfDeployed);
    }

    protected void filterDeltas(List<Delta> deltas) {
        deployedsCreated = newLinkedList(transform(
                filter(deltas, and(isOfType, operationIs(CREATE))),
                new Function<Delta, D>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public D apply(Delta input) {
                        return (D) input.getDeployed();
                    }
                }));
        deployedsModified = newLinkedList(transform(
                filter(deltas, and(isOfType, operationIs(MODIFY))),
                                               toTypedDelta));
        deployedsNoop = newLinkedList(transform(
                filter(deltas, and(isOfType, operationIs(NOOP))),
                                               toTypedDelta));
        deployedsRemoved = newLinkedList(transform(
                filter(deltas, and(isOfType, operationIs(DESTROY))), 
                new Function<Delta, D>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public D apply(Delta input) {
                        return (D) input.getPrevious();
                    }
                }));
    }

    protected class TypedDelta implements Delta {
        private final Delta delegate;

        private TypedDelta(Delta delegate) {
            this.delegate = delegate;
        }

        @Override
        public Operation getOperation() {
            return delegate.getOperation();
        }

        @SuppressWarnings("unchecked")
        @Override
        public D getPrevious() {
            return (D) delegate.getPrevious();
        }

        @SuppressWarnings("unchecked")
        @Override
        public D getDeployed() {
            return (D) delegate.getDeployed();
        }
        
        @Override
        public List<String> getIntermediateCheckpoints() {
            return delegate.getIntermediateCheckpoints();
        }
    }
}
