package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdd.common.constant.MddConstants;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.core.file.oss.FileType;
import com.yonyou.ucf.mdd.core.file.oss.Object;
import com.yonyou.ucf.mdd.core.file.oss.OssResult;
import com.yonyou.ucf.mdd.core.utils.FilePathValidUtil;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;

@Controller
@RequestMapping("/pub/fileupload")
public class FileUploadController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @RequestMapping("/getFileServerUrl")
    public void getFileServerUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // renderJson(response, ResultMessage.permissions(uploader.getFileServer()));
        renderJson(response, ResultMessage.data(Object.getFileServer()));
    }

    @RequestMapping("/upload")
    public void upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "foldrerName", required = false) String folderName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        File tmpFile = new File(FilePathValidUtil.validFilePath(System.getProperty("java.io.tmpdir")), FilePathValidUtil.validFilePath(file.getOriginalFilename()));
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        try {
            file.transferTo(tmpFile);
            final String fileServerName = Object.getFileServerName();
            Object.IObject uploader = MddBaseContext.getBean(fileServerName, Object.IObject.class);
            if (null == uploader) {
                uploader = MddBaseContext.getBean(MddConstants.BEAN_MDD_FILE_SERVER, Object.IObject.class);
            }
            OssResult result = uploader.upload(tmpFile, FileType.ATTACHMENT, folderName);
            renderJson(response, ResultMessage.data("/".equals(result.cFolder) ? "/" + result.cFileName : result.cFolder + "/" + result.cFileName));
            logger.info(result + "上传完成！");
        } catch (Exception ex) {
            logger.error("上传失败{}", ex);
            renderJson(response, ResultMessage.error("文件上传失败，" + ex.getMessage()));
        } finally {
            tmpFile.delete();
        }
    }

    @RequestMapping(value = "/download/{fileName}", method = RequestMethod.GET)
    public void fetchImageData(@PathVariable("fileName") String srcUrl, HttpServletRequest request, HttpServletResponse response) {
        final String fileServerName = Object.getFileServerName();
        Object.IObject uploader = MddBaseContext.getBean(fileServerName, Object.IObject.class);
        if (null == uploader) {
            uploader = MddBaseContext.getBean(MddConstants.BEAN_MDD_FILE_SERVER, Object.IObject.class);
        }
        try (ServletOutputStream out = response.getOutputStream()) {
            InputStream ips = uploader.downloadFile("", srcUrl);
            response.setContentType("multipart/form-permissions");
            //读取文件流
            int len = 0;
            byte[] buffer = new byte[1024 * 10];
            while ((len = ips.read(buffer)) != -1){
                out.write(buffer,0,len);
            }
            out.flush();
        } catch (Exception e) {
            logger.error("获取文件缩略图失败");
        }
    }

}