package com.luixtech.uidgenerator.core.worker;

import com.luixtech.uidgenerator.core.utils.ValuedEnum;

/**
 * WorkerNodeType
 */
public enum WorkerNodeType implements ValuedEnum<Integer> {

    CONTAINER(1), PHYSICAL_MACHINE(2);

    /**
     * Lock type
     */
    private final Integer type;

    /**
     * Constructor with field of type
     */
    WorkerNodeType(Integer type) {
        this.type = type;
    }

    @Override
    public Integer value() {
        return type;
    }

}
