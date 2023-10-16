package com.example.springbootreport.engine.service;

import com.example.springbootreport.engine.dto.OutputType;
import com.example.springbootreport.engine.dto.Report;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class BirtReportService implements DisposableBean {
    @Value("${reports.relative.path}")
    private String reportsPath;
    @Value("${images.relative.path}")
    private String imagesPath;

    private final HTMLServerImageHandler htmlImageHandler = new HTMLServerImageHandler();

    private IReportEngine birtEngine;
    private String imageFolder;

    private final Map<String, IReportRunnable> reports = new HashMap<>();

    private static final String RPT_DESIGN_SUFFIX = ".rptdesign";

    @SuppressWarnings("unchecked")
    @PostConstruct
    protected void initialize() throws BirtException {
        EngineConfig config = new EngineConfig();
        Platform.startup(config);
        IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        birtEngine = factory.createReportEngine(config);
        imageFolder = System.getProperty("user.dir") + File.separatorChar + reportsPath + imagesPath;
        loadReports();
    }

    /**
     * Load report files to memory
     *
     */
    public void loadReports() throws EngineException {
        File folder = new File(reportsPath);
        for (String file : Objects.requireNonNull(folder.list())) {
            if (!file.endsWith(RPT_DESIGN_SUFFIX))
                continue;

            reports.put(file.replace(RPT_DESIGN_SUFFIX, ""),
                    birtEngine.openReportDesign(folder.getAbsolutePath() + File.separator + file));

        }
    }

    private Report.ParameterType getParameterType(IParameterDefn param) {
        if (IParameterDefn.TYPE_INTEGER == param.getDataType()) {
            return Report.ParameterType.INT;
        }
        return Report.ParameterType.STRING;
    }

    public void generateMainReport(String reportName, OutputType output, HttpServletResponse response, HttpServletRequest request) {
        switch (output) {
        case HTML:
            generateHTMLReport(reports.get(reportName), response, request);
            break;
        case PDF:
            generatePDFReport(reports.get(reportName), response, request);
            break;
        default:
            throw new IllegalArgumentException("Output type not recognized:" + output);
        }
    }

    /**
     * Generate a report as HTML
     */
    @SuppressWarnings("unchecked")
    private void generateHTMLReport(IReportRunnable report, HttpServletResponse response, HttpServletRequest request) {
        IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
        response.setContentType(birtEngine.getMIMEType("html"));
        IRenderOption options = new RenderOption();
        HTMLRenderOption htmlOptions = new HTMLRenderOption(options);
        htmlOptions.setOutputFormat("html");
        htmlOptions.setBaseImageURL(imageFolder);
        htmlOptions.setImageDirectory(imageFolder);
        htmlOptions.setImageHandler(htmlImageHandler);
        runAndRenderTask.setRenderOption(htmlOptions);
        runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, request);

        try {
            htmlOptions.setOutputStream(response.getOutputStream());
            runAndRenderTask.run();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            runAndRenderTask.close();
        }
    }

    /**
     * Generate a report as PDF
     */
    @SuppressWarnings("unchecked")
    private void generatePDFReport(IReportRunnable report, HttpServletResponse response, HttpServletRequest request) {
        IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
        response.setContentType(birtEngine.getMIMEType("pdf"));
        IRenderOption options = new RenderOption();
        PDFRenderOption pdfRenderOption = new PDFRenderOption(options);
        pdfRenderOption.setOutputFormat("pdf");
        runAndRenderTask.setRenderOption(pdfRenderOption);
        runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, request);

        try {
            pdfRenderOption.setOutputStream(response.getOutputStream());
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
