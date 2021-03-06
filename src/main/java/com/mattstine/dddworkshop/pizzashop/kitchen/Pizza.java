package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Aggregate;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.AggregateState;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.function.BiFunction;

@Value
public final class Pizza implements Aggregate {
    PizzaRef ref;
    KitchenOrderRef kitchenOrderRef;
    Size size;
    EventLog $eventLog;
    @NonFinal
    State state;

    @Builder
    private Pizza(@NonNull PizzaRef ref,
                  @NonNull KitchenOrderRef kitchenOrderRef,
                  @NonNull Size size,
                  @NonNull EventLog eventLog) {
        this.ref = ref;
        this.kitchenOrderRef = kitchenOrderRef;
        this.size = size;
        this.$eventLog = eventLog;

        this.state = State.NEW;
    }

    /**
     * Private no-args ctor to support reflection ONLY.
     */
    @SuppressWarnings("unused")
    private Pizza() {
        this.ref = null;
        this.kitchenOrderRef = null;
        this.size = null;
        this.$eventLog = null;
    }

    public boolean isNew() {
        return state== State.NEW;
    }

    void startPrep() {
        if (!this.isNew())
        {
            throw new IllegalStateException();
        }
        state = State.PREPPING;
        $eventLog.publish(new Topic("pizzas"), new PizzaPrepStartedEvent(ref));

    }

    boolean isPrepping() {
        return state== State.PREPPING;
    }

    void finishPrep() {
        if (!this.isPrepping())
        {
            throw new IllegalStateException();
        }
        state = State.PREPPED;
        $eventLog.publish(new Topic("pizzas"), new PizzaPrepFinishedEvent(ref));

    }

    boolean hasFinishedPrep() {
        return state== State.PREPPED;
    }

    void startBake() {
        if (!this.hasFinishedPrep())
        {
            throw new IllegalStateException();
        }
        state = State. BAKING;
        $eventLog.publish(new Topic("pizzas"), new PizzaBakeStartedEvent(ref));

    }

    boolean isBaking() {
        return state==State.BAKING;
    }

    void finishBake() {
        if (!this.isBaking())
        {
            throw new IllegalStateException();
        }
        state = State.BAKED;
        $eventLog.publish(new Topic("pizzas"), new PizzaBakeFinishedEvent(ref));

    }

    boolean hasFinishedBaking() {
        return state==State.BAKED;
    }

    @Override
    public Pizza identity() {
        return null;
    }

    @Override
    public BiFunction<Pizza, PizzaEvent, Pizza> accumulatorFunction() {
        return new Accumulator();
    }

    @Override
    public PizzaRef getRef() {
        return ref;
    }

    @Override
    public PizzaState state() {
        return null;
    }

    enum Size {
        IDENTITY, SMALL, MEDIUM, LARGE
    }

    enum State {
        NEW,
        PREPPING,
        PREPPED,
        BAKING,
        BAKED
    }

    private static class Accumulator implements BiFunction<Pizza, PizzaEvent, Pizza> {

        @Override
        public Pizza apply(Pizza pizza, PizzaEvent pizzaEvent) {
            return null;
        }
    }

    @Value
    static class PizzaState implements AggregateState {
    }
}
