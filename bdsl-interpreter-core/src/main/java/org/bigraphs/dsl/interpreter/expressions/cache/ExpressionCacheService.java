package org.bigraphs.dsl.interpreter.expressions.cache;

import com.google.common.base.Preconditions;
import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.framework.core.reactivesystem.ReactionRule;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystem;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystemPredicate;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.InterpreterService;
import org.bigraphs.dsl.interpreter.InterpreterServiceManager;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import javax.inject.Singleton;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A caching service for all BDSL literals such as {@link LocalVarDecl} and {@link Signature}.
 * <p>
 * The concrete inner classes represent the available caching services.
 * The shall be acquired by the {@link InterpreterServiceManager}.
 *
 * @author Dominik Grzelak
 */
public abstract class ExpressionCacheService<T extends EObject, R> implements InterpreterService<T, R> {
    protected ConcurrentHashMap<T, Slot<T, R>> hashtable = new ConcurrentHashMap<>();
    protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    Lock writeLock = lock.writeLock();
    Lock readLock = lock.readLock();

    public ExpressionCacheService() {
        Class<T> genericTypeClass = getGenericTypeClass();
        assert getBdslLiteral().getInstanceClassName().equals(genericTypeClass.getCanonicalName());
    }

    protected abstract EClass getBdslLiteral();

    /**
     * As singleton - then we can spawn multiple cache service implementations at arbitrary places without losing the
     * contents.
     */
    @Singleton
    public static final class DefaultServiceCacheServiceImpl extends ExpressionCacheService<Signature, DefaultDynamicSignature> {

        public DefaultServiceCacheServiceImpl() {
            super();
        }

        @Override
        protected EClass getBdslLiteral() {
            return BDSLPackage.Literals.SIGNATURE;
        }
    }

    /**
     * As singleton - then we can spawn multiple cache service implementations at arbitrary places without loosing the
     * contents.
     */
    @Singleton
    public static final class DefaultBigraphCacheServiceImpl extends ExpressionCacheService<LocalVarDecl, Bigraph<?>> {

        public DefaultBigraphCacheServiceImpl() {
            super();
        }

        @Override
        protected EClass getBdslLiteral() {
            return BDSLPackage.Literals.LOCAL_VAR_DECL;
        }
    }

    /**
     * As singleton - then we can spawn multiple cache service implementations at arbitrary places without loosing the
     * contents.
     */
    @Singleton
    public static final class DefaultBRSCacheServiceImpl extends ExpressionCacheService<BRSDefinition, ReactiveSystem<?>> {

        public DefaultBRSCacheServiceImpl() {
            super();
        }

        @Override
        protected EClass getBdslLiteral() {
            return BDSLPackage.Literals.BRS_DEFINITION;
        }
    }

    /**
     * As singleton - then we can spawn multiple cache service implementations at arbitrary places without loosing the
     * contents.
     */
    @Singleton
    public static final class DefaultRuleCacheServiceImpl extends ExpressionCacheService<LocalRuleDecl, ReactionRule<?>> {

        public DefaultRuleCacheServiceImpl() {
            super();
        }

        @Override
        protected EClass getBdslLiteral() {
            return BDSLPackage.Literals.LOCAL_RULE_DECL;
        }
    }

    /**
     * As singleton - then we can spawn multiple cache service implementations at arbitrary places without loosing the
     * contents.
     */
    @Singleton
    public static final class DefaultPredCacheServiceImpl extends ExpressionCacheService<LocalPredicateDeclaration, ReactiveSystemPredicate<?>> {

        public DefaultPredCacheServiceImpl() {
            super();
        }

        @Override
        protected EClass getBdslLiteral() {
            return BDSLPackage.Literals.LOCAL_PREDICATE_DECLARATION;
        }
    }

    enum SlotCacheState {
        CLEAN, DIRTY, NEW, DELETE, NONE;
    }

    private void executeWithLocking(CacheWriteCallback<T, R> cacheReadCallback) {
        try {
            writeLock.lock();
            cacheReadCallback.performWrite(this);
        } finally {
            writeLock.unlock();
        }
    }

    private Optional<R> executeWithLocking(CacheReadCallback<T, R> cacheReadCallback) {
        try {
            readLock.lock();
            return cacheReadCallback.performRead(this);
        } finally {
            readLock.unlock();
        }
    }

    private SlotCacheState executeWithLocking(CacheSlotReadCallback<T, R> cacheCallbackWithLock) {
        try {
            readLock.lock();
            return cacheCallbackWithLock.performRead(this);
        } finally {
            readLock.unlock();
        }
    }

    protected Class<T> getGenericTypeClass() {
        try {
            String className = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
            Class<?> clazz = Class.forName(className);
            return (Class<T>) clazz;
        } catch (Exception e) {
            throw new IllegalStateException("Class is not parametrized with a generic type!");
        }
    }

    public void addNewObject(T object, R reference) {
        executeWithLocking((CacheWriteCallback<T, R>) service -> {
            Slot<T, R> slot = Slot.create(object, reference, SlotCacheState.NEW);
            service.hashtable.put(object, slot);
        });
    }

    public void addCleanObject(T object, R reference) {
        executeWithLocking((CacheWriteCallback<T, R>) service -> {
            Slot<T, R> slot = Slot.create(object, reference, SlotCacheState.CLEAN);
            hashtable.put(object, slot);
        });
    }

    public void setClean(T object) {
        executeWithLocking((CacheWriteCallback<T, R>) service -> {
            hashtable.computeIfPresent(object, (T, slot) -> {
                slot.state = SlotCacheState.CLEAN;
                return slot;
            });
        });
    }

    public SlotCacheState getState(T object) {
        return executeWithLocking((CacheSlotReadCallback<T, R>) service -> {
            Slot<T, R> slot = hashtable.get(object);
            if (Objects.isNull(slot)) {
                return SlotCacheState.NONE;
            }
            return slot.getState();
        });
    }

    public Optional<R> getObject(T object) {
        return executeWithLocking((CacheReadCallback<T, R>) service -> {
            Optional<Slot<T, R>> slot = Optional.ofNullable(hashtable.get(object));
            return slot.map(Slot::getReference);
        });
    }

    public void setDirtyIfNotNew(T object) {
        if (getState(object) != SlotCacheState.NEW && getState(object) != SlotCacheState.NONE) {
            executeWithLocking((CacheWriteCallback<T, R>) service -> {
                hashtable.computeIfPresent(object, (localVarDecl, slot) -> {
                    slot.state = SlotCacheState.DIRTY;
                    return slot;
                });
            });
        }
    }

    public void remove(T object) {
        executeWithLocking((CacheWriteCallback<T, R>) service -> {
            if (Objects.nonNull(object))
                hashtable.remove(object);
        });
    }

    public R synchronize(T object, R reference) {
        Preconditions.checkArgument(Objects.nonNull(object), "Object must not be null");
        Optional<R> referenceObj = getObject(object);
        if (referenceObj.isPresent() &&
                reference.equals(referenceObj.get())) {
            return referenceObj.get();
        }
        addNewObject(object, reference);
        return reference;
    }

    protected static class Slot<T extends EObject, R> {
        private final T object;
        private final R reference;
        DefaultBigraphCacheServiceImpl.SlotCacheState state;

        public static <T extends EObject, R> Slot<T, R> create(T object, R reference, DefaultBigraphCacheServiceImpl.SlotCacheState state) {
            return new Slot<>(object, reference, state);
        }

        public Slot(T object, R reference, DefaultBigraphCacheServiceImpl.SlotCacheState state) {
            this.object = object;
            this.reference = reference;
            this.state = state;
        }

        public SlotCacheState getState() {
            return state;
        }

        public R getReference() {
            return reference;
        }

        public T getObject() {
            return object;
        }
    }

}
