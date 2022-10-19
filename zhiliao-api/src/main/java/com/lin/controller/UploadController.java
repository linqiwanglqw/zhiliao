package com.lin.controller;

import com.lin.utils.QiniuUtils;
import com.lin.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private QiniuUtils qiniuUtils;

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file) {
        //原始文件名称 比如 aa.png
        String originalFilename = file.getOriginalFilename();
        //唯一的文件名称  StringUtils.substringAfterLast（a，b）把a按b分开，取b后面的值
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");

        boolean upload = qiniuUtils.upload(file, fileName);
        if (upload) {
            return Result.success(QiniuUtils.url + fileName);
        }
        return Result.fail(20001, "上传失败");
    }
}
