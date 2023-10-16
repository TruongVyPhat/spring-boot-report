package com.example.springbootreport.engine.controller;

import com.example.springbootreport.engine.dto.OutputType;
import com.example.springbootreport.engine.service.BirtReportService;
import org.eclipse.birt.report.engine.api.EngineException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/report")
public class BirtReportController {

    private final BirtReportService reportService;

    public BirtReportController(BirtReportService reportService) {
        this.reportService = reportService;
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.GET, value = "/report/reload")
    @ResponseBody
    public ResponseEntity<Void> reloadReports(HttpServletResponse response) {
        try {
            reportService.loadReports();
        } catch (EngineException e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(produces = "text/html", method = RequestMethod.GET)
    public ResponseEntity<Void> generateFullReport(
            HttpServletResponse response, HttpServletRequest request,
            @RequestParam("name") String name, @RequestParam("output") OutputType output
    ) {
        reportService.generateMainReport(name, output, response, request);
        return ResponseEntity.ok().build();
    }
}
