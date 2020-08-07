package com.pbz.demo.hello.Unit;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.pbz.demo.hello.controller.FileController;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class FileTest {

	@Autowired
	private FileController fileOperator = null;
	
	private static String FILENAME_JSON;
	private static String FILENAME_UPLOAD;
	private static String DOWNLOAD_URL;
	private static String FILENAME_WEBPAGE; 
	
	@BeforeClass
	public static void setup() throws Exception {

		Properties properties=new Properties();  
		InputStream inputstream = FileTest.class.getClassLoader().getResourceAsStream("config.properties");
        properties.load(inputstream);  
 
        FILENAME_JSON = properties.getProperty("jsonFileName");  
        FILENAME_UPLOAD= properties.getProperty("uploadFileName");
        FILENAME_WEBPAGE= properties.getProperty("webpageFileName");
        DOWNLOAD_URL= properties.getProperty("downloadURL");   
        
		File jsonFile = new File(System.getProperty("user.dir") + "/" + FILENAME_JSON);
		jsonFile.delete();
		File htmlFile = new File(System.getProperty("user.dir") + "/" + FILENAME_WEBPAGE);
		htmlFile.delete();
		File tempFile = new File(System.getProperty("user.dir") + "/temp/" +  FILENAME_UPLOAD);
		tempFile.delete();
		tempFile.getParentFile().delete();	
	}

	@AfterClass
	public static void teardown() throws Exception {
		
		File jsonFile = new File(System.getProperty("user.dir") + "/" + FILENAME_JSON);
		jsonFile.delete();
		File htmlFile = new File(System.getProperty("user.dir") + "/" + FILENAME_WEBPAGE);
		htmlFile.delete();
		File tempFile = new File(System.getProperty("user.dir") + "/temp/" +  FILENAME_UPLOAD);
		tempFile.delete();
		tempFile.getParentFile().delete();	
	}

	@Test
	public void TEST_saveJson2File() throws Exception {

		File jsonFile = new File(System.getProperty("user.dir") + "/" + FILENAME_JSON);

		String JsonString = "{\"a\": NULL}";
		String respJson = fileOperator.saveJson2File(FILENAME_JSON, JsonString);

		Assert.assertEquals(JsonString, respJson);
		if (!jsonFile.exists()) {
			Assert.fail("Generate file failed.");
		}
	}

	
	@Test
	@SuppressWarnings("unchecked")
	public void TEST_downLoadFileToServer() throws Exception {

		File audioFile = new File(System.getProperty("user.dir") + "/" + FILENAME_WEBPAGE);

		String url = DOWNLOAD_URL + FILENAME_WEBPAGE;//https://littleflute.github.io/english/NewConceptEnglish/Book2/5.mp3
		System.out.println("Begin test downLoadFileToServer");
		Map<String, Object> respObject = (Map<String, Object>) fileOperator.downLoadFileToServer(url);

		System.out.println(respObject.toString());
		Assert.assertEquals(System.getProperty("user.dir") + "/" +FILENAME_WEBPAGE, respObject.get("pathOnServer").toString());
		Assert.assertEquals(200, respObject.get("code"));
		Assert.assertEquals(FILENAME_WEBPAGE, respObject.get("filename").toString());		
		Assert.assertThat(respObject.get("message").toString(), containsString("文件下载成功"));

		if (!audioFile.exists()) {
			Assert.fail("Cannot found html file:" + FILENAME_WEBPAGE);
		}
		System.out.println("End of test downLoadFileToServer");
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void TEST_uploadFile() throws Exception {
		
		File tempFile = new File(System.getProperty("user.dir") + "/temp/" +  FILENAME_UPLOAD);
		if (!tempFile.getParentFile().exists()) {
			tempFile.getParentFile().mkdir();
		}
		if (!tempFile.exists())
		{
			tempFile.createNewFile();
			try{
				BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile.getPath()));
				bw.write("This is upload file");
				bw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}	

		MultipartFile mulFile = new MockMultipartFile(tempFile.getName(), tempFile.getName(), null, new FileInputStream(tempFile));
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setServerPort(8080);
		
		Map<String, Object> respObject = (Map<String, Object>) fileOperator.uploadFile(mulFile, request, response);
		
		Assert.assertEquals("http://localhost:8080/static/image/"+FILENAME_UPLOAD, respObject.get("path").toString());
		Assert.assertEquals(200, respObject.get("code"));
		Assert.assertEquals(FILENAME_UPLOAD, respObject.get("filename").toString());
		Assert.assertEquals("上传成功", respObject.get("message").toString());
		
		File checkFile = new File(System.getProperty("user.dir") + "/" +  FILENAME_UPLOAD);
		if (!checkFile.exists()) {
			Assert.fail("Cannot found uploaded file:" + FILENAME_UPLOAD);
		}		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void TEST_getResourceOnServer() throws Exception {
		
		String fileType;
		Map<String, Object> respObject;
				
		fileType = "json";
		respObject = (Map<String, Object>) fileOperator.getResourceOnServer(fileType);
		Assert.assertThat(respObject.get("resource").toString(), containsString(FILENAME_JSON));
		
		fileType = "html";
		respObject = (Map<String, Object>) fileOperator.getResourceOnServer(fileType);
		Assert.assertThat(respObject.get("resource").toString(), containsString(FILENAME_WEBPAGE));
		
		fileType = "txt";
		respObject = (Map<String, Object>) fileOperator.getResourceOnServer(fileType);
		Assert.assertThat(respObject.get("resource").toString(), containsString(FILENAME_UPLOAD));
		
	}
	
}
