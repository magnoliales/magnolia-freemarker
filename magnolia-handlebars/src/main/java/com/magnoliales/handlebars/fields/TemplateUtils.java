package com.magnoliales.handlebars.fields;

import com.magnoliales.handlebars.annotations.ParentTemplate;
import com.magnoliales.handlebars.annotations.SingletonTemplate;
import com.magnoliales.handlebars.setup.ApplicationContextContainer;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.blossom.annotation.Template;
import info.magnolia.repository.RepositoryConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.springframework.context.ApplicationContext;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.*;
import java.util.*;

public class TemplateUtils {

    final private Map<String, String> parentTemplates;
    final private List<String> singletonTemplates;
    final private Map<String, String> templates;

    TemplateUtils(ApplicationContextContainer applicationContextContainer) {
        parentTemplates = new HashMap<String, String>();
        singletonTemplates = new ArrayList<String>();
        templates = new TreeMap<String, String>();
        ApplicationContext context = applicationContextContainer.getContext();
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            Class<?> beanClass = context.getBean(beanDefinitionName).getClass();
            if (beanClass.isAnnotationPresent(Template.class)) {
                Template template = beanClass.getAnnotation(Template.class);
                templates.put(template.id(), template.title());
                if (beanClass.isAnnotationPresent(ParentTemplate.class)) {
                    Template parentTemplate = beanClass.getAnnotation(ParentTemplate.class)
                            .value().getAnnotation(Template.class);
                    parentTemplates.put(template.id(), parentTemplate.id());
                }
                if (beanClass.isAnnotationPresent(SingletonTemplate.class)) {
                    singletonTemplates.add(template.id());
                }
            }
        }
    }

    boolean isTemplateAvailable(String templateId) {
        return !singletonTemplates.contains(templateId) || findPagesUsingTemplate(templateId).isEmpty();
    }

    Map<String, String> getTemplates() {
        return templates;
    }

    Map<String, String> getParentTemplates() {
        return parentTemplates;
    }

    Map<String, String> findPagesUsingTemplate(String templateId) {
        Map<String, String> pages = new TreeMap<String, String>();
        String expression = "SELECT * FROM [mgnl:page] WHERE [mgnl:template] = '" + templateId + "'";
        try {
            Session session = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
            QueryResult result = query.execute();
            for (Row row : JcrUtils.getRows(result)) {
                Node node = row.getNode();
                pages.put(node.getPath(), node.getIdentifier());
            }
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InvalidQueryException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return pages;
    }
}
