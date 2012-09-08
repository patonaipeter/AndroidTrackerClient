/*
*    This file is part of GPSLogger for Android.
*
*    GPSLogger for Android is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 2 of the License, or
*    (at your option) any later version.
*
*    GPSLogger for Android is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
*/

package at.ac.tuwien.tracker.android.senders.server;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.res.Resources;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.IActionListener;
import at.ac.tuwien.tracker.android.common.Utilities;
import at.ac.tuwien.tracker.android.senders.IFileSender;

import com.example.androidtrackerclient.R;

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
        	

        	final String sendUrl = url + "/postformdata";
			
			HttpHeaders requestHeaders = new HttpHeaders();
			
			MultiValueMap<String, Object> formData;
			formData = new LinkedMultiValueMap<String, Object>();
			formData.add("description", "GPX Files");
			
			// Sending multipart/form-data
			requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
			// Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(formData, requestHeaders);
        	
        	RestTemplate rest  = new RestTemplate();
        	
        	int nr = 0;
            for (File f : files)
            {
            	nr++;
            	
            	formData.add("file"+nr, new FileSystemResource(f));
            	
            }
            formData.add("nrOfFiles", nr);
            formData.add("username", username);
            formData.add("password", password);
            formData.add("fileType", "gpx");
            Utilities.LogInfo("Uploading files ...");
            String response = rest.postForObject(url, formData, String.class);

            if (response.equals("success")){
                helper.OnComplete();
            }else{
                helper.OnFailure();
            }
        }
        catch (Exception e)
        {
            helper.OnFailure();
            Utilities.LogError("AutoSendHandler.run", e);
        }

    }

}


