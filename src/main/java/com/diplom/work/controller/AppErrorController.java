package com.diplom.work.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Контроллер для обработки ошибок (/error)
 */
@Controller
@Slf4j
public class AppErrorController implements ErrorController {

    /**
     * Error Attributes in the Application
     */
    private final ErrorAttributes errorAttributes;

    private static final String ERROR_PATH = "/error";


    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     * Смотрим код ошибки и возвращаем страницу для неё, если есть
     * Если особой страницы нет, возвращаем как обычно
     *
     * @param request запрос с инфой об ошибке
     * @return название шаблона для вывода инфы об ошибке
     */
    @GetMapping(value = ERROR_PATH, produces = "text/html")
    public String errorHtml(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            switch (Integer.parseInt(status.toString())) {
                case 403:
                    return "errors/403";
                case 404:
                    return "errors/404";
                case 500:
                    return "errors/500";
                default:
                    return "error";
            }
        }
        return "error";
    }
    //Ниже ворованный код, который неизвестно как работает

    /**
     * Supports other formats like JSON, XML
     */
    @GetMapping(value = ERROR_PATH)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<>(body, status);
    }

    /**
     * Returns the path of the error page.
     *
     * @return the error path
     */
    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }


    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equalsIgnoreCase(parameter);
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request,
                                                   boolean isStackTrace) {
        WebRequest requestAttributes = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(requestAttributes,
                ErrorAttributeOptions.of(isStackTrace ? ErrorAttributeOptions.Include.STACK_TRACE :
                        ErrorAttributeOptions.Include.EXCEPTION));
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception ex) {
                log.error("Вызвано исключение при выводе ошибки {}", ex.getMessage());
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}