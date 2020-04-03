package com.kelab.experiment.aop;

import com.alibaba.fastjson.JSON;
import com.kelab.info.base.ExceptionInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ModelAndView jsonErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        ModelAndView result = new ModelAndView(new MappingJackson2JsonView());
        result.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        ExceptionInfo info = new ExceptionInfo();
        info.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        info.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        info.setDate(date);
        info.setMessage(e.getMessage());
        result.addAllObjects(JSON.parseObject(JSON.toJSONString(info)));
        return result;
    }

}