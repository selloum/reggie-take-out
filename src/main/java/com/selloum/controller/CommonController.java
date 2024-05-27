package com.selloum.controller;

import ch.qos.logback.core.pattern.color.BoldYellowCompositeConverter;
import com.selloum.common.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/***
 * 文件下载和上传
 */
@RestController
@RequestMapping("/common")
@Slf4j

public class CommonController {


    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {//参数文件名必须和前端代码保持一致
        log.info("进行文件上传……");

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //使用uuid重新生成文件名，防止文件名重复

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//.jpg

        String fileName = UUID.randomUUID().toString() + suffix;

        //目录对象不存在，则创建
        File dir=new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info(file.toString());
        return Result.success(fileName);
    }
    //不需要返回值，通过输出流将浏览器页面写回数据即刻

    /***
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try{
            //输入流读取文件内容
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
            //输出流，通过输出流将文件写回浏览器，在浏览器里展示图片
            ServletOutputStream outputStream= response.getOutputStream();

            //设置响应回去的是什么文件
            response.setContentType("image/jpeg");

            int len=0;
            byte[] bytes=new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }



    }
}
