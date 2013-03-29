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

//TODO: Move GPSMain email now call to gpsmain to allow closing of progress bar

package at.ac.tuwien.tracker.android;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.IActionListener;
import at.ac.tuwien.tracker.android.common.Session;
import at.ac.tuwien.tracker.android.common.Utilities;
import at.ac.tuwien.tracker.android.loggers.FileLoggerFactory;
import at.ac.tuwien.tracker.android.loggers.IFileLogger;
import at.ac.tuwien.tracker.android.senders.FileSenderFactory;
import at.ac.tuwien.tracker.android.senders.IFileSender;
import at.ac.tuwien.tracker.android.senders.server.AutoUploadActivity;

public class GpsMainActivity extends Activity implements OnCheckedChangeListener,
        IGpsLoggerServiceClient, View.OnClickListener, IActionListener
{

    /**
     * General all purpose handler used for updating the UI from threads.
     */
    private static Intent serviceIntent;
    private GpsLoggingService loggingService;

    /**
     * Provides a connection to the GPS Logging Service
     */
    private final ServiceConnection gpsServiceConnection = new ServiceConnection()
    {

        public void onServiceDisconnected(ComponentName name)
        {
            loggingService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service)
        {
            loggingService = ((GpsLoggingService.GpsLoggingBinder) service).getService();
            GpsLoggingService.SetServiceClient(GpsMainActivity.this);


////            Button buttonSinglePoint = (Button) findViewById(R.id.buttonSinglePoint);
//
//            buttonSinglePoint.setOnClickListener(GpsMainActivity.this);
//
//            if (Session.isStarted())
//            {
//                if (Session.isSinglePointMode())
//                {
//                    SetMainButtonEnabled(false);
//                }
//                else
//                {
//                    SetMainButtonChecked(true);
//                    SetSinglePointButtonEnabled(false);
//                }
//
//                DisplayLocationInfo(Session.getCurrentLocationInfo());
//            }

            // Form setup - toggle button, display existing location info
            ToggleButton buttonOnOff = (ToggleButton) findViewById(R.id.buttonOnOff);
            buttonOnOff.setOnCheckedChangeListener(GpsMainActivity.this);
        }
    };


    /**
     * Event raised when the form is created for the first time
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        Utilities.LogDebug("GpsMainActivity.onCreate");

        super.onCreate(savedInstanceState);

        Utilities.LogInfo("GPSLogger started");

        setContentView(R.layout.main);

        // Moved to onResume to update the list of loggers
        //GetPreferences();

        StartAndBindService();
    }

    @Override
    protected void onStart()
    {
        Utilities.LogDebug("GpsMainActivity.onStart");
        super.onStart();
        StartAndBindService();
    }

    @Override
    protected void onResume()
    {
        Utilities.LogDebug("GpsMainactivity.onResume");
        super.onResume();
        GetPreferences();
        StartAndBindService();
    }

    /**
     * Starts the service and binds the activity to it.
     */
    private void StartAndBindService()
    {
        Utilities.LogDebug("StartAndBindService - binding now");
        serviceIntent = new Intent(this, GpsLoggingService.class);
        // Start the service in case it isn't already running
        startService(serviceIntent);
        // Now bind to service
        bindService(serviceIntent, gpsServiceConnection, Context.BIND_AUTO_CREATE);
        Session.setBoundToService(true);
    }


    /**
     * Stops the service if it isn't logging. Also unbinds.
     */
    private void StopAndUnbindServiceIfRequired()
    {
        Utilities.LogDebug("GpsMainActivity.StopAndUnbindServiceIfRequired");
        if (Session.isBoundToService())
        {
            unbindService(gpsServiceConnection);
            Session.setBoundToService(false);
        }

        if (!Session.isStarted())
        {
            Utilities.LogDebug("StopServiceIfRequired - Stopping the service");
            //serviceIntent = new Intent(this, GpsLoggingService.class);
            stopService(serviceIntent);
        }

    }

    @Override
    protected void onPause()
    {

        Utilities.LogDebug("GpsMainActivity.onPause");
        StopAndUnbindServiceIfRequired();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {

        Utilities.LogDebug("GpsMainActivity.onDestroy");
        StopAndUnbindServiceIfRequired();
        super.onDestroy();

    }

    /**
     * Called when the toggle button is clicked
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        Utilities.LogDebug("GpsMainActivity.onCheckedChanged");

        if (isChecked)
        {
            GetPreferences();
            SetSinglePointButtonEnabled(false);
            loggingService.SetupAutoSendTimers();
            loggingService.StartLogging();
        }
        else
        {
            SetSinglePointButtonEnabled(true);
            loggingService.StopLogging();
        }
    }

    /**
     * Called when the single point button is clicked
     */
    public void onClick(View view)
    {
        Utilities.LogDebug("GpsMainActivity.onClick");

        if (!Session.isStarted())
        {
            SetMainButtonEnabled(false);
            loggingService.StartLogging();
            Session.setSinglePointMode(true);
        }
        else if (Session.isStarted() && Session.isSinglePointMode())
        {
            loggingService.StopLogging();
            SetMainButtonEnabled(true);
            Session.setSinglePointMode(false);
        }
    }


    public void SetSinglePointButtonEnabled(boolean enabled)
    {
//        Button buttonSinglePoint = (Button) findViewById(R.id.buttonSinglePoint);
//        buttonSinglePoint.setEnabled(enabled);
    }

    public void SetMainButtonEnabled(boolean enabled)
    {
        ToggleButton buttonOnOff = (ToggleButton) findViewById(R.id.buttonOnOff);
        buttonOnOff.setEnabled(enabled);
    }

    public void SetMainButtonChecked(boolean checked)
    {
        ToggleButton buttonOnOff = (ToggleButton) findViewById(R.id.buttonOnOff);
        buttonOnOff.setChecked(checked);
    }

    /**
     * Gets preferences chosen by the user
     */
    private void GetPreferences()
    {
        Utilities.PopulateAppSettings(getApplicationContext());
        ShowPreferencesSummary();
    }

    /**
     * Displays a human readable summary of the preferences chosen by the user
     * on the main form
     */
    private void ShowPreferencesSummary()
    {
        Utilities.LogDebug("GpsMainActivity.ShowPreferencesSummary");
        try
        {
            TextView txtLoggingTo = (TextView) findViewById(R.id.txtLoggingTo);
            TextView txtFrequency = (TextView) findViewById(R.id.txtFrequency);
            TextView txtDistance = (TextView) findViewById(R.id.txtDistance);
            TextView txtAutoEmail = (TextView) findViewById(R.id.txtAutoEmail);

            List<IFileLogger> loggers = FileLoggerFactory.GetFileLoggers();

            if (loggers.size() > 0)
            {

                ListIterator<IFileLogger> li = loggers.listIterator();
                String logTo = li.next().getName();
                while (li.hasNext())
                {
                    logTo += ", " + li.next().getName();
                }
                txtLoggingTo.setText(logTo);

            }
            else
            {

                txtLoggingTo.setText(R.string.summary_loggingto_screen);

            }

            if (AppSettings.getMinimumSeconds() > 0)
            {
                String descriptiveTime = Utilities.GetDescriptiveTimeString(AppSettings.getMinimumSeconds(),
                        getApplicationContext());

                txtFrequency.setText(descriptiveTime);
            }
            else
            {
                txtFrequency.setText(R.string.summary_freq_max);

            }


            if (AppSettings.getMinimumDistanceInMeters() > 0)
            {
                if (AppSettings.shouldUseImperial())
                {
                    int minimumDistanceInFeet = Utilities.MetersToFeet(AppSettings.getMinimumDistanceInMeters());
                    txtDistance.setText(((minimumDistanceInFeet == 1)
                            ? getString(R.string.foot)
                            : String.valueOf(minimumDistanceInFeet) + getString(R.string.feet)));
                }
                else
                {
                    txtDistance.setText(((AppSettings.getMinimumDistanceInMeters() == 1)
                            ? getString(R.string.meter)
                            : String.valueOf(AppSettings.getMinimumDistanceInMeters()) + getString(R.string.meters)));
                }
            }
            else
            {
                txtDistance.setText(R.string.summary_dist_regardless);
            }


            if (AppSettings.isAutoSendEnabled())
            {
                String autoEmailResx;

                if (AppSettings.getAutoSendDelay() == 0)
                {
                    autoEmailResx = "autoemail_frequency_whenistop";
                }
                else
                {

                    autoEmailResx = "autoemail_frequency_"
                            + String.valueOf(AppSettings.getAutoSendDelay()).replace(".", "");
                }

                String autoEmailDesc = getString(getResources().getIdentifier(autoEmailResx, "string", getPackageName()));

                txtAutoEmail.setText(autoEmailDesc);
            }
            else
            {
                TableRow trAutoEmail = (TableRow) findViewById(R.id.trAutoEmail);
                trAutoEmail.setVisibility(View.INVISIBLE);
            }

            onFileName(Session.getCurrentFileName());
        }
        catch (Exception ex)
        {
            Utilities.LogError("ShowPreferencesSummary", ex);
        }


    }

    /**
     * Handles the hardware back-button press
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Utilities.LogInfo("KeyDown - " + String.valueOf(keyCode));

        if (keyCode == KeyEvent.KEYCODE_BACK && Session.isBoundToService())
        {
            StopAndUnbindServiceIfRequired();
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Called when the menu is created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu, menu);

        return true;

    }

    /**
     * Called when one of the menu items is selected.
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int itemId = item.getItemId();
        Utilities.LogInfo("Option item selected - " + String.valueOf(item.getTitle()));

        switch (itemId)
        {
        
        //SelectAndUploadFile()
        
            case R.id.mnuSettings:
                Intent settingsActivity = new Intent(getApplicationContext(), GpsSettingsActivity.class);
                startActivity(settingsActivity);
                break;
            case R.id.mnuAnnotate:
                Annotate();
                break;
//            case R.id.mnuShare:
//                Share();
//                break;
            case R.id.mnuUpload:
                UploadNow();
                break;
            case R.id.mnuExit:
                loggingService.StopLogging();
                loggingService.stopSelf();
                finish();
                break;
        }
        return false;
    }


    private void UploadNow()
    {
        Utilities.LogDebug("GpsMainActivity.UploadNow");

        if (AppSettings.isAutoSendEnabled())
        {
            loggingService.ForceUploadLogFile();
        }
        else
        {

            Intent pref = new Intent().setClass(this, GpsSettingsActivity.class);
            pref.putExtra("autosend_preferencescreen", true);
            startActivity(pref);

        }

    }


   
    
    //TODO make a menupoint and call this method on click (above in select case)
    private void SelectAndUploadFile()
    {
    	Utilities.LogDebug("GpsMainActivity.SelectAndEmailFile");

    	Intent settingsIntent = new Intent(getApplicationContext(), AutoUploadActivity.class);

    	if (!Utilities.IsLoginSetup())
    	{

    		startActivity(settingsIntent);
    	}
    	else
    	{
    		ShowFileListDialog(settingsIntent, FileSenderFactory.GetServerUploadSender(getApplicationContext(), this));
    	}

    }


    private void ShowFileListDialog(final Intent settingsIntent, final IFileSender sender)
    {

        final File gpxFolder = new File(Environment.getExternalStorageDirectory(), "GPSLogger");

        if (gpxFolder.exists())
        {
            File[] enumeratedFiles = gpxFolder.listFiles(sender);

            Arrays.sort(enumeratedFiles, new Comparator<File>()
            {
                public int compare(File f1, File f2)
                {
                    return -1 * Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                }
            });

            List<String> fileList = new ArrayList<String>(enumeratedFiles.length);

            for (File f : enumeratedFiles)
            {
                fileList.add(f.getName());
            }

            final String settingsText = getString(R.string.menu_settings);

            fileList.add(0, settingsText);
            final String[] files = fileList.toArray(new String[fileList.size()]);

            final Dialog dialog = new Dialog(this);
            dialog.setTitle(R.string.osm_pick_file);
            dialog.setContentView(R.layout.filelist);
            ListView displayList = (ListView) dialog.findViewById(R.id.listViewFiles);

            displayList.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_single_choice, files));

            displayList.setOnItemClickListener(new OnItemClickListener()
            {
                public void onItemClick(AdapterView<?> av, View v, int index, long arg)
                {

                    dialog.dismiss();
                    String chosenFileName = files[index];

                    if (chosenFileName.equalsIgnoreCase(settingsText))
                    {
                        startActivity(settingsIntent);
                    }
                    else
                    {
                        Utilities.ShowProgress(GpsMainActivity.this, getString(R.string.please_wait),
                                getString(R.string.please_wait));
                        List<File> files = new ArrayList<File>();
                        files.add(new File(gpxFolder, chosenFileName));
                        sender.UploadFile(files);
                    }
                }
            });
            dialog.show();
        }
        else
        {
            Utilities.MsgBox(getString(R.string.sorry), getString(R.string.no_files_found), this);
        }
    }


    /**
     * Prompts user for input, then adds text to log file
     */
    private void Annotate()
    {
        Utilities.LogDebug("GpsMainActivity.Annotate");

        if (!AppSettings.shouldLogToGpx() && !AppSettings.shouldLogToKml())
        {
            return;
        }

        if (!Session.shoulAllowDescription())
        {
            Utilities.MsgBox(getString(R.string.not_yet),
                    getString(R.string.cant_add_description_until_next_point),
                    GetActivity());

            return;

        }

        AlertDialog.Builder alert = new AlertDialog.Builder(GpsMainActivity.this);

        alert.setTitle(R.string.add_description);
        alert.setMessage(R.string.letters_numbers);

        // Set an EditText view to get user input
        final EditText input = new EditText(getApplicationContext());
        alert.setView(input);

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                final String desc = Utilities.CleanDescription(input.getText().toString());
                Annotate(desc);
            }

        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                // Cancelled.
            }
        });

        alert.show();
    }

    private void Annotate(String description)
    {
        Utilities.LogDebug("GpsMainActivity.Annotate(description)");

        List<IFileLogger> loggers = FileLoggerFactory.GetFileLoggers();

        for (IFileLogger logger : loggers)
        {
            try
            {
                logger.Annotate(description, Session.getCurrentLocationInfo());
                SetStatus(getString(R.string.description_added));
                Session.setAllowDescription(false);
            }
            catch (Exception e)
            {
                SetStatus(getString(R.string.could_not_write_to_file));
            }
        }
    }

    /**
     * Clears the table, removes all values.
     */
    public void ClearForm()
    {

        Utilities.LogDebug("GpsMainActivity.ClearForm");

        TextView tvLatitude = (TextView) findViewById(R.id.txtLatitude);
        TextView tvLongitude = (TextView) findViewById(R.id.txtLongitude);
        TextView tvDateTime = (TextView) findViewById(R.id.txtDateTimeAndProvider);

        TextView tvAltitude = (TextView) findViewById(R.id.txtAltitude);

        TextView txtSpeed = (TextView) findViewById(R.id.txtSpeed);

        TextView txtSatellites = (TextView) findViewById(R.id.txtSatellites);
        TextView txtDirection = (TextView) findViewById(R.id.txtDirection);
        TextView txtAccuracy = (TextView) findViewById(R.id.txtAccuracy);
        TextView txtDistance = (TextView) findViewById(R.id.txtDistanceTravelled);

        tvLatitude.setText("");
        tvLongitude.setText("");
        tvDateTime.setText("");
        tvAltitude.setText("");
        txtSpeed.setText("");
        txtSatellites.setText("");
        txtDirection.setText("");
        txtAccuracy.setText("");
        txtDistance.setText("");
        Session.setPreviousLocationInfo(null);
        Session.setTotalTravelled(0d);
    }

    public void OnStopLogging()
    {
        Utilities.LogDebug("GpsMainActivity.OnStopLogging");
        SetMainButtonChecked(false);
    }

    /**
     * Sets the message in the top status label.
     *
     * @param message The status message
     */
    private void SetStatus(String message)
    {
        Utilities.LogDebug("GpsMainActivity.SetStatus: " + message);
        TextView tvStatus = (TextView) findViewById(R.id.textStatus);
        tvStatus.setText(message);
        Utilities.LogInfo(message);
    }

    /**
     * Sets the number of satellites in the satellite row in the table.
     *
     * @param number The number of satellites
     */
    private void SetSatelliteInfo(int number)
    {
        Session.setSatelliteCount(number);
        TextView txtSatellites = (TextView) findViewById(R.id.txtSatellites);
        txtSatellites.setText(String.valueOf(number));
    }

    /**
     * Given a location fix, processes it and displays it in the table on the
     * form.
     *
     * @param loc Location information
     */
    private void DisplayLocationInfo(Location loc)
    {
        Utilities.LogDebug("GpsMainActivity.DisplayLocationInfo");
        try
        {

            if (loc == null)
            {
                return;
            }

            TextView tvLatitude = (TextView) findViewById(R.id.txtLatitude);
            TextView tvLongitude = (TextView) findViewById(R.id.txtLongitude);
            TextView tvDateTime = (TextView) findViewById(R.id.txtDateTimeAndProvider);

            TextView tvAltitude = (TextView) findViewById(R.id.txtAltitude);

            TextView txtSpeed = (TextView) findViewById(R.id.txtSpeed);

            TextView txtSatellites = (TextView) findViewById(R.id.txtSatellites);
            TextView txtDirection = (TextView) findViewById(R.id.txtDirection);
            TextView txtAccuracy = (TextView) findViewById(R.id.txtAccuracy);
            TextView txtTravelled = (TextView) findViewById(R.id.txtDistanceTravelled);
            String providerName = loc.getProvider();

            if (providerName.equalsIgnoreCase("gps"))
            {
                providerName = getString(R.string.providername_gps);
            }
            else
            {
                providerName = getString(R.string.providername_celltower);
            }

            tvDateTime.setText(new Date(Session.getLatestTimeStamp()).toLocaleString()
                    + getString(R.string.providername_using, providerName));
            tvLatitude.setText(String.valueOf(loc.getLatitude()));
            tvLongitude.setText(String.valueOf(loc.getLongitude()));

            if (loc.hasAltitude())
            {

                double altitude = loc.getAltitude();

                if (AppSettings.shouldUseImperial())
                {
                    tvAltitude.setText(String.valueOf(Utilities.MetersToFeet(altitude))
                            + getString(R.string.feet));
                }
                else
                {
                    tvAltitude.setText(String.valueOf(altitude) + getString(R.string.meters));
                }

            }
            else
            {
                tvAltitude.setText(R.string.not_applicable);
            }

            if (loc.hasSpeed())
            {

                float speed = loc.getSpeed();
                String unit;
                if (AppSettings.shouldUseImperial())
                {
                    if (speed > 1.47)
                    {
                        speed = speed * 0.6818f;
                        unit = getString(R.string.miles_per_hour);

                    }
                    else
                    {
                        speed = Utilities.MetersToFeet(speed);
                        unit = getString(R.string.feet_per_second);
                    }
                }
                else
                {
                    if (speed > 0.277)
                    {
                        speed = speed * 3.6f;
                        unit = getString(R.string.kilometers_per_hour);
                    }
                    else
                    {
                        unit = getString(R.string.meters_per_second);
                    }
                }

                txtSpeed.setText(String.valueOf(speed) + unit);

            }
            else
            {
                txtSpeed.setText(R.string.not_applicable);
            }

            if (loc.hasBearing())
            {

                float bearingDegrees = loc.getBearing();
                String direction;

                direction = Utilities.GetBearingDescription(bearingDegrees, getApplicationContext());

                txtDirection.setText(direction + "(" + String.valueOf(Math.round(bearingDegrees))
                        + getString(R.string.degree_symbol) + ")");
            }
            else
            {
                txtDirection.setText(R.string.not_applicable);
            }

            if (!Session.isUsingGps())
            {
                txtSatellites.setText(R.string.not_applicable);
                Session.setSatelliteCount(0);
            }

            if (loc.hasAccuracy())
            {

                float accuracy = loc.getAccuracy();

                if (AppSettings.shouldUseImperial())
                {
                    txtAccuracy.setText(getString(R.string.accuracy_within,
                            String.valueOf(Utilities.MetersToFeet(accuracy)), getString(R.string.feet)));

                }
                else
                {
                    txtAccuracy.setText(getString(R.string.accuracy_within, String.valueOf(accuracy),
                            getString(R.string.meters)));
                }

            }
            else
            {
                txtAccuracy.setText(R.string.not_applicable);
            }


            String distanceUnit;
            double distanceValue = Session.getTotalTravelled();
            if (AppSettings.shouldUseImperial())
            {
                distanceUnit = getString(R.string.feet);
                distanceValue = Utilities.MetersToFeet(distanceValue);
                // When it passes more than 1 kilometer, convert to miles.
                if (distanceValue > 3281)
                {
                    distanceUnit = getString(R.string.miles);
                    distanceValue = distanceValue / 5280;
                }
            }
            else
            {
                distanceUnit = getString(R.string.meters);
                if (distanceValue > 1000)
                {
                    distanceUnit = getString(R.string.kilometers);
                    distanceValue = distanceValue / 1000;
                }
            }

            txtTravelled.setText(String.valueOf(Math.round(distanceValue)) + " " + distanceUnit +
                    " (" + Session.getNumLegs() + " points)");

        }
        catch (Exception ex)
        {
            SetStatus(getString(R.string.error_displaying, ex.getMessage()));
        }

    }

    public void OnLocationUpdate(Location loc)
    {
        Utilities.LogDebug("GpsMainActivity.OnLocationUpdate");
        DisplayLocationInfo(loc);
        ShowPreferencesSummary();
        SetMainButtonChecked(true);

        if (Session.isSinglePointMode())
        {
            loggingService.StopLogging();
            SetMainButtonEnabled(true);
            Session.setSinglePointMode(false);
        }

    }

    public void OnSatelliteCount(int count)
    {
        SetSatelliteInfo(count);

    }

    public void onFileName(String newFileName)
    {
        if (newFileName == null || newFileName.length() <= 0)
        {
            return;
        }

        TextView txtFilename = (TextView) findViewById(R.id.txtFileName);

        if (AppSettings.shouldLogToGpx() || AppSettings.shouldLogToKml())
        {
        	Thread t = new Thread(){

				@Override
				public void run() {
					runOnUiThread(new Runnable(){

						public void run() {
							TextView txtFilename = (TextView) findViewById(R.id.txtFileName);
							txtFilename.setText(getString(R.string.summary_current_filename_format,Session.getCurrentFileName()));
						}
						
					});
				}
        		
        	};

          
        }
        else
        {
            txtFilename.setText("");
        }


    }

    public void OnStatusMessage(String message)
    {
        SetStatus(message);
    }

    public void OnFatalMessage(String message)
    {
        Utilities.MsgBox(getString(R.string.sorry), message, this);
    }

    public Activity GetActivity()
    {
        return this;
    }


    public void OnComplete()
    {
        Utilities.HideProgress();
    }

    public void OnFailure()
    {
        Utilities.HideProgress();
    }
}
