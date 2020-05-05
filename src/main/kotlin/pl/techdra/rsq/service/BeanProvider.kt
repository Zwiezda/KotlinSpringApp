package pl.techdra.rsq.service

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service

@Service
/*
* Bean provider for classes outside Spring
 */
class BeanProvider : ApplicationContextAware {
    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    companion object {
        private var context: ApplicationContext? = null

        fun <T> getBean(beanClass: Class<T>): T {
            return context!!.getBean(beanClass)
        }
    }

}