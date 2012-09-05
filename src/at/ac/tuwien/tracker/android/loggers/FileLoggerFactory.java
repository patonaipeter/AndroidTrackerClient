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

package at.ac.tuwien.tracker.android.loggers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.Session;

public class FileLoggerFactory
{
    public static List<IFileLogger> GetFileLoggers()
    {
        File gpxFolder = new File(Environment.getExternalStorageDirectory(), "GPSLogger");
        if (!gpxFolder.exists())
        {
            gpxFolder.mkdirs();
        }

        List<IFileLogger> loggers = new ArrayList<IFileLogger>();

        if (AppSettings.shouldLogToGpx())
        {
            File gpxFile = new File(gpxFolder.getPath(), Session.getCurrentFileName() + ".gpx");
            loggers.add(new Gpx10FileLogger(gpxFile, AppSettings.shouldUseSatelliteTime(), Session.shouldAddNewTrackSegment(), Session.getSatelliteCount()));
        }

        if (AppSettings.shouldLogToKml())
        {
            File kmlFile = new File(gpxFolder.getPath(), Session.getCurrentFileName() + ".kml");
            loggers.add(new Kml22FileLogger(kmlFile, AppSettings.shouldUseSatelliteTime(), Session.shouldAddNewTrackSegment()));
        }

        if (AppSettings.shouldLogToPlainText())
        {
            File file = new File(gpxFolder.getPath(), Session.getCurrentFileName() + ".txt");
            loggers.add(new PlainTextFileLogger(file, AppSettings.shouldUseSatelliteTime()));
        }

        return loggers;
    }
}
