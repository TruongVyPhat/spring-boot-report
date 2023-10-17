package com.example.springbootreport.engine.service;

import com.example.springbootreport.engine.dto.OutputType;
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
            case html:
                option = new HTMLRenderOption(iOptions);
                ((HTMLRenderOption) option).setBaseImageURL(imageFolder);
                ((HTMLRenderOption) option).setImageDirectory(imageFolder);
                option.setImageHandler(htmlImageHandler);
                break;
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
