package at.ac.tuwien.tracker.android.serverConnection.dtos.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import at.ac.tuwien.tracker.android.serverConnection.dtos.LocationDTO;

public class LocationParcelCreator implements Parcelable.Creator<LocationDTO> {
    public LocationDTO createFromParcel(Parcel source) {
          return new LocationDTO(source);
    }
    public LocationDTO[] newArray(int size) {
          return new LocationDTO[size];
    }
}