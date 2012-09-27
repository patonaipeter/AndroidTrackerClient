package at.ac.tuwien.tracker.android.serverConnection.dtos.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserDTO;

public class ParcelCreator implements Parcelable.Creator<UserDTO> {
    public UserDTO createFromParcel(Parcel source) {
          return new UserDTO(source);
    }
    public UserDTO[] newArray(int size) {
          return new UserDTO[size];
    }
}