package at.ac.tuwien.tracker.android.senders.server;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.IActionListener;
import at.ac.tuwien.tracker.android.common.IMessageBoxCallback;
import at.ac.tuwien.tracker.android.common.Utilities;

public class AutoUploadActivity extends PreferenceActivity implements
        OnPreferenceChangeListener, IMessageBoxCallback, IActionListener,
        OnPreferenceClickListener
{

    private final Handler handler = new Handler();

    SharedPreferences prefs; 
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.autouploadsettings);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        EditTextPreference username = (EditTextPreference) findPreference("server_username");
        username.setOnPreferenceChangeListener(this);
        EditTextPreference password = (EditTextPreference) findPreference("server_password");
        password.setOnPreferenceChangeListener(this);
        
        username.setText(AppSettings.getServer_username());
        password.setText(AppSettings.getServer_password());
        
        
    }
    //TODO Test login data
    public boolean onPreferenceClick(Preference preference)
    {

        if (!IsFormValid())
        {
            Utilities.MsgBox(getString(R.string.autoupload_invalid_form),
                    getString(R.string.autoupload_invalid_form_message),
                    AutoUploadActivity.this);
            return false;
        }

    
        EditTextPreference username = (EditTextPreference) findPreference("server_username");
        EditTextPreference password = (EditTextPreference) findPreference("server_password");

//TODO communicate with webserver and check login

//        AutoUploadHelper aeh = new AutoUploadHelper(null);
//        aeh.SendTestEmail(txtSmtpServer.getText(), txtSmtpPort.getText(),
//                txtUsername.getText(), txtPassword.getText(),
//                chkUseSsl.isChecked(), txtTarget.getText(), txtFrom.getText(),
//                AutoUploadActivity.this);

        return true;
    }

    private boolean IsFormValid()
    {

        EditTextPreference txtUsername = (EditTextPreference) findPreference("server_username");
        EditTextPreference txtPassword = (EditTextPreference) findPreference("server_password");

        return  txtUsername.getText().length() > 0 && txtPassword.getText() != null;

    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (!IsFormValid())
            {
//                Utilities.MsgBox(getString(R.string.autoemail_invalid_form),
//                        getString(R.string.autoemail_invalid_form_message),
//                        this);
                return false;
            }
            else
            {
                return super.onKeyDown(keyCode, event);
            }
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
    }


    public void MessageBoxResult(int which)
    {
        finish();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
    	Utilities.LogInfo("AutoUpload Preference Changed");
    	if (preference.getKey().equals("server_username"))
    	{
    		Utilities.LogInfo("AutoUpload usearname Changed");
    		EditTextPreference txtUsername = (EditTextPreference) findPreference("server_username");
    		txtUsername.setText((String) newValue);
    		AppSettings.setServer_username((String) newValue);
    	}else if(preference.getKey().equals("server_password")) {
    		Utilities.LogInfo("AutoUpload pass Changed");
    		EditTextPreference txtPassword = (EditTextPreference) findPreference("server_password");
    		txtPassword.setText((String) newValue);
    		AppSettings.setServer_password((String) newValue);
    	}	
        	
       
        return true;
    }

    private void setServerValues(String username, String password)
    {
//        SharedPreferences prefs = PreferenceManager
//                .getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor editor = prefs.edit();
//
//        EditTextPreference txtSmtpServer = (EditTextPreference) findPreference("smtp_server");
//        EditTextPreference txtSmtpPort = (EditTextPreference) findPreference("smtp_port");
//        CheckBoxPreference chkUseSsl = (CheckBoxPreference) findPreference("smtp_ssl");
//
//        // Yahoo
//        txtSmtpServer.setText(server);
//        editor.putString("smtp_server", server);
//        txtSmtpPort.setText(port);
//        editor.putString("smtp_port", port);
//        chkUseSsl.setChecked(useSsl);
//        editor.putBoolean("smtp_ssl", useSsl);
//
//        editor.commit();

    }


    private final Runnable successfullySent = new Runnable()
    {
        public void run()
        {
            SuccessfulSending();
        }
    };

    private final Runnable failedSend = new Runnable()
    {

        public void run()
        {
            FailureSending();
        }
    };

    private void FailureSending()
    {
        Utilities.HideProgress();
        Utilities.MsgBox(getString(R.string.sorry), getString(R.string.error_connection), this);
    }

    //TODO replace placeholder
    private void SuccessfulSending()
    {
        Utilities.HideProgress();
        Utilities.MsgBox(getString(R.string.success),
                getString(R.string.placehodler), this);
    }

    public void OnComplete()
    {
        handler.post(successfullySent);
    }

    public void OnFailure()
    {

        handler.post(failedSend);

    }
}
