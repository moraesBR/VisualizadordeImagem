package senac.visualizadordeimagem.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
    private Uri image;
    private boolean selected;

    public Photo(Uri image) {
        this.image = image;
        this.selected = false;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelect() {
        selected = selected? false: true;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.image, flags);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected Photo(Parcel in) {
        this.image = in.readParcelable(Uri.class.getClassLoader());
        this.selected = in.readByte() != 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

}
