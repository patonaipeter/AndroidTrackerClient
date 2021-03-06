
package at.ac.tuwien.tracker.android.senders.server;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.IActionListener;
import at.ac.tuwien.tracker.android.common.Utilities;
import at.ac.tuwien.tracker.android.senders.IFileSender;

public class AutoUploadHelper implements IActionListener, IFileSender
{

    IActionListener callback;
    Context ctx;

    public AutoUploadHelper(Context ctx, IActionListener callback)
    {
        this.callback = callback;
        this.ctx = ctx;
    }

    public void UploadFile(List<File> files)
    {

        ArrayList<File> filesToSend = new ArrayList<File>();

        //If a zip file exists, remove others
        for (File f : files)
        {
            filesToSend.add(f);

            if (f.getName().contains(".zip"))
            {
                filesToSend.clear();
                filesToSend.add(f);
                break;
            }
        }
        
        // SETTING BASE URL
        URI url = null;
		try {
			url = new URI(ctx.getString(R.string.base_uri));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

        Thread t = new Thread(new AutoSendHandler(filesToSend.toArray(new File[filesToSend.size()]), this, url));
        t.start();
    }



    public void OnComplete()
    {
        // This was a success
        Utilities.LogInfo("File uploaded");

        callback.OnComplete();
    }

    public void OnFailure()
    {
        callback.OnFailure();
    }

    public boolean accept(File dir, String name)
    {
        return name.toLowerCase().endsWith(".zip")
                || name.toLowerCase().endsWith(".gpx")
                || name.toLowerCase().endsWith(".kml");
    }

}

class AutoSendHandler implements Runnable
{

    File[] files;
    private final IActionListener helper;
    private URI url;
    String uploadPath = "/sendLogFile"; 

    public AutoSendHandler(File[] files, IActionListener helper, URI url)
    {
        this.files = files;
        this.helper = helper;
        this.url = url;
    }

    public void run()
    {
        try
        {
        	

        	String username = AppSettings.getServer_username();
        	String password = AppSettings.getServer_password();
        	

			final String sendUrl = url + uploadPath;
			

			MultiValueMap<String, Object> formData;
			formData = new LinkedMultiValueMap<String, Object>();
			ResponseEntity<String> response = null;
			
			//server-call for each file
			for (File f : files)
			{
				Resource resource = new FileSystemResource(f);
				formData.add("description", "GPX");
				formData.add("username", username);
				formData.add("password",password);
				formData.add("file", resource);
				formData.add("nrOfFiles", ""+files.length);	
				formData.add("fileName", f.getName());

				// The URL for making the POST request
				
				HttpHeaders requestHeaders = new HttpHeaders();

				// Sending multipart/form-data
				requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

				// Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(formData, requestHeaders);

				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();

				// Make the network request, posting the message, expecting a String in response from the server
				response = restTemplate.exchange(sendUrl, HttpMethod.POST, requestEntity, String.class);
				
			
				
			}
		
				Utilities.LogInfo(response.getBody());
				helper.OnComplete();

			
			
        }
        catch (Exception e)
        {
            helper.OnFailure();
            Utilities.LogError("AutoSendHandler.run", e);
            Utilities.LogInfo(e.toString());
        }

    }

}


