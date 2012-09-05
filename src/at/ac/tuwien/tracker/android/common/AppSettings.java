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

package at.ac.tuwien.tracker.android.common;

import android.app.Application;

public class AppSettings extends Application
{
    // ---------------------------------------------------
    // User Preferences
    // ---------------------------------------------------
    private static boolean useImperial = false;
    private static boolean newFileOnceADay;
    private static boolean preferCellTower;
    private static boolean useSatelliteTime;
    private static boolean logToKml;
    private static boolean logToGpx;
    private static boolean logToPlainText;
    private static boolean showInNotificationBar;
    private static int minimumSeconds;
    private static String newFileCreation;
    private static Float autoSendDelay = 0f;
    private static boolean autoSendEnabled = true;
    private static boolean autoEmailEnabled = false;

    private static boolean debugToFile;
    private static int minimumDistance;
    private static boolean shouldSendZipFile;

    private static String server_username;
    private static String server_password;
    
    /**
     * @return the useImperial
     */
    public static boolean shouldUseImperial()
    {
        return useImperial;
    }

    /**
     * @param useImperial the useImperial to set
     */
    static void setUseImperial(boolean useImperial)
    {
        AppSettings.useImperial = useImperial;
    }

    /**
     * @return the newFileOnceADay
     */
    public static boolean shouldCreateNewFileOnceADay()
    {
        return newFileOnceADay;
    }

    /**
     * @param newFileOnceADay the newFileOnceADay to set
     */
    static void setNewFileOnceADay(boolean newFileOnceADay)
    {
        AppSettings.newFileOnceADay = newFileOnceADay;
    }

    /**
     * @return the preferCellTower
     */
    public static boolean shouldPreferCellTower()
    {
        return preferCellTower;
    }

    /**
     * @param preferCellTower the preferCellTower to set
     */
    static void setPreferCellTower(boolean preferCellTower)
    {
        AppSettings.preferCellTower = preferCellTower;
    }

    /**
     * @return the useSatelliteTime
     */
    public static boolean shouldUseSatelliteTime()
    {
        return useSatelliteTime;
    }

    /**
     * @param useSatelliteTime the useSatelliteTime to set
     */
    static void setUseSatelliteTime(boolean useSatelliteTime)
    {
        AppSettings.useSatelliteTime = useSatelliteTime;
    }

    /**
     * @return the logToKml
     */
    public static boolean shouldLogToKml()
    {
        return logToKml;
    }

    /**
     * @param logToKml the logToKml to set
     */
    static void setLogToKml(boolean logToKml)
    {
        AppSettings.logToKml = logToKml;
    }

    /**
     * @return the logToGpx
     */
    public static boolean shouldLogToGpx()
    {
        return logToGpx;
    }

    /**
     * @param logToGpx the logToGpx to set
     */
    static void setLogToGpx(boolean logToGpx)
    {
        AppSettings.logToGpx = logToGpx;
    }

    public static boolean shouldLogToPlainText()
    {
        return logToPlainText;
    }

    static void setLogToPlainText(boolean logToPlainText)
    {
        AppSettings.logToPlainText = logToPlainText;
    }

    /**
     * @return the showInNotificationBar
     */
    public static boolean shouldShowInNotificationBar()
    {
        return showInNotificationBar;
    }

    /**
     * @param showInNotificationBar the showInNotificationBar to set
     */
    static void setShowInNotificationBar(boolean showInNotificationBar)
    {
        AppSettings.showInNotificationBar = showInNotificationBar;
    }


    /**
     * @return the minimumSeconds
     */
    public static int getMinimumSeconds()
    {
        return minimumSeconds;
    }

    /**
     * @param minimumSeconds the minimumSeconds to set
     */
    static void setMinimumSeconds(int minimumSeconds)
    {
        AppSettings.minimumSeconds = minimumSeconds;
    }


    /**
     * @return the minimumDistance
     */
    public static int getMinimumDistanceInMeters()
    {
        return minimumDistance;
    }

    /**
     * @param minimumDistance the minimumDistance to set
     */
    static void setMinimumDistanceInMeters(int minimumDistance)
    {
        AppSettings.minimumDistance = minimumDistance;
    }


    /**
     * @return the newFileCreation
     */
    static String getNewFileCreation()
    {
        return newFileCreation;
    }

    /**
     * @param newFileCreation the newFileCreation to set
     */
    static void setNewFileCreation(String newFileCreation)
    {
        AppSettings.newFileCreation = newFileCreation;
    }


    /**
     * @return the autoSendDelay
     */
    public static Float getAutoSendDelay()
    {
        if (autoSendDelay >= 8f)
        {
            return 8f;
        }
        else
        {
            return autoSendDelay;
        }
    }

    /**
     * @param autoSendDelay the autoSendDelay to set
     */
    static void setAutoSendDelay(Float autoSendDelay)
    {
        if (autoSendDelay >= 8f)
        {
            AppSettings.autoSendDelay = 8f;
        }
        else
        {
            AppSettings.autoSendDelay = autoSendDelay;
        }
    }

    /**
     * @return the autoEmailEnabled
     */
    public static boolean isAutoEmailEnabled()
    {
        return autoEmailEnabled;
    }

    /**
     * @param autoEmailEnabled the autoEmailEnabled to set
     */
    static void setAutoEmailEnabled(boolean autoEmailEnabled)
    {
        AppSettings.autoEmailEnabled = autoEmailEnabled;
    }

  
    public static boolean isDebugToFile()
    {
        return debugToFile;
    }

    public static void setDebugToFile(boolean debugToFile)
    {
        AppSettings.debugToFile = debugToFile;
    }


    public static boolean shouldSendZipFile()
    {
        return shouldSendZipFile;
    }

    public static void setShouldSendZipFile(boolean shouldSendZipFile)
    {
        AppSettings.shouldSendZipFile = shouldSendZipFile;
    }



    public static boolean isAutoSendEnabled()
    {
        return autoSendEnabled;
    }

    public static void setAutoSendEnabled(boolean autoSendEnabled)
    {
        AppSettings.autoSendEnabled = autoSendEnabled;
    }

	public static String getServer_username() {
		return server_username;
	}

	public static void setServer_username(String server_username) {
		AppSettings.server_username = server_username;
	}

	public static String getServer_password() {
		return server_password;
	}

	public static void setServer_password(String server_password) {
		AppSettings.server_password = server_password;
	}

    

}
