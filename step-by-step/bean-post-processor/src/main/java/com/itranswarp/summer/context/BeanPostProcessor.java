package com.itranswarp.summer.context;

public interface BeanPostProcessor {

    /**
     * Invoked after new Bean().
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * Invoked after bean.init() called.
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * Invoked before bean.setXyz() called.
     */
    default Object postProcessOnSetProperty(Object bean, String beanName) {
        return bean;
    }
}
