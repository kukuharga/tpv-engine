package com.nuvola.tpv.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
public class DownloadController {
	
	 @Value("${export.file.dir}")
	 private String exportFilePath;
	
	
	
	
	@RequestMapping(path = "/download", method = RequestMethod.GET)
	public ResponseEntity<Resource> download(String param) throws IOException {

		File file = new File(exportFilePath + File.separator + "Lora.csv");
		HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
	    Path path = Paths.get(file.getAbsolutePath());
//	    String text = "hello world example!";
	    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

	    return ResponseEntity.ok()
	            .headers(headers)
	            .contentLength(file.length())
	            .contentType(MediaType.parseMediaType("application/csv"))
	            .body(resource);
	}
}
