package com.itranswarp.summer.jdbc;

import com.itranswarp.summer.annotation.Transactional;
import com.itranswarp.summer.aop.AnnotationProxyBeanPostProcessor;

public class TransactionalBeanPostProcessor extends AnnotationProxyBeanPostProcessor<Transactional> {

}
