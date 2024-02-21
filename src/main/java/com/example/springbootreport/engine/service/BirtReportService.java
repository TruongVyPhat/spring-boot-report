package com.example.springbootreport.engine.service;

import com.example.springbootreport.engine.dto.OutputType;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Service
public class BirtReportService implements DisposableBean {

    private IReportEngine birtEngine;

    private final Map<String, IReportRunnable> reports = new HashMap() {{
        put("AML_REPORT_CTR_001_en", null);
        put("AML_REPORT_SacomBank_002_en", null);
        put("AML_REPORT_SacomBank_003_en", null);
        put("AML_REPORT_SacomBank_STR_001_en", null);
        put("AML_REPORT_SacomBank_STR_002_en", null);
        put("AML_REPORT_SacomBank_STR_002_limited_en", null);
        put("AML_REPORT_STR_001_en", null);
    }};

    private static final String RPT_DESIGN_SUFFIX = ".rptdesign";

    @SuppressWarnings("unchecked")
    @PostConstruct
    protected void initialize() throws BirtException, IOException {
        EngineConfig config = new EngineConfig();
        Platform.startup(config);
        IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        birtEngine = factory.createReportEngine(config);
        loadReports();
    }

    /**
     * Load report files to memory
     *
     */
    public void loadReports() throws EngineException, IOException {
        String templatePath;
        IReportRunnable report;
        InputStream templateStream = null;

        try {
            for (String templateName: reports.keySet()) {
                templatePath = "/reports/" + templateName + RPT_DESIGN_SUFFIX;
                templateStream = getClass().getResourceAsStream(templatePath);

                report = birtEngine.openReportDesign(templateStream);
                // ... process the template here ...
                reports.put(templateName, report);
            }
        } finally {
            if (templateStream != null)
                templateStream.close();
        }
    }

    public void generateMainReport(String reportName, OutputType type, HttpServletResponse response, HttpServletRequest request) {
        response.setContentType(birtEngine.getMIMEType(type.name()));
        RenderOption renderOption = getRenderOption(type);
        generateReport(reports.get(reportName), response, request, renderOption);
    }

    private RenderOption getRenderOption(OutputType type) {
        IRenderOption iOptions = new RenderOption();
        iOptions.setOutputFormat(type.name());
        RenderOption option;
        switch (type) {
            case pdf:
                option = new PDFRenderOption(iOptions);
                break;
            case docx:
                option = new DocxRenderOption();
                option.setOutputFormat(type.name());
                break;
            case xlsx:
                option = new EXCELRenderOption(iOptions);
                break;
            default:
                throw new IllegalArgumentException("Output type not recognized:" + type);
        }
        return option;
    }
    private void generateReport(IReportRunnable report, HttpServletResponse response, HttpServletRequest request, RenderOption renderOption) {
        IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
        runAndRenderTask.setRenderOption(renderOption);
        runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, request);
        try {
            renderOption.setOutputStream(response.getOutputStream());
            runAndRenderTask.run();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            runAndRenderTask.close();
        }
    }

    @Override
    public void destroy() {
        birtEngine.destroy();
        Platform.shutdown();
    }
}
