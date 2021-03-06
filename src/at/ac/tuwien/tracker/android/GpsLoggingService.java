
package at.ac.tuwien.tracker.android;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.IActionListener;
import at.ac.tuwien.tracker.android.common.Session;
import at.ac.tuwien.tracker.android.common.Utilities;
import at.ac.tuwien.tracker.android.loggers.FileLoggerFactory;
import at.ac.tuwien.tracker.android.loggers.IFileLogger;
import at.ac.tuwien.tracker.android.senders.AlarmReceiver;
import at.ac.tuwien.tracker.android.senders.FileSenderFactory;
import at.ac.tuwien.tracker.android.serverConnection.RaceMainActivity;

public class GpsLoggingService extends Service implements IActionListener
{
    private static NotificationManager gpsNotifyManager;
    private static int NOTIFICATION_ID = 8675309;

    private final IBinder mBinder = new GpsLoggingBinder();
    private static IGpsLoggerServiceClient mainServiceClient;
    private static RaceMainActivity raceMainActivity;

    private GeneralLocationListener gpsLocationListener;
    private GeneralLocationListener towerLocationListener;
    LocationManager gpsLocationManager;
    private LocationManager towerLocationManager;

    private Intent alarmIntent;

    AlarmManager nextPointAlarmManager;

    
    // ---------------------------------------------------

    @Override
    public IBinder onBind(Intent arg0)
    {
        Utilities.LogDebug("GpsLoggingService.onBind");
        return mBinder;
    }

    @Override
    public void onCreate()
    {
        Utilities.LogDebug("GpsLoggingService.onCreate");
        nextPointAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Utilities.LogInfo("GPSLoggerService created");
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        Utilities.LogDebug("GpsLoggingService.onStart");
        HandleIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Utilities.LogDebug("GpsLoggingService.onStartCommand");
        HandleIntent(intent);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy()
    {
        Utilities.LogWarning("GpsLoggingService is being destroyed by Android OS.");
        mainServiceClient = null;
        raceMainActivity = null;
        super.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        Utilities.LogWarning("Android is low on memory.");
        super.onLowMemory();
    }

    private void HandleIntent(Intent intent)
    {

        Utilities.LogDebug("GpsLoggingService.handleIntent");
        GetPreferences();

        Utilities.LogDebug("Null intent? " + String.valueOf(intent == null));

        if (intent != null)
        {
            Bundle bundle = intent.getExtras();

            if (bundle != null)
            {
                boolean stopRightNow = bundle.getBoolean("immediatestop");
                boolean startRightNow = bundle.getBoolean("immediate");
                boolean sendNow = bundle.getBoolean("autosendAlarm");
                boolean getNextPoint = bundle.getBoolean("getnextpoint");

                Utilities.LogDebug("startRightNow - " + String.valueOf(startRightNow));

                Utilities.LogDebug("autosendAlarm - " + String.valueOf(sendNow));

                if (startRightNow)
                {
                    Utilities.LogInfo("Auto starting logging");

                    StartLogging();
                }

                if (stopRightNow)
                {
                    Utilities.LogInfo("Auto stop logging");
                    StopLogging();
                }

                if (sendNow)
                {

                    Utilities.LogDebug("setReadyToBeAutoSent = true");

                    Session.setReadyToBeAutoSent(true);
                    AutoSendLogFile();
                }

                if (getNextPoint && Session.isStarted())
                {
                    Utilities.LogDebug("HandleIntent - getNextPoint");
                    StartGpsManager();
                }

            }
        }
        else
        {
            // A null intent is passed in if the service has been killed and
            // restarted.
            Utilities.LogDebug("Service restarted with null intent. Start logging.");
            StartLogging();

        }
    }

    public void OnComplete()
    {
    	//delete already uploaded files and create new
    	
    	File gpxFolder = new File(Environment.getExternalStorageDirectory(), "GPSLogger");
    	for(File f : gpxFolder.listFiles()){
    		if(f.getName().endsWith("gpx")){
    			f.delete();
    		}
    	}
    	ResetCurrentFileName(true);
    	
    	
        Utilities.HideProgress();
    }

    public void OnFailure()
    {
        Utilities.HideProgress();
    }

    /**
     * Can be used from calling classes as the go-between for methods and
     * properties.
     */
    public class GpsLoggingBinder extends Binder
    {
        public GpsLoggingService getService()
        {
            Utilities.LogDebug("GpsLoggingBinder.getService");
            return GpsLoggingService.this;
        }
    }

    /**
     * Sets up the auto email timers based on user preferences.
     */
    public void SetupAutoSendTimers()
    {
        Utilities.LogDebug("GpsLoggingService.SetupAutoSendTimers");
        Utilities.LogDebug("isAutoSendEnabled - " + String.valueOf(AppSettings.isAutoSendEnabled()));
        Utilities.LogDebug("Session.getAutoSendDelay - " + String.valueOf(Session.getAutoSendDelay()));
        if (AppSettings.isAutoSendEnabled() && Session.getAutoSendDelay() > 0)
        {
            Utilities.LogDebug("Setting up autosend alarm");
            long triggerTime = System.currentTimeMillis()
                    + (long) (Session.getAutoSendDelay() * 60 * 60 * 1000);

            alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            CancelAlarm();
            Utilities.LogDebug("New alarm intent");
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, triggerTime, sender);
            Utilities.LogDebug("Alarm has been set");

        }
        else
        {
            Utilities.LogDebug("Checking if alarmIntent is null");
            if (alarmIntent != null)
            {
                Utilities.LogDebug("alarmIntent was null, canceling alarm");
                CancelAlarm();
            }
        }
    }

    private void CancelAlarm()
    {
        Utilities.LogDebug("GpsLoggingService.CancelAlarm");

        if (alarmIntent != null)
        {
            Utilities.LogDebug("GpsLoggingService.CancelAlarm");
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Utilities.LogDebug("Pending alarm intent was null? " + String.valueOf(sender == null));
            am.cancel(sender);
        }

    }

    /**
     * Method to be called if user has chosen to auto email log files when he
     * stops logging
     */
    private void AutoSendLogFileOnStop()
    {
        Utilities.LogDebug("GpsLoggingService.AutoSendLogFileOnStop");
        Utilities.LogVerbose("isAutoSendEnabled - " + AppSettings.isAutoSendEnabled());
        // autoSendDelay 0 means send it when you stop logging.
        if (AppSettings.isAutoSendEnabled() && Session.getAutoSendDelay() == 0)
        {
            Session.setReadyToBeAutoSent(true);
            AutoSendLogFile();
        }
    }

    /**
     * Calls the Auto Email Helper which processes the file and sends it.
     */
    private void AutoSendLogFile()
    {

        Utilities.LogDebug("GpsLoggingService.AutoSendLogFile");
        Utilities.LogVerbose("isReadyToBeAutoSent - " + Session.isReadyToBeAutoSent());

        // Check that auto emailing is enabled, there's a valid location and
        // file name.
        if (Session.getCurrentFileName() != null && Session.getCurrentFileName().length() > 0
                && Session.isReadyToBeAutoSent() && Session.hasValidLocation())
        {

            //Don't show a progress bar when auto-emailing
            Utilities.LogInfo("Emailing Log File");

            FileSenderFactory.SendFiles(getApplicationContext(), this);
            Session.setReadyToBeAutoSent(true);
            SetupAutoSendTimers();

        }
    }

    protected void ForceUploadLogFile()
    {

        Utilities.LogDebug("GpsLoggingService.ForceUploadLogFile");
        if (AppSettings.isAutoSendEnabled() && Session.getCurrentFileName() != null && Session.getCurrentFileName().length() > 0)
        {
        	Utilities.LogDebug("GpsLoggingService.ForceUploadLogFile");
            if (IsMainFormVisible())
            {
            	Utilities.LogDebug("Showing AutoUpload Progress");
                Utilities.ShowProgress(mainServiceClient.GetActivity(), getString(R.string.autosend_sending),
                        getString(R.string.please_wait));
            }
            if(IsRaceActivityVisible()){
            	Utilities.ShowProgress(raceMainActivity.GetActivity(), getString(R.string.autosend_sending),
                        getString(R.string.please_wait));
            }

            Utilities.LogInfo("Force Uploading Log File (calling sendFiles...)");
            FileSenderFactory.SendFiles(getApplicationContext(), this);
        }
    }


    /**
     * Sets the activity form for this service. The activity form needs to
     * implement IGpsLoggerServiceClient.
     *
     * @param mainForm The calling client
     */
    protected static void SetServiceClient(IGpsLoggerServiceClient mainForm)
    {
        mainServiceClient = mainForm;
    }

    /**
     * Gets preferences chosen by the user and populates the AppSettings object.
     * Also sets up email timers if required.
     */
    private void GetPreferences()
    {
        Utilities.LogDebug("GpsLoggingService.GetPreferences");
        Utilities.PopulateAppSettings(getApplicationContext());

        Utilities.LogDebug("Session.getAutoSendDelay: " + Session.getAutoSendDelay());
        Utilities.LogDebug("AppSettings.getAutoSendDelay: " + AppSettings.getAutoSendDelay());

        if (Session.getAutoSendDelay() != AppSettings.getAutoSendDelay())
        {
            Utilities.LogDebug("Old autoSendDelay - " + String.valueOf(Session.getAutoSendDelay())
                    + "; New -" + String.valueOf(AppSettings.getAutoSendDelay()));
            Session.setAutoSendDelay(AppSettings.getAutoSendDelay());
            SetupAutoSendTimers();
        }

    }

    /**
     * Resets the form, resets file name if required, reobtains preferences
     */
    public void StartLogging()
    {
        Utilities.LogDebug("GpsLoggingService.StartLogging");
        Session.setAddNewTrackSegment(true);

        if (Session.isStarted())
        {
            return;
        }

        Utilities.LogInfo("Starting logging procedures");
        try
        {
            startForeground(NOTIFICATION_ID, new Notification());
        }
        catch (Exception ex)
        {
            System.out.print(ex.getMessage());
        }


        Session.setStarted(true);

        GetPreferences();
        Notify();
        ResetCurrentFileName(true);
        ClearForm();
        StartGpsManager();

    }

    /**
     * Asks the main service client to clear its form.
     */
    private void ClearForm()
    {
        if (IsMainFormVisible())
        {
            mainServiceClient.ClearForm();
        }
    }

    /**
     * Stops logging, removes notification, stops GPS manager, stops email timer
     */
    public void StopLogging()
    {
        Utilities.LogDebug("GpsLoggingService.StopLogging");
        Session.setAddNewTrackSegment(true);

        Utilities.LogInfo("Stopping logging");
        Session.setStarted(false);
        // Email log file before setting location info to null
        AutoSendLogFileOnStop();
        CancelAlarm();
        Session.setCurrentLocationInfo(null);
        stopForeground(true);

        RemoveNotification();
        StopAlarm();
        StopGpsManager();
        StopMainActivity();
    }

    /**
     * Manages the notification in the status bar
     */
    private void Notify()
    {

        Utilities.LogDebug("GpsLoggingService.Notify");
        if (AppSettings.shouldShowInNotificationBar())
        {
            gpsNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            ShowNotification();
        }
        else
        {
            RemoveNotification();
        }
    }

    /**
     * Hides the notification icon in the status bar if it's visible.
     */
    private void RemoveNotification()
    {
        Utilities.LogDebug("GpsLoggingService.RemoveNotification");
        try
        {
            if (Session.isNotificationVisible())
            {
                gpsNotifyManager.cancelAll();
            }
        }
        catch (Exception ex)
        {
            Utilities.LogError("RemoveNotification", ex);
        }
        finally
        {
            Session.setNotificationVisible(false);
        }
    }

    /**
     * Shows a notification icon in the status bar for GPS Logger
     */
    private void ShowNotification()
    {
        Utilities.LogDebug("GpsLoggingService.ShowNotification");
        // What happens when the notification item is clicked
        Intent contentIntent = new Intent(this, GpsMainActivity.class);

        PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, contentIntent,
                android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification nfc = new Notification(R.drawable.gpsloggericon2, null, System.currentTimeMillis());
        nfc.flags |= Notification.FLAG_ONGOING_EVENT;

        NumberFormat nf = new DecimalFormat("###.######");

        String contentText = getString(R.string.gpslogger_still_running);
        if (Session.hasValidLocation())
        {
            contentText = nf.format(Session.getCurrentLatitude()) + ","
                    + nf.format(Session.getCurrentLongitude());
        }

        nfc.setLatestEventInfo(getApplicationContext(), getString(R.string.gpslogger_still_running),
                contentText, pending);

        gpsNotifyManager.notify(NOTIFICATION_ID, nfc);
        Session.setNotificationVisible(true);
    }

    /**
     * Starts the location manager. There are two location managers - GPS and
     * Cell Tower. This code determines which manager to request updates from
     * based on user preference and whichever is enabled. If GPS is enabled on
     * the phone, that is used. But if the user has also specified that they
     * prefer cell towers, then cell towers are used. If neither is enabled,
     * then nothing is requested.
     */
    private void StartGpsManager()
    {
        Utilities.LogDebug("GpsLoggingService.StartGpsManager");

        GetPreferences();

        if (gpsLocationListener == null)
        {
            gpsLocationListener = new GeneralLocationListener(this);
        }

        if (towerLocationListener == null)
        {
            towerLocationListener = new GeneralLocationListener(this);
        }


        gpsLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        towerLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        CheckTowerAndGpsStatus();

        if (Session.isGpsEnabled() && !AppSettings.shouldPreferCellTower())
        {
            Utilities.LogInfo("Requesting GPS location updates");
            // gps satellite based
            gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0,
                    gpsLocationListener);

            gpsLocationManager.addGpsStatusListener(gpsLocationListener);

            Session.setUsingGps(true);
        }
        else if (Session.isTowerEnabled())
        {
            Utilities.LogInfo("Requesting tower location updates");
            Session.setUsingGps(false);
            // Cell tower and wifi based
            towerLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 0,
                    towerLocationListener);

        }
        else
        {
            Utilities.LogInfo("No provider available");
            Session.setUsingGps(false);
            SetStatus(R.string.gpsprovider_unavailable);
            SetFatalMessage(R.string.gpsprovider_unavailable);
            StopLogging();
            return;
        }

        SetStatus(R.string.started);
    }

    /**
     * This method is called periodically to determine whether the cell tower /
     * gps providers have been enabled, and sets class level variables to those
     * values.
     */
    private void CheckTowerAndGpsStatus()
    {
        Session.setTowerEnabled(towerLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
        Session.setGpsEnabled(gpsLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    /**
     * Stops the location managers
     */
    private void StopGpsManager()
    {

        Utilities.LogDebug("GpsLoggingService.StopGpsManager");

        if (towerLocationListener != null)
        {
            Utilities.LogDebug("Removing towerLocationManager updates");
            towerLocationManager.removeUpdates(towerLocationListener);
        }

        if (gpsLocationListener != null)
        {
            Utilities.LogDebug("Removing gpsLocationManager updates");
            gpsLocationManager.removeUpdates(gpsLocationListener);
            gpsLocationManager.removeGpsStatusListener(gpsLocationListener);
        }

        SetStatus(getString(R.string.stopped));
    }

    /**
     * Sets the current file name based on user preference.
     */
    private void ResetCurrentFileName(boolean newStart)
    {

        Utilities.LogDebug("GpsLoggingService.ResetCurrentFileName");

        String newFileName = Session.getCurrentFileName();
        if (AppSettings.shouldCreateNewFileOnceADay())
        {
            // 20100114.gpx
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            newFileName = sdf.format(new Date());
            Session.setCurrentFileName(newFileName);
        }
        else if (newStart)
        {
            // 20100114183329.gpx
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            newFileName = sdf.format(new Date());
            Session.setCurrentFileName(newFileName);
        }

        if (IsMainFormVisible())
        {
            mainServiceClient.onFileName(newFileName);
        }

    }

    /**
     * Gives a status message to the main service client to display
     *
     * @param status The status message
     */
    void SetStatus(String status)
    {
        if (IsMainFormVisible())
        {
            mainServiceClient.OnStatusMessage(status);
        }
        
        if(IsRaceActivityVisible()){
        	raceMainActivity.OnStatusMessage(status);
        }
    }

    /**
     * Gives an error message to the main service client to display
     *
     * @param messageId ID of string to lookup
     */
    void SetFatalMessage(int messageId)
    {
        if (IsMainFormVisible())
        {
            mainServiceClient.OnFatalMessage(getString(messageId));
        }
        
        if(IsRaceActivityVisible()){
        	raceMainActivity.OnFatalMessage(getString(messageId));
        }
        
    }

    /**
     * Gets string from given resource ID, passes to SetStatus(String)
     *
     * @param stringId ID of string to lookup
     */
    private void SetStatus(int stringId)
    {
        String s = getString(stringId);
        SetStatus(s);
    }

    /**
     * Notifies main form that logging has stopped
     */
    void StopMainActivity()
    {
        if (IsMainFormVisible())
        {
            mainServiceClient.OnStopLogging();
        }
        
        if(IsRaceActivityVisible()){
        	raceMainActivity.OnStopLogging();
        }
    }


    /**
     * Stops location manager, then starts it.
     */
    void RestartGpsManagers()
    {
        Utilities.LogDebug("GpsLoggingService.RestartGpsManagers");
        StopGpsManager();
        StartGpsManager();
    }


    /**
     * This event is raised when the GeneralLocationListener has a new location.
     * This method in turn updates notification, writes to file, reobtains
     * preferences, notifies main service client and resets location managers.
     *
     * @param loc Location object
     */
    void OnLocationChanged(Location loc)
    {

        if (!Session.isStarted())
        {
            Utilities.LogDebug("OnLocationChanged called, but Session.isStarted is false");
            StopLogging();
            return;
        }

        Utilities.LogDebug("GpsLoggingService.OnLocationChanged");


        long currentTimeStamp = System.currentTimeMillis();

        // Wait some time even on 0 frequency so that the UI doesn't lock up

        if ((currentTimeStamp - Session.getLatestTimeStamp()) < 1000)
        {
            return;
        }

        // Don't do anything until the user-defined time has elapsed
        if ((currentTimeStamp - Session.getLatestTimeStamp()) < (AppSettings.getMinimumSeconds() * 1000))
        {
            return;
        }

        //Don't do anything until the user-defined distance has been traversed
        if (AppSettings.getMinimumDistanceInMeters() > 0 && Session.hasValidLocation())
        {

            double distanceTraveled = Utilities.CalculateDistance(loc.getLatitude(), loc.getLongitude(),
                    Session.getCurrentLatitude(), Session.getCurrentLongitude());

            if (AppSettings.getMinimumDistanceInMeters() > distanceTraveled)
            {
                SetStatus("Only " + String.valueOf(Math.floor(distanceTraveled)) + " m traveled.");
                StopManagerAndResetAlarm();
                return;
            }

        }


        Utilities.LogInfo("New location obtained");
        ResetCurrentFileName(false);
       
        Session.setCurrentLocationInfo(loc);
        Double distance = SetDistanceTraveled(loc);
        SetSpeed(loc,distance,currentTimeStamp);
        Notify();
        WriteToFile(loc);
        GetPreferences();
        StopManagerAndResetAlarm();

        if (IsMainFormVisible())
        {
            mainServiceClient.OnLocationUpdate(loc);
        }
        
        if(IsRaceActivityVisible()){
        	raceMainActivity.OnLocationUpdate(loc);
        } 
        Session.setLatestTimeStamp(System.currentTimeMillis());
    }

    private void SetSpeed(Location loc, double distance, double currentTimeStamp) {
    	 if (Session.getPreviousLocationInfo() == null)
         {
             Session.setPreviousLocationInfo(loc);
         }
    	 
    	 double temp = (currentTimeStamp -Session.getLatestTimeStamp())/1000;
    	 double speed = distance/temp;
    	 loc.setSpeed((float)speed);
		
	}

	private double SetDistanceTraveled(Location loc)
    {
        // Distance
        if (Session.getPreviousLocationInfo() == null)
        {
            Session.setPreviousLocationInfo(loc);
        }
        // Calculate this location and the previous location location and add to the current running total distance.
        // NOTE: Should be used in conjunction with 'distance required before logging' for more realistic values.
        final float[] results= new float[3];
        loc.distanceBetween(Session.getPreviousLatitude(),  Session.getPreviousLongitude(), loc.getLatitude(), loc.getLongitude(), results);
        final float bearing = results[1];
        loc.setBearing(bearing);
        
        double distance = Utilities.CalculateDistance(
                Session.getPreviousLatitude(),
                Session.getPreviousLongitude(),
                loc.getLatitude(),
                loc.getLongitude());
        Session.setPreviousLocationInfo(loc);
        Session.setTotalTravelled(Session.getTotalTravelled() + distance);
        return results[0];
    }

    protected void StopManagerAndResetAlarm()
    {
        Utilities.LogDebug("GpsLoggingService.StopManagerAndResetAlarm");
        StopGpsManager();
        SetAlarmForNextPoint();
    }


    private void StopAlarm()
    {
        Utilities.LogDebug("GpsLoggingService.StopAlarm");
        Intent i = new Intent(this, GpsLoggingService.class);
        i.putExtra("getnextpoint", true);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        nextPointAlarmManager.cancel(pi);
    }


    private void SetAlarmForNextPoint()
    {

        Utilities.LogDebug("GpsLoggingService.SetAlarmForNextPoint");

        Intent i = new Intent(this, GpsLoggingService.class);

        i.putExtra("getnextpoint", true);

        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        nextPointAlarmManager.cancel(pi);

        nextPointAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AppSettings.getMinimumSeconds() * 1000, pi);

    }

    /**
     * Calls file helper to write a given location to a file.
     *
     * @param loc Location object
     */
    private void WriteToFile(Location loc)
    {
        Utilities.LogDebug("GpsLoggingService.WriteToFile");
        List<IFileLogger> loggers = FileLoggerFactory.GetFileLoggers();
        Session.setAddNewTrackSegment(false);

        for (IFileLogger logger : loggers)
        {
            try
            {
                logger.Write(loc);
                Session.setAllowDescription(true);
            }
            catch (Exception e)
            {
                SetStatus(R.string.could_not_write_to_file);
            }
        }

    }

    /**
     * Informs the main service client of the number of visible satellites.
     *
     * @param count Number of Satellites
     */
    void SetSatelliteInfo(int count)
    {
        if (IsMainFormVisible())
        {
            mainServiceClient.OnSatelliteCount(count);
        }
        
        if(IsRaceActivityVisible()){
        	raceMainActivity.OnSatelliteCount(count);
        }
    }


    private boolean IsMainFormVisible()
    {
        return mainServiceClient != null;
    }
    
    private boolean IsRaceActivityVisible()
    {
        return raceMainActivity != null;
    }
    

	public static void SetRaceClient(RaceMainActivity raceMain) {
		raceMainActivity = raceMain;
		
	}


}
