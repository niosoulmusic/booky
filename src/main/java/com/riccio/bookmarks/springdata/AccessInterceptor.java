package com.riccio.bookmarks.springdata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Slf4j
public class AccessInterceptor implements WebRequestInterceptor {


    final AccessLogRepository repository;

    public AccessInterceptor(AccessLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void preHandle(WebRequest webRequest) throws Exception {
        AccessLogEntry entry = new AccessLogEntry(webRequest);
        try {
            repository.storeEntry(new AccessLogEntry(webRequest));
        }catch (Exception e){
            log.error("could not store accessLogEntry {} Error :",entry,e);
        }
    }

    @Override
    public void postHandle(WebRequest webRequest, ModelMap modelMap) throws Exception {

    }

    @Override
    public void afterCompletion(WebRequest webRequest, Exception e) throws Exception {

    }
}
