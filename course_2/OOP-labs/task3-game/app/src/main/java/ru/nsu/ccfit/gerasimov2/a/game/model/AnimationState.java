package ru.nsu.ccfit.gerasimov2.a.game.model;

public enum AnimationState {
    IDLE,
    SWAP,
    DESTROY,
    FALLING;

    static public final AnimationState[] values = values();

    public AnimationState next() {
        return values[(ordinal() + 1 + values.length) % values.length];
    }

    public AnimationState prev() {
        return values[(ordinal() - 1 + values.length) % values.length];
    }
}
