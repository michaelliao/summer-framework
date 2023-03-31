package com.itranswarp.summer.context;

public interface ApplicationContext extends AutoCloseable {

    /**
     * Does this ApplicationContext contain a bean definition or externally
     * registered singleton instance with the given name?
     */
    boolean containsBean(String name);

    /**
     * Return an instance, which may be shared or independent, of the specified
     * bean.
     * 
     * @throws NoSuchBeanDefinitionException if there is no bean with the specified
     *                                       name
     */
    <T> T getBean(String name);

    /**
     * Return an instance, which may be shared or independent, of the specified
     * bean.
     * 
     * Behaves the same as {@link #getBean(String)}, but provides a measure of type
     * safety by throwing a BeanNotOfRequiredTypeException if the bean is not of the
     * required type. This means that ClassCastException can't be thrown on casting
     * the result correctly, as can happen with {@link #getBean(String)}.
     * 
     * @param name         the name of the bean to retrieve
     * @param requiredType type the bean must match; can be an interface or
     *                     superclass
     * @return an instance of the bean
     * @throws NoSuchBeanDefinitionException  if there is no such bean definition
     * @throws BeanNotOfRequiredTypeException if the bean is not of the required
     *                                        type
     */
    <T> T getBean(String name, Class<T> requiredType);

    /**
     * Return the bean instance that uniquely matches the given object type, if any.
     * 
     * This method goes into {@link ListableBeanFactory} by-type lookup territory
     * but may also be translated into a conventional by-name lookup based on the
     * name of the given type. For more extensive retrieval operations across sets
     * of beans, use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
     * 
     * @param requiredType type the bean must match; can be an interface or
     *                     superclass
     * @return an instance of the single bean matching the required type
     * @throws NoSuchBeanDefinitionException   if no bean of the given type was
     *                                         found
     * @throws NoUniqueBeanDefinitionException if more than one bean of the given
     *                                         type was found
     */
    <T> T getBean(Class<T> requiredType);

    void close();
}
